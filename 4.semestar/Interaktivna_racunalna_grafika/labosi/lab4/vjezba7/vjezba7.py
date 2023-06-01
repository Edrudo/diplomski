import sys
from pyglet.gl import *
import numpy as np
from transformation_and_projection import *

window = pyglet.window.Window(width=1280, height=720, resizable=True)
window.set_minimum_size(300,200)
window.projection = pyglet.window.Projection3D()
pyglet.gl.glClearColor(0.6,0.6,0.5,1)
batch = pyglet.graphics.Batch()

vertices, polygons = [], []
transform_matrix = np.array
with_v_up = False
center, eye_coordinates, light_source = np.array, np.array, np.array
O, G, v_up = np.zeros(3), np.zeros(3), np.zeros(3)
col_min, col_max = 0, 1

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

@window.event
def on_draw():
    check_visible_polygons()
    compute_constant_lighting(k_a=1,i_a=10,k_d=0.78125,i_p=256,k_c=0.1,k_s=0.17578125,n=5)
    
    glClear(GL_COLOR_BUFFER_BIT)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_TRIANGLES)
    r,g,b = 0.6,0.75,0.7
    for polygon in polygons:
        if polygon.is_visible():
            glColor3f(r * polygon.get_intensity(),g * polygon.get_intensity(),b * polygon.get_intensity())
            glVertex3f(vertices[polygon.v_1-1][0], vertices[polygon.v_1-1][1], vertices[polygon.v_1-1][2])
            glVertex3f(vertices[polygon.v_2-1][0], vertices[polygon.v_2-1][1], vertices[polygon.v_2-1][2])
            glVertex3f(vertices[polygon.v_3-1][0], vertices[polygon.v_3-1][1], vertices[polygon.v_3-1][2])
    glEnd()

@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)

@window.event
def on_key_press(symbol, modifier):
    global lighting_type
    
    #object coordinates adjustment
    if symbol == pyglet.window.key.T:
        transformation()
    if symbol == pyglet.window.key.A:
        change_object_coordinates(0,-1)
        #rotate_x(-0.02)
    if symbol == pyglet.window.key.D:
        change_object_coordinates(0,1)
        #rotate_x(0.02)
    if symbol == pyglet.window.key.S:
        change_object_coordinates(1,-1)
    if symbol == pyglet.window.key.W:
        change_object_coordinates(1,1)
    if symbol == pyglet.window.key.Q:
        change_object_coordinates(2,-1)
    if symbol == pyglet.window.key.E:
        change_object_coordinates(2,1)
    #eye coordinates adjustment
    if symbol == pyglet.window.key.UP:
        change_light_source_coordinate(1,3)
    if symbol == pyglet.window.key.DOWN:
        change_light_source_coordinate(1,-3)
    if symbol == pyglet.window.key.RIGHT:
        change_light_source_coordinate(0,3)
    if symbol == pyglet.window.key.LEFT:
        change_light_source_coordinate(0,-3)


def check_visible_polygons() -> None:
    global center, vertices, polygons, eye_coordinates
    n_p = np.subtract(eye_coordinates, center)
    n_p = np.array([n_p[0], n_p[1], n_p[2]])
    n_p_hat = n_p / np.linalg.norm(n_p)
    

    for polygon in polygons:
        polygon.compute_normal()
        dot_product = np.dot(polygon.n_hat, n_p_hat)
        angle = np.arccos(dot_product)
        if np.degrees(angle) < 90:
            polygon.set_visible(True)

def compute_constant_lighting(k_a,i_a,k_d,i_p,k_c,k_s,n) -> None:
    global light_source, eye_coordinates, col_min, col_max
    for polygon in polygons:
        if polygon.is_visible():
            v_1 = vertices[polygon.v_1-1]
            v_2 = vertices[polygon.v_2-1]
            v_3 = vertices[polygon.v_3-1]
            x_c = ( v_1[0]+v_2[0]+v_3[0] ) / 3
            y_c = ( v_1[1]+v_2[1]+v_3[1] ) / 3
            z_c = ( v_1[2]+v_2[2]+v_3[2] ) / 3
            p_center = np.array([x_c, y_c, z_c, 1])
            L = np.subtract(light_source,p_center)
            L_hat = L / np.linalg.norm(L)
            V = np.subtract(eye_coordinates,p_center)
            V_hat = V / np.linalg.norm(V)
            
            #ambient component
            ambient_total = k_a * i_a
            
            #diffuse component
            dot_product = np.dot(polygon.n_hat, np.array([L_hat[0], L_hat[1], L_hat[2]]))
            diffuse_total = k_d*i_p*max(0,dot_product)
            
            """ #mirror component
            initial_ray = np.subtract(center, light_source)
            initial_ray_hat = initial_ray / np.linalg.norm(initial_ray)
            initial_ray_hat = np.array([initial_ray_hat[0], initial_ray_hat[1], initial_ray_hat[2]])
            reflected_ray =  initial_ray_hat - (2 * np.dot(np.dot(initial_ray_hat, polygon.n_hat), polygon.n_hat))
            mirror_total = i_p * k_s * np.dot(reflected_ray, np.array([L_hat[0], L_hat[1], L_hat[2]])) * np.dot(reflected_ray, np.array([L_hat[0], L_hat[1], L_hat[2]]))
 """
            total = ambient_total + diffuse_total
            if total > 255:
                total = 255
                
            if total < col_min:
                col_min = total
            elif total > col_max:
                col_max = total
            polygon.set_intensity(total)

    for polygon in polygons:
        intensity = polygon.get_intensity()
        scaled_intensity = (intensity - col_min) / (col_max - col_min)
        polygon.set_intensity(scaled_intensity)


def change_light_source_coordinate(pos, dt):
    global light_source
    light_source[pos] += dt

def change_eye_coordinates(pos, dt):
    global eye_coordinates
    eye_coordinates[pos] += dt

def change_object_coordinates(pos, dt):
    global vertices
    for i in range(len(vertices)):
        vertices[i][pos] += dt


def load_data(filename):
    global vertices, polygons, O, G, v_up
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('v'):
                coords = line.strip().split(' ')
                vertices.append(np.array([float(coords[1]), float(coords[2]), float(coords[3]), 1]) )
            if line.startswith('f'):
                polygon_indexes = line.strip().split(' ')
                p = Polygon(int(polygon_indexes[1]), int(polygon_indexes[2]), int(polygon_indexes[3]), False)
                polygons.append(p)
            if line.startswith('#'):
                continue
            coords = line.strip().split(' ')
            coords = np.array([float(coords[1]), float(coords[2]), float(coords[3])])
            if line.upper().startswith('O'):
                O = coords
            if line.upper().startswith('G'):
                G = coords
            elif line.upper().startswith('UP'):
                v_up = coords


def transformation() -> None:
    global transform_matrix, vertices
    
    for i in range(len(vertices)):
        vertices[i] = vertices[i] @ transform_matrix

def scale_and_translate():
    global vertices, center
    
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
    center = np.array([x_center, y_center, z_center, 1])

    M = max(x_max-x_min, y_max-y_min, z_max-z_min)
    #translate each vertex for (-x_center, -y_center, -z_center)
    glTranslatef(-x_center, -y_center, -z_center)

    #scale vertices for 2/M
    for i, v in enumerate(vertices):
        vertices[i] = np.array([v[0] * 2/M, v[1] * 2/M, v[2] * 2/M, 1])

def main():
    global vertices, center, eye_coordinates, light_source, transform_matrix, with_v_up
    filename = sys.argv[1]

    load_data(filename)
    
    eye_coordinates = np.append(O,1)
    
    scale_and_translate()

    light_source = np.array([0, 0, 0,1])

    transform_matrix = get_transformation_matrix(O, G, v_up)

    print(f'Transform matrix:\n{transform_matrix}')

    glTranslatef(0, 0, eye_coordinates[2]/2)
    pyglet.app.run()


if __name__=='__main__':
    main()
