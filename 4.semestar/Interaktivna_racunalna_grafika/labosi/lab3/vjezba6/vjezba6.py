import sys

import numpy as np
from pyglet.gl import *
from pyglet.window import mouse

config=pyglet.gl.Config(double_buffer=False)
window = pyglet.window.Window(1280, 720, config=config)
#window.projection = pyglet.window.Projection3D()
batch = pyglet.graphics.Batch()

vertices = []
n = -1

@window.event
def on_draw():
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_LINE_STRIP)
    glColor3f(1,1,1)
    for vertex in vertices:
        glVertex3f(vertex[0], vertex[1], vertex[2])
    glEnd()
    
    
@window.event
def on_key_press(symbol, modiefier):
    if symbol == pyglet.window.key.SPACE:
        draw_curve()
        
@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)
    
def load_data(filename) -> None:
    global vertices, n
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('#'):
                continue
            coords = line.strip().split(',')
            vertices.append(np.array([float(coords[0]), float(coords[1]), float(coords[2])]))
            n += 1

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
       
def main():
    load_data(sys.argv[1])
    
    pyglet.app.run()


if __name__=='__main__':
    main()