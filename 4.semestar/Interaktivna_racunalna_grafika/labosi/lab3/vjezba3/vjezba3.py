import sys
import pyglet
from pyglet.gl import *
import numpy as np

window = pyglet.window.Window(1280, 720)
window.projection = pyglet.window.Projection3D()
batch = pyglet.graphics.Batch()

vertices, polygons = [], []
transform_matrix, pp_matrix = np.array, np.array
O, G = np.zeros(3), np.zeros(3)

@window.event
def on_draw():
    glClear(GL_COLOR_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_TRIANGLES)
    for polygon in polygons:
        glColor3f(1,1,0)
        glColor3f(1,1,1)
        glVertex3f(vertices[polygon[0]-1][0], vertices[polygon[0]-1][1], vertices[polygon[0]-1][2])
        glVertex3f(vertices[polygon[1]-1][0], vertices[polygon[1]-1][1], vertices[polygon[1]-1][2])
        glColor3f(0,1,1)
        glVertex3f(vertices[polygon[2]-1][0], vertices[polygon[2]-1][1], vertices[polygon[2]-1][2])
    glEnd()
    
@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)

def change_coordinate(coordinate, d) -> None:
    global vertices
    for i in range(len(vertices)):
        vertices[i][coordinate] += d
        
@window.event
def on_key_press(symbol, modifier):
    # coordinate change 
    if symbol == pyglet.window.key.A:
        change_coordinate(0,-1)
    if symbol == pyglet.window.key.D:
        change_coordinate(0,1)
    if symbol == pyglet.window.key.S:
        change_coordinate(1,-1)
    if symbol == pyglet.window.key.W:
        change_coordinate(1,1)
    if symbol == pyglet.window.key.Q:
        change_coordinate(2,-1)
    if symbol == pyglet.window.key.E:
        change_coordinate(2,1)
    if symbol == pyglet.window.key.T:
        transformation()
    if symbol == pyglet.window.key.P:
        perspective_projection()
    
def transformation() -> None:
    global transform_matrix, vertices
    
    for i in range(len(vertices)):
        vertices[i] = vertices[i] @ transform_matrix
       
def perspective_projection() -> None:
    '''Perspective projection on vertices.'''
    global pp_matrix, vertices

    for i in range(len(vertices)):
        vertices[i] = vertices[i] @ pp_matrix
 
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


def change_coordinate(pos, d) -> None:
    global vertices
    for i in range(len(vertices)):
        vertices[i][pos] += d

def get_perspective_projection_matrix(H) -> np.array:
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,0,1/H],
                    [0,0,0,0]])
       
def perspective_projection() -> None:
    global pp_matrix, vertices

    for i in range(len(vertices)):
        vertices[i] = vertices[i] @ pp_matrix
               
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
    global vertices, polygons, transform_matrix, pp_matrix, O, G

    f1 = sys.argv[1]
    load_data(f1)
    
    x_values = [x[0] for x in vertices]
    y_values = [x[1] for x in vertices]
    z_values = [x[2] for x in vertices]
    #find smallest and largest coordinates
    x_min, x_max = min(x_values), max(x_values)
    y_min, y_max = min(y_values), max(y_values)
    z_min, z_max = min(z_values), max(z_values)
    #find center of the body
    x_center = (x_min+x_max) / 2
    y_center = (y_min+y_max) / 2
    z_center = (z_min+z_max) / 2

    M = max(x_max-x_min, y_max-y_min, z_max-z_min)
    #translate each vertex for (-x_center, -y_center, -z_center)
    for v in vertices:
        v[0] -= x_center
        v[1] -= y_center
        v[2] -= z_center
    #scale vertices for 2/M
    for i, v in enumerate(vertices):
        vertices[i] = np.array([v[0] * 2/M, v[1] * 2/M, v[2] * 2/M, 1])

    
    # Calculate transformation matrix
    
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

    #perspective projection calculation
    H = np.linalg.norm(z)
    pp_matrix = get_perspective_projection_matrix(H)
    print(f'Perspective projection matrix:\n{pp_matrix}')
    
    glTranslatef(0, 0, -3)
    pyglet.app.run()


if __name__=='__main__':
    main()