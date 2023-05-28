import sys
from pyglet.gl import *
import numpy as np

window = pyglet.window.Window(width=1280, height=720, resizable=True, caption="Sjencanje")
window.set_minimum_size(300,200)
window.projection = pyglet.window.Projection3D()
pyglet.gl.glClearColor(0.6,0.6,0.5,1)
batch = pyglet.graphics.Batch()

vertices, polygons = [], []
transform_matrix = np.array
O, G = np.zeros(3), np.zeros(3)
center, eye_coordinates, light_source = np.array, np.array, np.array

lighting_type = False

class Polygon():
    def __init__(self, v_1, v_2, v_3, visible:bool):
        self.v_1=v_1
        self.v_2=v_2
        self.v_3=v_3
        self._intensity = 1
        self._visible=visible
        self.n_hat=np.array

    def set_intensity(self, intensity) -> None:
        self._intensity = intensity

    def get_intensity(self) -> float:
        return self._intensity

    def compute_normal(self) -> None:
        v_1 = vertices[self.v_1-1]
        v_2 = vertices[self.v_2-1]
        v_3 = vertices[self.v_3-1]
        v1 = np.array([v_1[0], v_1[1], v_1[2]])
        v2 = np.array([v_2[0], v_2[1], v_2[2]])
        v3 = np.array([v_3[0], v_3[1], v_3[2]])
        n = np.cross( np.subtract(v1,v2), np.subtract(v1,v3))
        self.n_hat = n / np.linalg.norm(n)

    def is_visible(self) -> bool:
        return self._visible

    def set_visible(self, visible:bool) -> None:
        self._visible=visible

def get_t1_matrix(coordinates) -> np.array:
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,1,0],
                    [-coordinates[0], -coordinates[1], -coordinates[2], 1]])

def get_t2_matrix(sin_a, cos_a) -> np.array:
    return np.array([[cos_a, -sin_a, 0, 0],
                    [sin_a, cos_a, 0, 0],
                    [0, 0, 1, 0],
                    [0, 0, 0, 1]])

def get_t3_matrix(sin_b, cos_b) -> np.array:
    return np.array([[cos_b, 0, sin_b, 0],
                    [0, 1, 0, 0],
                    [-sin_b, 0, cos_b, 0],
                    [0, 0, 0, 1]])

def get_t4_matrix() -> np.array:
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,-1,0],
                    [0,0,0,1]])
    
def transformation() -> None:
    global transform_matrix, vertices
    
    for i in range(len(vertices)):
        vertices[i] = vertices[i] @ transform_matrix
  
def draw_vertex(coordinates):
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v3f', (coordinates[0],coordinates[1], coordinates[2] ) ), ('c3B', (1,1,0)) )

      
def load_data(filename):
    global vertices, polygons, O, G

    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('#'):
                continue
            coords = line.strip().split(' ')
            coords = np.array([float(coords[1]), float(coords[2]), float(coords[3])])
            if line.upper().startswith('O'):
                O = coords
            elif line.upper().startswith('G'):
                G = coords
            elif line.startswith('v'):
                vertices.append(coords)
            elif line.startswith('f'):
                polygon_indexes = line.strip().split(' ')
                polygons.append((int(polygon_indexes[1]), int(polygon_indexes[2]), int(polygon_indexes[3])))
     
        
def main():
    global vertices, center, eye_coordinates, light_source, transform_matrix
    filename = sys.argv[1]
    
    load_data(filename)
    
    xValues = []
    yValues = []
    zValues = []
    for v in vertices:
        xValues.append(v[0])
        yValues.append(v[1])
        zValues.append(v[2])
        
    x_min, x_max = min(xValues), max(xValues)
    y_min, y_max = min(yValues), max(yValues)
    z_min, z_max = min(zValues), max(zValues)
    
    
    MAX = max(x_max-x_min, y_max-y_min, z_max-z_min)
    
    glTranslatef(-x_center, -y_center, -z_center)
    
    for i, v in enumerate(vertices):
        vertices[i] = (v[0] * 2/MAX, v[1] * 2/MAX, v[2] * 2/MAX)

    t_v = input('x, y, z za izvor svjetla: ')
    t_v = t_v.strip().split(' ')
    light_source = (float(t_v[0]), float(t_v[1]), float(t_v[2]))
    draw_vertex(light_source)
    
    t_1 = get_t1_matrix(O)
    z = np.subtract(G,O)

    g_1 = np.subtract(G,O)
    g_1 = np.append(g_1,1)
    sin_a = g_1[1] / np.sqrt(g_1[0]**2+g_1[1]**2)
    cos_a = g_1[0] / np.sqrt(g_1[0]**2+g_1[1]**2)

    t_2 = get_t2_matrix(sin_a,cos_a)

    g_2 = g_1 @ t_2
    sin_b = g_2[0] / np.sqrt(g_2[0]**2+g_2[2]**2)
    cos_b = g_2[2] / np.sqrt(g_2[0]**2+g_2[2]**2)

    t_3 = get_t3_matrix(sin_b,cos_b)

    t_4 = np.array([[0,-1,0,0],
                    [1,0,0,0],
                    [0,0,1,0],
                    [0,0,0,1]])
                    
    t_5 = np.array([[-1,0,0,0],
                    [0,1,0,0],
                    [0,0,1,0],
                    [0,0,0,1]])

    t_2_5 = t_2 @ t_3 @ t_4 @t_5
    transform_matrix = t_1 @ t_2_5

    print(f'Transform matrix:\n{transform_matrix}')
    
    glTranslatef(0, 0, eye_coordinates[2]/2)
    
    pyglet.app.run()
        
if __name__=='__main__':
    main()
