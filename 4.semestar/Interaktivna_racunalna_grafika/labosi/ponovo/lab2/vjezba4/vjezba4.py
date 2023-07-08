import sys
from pyglet.gl import *
import pyglet
import numpy as np

window = pyglet.window.Window()
window.projection = pyglet.window.Projection3D()

vertices = []
polygons = []
planeCoefs = []
testPoint = []


@window.event
def on_draw():
    global polygons, vertices

    glClear(GL_COLOR_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)

    glBegin(GL_TRIANGLES)

    for p in polygons:
        glVertex3d(vertices[p[0]][0], vertices[p[0]][1], 0)
        glVertex3d(vertices[p[1]][0], vertices[p[1]][1], 0)
        glVertex3d(vertices[p[2]][0], vertices[p[2]][1], 0)

    glEnd()


def checkTestPoint():
    global testPoint, planeCoefs, vertices

    for coef in planeCoefs:
        if np.dot([testPoint[0], testPoint[1], testPoint[2], 1], coef) > 0:
            print("Testna tocka je izvan tijela")
            return

    print("Testna tocka je unutar tijela")


def loadData(filename):
    global vertices, polygons

    with open(filename, mode="r") as f:
        global xmin, xmax, ymin, ymax, zmin, zmax, vertices, polygons, testPoint

        for line in f.readlines():
            if line.startswith("v"):
                v = line.strip().split(" ")
                vertices.append([float(v[1]), float(v[2]), float(v[3])])
            if line.startswith("f"):
                f = line.strip().split(" ")
                polygons.append([int(f[1]) - 1, int(f[2]) - 1, int(f[3]) - 1])

        testPointX = float(input("X koordinata testne tocke:"))
        testPointY = float(input("Y koordinata testne tocke:"))
        testPointZ = float(input("Z koordinata testne tocke:"))
        testPoint = [testPointX, testPointY, testPointZ]

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
                           v[2] / maxdimension]


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


if __name__ == "__main__":
    loadData(sys.argv[1])
    calculatePlaneCoefs()
    checkTestPoint()

    # translate the z axis so we can see the object
    glTranslatef(0, 0, -2)
    pyglet.app.run()
