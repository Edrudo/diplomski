import sys

import numpy as np
from pyglet.gl import *
from pyglet.window import mouse
import time

from transformation_and_projection import *

config=pyglet.gl.Config(double_buffer=False)
window = pyglet.window.Window(1280, 720, config=config)
#window.projection = pyglet.window.Projection3D()
batch = pyglet.graphics.Batch()

vertices, bezier_curve_coords, animation_object_vertices = [], [], []
obj_vertices, obj_polygons = [], []
center_of_body = np.zeros(3)
n = -1

@window.event
def on_draw():
    glClear(GL_COLOR_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_TRIANGLES)
    for polygon in obj_polygons:
        glColor3f(1,1,0)
        glColor3f(1,1,1)
        glVertex3f(obj_vertices[polygon[0]-1][0], obj_vertices[polygon[0]-1][1], obj_vertices[polygon[0]-1][2])
        glVertex3f(obj_vertices[polygon[1]-1][0], obj_vertices[polygon[1]-1][1], obj_vertices[polygon[1]-1][2])
        glColor3f(0,1,1)
        glVertex3f(obj_vertices[polygon[2]-1][0], obj_vertices[polygon[2]-1][1], obj_vertices[polygon[2]-1][2])
    glEnd()
    """ glMatrixMode(GL_MODELVIEW)
    glBegin(GL_LINE_STRIP)
    glColor3f(1,1,1)
    for vertex in vertices:
        glVertex3f(vertex[0], vertex[1], vertex[2])
    glEnd() """
        
    
    
@window.event
def on_key_press(symbol, modiefier):
    if symbol == pyglet.window.key.SPACE:
        draw_curve()
    if symbol == pyglet.window.key.A:
        animation()
        
@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)
    
def load_data(filename1, filename2) -> None:
    global vertices, n, obj_vertices, obj_polygons, center_of_body
    
    with open(filename1) as f:
        for line in f.readlines():
            if line.startswith('#'):
                continue
            coords = line.strip().split(',')
            vertices.append(np.array([float(coords[0]), float(coords[1]), float(coords[2])]))
            n += 1
            
    with open(filename2) as f:
        for line in f.readlines():
            if line.startswith('#'):
                continue
            if line.startswith('v'):
                coords = line.strip().split(' ')
                coords = np.array([float(coords[1]), float(coords[2]), float(coords[3])])
                obj_vertices.append(coords)
            elif line.startswith('f'):
                polygon_indexes = line.strip().split(' ')
                obj_polygons.append((int(polygon_indexes[1]), int(polygon_indexes[2]), int(polygon_indexes[3])))
    # get center of body
    x_values = [x[0] for x in obj_vertices]
    y_values = [x[1] for x in obj_vertices]
    z_values = [x[2] for x in obj_vertices]
    #find smallest and largest coordinates
    x_min, x_max = min(x_values), max(x_values)
    y_min, y_max = min(y_values), max(y_values)
    z_min, z_max = min(z_values), max(z_values)
    x_center = (x_min+x_max) / 2
    y_center = (y_min+y_max) / 2
    z_center = (z_min+z_max) / 2
    center_of_body = [x_center, y_center, z_center]
    
    M = max(x_max-x_min, y_max-y_min, z_max-z_min)
    #translate each vertex for (-x_center, -y_center, -z_center)
    for v in vertices:
        v[0] -= x_center
        v[1] -= y_center
        v[2] -= z_center
    #scale vertices for 2/M
    for i, v in enumerate(obj_vertices):
        obj_vertices[i] = np.array([v[0] * 2/M, v[1] * 2/M, v[2] * 2/M, 1])
    

def get_base_function(i, n, t) -> float:
     return ( np.math.factorial(n) / ( np.math.factorial(i) * np.math.factorial(n-i)) ) * t**i * (1-t)**(n-i)

def draw_vertex(x, y, z) -> None:
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v3f', (x,y,z)), ('c3B', (255,255,255)))
    
def draw_curve() -> None:
    global vertices, bezier_curve_coords
    for t in range(0, 101, 1):
        t = t / 100
        curve_coords = np.zeros(3)
        for i, vertex in enumerate(vertices):
            curve_coords += get_base_function(i,n,t) * vertex

        draw_vertex(curve_coords[0],curve_coords[1],curve_coords[2])
        
        # store coordinates
        bezier_curve_coords.append(curve_coords)
  
        
def animation() -> None:
    global bezier_curve_coords, obj_vertices
    
    
    for O in bezier_curve_coords:
        t_matrix = get_transformation_matrix(O, center_of_body)
        p_matrix = get_projection_matrix(O, center_of_body)
        
        for i in range(len(obj_vertices)):
            obj_vertices[i] = obj_vertices[i] @ t_matrix @ p_matrix
        time.sleep(0.2)
        
        
def main():
    load_data(sys.argv[1], sys.argv[2])
    
    pyglet.app.run()


if __name__=='__main__':
    main()