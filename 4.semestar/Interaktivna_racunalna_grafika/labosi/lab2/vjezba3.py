import numpy as np
from pyglet.gl import *
from pyglet import shapes
from pyglet import window
from pyglet.window import mouse

config=pyglet.gl.Config(double_buffer=False)
window = pyglet.window.Window(config=config)
batch = pyglet.graphics.Batch()

RIGHT = "RIGTH"
LEFT = "LEFT"

numOfVerticesRequested = 0
vertices = []
numOfVertices = 0
edgesOrientationsAndCoef = [] # edge orientation a b c
testVertex = ()

def drawVertex(x, y):
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v2i', (x,y)))
    
def drawPolygon():
    for i in range(numOfVerticesRequested):
        character = RIGHT     #character indicator
        line = shapes.Line(
            vertices[i][0], 
            vertices[i][1], vertices[(i+1) % len(vertices)][0], 
            vertices[(i+1) % len(vertices)][1], 
            batch=batch
        )
        
        if vertices[i][1] < vertices[(i+1) % len(vertices)][1]:
            character = LEFT
            
        a = vertices[i][1] - vertices[(i+1) % len(vertices)][1]
        b = -vertices[i][0] + vertices[(i+1) % len(vertices)][0]
        c = vertices[i][0] * vertices[(i+1) % len(vertices)][1] - vertices[(i+1) % len(vertices)][0] * vertices[i][1]
        edgesOrientationsAndCoef.append((line, character, a, b, c))
    batch.draw()

def findIntersection(line1, line2):
    m1 = (line1.y2-line1.y) / (line1.x2-line1.x)
    b1 = -m1*line1.x + line1.y

    m2 = (line2.y2-line2.y) / (line2.x2-line2.x)
    b2 = -m2*line2.x + line2.y

    return (b1-b2) / (m2-m1)

def fillPolygon():
    x_min = min([x[0] for x in vertices])
    x_max = max([x[0] for x in vertices])
    y_min = min(x[1] for x in vertices)
    y_max = max(x[1] for x in vertices)
    
    for y in range(y_min, y_max+1):
        L = x_min
        D = x_max
        for edgeOrientationAndCoef in edgesOrientationsAndCoef:
            edge = edgeOrientationAndCoef[0]
            char = edgeOrientationAndCoef[1]
            a = edgeOrientationAndCoef[2]
            b = edgeOrientationAndCoef[3]
            c = edgeOrientationAndCoef[4]
            
            if a == 0:
                continue

            x1 = ((-b * y) - c) / a
            if char == LEFT and x1 > L:
                L = x1
            elif char == RIGHT and x1 < D:
                D = x1
        if L < D:
            line = shapes.Line(L, y, D, y, batch=batch)
            batch.draw()

def checkVertexPosition(vertex):
    vertexIsInPolygon = True
    vt = np.array([vertex[0], vertex[1], 1])
    for i in range(len(vertices)):
        v1 = np.array([vertices[i][0], vertices[i][1], 1])
        v2 = np.array([vertices[(i+1) % len(vertices)][0], vertices[(i+1) % len(vertices)][1], 1])
        direction = np.cross(v1, v2)
        if np.dot(vt, direction) > 0:
            vertexIsInPolygon = False
            break

    if vertexIsInPolygon:
        print('Vrh je u poligonu.')
    else:
        print('Vrh je izvan poligona.')
        
@window.event
def on_mouse_press(x, y, button, modifiers):
    global vertices, numOfVertices, testVertex
    
    if button & mouse.LEFT:
        if numOfVertices < numOfVerticesRequested - 1:
            vertices.append((x, y))
        elif numOfVertices == numOfVerticesRequested - 1:
            vertices.append((x, y))
            drawPolygon()
        else:
            print("Test vrh oznacen")
            testVertex = (x, y)
            
    print(x,y)
    drawVertex(x,y)
    numOfVertices += 1
            
@window.event
def on_key_press(symbol, modiefier):
    if symbol == 99: # C key
        checkVertexPosition(testVertex)
    elif symbol == 32: # SPACE key
        fillPolygon()
        
        
        
if __name__=='__main__':
    numOfVerticesRequested = int(input("Broj vrhova: "))
    pyglet.app.run()