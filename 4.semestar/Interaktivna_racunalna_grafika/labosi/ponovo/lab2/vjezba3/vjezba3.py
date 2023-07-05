import numpy as np
import pyglet
from pyglet.gl import *
from pyglet.window import mouse

window = pyglet.window.Window()
numOfVertices = 0
vertices = []
coefs = []
V = (0, 0)
xmin, xmax, ymin, ymax = 0, 0, 0, 0
colorPolygon = False
colorLines = []

def checkIfPointIsInPolygon(x, y):
    global coefs
    for coef in coefs:
        if (x * coef[0] + y * coef[1] + coef[2]) > 0:
            print(f"Tocka ({x}, {y}) je izvan poligona")
            return
    
    print(f"Tocka ({x}, {y}) je unutar poligona")
    
def calculateCoefs():
    global vertices
    
    for i, v in enumerate(vertices):   
        v2 = vertices[(i + 1) % len(vertices)]
        a = v[1] - v2[1]
        b = -v[0] + v2[0]
        c = v[0] * v2[1] - v2[0] * v[1]
        
        coefs.append((a, b, c))
        
def calculateColorLines():
    global vertices, coefs, xmin, xmax, colorPolygon, colorLines, ymin, ymax
    
    for y0 in range(ymin, ymax + 1):
        L, D = xmin, xmax
        for i in range(0, len(vertices)):
            if coefs[i][0] != 0:
                x1 = (-coefs[i][1] * y0 - coefs[i][2]) / coefs[i][0]
                if vertices[i][1] < vertices[(i + 1) % len(vertices)][1] and x1 > L:
                    L = x1
                if vertices[i][1] >= vertices[(i + 1) % len(vertices)][1] and x1 < D:
                    D = x1
        if L < D:
            colorLines.append([(L, y0), (D, y0)])
                
@window.event
def on_draw():
    if numOfVertices == 0:
        glClearColor(0, 0, 0, 0);
        glBegin(GL_LINES)
        for i, v in enumerate(vertices):
            glVertex2d(v[0], v[1])
            glVertex2d(vertices[(i + 1) % len(vertices)][0], vertices[(i + 1) % len(vertices)][1])
        glEnd()
        
        if colorPolygon:
            glBegin(GL_LINES)
            for line in colorLines:
                glVertex2d(line[0][0], line[0][1])
                glVertex2d(line[1][0], line[1][1])
            glEnd()
    
@window.event
def on_mouse_press(x, y, button, modifiers):
    global numOfVertices, vertices, xmin, xmax, ymin, ymax
    
    if button == mouse.LEFT and numOfVertices > 0:
        vertices.append((x, y))
        numOfVertices -= 1
        
        if numOfVertices == 0:
            xmin = min(v[0] for v in vertices)
            xmax = max(v[0] for v in vertices)
            ymin = min(v[1] for v in vertices)
            ymax = max(v[1] for v in vertices)
            calculateCoefs()
            calculateColorLines()
    elif button == mouse.LEFT:
        checkIfPointIsInPolygon(x, y)
       
@window.event
def on_key_press(symbol, modifiers):
    global colorPolygon
    if symbol == 32:
        colorPolygon = True
        




if __name__=='__main__':
    numOfVertices = int(input("Unesite broj vrhova: "))
    pyglet.app.run()
    