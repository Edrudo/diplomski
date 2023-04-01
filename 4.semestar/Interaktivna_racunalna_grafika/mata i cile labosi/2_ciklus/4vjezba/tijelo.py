import sys
import pyglet
from pyglet.gl import *
import numpy as np

window = pyglet.window.Window(1280, 720)
window.projection = pyglet.window.Projection3D()
# pyglet.gl.glClearColor(1,1,1,1)
batch = pyglet.graphics.Batch()

vertices, polygons = [], []


@window.event
def on_draw():
    glClear(GL_COLOR_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_TRIANGLES)
    for polygon in polygons:
        glColor3f(1,1,0)
        glVertex3f(vertices[polygon[0]-1][0], vertices[polygon[0]-1][1], 0)
        glColor3f(1,1,1)
        glVertex3f(vertices[polygon[1]-1][0], vertices[polygon[1]-1][1], 0)
        glColor3f(0,1,1)
        glVertex3f(vertices[polygon[2]-1][0], vertices[polygon[2]-1][1], 0)
    glEnd()

@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)

def rotate(dt):
    glRotatef(0.5, dt, dt, dt)


def main():
    global vertices, polygons
    filename = sys.argv[1]

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

    M = max(x_max-x_min, y_max-y_min, z_max-z_min)

    #translate each vertex for (-x_center, -y_center, -z_center)
    for v in vertices:
        v[0] -= x_center
        v[1] -= y_center
        v[2] -= z_center

    #scale vertices for 2/M
    for i, v in enumerate(vertices):
        vertices[i] = (v[0] * 2/M, v[1] * 2/M, v[2] * 2/M)

    t_v = input('x, y, z coordinates of test vertex: ')
    t_v = t_v.strip().split(' ')
    test_vertex = (float(t_v[0]), float(t_v[1]), float(t_v[2]))
    draw_vertex(test_vertex)

    #check test vertex position in relation to given polygon
    check_vertex_position(test_vertex)

    glTranslatef(0, 0, -3)
    pyglet.app.run()


def draw_vertex(coordinates):
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v2f', (coordinates[0],coordinates[1] ) ) )


def check_vertex_position(vertex):
    '''
    Checks if given vertex is inside the object.
    '''
    inside = True

    for polygon in polygons:
        v_1 = vertices[polygon[0]-1]
        v_2 = vertices[polygon[1]-1]
        v_3 = vertices[polygon[2]-1]

        A = (v_2[1]-v_1[1]) * (v_3[2]-v_1[2]) - (v_2[2]-v_1[2]) * (v_3[1]-v_1[1])
        B = -(v_2[0]-v_1[0]) * (v_3[2]-v_1[2]) + (v_2[2]-v_1[2]) * (v_3[0]-v_1[0])
        C = (v_2[0]-v_1[0]) * (v_3[1]-v_1[1]) - (v_2[1]-v_1[1]) * (v_3[0]-v_1[0])
        D = -v_1[0] * A - v_1[1] * B - v_1[2] * C

        if np.dot(np.array([vertex[0], vertex[1], vertex[2], 1]), np.array([A, B, C, D])) > 0:
            inside = False
            break

    print(f'Test vertex is inside the object: {inside}')
        

def load_data(filename):
    '''
    Loads vertices and polygons from given .obj file.
    '''
    global vertices, polygons
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('v'):
                coords = line.strip().split(' ')
                vertices.append([float(coords[1]), float(coords[2]), float(coords[3])] )
            if line.startswith('f'):
                polygon_indexes = line.strip().split(' ')
                polygons.append((int(polygon_indexes[1]), int(polygon_indexes[2]), int(polygon_indexes[3])))


if __name__=='__main__':
    main()
