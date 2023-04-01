import sys
import pyglet
from pyglet.gl import *
import numpy as np

window = pyglet.window.Window()
window.projection = pyglet.window.Projection3D()
batch = pyglet.graphics.Batch()

polygons, vertices = [], []

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
    
def load_data(filename):
    global vertices, polygons
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('v'):
                coords = line.strip().split(' ')
                vertices.append([float(coords[1]), float(coords[2]), float(coords[3])] )
            if line.startswith('f'):
                polygon_indexes = line.strip().split(' ')
                polygons.append((int(polygon_indexes[1]), int(polygon_indexes[2]), int(polygon_indexes[3])))

def check_vertex_position(vertex):
    inside = True

    for polygon in polygons:
        v_1 = vertices[polygon[0]-1]
        v_2 = vertices[polygon[1]-1]
        v_3 = vertices[polygon[2]-1]

        A = (v_2[1]-v_1[1]) * (v_3[2]-v_1[2]) - (v_2[2]-v_1[2]) * (v_3[1]-v_1[1])
        B = -(v_2[0]-v_1[0]) * (v_3[2]-v_1[2]) + (v_2[2]-v_1[2]) * (v_3[0]-v_1[0])
        C = (v_2[0]-v_1[0]) * (v_3[1]-v_1[1]) - (v_2[1]-v_1[1]) * (v_3[0]-v_1[0])
        D = -v_1[0] * A - v_1[1] * B - v_1[2] * C

        if np.dot(np.array([vertex[0], vertex[1], vertex[2], 1]), np.array([A,B, C, D])) >= 0:
            inside = False
            break

    print(f'Testni vrh je unutar objekta: {inside}')
    
def main():
    file = sys.argv[1]

    load_data(file)
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
    
    xCenter = (x_min+x_max) / 2
    yCenter = (y_min+y_max) / 2
    zCenter = (z_min+z_max) / 2
    
    M = max(x_max-x_min, y_max-y_min, z_max-z_min)
    
    for v in vertices:
        v[0] -= xCenter
        v[1] -= yCenter
        v[2] -= zCenter
        
    for i, v in enumerate(vertices):
        vertices[i] = (v[0] * 2/M, v[1] * 2/M, v[2] * 2/M)
        
    t_v = input('x, y, z za testni vrh: ')
    t_v = t_v.strip().split(' ')
    test_vertex = (float(t_v[0]), float(t_v[1]), float(t_v[2]))
    
    check_vertex_position(test_vertex)
    
    glTranslatef(0, 0, -3)
    pyglet.app.run()
    
if __name__=='__main__':
    main()