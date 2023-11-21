import sys
from pyglet.gl import *
import pyglet
import numpy as np
import pyglet.window.key as key
from time import sleep

width, height = 1280, 720
window = pyglet.window.Window(
    width, height, config=pyglet.gl.Config(double_buffer=True))
window.projection = pyglet.window.Projection3D()

vertices = []
polygons = []
O, G, V = [], [], []

verticesTransformed1, verticesTransformed2 = [], []

polygonsVisible1, polygonsVisible2 = [], []
planeCoefs1, planeCoefs2 = [], []

T1, T2, P = [], [], []

rotationMatrix1 = np.array([[1, 0, 0, 0],
                           [0, 1, 0, 0],
                           [0, 0, 1, 0],
                           [0, 0, 0, 1]])
rotationMatrix2 = np.array([[1, 0, 0, 0],
                           [0, 1, 0, 0],
                           [0, 0, 1, 0],
                           [0, 0, 0, 1]])

xaxis, yaxis, zaxis = [], [], []


animation = False


def loadData(filenameObj):
    global vertices, polygons, O, G, V

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
    global T1, T2, O

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

    T1 = t1 @ rotation_matrix @ mirrorZMatrix @ rotationMatrix1
    T2 = t1 @ rotation_matrix @ mirrorZMatrix @ np.array([[1, 0, 0, 0],
                                                          [0, 1, 0, 0],
                                                          [0, 0, 1, 0],
                                                          [4, 0, 0, 1]]) @ rotationMatrix2


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
    global T1, T2, P, verticesTransformed1, verticesTransformed2, vertices

    verticesTransformed1 = []
    verticesTransformed2 = []

    calculateTransformationMatrixWithViewUpVector()
    calculatePerspectiveMatrix()

    for v in vertices:
        verticesTransformed1.append(np.dot(np.dot(v, T1), P))
        verticesTransformed2.append(np.dot(np.dot(v, T2), P))


def calculatePlaneCoefs():
    global planeCoefs1, planeCoefs2, vertices, polygons

    for p in polygons:
        v1 = verticesTransformed1[p[0]]
        v2 = verticesTransformed1[p[1]]
        v3 = verticesTransformed1[p[2]]

        A = (v2[1] - v1[1]) * (v3[2] - v1[2]) - \
            (v2[2] - v1[2]) * (v3[1] - v1[1])
        B = -(v2[0] - v1[0]) * (v3[2] - v1[2]) + \
            (v2[2] - v1[2]) * (v3[0] - v1[0])
        C = (v2[0] - v1[0]) * (v3[1] - v1[1]) - \
            (v2[1] - v1[1]) * (v3[0] - v1[0])
        D = -v1[0] * A - v1[1] * B - v1[2] * C

        planeCoefs1.append([A, B, C, D])

    for p in polygons:
        v1 = verticesTransformed2[p[0]]
        v2 = verticesTransformed2[p[1]]
        v3 = verticesTransformed2[p[2]]

        A = (v2[1] - v1[1]) * (v3[2] - v1[2]) - \
            (v2[2] - v1[2]) * (v3[1] - v1[1])
        B = -(v2[0] - v1[0]) * (v3[2] - v1[2]) + \
            (v2[2] - v1[2]) * (v3[0] - v1[0])
        C = (v2[0] - v1[0]) * (v3[1] - v1[1]) - \
            (v2[1] - v1[1]) * (v3[0] - v1[0])
        D = -v1[0] * A - v1[1] * B - v1[2] * C

        planeCoefs2.append([A, B, C, D])


def rotateObjects():
    global rotationMatrix1, rotationMatrix2, T2, P

    sina1 = np.sin(np.deg2rad(1.5))
    cosa1 = np.cos(np.deg2rad(1.5))

    t1 = np.array([[cosa1, -sina1, 0, 0],
                   [sina1, cosa1, 0, 0],
                   [0, 0, 1, 0],
                   [0, 0, 0, 1]])

    rotationMatrix1 = rotationMatrix1 @ t1

    """ for i, v in enumerate(verticesTransformed1):
        verticesTransformed1[i] = np.dot(v, t1) """

    sina2 = np.sin(np.deg2rad(0.75))
    cosa2 = np.cos(np.deg2rad(0.75))

    t2 = np.array([[cosa2, -sina2, 0, 0],
                   [sina2, cosa2, 0, 0],
                   [0, 0, 1, 0],
                   [0, 0, 0, 1]])

    rotationMatrix2 = rotationMatrix2 @ t2

    """ for i, v in enumerate(verticesTransformed2):
        verticesTransformed2[i] = np.dot(np.dot(v, T2), P) """

    transformVertices()


def findVisiblePolygons():
    global vertices, polygons, polygonsVisible1, polygonsVisible2, planeCoefs, O
    calculatePlaneCoefs()

    polygonsVisible1 = []
    polygonsVisible2 = []

    for i, p in enumerate(polygons):
        if np.dot([planeCoefs1[i][0], planeCoefs1[i][1], planeCoefs1[i][2]], O) >= 0:
            polygonsVisible1.append(p)

    for i, p in enumerate(polygons):
        if np.dot([planeCoefs2[i][0], planeCoefs2[i][1], planeCoefs2[i][2]], O) >= 0:
            polygonsVisible2.append(p)


@window.event
def on_key_press(symbol, modifiers):
    global animation

    if symbol == key.SPACE:
        animation = True


@window.event
def on_draw():
    global verticesTransformed1, verticesTransformed2, polygonsVisible1, polygonsVisible2, animation, window

    if animation:
        while True:
            glClear(GL_COLOR_BUFFER_BIT)
            glMatrixMode(GL_MODELVIEW)

            rotateObjects()

            glBegin(GL_TRIANGLES)
            for p in polygonsVisible1:
                v1 = verticesTransformed1[p[0]]
                v2 = verticesTransformed1[p[1]]
                v3 = verticesTransformed1[p[2]]
                glColor3f(0, 0, 1)
                glVertex3d(v1[0], v1[1], v1[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v3[0], v3[1], v3[2])
            glEnd()

            glBegin(GL_LINES)
            for p in polygonsVisible1:
                v1 = verticesTransformed1[p[0]]
                v2 = verticesTransformed1[p[1]]
                v3 = verticesTransformed1[p[2]]
                glColor3f(0, 0, 0)
                glVertex3d(v1[0], v1[1], v1[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v3[0], v3[1], v3[2])
                glVertex3d(v3[0], v3[1], v3[2])
                glVertex3d(v1[0], v1[1], v1[2])
            glEnd()

            glBegin(GL_TRIANGLES)
            for p in polygonsVisible2:
                v1 = verticesTransformed2[p[0]]
                v2 = verticesTransformed2[p[1]]
                v3 = verticesTransformed2[p[2]]
                glColor3f(1, 0, 0)
                glVertex3d(v1[0], v1[1], v1[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v3[0], v3[1], v3[2])
            glEnd()

            glBegin(GL_LINES)
            for p in polygonsVisible1:
                v1 = verticesTransformed2[p[0]]
                v2 = verticesTransformed2[p[1]]
                v3 = verticesTransformed2[p[2]]
                glColor3f(0, 0, 0)
                glVertex3d(v1[0], v1[1], v1[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v3[0], v3[1], v3[2])
                glVertex3d(v3[0], v3[1], v3[2])
                glVertex3d(v1[0], v1[1], v1[2])
            glEnd()

            window.flip()
            sleep(0.025)

    else:
        glClear(GL_COLOR_BUFFER_BIT)
        glMatrixMode(GL_MODELVIEW)

        glBegin(GL_TRIANGLES)
        for p in polygonsVisible1:
            v1 = verticesTransformed1[p[0]]
            v2 = verticesTransformed1[p[1]]
            v3 = verticesTransformed1[p[2]]
            glColor3f(0, 0, 1)
            glVertex3d(v1[0], v1[1], v1[2])
            glVertex3d(v2[0], v2[1], v2[2])
            glVertex3d(v3[0], v3[1], v3[2])
        glEnd()

        glBegin(GL_LINES)
        for p in polygonsVisible1:
            v1 = verticesTransformed1[p[0]]
            v2 = verticesTransformed1[p[1]]
            v3 = verticesTransformed1[p[2]]
            glColor3f(0, 0, 0)
            glVertex3d(v1[0], v1[1], v1[2])
            glVertex3d(v2[0], v2[1], v2[2])
            glVertex3d(v2[0], v2[1], v2[2])
            glVertex3d(v3[0], v3[1], v3[2])
            glVertex3d(v3[0], v3[1], v3[2])
            glVertex3d(v1[0], v1[1], v1[2])
        glEnd()

        glBegin(GL_TRIANGLES)
        for p in polygonsVisible2:
            v1 = verticesTransformed2[p[0]]
            v2 = verticesTransformed2[p[1]]
            v3 = verticesTransformed2[p[2]]
            glColor3f(1, 0, 0)
            glVertex3d(v1[0], v1[1], v1[2])
            glVertex3d(v2[0], v2[1], v2[2])
            glVertex3d(v3[0], v3[1], v3[2])
        glEnd()

        glBegin(GL_LINES)
        for p in polygonsVisible1:
            v1 = verticesTransformed2[p[0]]
            v2 = verticesTransformed2[p[1]]
            v3 = verticesTransformed2[p[2]]
            glColor3f(0, 0, 0)
            glVertex3d(v1[0], v1[1], v1[2])
            glVertex3d(v2[0], v2[1], v2[2])
            glVertex3d(v2[0], v2[1], v2[2])
            glVertex3d(v3[0], v3[1], v3[2])
            glVertex3d(v3[0], v3[1], v3[2])
            glVertex3d(v1[0], v1[1], v1[2])
        glEnd()


if __name__ == "__main__":
    loadData(sys.argv[1])

    transformVertices()
    findVisiblePolygons()

    glTranslatef(0, 0, -7)

    pyglet.app.run()
