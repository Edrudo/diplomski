import sys

import numpy as np
from pyglet.gl import *
from pyglet.window import mouse

config=pyglet.gl.Config(double_buffer=False)
window = pyglet.window.Window(1280, 720, config=config)
#window.projection = pyglet.window.Projection3D()
batch = pyglet.graphics.Batch()

vertices, all_curve_coords = [], []
n = int

@window.event
def on_draw():
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_LINE_STRIP)
    glColor3f(1,1,1)
    for vertex in vertices:
        glVertex3f(vertex[0], vertex[1], vertex[2])
    glEnd()
    #line = shapes.Line(100, 100, 200, 200, batch=batch)

@window.event
def on_mouse_press(x, y, button, modifier):
    if button & mouse.LEFT:
        draw_vertex(x,y,1)

@window.event
def on_key_press(symbol, modiefier):
    if symbol == pyglet.window.key.SPACE:
        draw_curve()

@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)

def load_vertices(filename) -> int:
    global vertices
    ''' Returns  (num_of_vertices - 1) loaded from given file.'''
    n = -1
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('#'):
                continue
            coords = line.strip().split(',')
            vertices.append(np.array([float(coords[0]), float(coords[1]), float(coords[2])]))
            n += 1
    return n

def load_vertices_manual() -> int:
    '''Returns  (num_of_vertices - 1) loaded from user.'''
    n = int(input('Unesite broj vrhova: '))-1
    for i in range(n+1):
        coords = input('Upisite 3D koordinate (odvojeno zarezima): ')
        coords = list(map(float, coords.split(',')))
        vertices.append(np.array(coords))
    return n

def get_base_function(i, n, t) -> float:
    '''Returns the value of the base function with given parameters:
    [n! / (i!(n-i)!) ] * t**i * (1-t)**(n-i)'''
    return ( np.math.factorial(n) / ( np.math.factorial(i) * np.math.factorial(n-i)) ) * t**i * (1-t)**(n-i)

def draw_vertex(x, y, z) -> None:
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v3f', (x,y,z)), ('c3B', (255,255,255)))

def draw_curve() -> None:
    global vertices, all_curve_coords
    b_n = [get_base_function for x in range(len(vertices))]
    for t in range(0, 101, 1):
        t = t / 100
        curve_coords = np.zeros(3)
        for i, vertex in enumerate(vertices):
            curve_coords += get_base_function(i,n,t) * vertex

        #store curve coordinates
        all_curve_coords.append(curve_coords)
        draw_vertex(curve_coords[0],curve_coords[1],curve_coords[2])

def animation() -> None:
    '''Move through curve coordinates and draw the inside of the object.'''
    for t in all_curve_coords:
        O = t

def update(dt):
    pass

def main():
    global n
    if len(sys.argv) < 2:
        n = load_vertices_manual()
    else:
        n = load_vertices(sys.argv[1])
    for vertex in vertices:
        print(vertex)
    print(f'Broj točaka kontrolnog poligona: {n+1}.')
    #pyglet.clock.schedule_interval(update, 1/60) # schedule 60 times per second
    
    #glTranslatef(0,0,-3)
    pyglet.app.run()


if __name__=='__main__':
    main()
