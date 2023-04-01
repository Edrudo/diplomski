import sys
import pyglet
from pyglet.gl import *
import numpy as np

window = pyglet.window.Window(1280, 720)
window.projection = pyglet.window.Projection3D()
batch = pyglet.graphics.Batch()

vertices, polygons = [], []
center, O = np.array, np.array

class Polygon():
    def __init__(self, v_1, v_2, v_3, visible):
        self.v_1=v_1
        self.v_2=v_2
        self.v_3=v_3
        self.visible=visible
    def set_visible(self, visible:bool):
        self.visible=visible

@window.event
def on_draw():
    check_polygons()
    glClear(GL_COLOR_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_TRIANGLE_FAN)
    for polygon in polygons:
        if polygon.visible:
            glColor3f(1,1,0)
            glVertex3f(vertices[polygon.v_1-1][0], vertices[polygon.v_1-1][1], vertices[polygon.v_1-1][2])
            glColor3f(1,1,1)
            glVertex3f(vertices[polygon.v_2-1][0], vertices[polygon.v_2-1][1], vertices[polygon.v_2-1][2])
            glColor3f(0,1,1)
            glVertex3f(vertices[polygon.v_3-1][0], vertices[polygon.v_3-1][1], vertices[polygon.v_3-1][2])
    glEnd()

@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)

@window.event
def on_key_press(symbol, modifier):
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

def rotate(dt):
    glRotatef(0.5, dt, dt, dt)

def check_polygons() -> None:
    '''Checks what polygons are visible to the user.'''
    global center, vertices, polygons, O
    n_p = np.subtract(O, center)
    n_p = np.array([n_p[0], n_p[1], n_p[2]])
    n_p_hat = n_p / np.linalg.norm(n_p)
    

    for polygon in polygons:
        v_1 = vertices[polygon.v_1-1]
        v_2 = vertices[polygon.v_2-1]
        v_3 = vertices[polygon.v_3-1]
        n = np.cross( np.subtract(v_1,v_2), np.subtract(v_1,v_3))
        n_hat = n / np.linalg.norm(n)
        dot_product = np.dot(n_hat, n_p_hat)
        angle = np.arccos(dot_product)
        #angle = np.arccos(np.dot(n,n_p) / (np.linalg.norm(n) * np.linalg.norm(n_p) ) )
        if np.degrees(angle) > 90:
            polygon.set_visible(False)

def change_coordinate(pos, dt):
    #O[pos] += dt
    '''Adjust given coordinate for shift 'd' . '''
    global vertices
    for i in range(len(vertices)):
        vertices[i][pos] += dt

def draw_vertex(coordinates):
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v2f', (coordinates[0],coordinates[1] ) ) )

def load_data(filename):
    '''Loads vertices and polygons from given .obj file.'''
    global vertices, polygons
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('v'):
                coords = line.strip().split(' ')
                vertices.append(np.array([float(coords[1]), float(coords[2]), float(coords[3]), 1]) )
            if line.startswith('f'):
                polygon_indexes = line.strip().split(' ')
                p = Polygon(int(polygon_indexes[1]), int(polygon_indexes[2]), int(polygon_indexes[3]), True)
                polygons.append(p)

def rotate(dt):
    glRotatef(0.5, dt, dt, dt)

def main():
    global vertices, center, O
    filename = sys.argv[1] + '.obj'

    load_data(filename)
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
    for v in vertices:
        v[0] -= x_center
        v[1] -= y_center
        v[2] -= z_center

    #scale vertices for 2/M
    for i, v in enumerate(vertices):
        vertices[i] = np.array([v[0] * 2/M, v[1] * 2/M, v[2] * 2/M])

    O = input('Unesite 3D koordinate ocista (odvojeno zarezima): ')
    O = O.split(',')
    O = np.array([float(O[0]), float(O[1]), float(O[2]), 1])

    glTranslatef(0, 0, O[2])
    pyglet.clock.schedule_interval(rotate, 1/60)
    pyglet.app.run()


if __name__=='__main__':
    main()
