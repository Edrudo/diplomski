import sys
from pyglet.gl import *
import pyglet
import numpy as np
from transformationMatrixes import *
import pyglet.window.key as key
from time import sleep


window = pyglet.window.Window(
    1280, 720, config=pyglet.gl.Config(double_buffer=True))
window.projection = pyglet.window.Projection3D()


vertices = []
verticesTransformed = []
polygonsVisible = []
polygons = []
O, G, V = [], [], []
planeCoefs = []
controlPoints = []
bezierCurvePoints = []
objectCenter = []
animation = False

T, P = [], []

xaxis, yaxis, zaxis = [], [], []


def loadData(filenameObj, filenameControlPolygon):
    global vertices, polygons, O, G, V, objectCenter

    with open(filenameObj) as f:
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

    objectCenter = [xcenter, ycenter, zcenter]

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

    with open(filenameControlPolygon) as f:
        for line in f.readlines():
            coords = line.split(",")
            controlPoints.append(
                [float(coords[0]), float(coords[1]), float(coords[2])])

    caluculateBezierCurvePoints()


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
    global P, O, G

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
    global animation

    if symbol == key.A:
        O[0] -= 1
    if symbol == key.D:
        O[0] += 1
    if symbol == key.S:
        O[1] -= 1
    if symbol == key.W:
        O[1] += 1
    if symbol == key.Q:
        O[2] += 1
    if symbol == key.E:
        O[2] -= 1

    if symbol == key.RIGHT:
        G[0] += 1
    if symbol == key.DOWN:
        G[1] -= 1
    if symbol == key.LEFT:
        G[0] -= 1
    if symbol == key.UP:
        G[1] += 1

    transformVertices()
    findVisiblePolygons()

    if symbol == key.SPACE:
        animation = True


def calculatePlaneCoefs():
    global planeCoefs, vertices, polygons

    for p in polygons:
        v1 = vertices[p[0]]
        v2 = vertices[p[1]]
        v3 = vertices[p[2]]

        A = (v2[1] - v1[1]) * (v3[2] - v1[2]) - \
            (v2[2] - v1[2]) * (v3[1] - v1[1])
        B = -(v2[0] - v1[0]) * (v3[2] - v1[2]) + \
            (v2[2] - v1[2]) * (v3[0] - v1[0])
        C = (v2[0] - v1[0]) * (v3[1] - v1[1]) - \
            (v2[1] - v1[1]) * (v3[0] - v1[0])
        D = -v1[0] * A - v1[1] * B - v1[2] * C

        planeCoefs.append([A, B, C, D])


def findVisiblePolygons():
    global vertices, polygons, polygonsVisible, planeCoefs, O
    calculatePlaneCoefs()

    polygonsVisible = []

    for i, p in enumerate(polygons):
        v1 = vertices[p[0]]
        v2 = vertices[p[1]]
        v3 = vertices[p[2]]
        centerPoint = [float((v1[0] + v2[0] + v3[0]) / 3),
                       float((v1[1] + v2[1] + v3[1]) / 3),
                       float((v1[2] + v2[2] + v3[2]) / 3)]

        OcenterPointVector = np.subtract(centerPoint, O)

        if np.dot([planeCoefs[i][0], planeCoefs[i][1], planeCoefs[i][2]], OcenterPointVector) >= 0:
            # if np.dot([planeCoefs[i][0], planeCoefs[i][1], planeCoefs[i][2]], O) + planeCoefs[i][3] > 0:

            polygonsVisible.append(p)


def caluculateBezierCurvePoints():
    global bezierCurvePoints

    n = len(controlPoints)

    for t1 in range(0, 101):
        t = float(t1 / 100)

        curveCoords = [0, 0, 0]
        for i, controlPoint in enumerate(controlPoints):
            curveCoords += np.multiply(((np.math.factorial(n) / (np.math.factorial(i) *
                                                                 np.math.factorial(n-i))) * t**i * (1-t)**(n-i)), controlPoint)

        bezierCurvePoints.append(curveCoords)


@window.event
def on_draw():
    global verticesTransformed, polygonsVisible, animation, window

    if animation:
        global O, G, objectCenter

        G = objectCenter

        for bPoint in bezierCurvePoints:
            O = bPoint
            transformVertices()
            findVisiblePolygons()

            glClear(GL_COLOR_BUFFER_BIT)
            glMatrixMode(GL_MODELVIEW)

            glBegin(GL_LINE_STRIP)
            for p in polygonsVisible:
                v1 = verticesTransformed[p[0]]
                v2 = verticesTransformed[p[1]]
                v3 = verticesTransformed[p[2]]
                glVertex3d(v1[0], v1[1], v1[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v3[0], v3[1], v3[2])
            glEnd()
            window.flip()

        animation = False

    else:
        glClear(GL_COLOR_BUFFER_BIT)
        glMatrixMode(GL_MODELVIEW)

        glBegin(GL_LINE_STRIP)
        for p in polygonsVisible:
            v1 = verticesTransformed[p[0]]
            v2 = verticesTransformed[p[1]]
            v3 = verticesTransformed[p[2]]
            glVertex3d(v1[0], v1[1], v1[2])
            glVertex3d(v2[0], v2[1], v2[2])
            glVertex3d(v3[0], v3[1], v3[2])
        glEnd()

        glMatrixMode(GL_MODELVIEW)
        glColor3f(1, 1, 1)
        glBegin(GL_POINTS)
        for p in bezierCurvePoints:
            glVertex3d(p[0], p[1], p[2])
        glEnd()


if __name__ == "__main__":
    loadData(sys.argv[1], sys.argv[2])
    transformVertices()
    findVisiblePolygons()

    glTranslatef(0, 0, -3)

    pyglet.app.run()
