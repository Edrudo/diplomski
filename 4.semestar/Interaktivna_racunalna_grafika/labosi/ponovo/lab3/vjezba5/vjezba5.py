import sys
from pyglet.gl import *
import pyglet
import numpy as np
from transformationMatrixes import *
import pyglet.window.key as key


window = pyglet.window.Window()
window.projection = pyglet.window.Projection3D()


vertices = []
verticesTransformed = []
polygons = []
O, G, V = [], [], []

T, P = [], []

xaxis, yaxis, zaxis = [], [], []


def loadData(filename):
    global vertices, polygons, O, G, V

    with open(filename) as f:
        for line in f.readlines():
            if line.startswith("v"):
                v = line.split(" ")
                vertices.append([float(v[1]), float(v[2]), float(v[3])])

            if line.startswith("f"):
                f = line.strip().split(" ")
                polygons.append([int(f[1]) - 1, int(f[2]) - 1, int(f[3]) - 1])

            if line.startswith("O"):
                o = line.split(" ")
                O = [float(o[1]), float(o[2]), float(o[3])]
            if line.startswith("G"):
                g = line.split(" ")
                G = [float(g[1]), float(g[2]), float(g[3])]
            if line.startswith("V"):
                v = line.split(" ")
                V = [float(v[1]), float(v[2]), float(v[3])]

    xmin = min([x[0] for x in vertices])
    xmax = max([x[0] for x in vertices])
    ymin = min([x[1] for x in vertices])
    ymax = max([x[1] for x in vertices])
    zmin = min([x[2] for x in vertices])
    zmax = max([x[2] for x in vertices])

    xcenter = (xmin + xmax) / 2
    ycenter = (ymin + ymax) / 2
    zcenter = (zmin + zmax) / 2

    for v in vertices:
        v[0] -= xcenter
        v[1] -= ycenter
        v[2] -= zcenter

    maxdimension = max(xmax - xmin, ymax - ymin, zmax - zmin) / 2

    for i, v in enumerate(vertices):
        vertices[i] = [v[0] / maxdimension,
                       v[1] / maxdimension,
                       v[2] / maxdimension,
                       1]


def calculateAxisVUp():
    global O, G, V, xaxis, yaxis, zaxis

    z = np.subtract(G, O)
    zaxis = z / np.linalg.norm(z)

    Vnormal = V / np.linalg.norm(V)

    xaxis = np.cross(zaxis, Vnormal)

    yaxis = np.cross(xaxis, zaxis)


def calculateTransformationMatrixWithViewUpVector():
    global T, O

    calculateAxisVUp()

    t1 = [[1, 0, 0, 0],
          [0, 1, 0, 0],
          [0, 0, 1, 0],
          [-O[0], -O[1], -O[2], 1]]

    rotation_matrix = np.transpose(np.vstack((np.append(xaxis, 0), np.append(
        yaxis, 0), np.append(zaxis, 0), np.array([0, 0, 0, 1]))))

    mirrorZMatrix = [[1, 0, 0, 0],
                     [0, 1, 0, 0],
                     [0, 0, -1, 0],
                     [0, 0, 0, 1]]

    T = t1 @ rotation_matrix @ mirrorZMatrix


def calculateTransformationMatrixWithTransformations():
    global O, G, T

    t1 = getT1(O)
    t2 = getT2(O, G)
    t3 = getT3(O, G)
    t4 = getT4()
    t5 = getT5()

    t25 = np.dot(np.dot(np.dot(t2, t3), t4), t5)
    T = np.dot(t1, t25)


def calculatePerspectiveMatrix():
    global P

    z = np.subtract(G, O)
    zaxis = z / np.linalg.norm(z)

    H = np.linalg.norm(zaxis)

    P = np.array([[1, 0, 0, 0],
                  [0, 1, 0, 0],
                  [0, 0, 0, 1/H],
                  [0, 0, 0, 0]])


def transformVertices():
    global T, P, verticesTransformed, vertices

    calculateTransformationMatrixWithViewUpVector()
    # calculateTransformationMatrixWithTransformations()
    calculatePerspectiveMatrix()

    verticesTransformed = []

    for v in vertices:
        verticesTransformed.append(np.dot(np.dot(v, T), P))


@window.event
def on_key_press(symbol, modifiers):
    if symbol == key.D:
        O[0] += 1
    if symbol == key.S:
        O[1] -= 1
    if symbol == key.A:
        O[0] -= 1
    if symbol == key.W:
        O[1] += 1

    if symbol == key.RIGHT:
        G[0] += 1
    if symbol == key.DOWN:
        G[1] -= 1
    if symbol == key.LEFT:
        G[0] -= 1
    if symbol == key.UP:
        G[1] += 1

    transformVertices()


@window.event
def on_draw():
    global verticesTransformed, polygons

    glClear(GL_COLOR_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)

    glBegin(GL_TRIANGLES)
    for p in polygons:
        v1 = verticesTransformed[p[0]]
        v2 = verticesTransformed[p[1]]
        v3 = verticesTransformed[p[2]]
        glColor3f(1, 1, 0)
        glVertex3d(v1[0], v1[1], v1[2])
        glColor3f(1, 1, 1)
        glVertex3d(v2[0], v2[1], v2[2])
        glColor3f(0, 1, 1)
        glVertex3d(v3[0], v3[1], v3[2])
    glEnd()


if __name__ == "__main__":
    loadData(sys.argv[1])

    transformVertices()

    glTranslatef(0, 0, -3)

    pyglet.app.run()
