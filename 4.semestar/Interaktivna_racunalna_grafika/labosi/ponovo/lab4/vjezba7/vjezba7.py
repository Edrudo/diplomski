import sys
from pyglet.gl import *
import pyglet
import numpy as np
from transformationMatrixes import *
import pyglet.window.key as key
from time import sleep

width, height = 1280, 720
window = pyglet.window.Window(
    width, height, config=pyglet.gl.Config(double_buffer=True))
window.projection = pyglet.window.Projection3D()


vertices = []
verticesTransformed = []
polygonsVisible = []
polygons = []
O, G, V = [], [], []
planeCoefs = []
objectCenter = []
openGlTransformation = False

lightSource = [10, 10, 10]
polygonConstantIntensities = []
visiblePolygonConstantIntensities = []

verticesGouraudovIntensities = []

constantLighting = False

T, P = [], []

xaxis, yaxis, zaxis = [], [], []


def loadData(filenameObj):
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
    global T, P, verticesTransformed, vertices, openGlTransformation

    verticesTransformed = []

    calculateTransformationMatrixWithViewUpVector()
    # calculateTransformationMatrixWithTransformations()
    calculatePerspectiveMatrix()

    for v in vertices:
        verticesTransformed.append(np.dot(np.dot(v, T), P))


@window.event
def on_key_press(symbol, modifiers):
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
    global vertices, polygons, polygonsVisible, planeCoefs, O, visiblePolygonConstantIntensities

    polygonsVisible = []
    visiblePolygonConstantIntensities = []

    for i, p in enumerate(polygons):
        if np.dot([planeCoefs[i][0], planeCoefs[i][1], planeCoefs[i][2]], O) + planeCoefs[i][3] > 0:
            polygonsVisible.append(p)
            visiblePolygonConstantIntensities.append(
                polygonConstantIntensities[i])


def calculateIntensity():
    global polygons, polygonConstantIntensities, planeCoefs, verticesGouraudovIntensities

    polygonConstantIntensities = []
    verticesGouraudovIntensities = []

    ka = 0.5
    ia = 100
    kd = 0.78125
    ii = 255

    ambientComponent = ka * ia

    for i, p in enumerate(polygons):
        v1 = vertices[p[0]]
        v2 = vertices[p[1]]
        v3 = vertices[p[2]]
        pCenter = [(v1[0] + v2[0] + v3[0]) / 3,
                   (v1[1] + v2[1] + v3[1]) / 3,
                   (v1[2] + v2[2] + v3[2]) / 3]

        L1 = np.subtract(lightSource, pCenter)

        L = np.divide(L1, np.linalg.norm(L1))
        N = np.divide([planeCoefs[i][0],
                       planeCoefs[i][1], planeCoefs[i][2]], np.linalg.norm([planeCoefs[i][0],
                                                                            planeCoefs[i][1], planeCoefs[i][2]]))

        diffuseComponent = kd * ii * max(0, np.dot(L, N))

        total = ambientComponent + diffuseComponent

        if total > 255:
            total = 255

        polygonConstantIntensities.append(total)

    # Gourad
    for i, v in enumerate(vertices):
        adjacentNormals = []
        for j, p in enumerate(polygons):
            if i + 1 in p:
                pNormal = [planeCoefs[j][0],
                           planeCoefs[j][1],
                           planeCoefs[j][2]]
                adjacentNormals.append(
                    np.divide(pNormal, np.linalg.norm(pNormal)))

        normal = [0, 0, 0]
        for an in adjacentNormals:
            normal = np.add(normal, an)

        if (len(adjacentNormals) > 0):
            N = np.divide(normal, len(adjacentNormals))

            L1 = np.subtract(lightSource, [v[0], v[1], v[2]])

            L = np.divide(L1, np.linalg.norm(L1))

            diffuseComponent = kd * ii * max(0, np.dot(L, N))

            total = ambientComponent + diffuseComponent

            if total > 255:
                total = 255

            verticesGouraudovIntensities.append(total)
        else:
            verticesGouraudovIntensities.append(0)


@window.event
def on_draw():
    global verticesTransformed, polygonsVisible, O, width, height, V

    if openGlTransformation:

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        gluPerspective(45.0, float(width/height), 0.5, 8.0)
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
        glTranslatef(0, 0, -3)
        gluLookAt(O[0], O[1], O[2], G[0], G[1], G[2], V[0], V[1], V[2])

    else:
        if constantLighting:
            glClear(GL_COLOR_BUFFER_BIT)
            glMatrixMode(GL_MODELVIEW)

            glBegin(GL_TRIANGLES)
            for i, p in enumerate(polygonsVisible):
                v1 = verticesTransformed[p[0]]
                v2 = verticesTransformed[p[1]]
                v3 = verticesTransformed[p[2]]
                glColor3f(visiblePolygonConstantIntensities[i] / 255,
                          visiblePolygonConstantIntensities[i] / 255,
                          visiblePolygonConstantIntensities[i] / 255)
                glVertex3d(v1[0], v1[1], v1[2])
                glVertex3d(v2[0], v2[1], v2[2])
                glVertex3d(v3[0], v3[1], v3[2])
            glEnd()
        else:
            glClear(GL_COLOR_BUFFER_BIT)
            glMatrixMode(GL_MODELVIEW)

            glBegin(GL_TRIANGLES)
            for i, p in enumerate(polygonsVisible):
                v1 = verticesTransformed[p[0]]
                v2 = verticesTransformed[p[1]]
                v3 = verticesTransformed[p[2]]
                glColor3f(verticesGouraudovIntensities[i] / 255,
                          verticesGouraudovIntensities[i] / 255,
                          verticesGouraudovIntensities[i] / 255)
                glVertex3d(v1[0], v1[1], v1[2])
                glColor3f(verticesGouraudovIntensities[i] / 255,
                          verticesGouraudovIntensities[i] / 255,
                          verticesGouraudovIntensities[i] / 255)
                glVertex3d(v2[0], v2[1], v2[2])
                glColor3f(verticesGouraudovIntensities[i] / 255,
                          verticesGouraudovIntensities[i] / 255,
                          verticesGouraudovIntensities[i] / 255)
                glVertex3d(v3[0], v3[1], v3[2])
            glEnd()


if __name__ == "__main__":
    loadData(sys.argv[1])
    transformVertices()
    calculatePlaneCoefs()
    calculateIntensity()
    findVisiblePolygons()

    glTranslatef(0, 0, -3)

    pyglet.app.run()
