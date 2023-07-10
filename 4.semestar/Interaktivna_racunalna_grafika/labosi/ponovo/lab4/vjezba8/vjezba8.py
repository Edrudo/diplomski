import pyglet
from pyglet.gl import *


eps = 100
m = 16

xmin, xmax, ymin, ymax, umin, umax, vmin, vmax = 0, 500, 0, 500, -1, 1, -1.2, 1.2
window = pyglet.window.Window(xmax, ymax)

mandelbrotPoints = []
julijPoints = []

mandelBrot = False


def calculateFractals():
    global mandelbrotPoints, julijPoints

    for x in range(xmin, xmax + 1):
        for y in range(ymin, ymax + 1):
            u0 = ((x - xmin) / (xmax - xmin)) * (umax - umin) + umin
            v0 = ((y - ymin) / (ymax - ymin)) * (vmax - vmin) + vmin

            if mandelBrot:
                k = -1
                c = complex(u0, v0)
                zn = complex(0, 0)

                while True:
                    k = k + 1
                    zn1 = complex(zn.real**2 - zn.imag**2 + c.real,
                                  2 * zn.real*zn.imag + c.imag)

                    zn = zn1

                    modul = zn1.real**2 + zn1.imag**2

                    if modul > eps:
                        break

                    if k >= m - 1:
                        k = -1
                        break
                mandelbrotPoints.append([x, y, k])

            else:
                c = complex(0.32, 0.043)
                k = -1
                zn = complex(u0, v0)
                modul = zn.real**2 + zn.imag**2
                if modul > eps:
                    julijPoints.append([x, y, 0])

                else:

                    while True:
                        k = k + 1
                        zn1 = complex(zn.real**2 - zn.imag**2 + c.real,
                                      2 * zn.real*zn.imag + c.imag)

                        modul = zn1.real**2 + zn1.imag**2

                        zn = zn1
                        if modul > eps:
                            break

                        if k >= m - 1:
                            k = -1
                            break

                    julijPoints.append([x, y, k])


@window.event
def on_draw():
    global mandelBrot

    window.clear()

    if mandelBrot:
        glPointSize(1)
        glBegin(GL_POINTS)
        for point in mandelbrotPoints:
            if point[2] == -1:
                glColor3f(0, 0, 0)
            else:
                glColor3f(float(point[2]) / m, 1-float(point[2]) /
                          m / 2.0, 0.8-float(point[2]) / m / 3.0)
            glVertex2d(point[0], point[1])
        glEnd()

    else:
        glPointSize(1)
        glBegin(GL_POINTS)

        for point in julijPoints:
            if point[2] == -1:
                glColor3f(0, 0, 0)
            else:
                glColor3f(float(point[2]) / m, 1-float(point[2]) /
                          m / 2.0, 0.8-float(point[2]) / m / 3.0)
            glVertex2i(point[0], point[1])

        glEnd()


if __name__ == "__main__":
    calculateFractals()
    pyglet.app.run()
