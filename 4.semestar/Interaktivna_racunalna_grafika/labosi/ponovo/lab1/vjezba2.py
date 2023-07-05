import pyglet
from pyglet.gl import *
from pyglet import shapes

startPoint, endPoint = (0, 0), (0, 0)
points = []
secondPoint = False
x0, y0, D = 0, 0, 0

window = pyglet.window.Window()


@window.event
def on_mouse_press(x, y, button, modifiers):
    global startPoint, endPoint, points, secondPoint, x0, y0, D

    if button == pyglet.window.mouse.LEFT:
        if secondPoint:
            endPoint = (x, y)
            secondPoint = False
            x0 = endPoint[0] - startPoint[0]
            y0 = endPoint[1] - startPoint[1]
            D = y0/x0 - 0.5
            points = calculatePoints()

        else:
            startPoint = (x, y)
            secondPoint = True


@window.event
def on_draw():
    global points, startPoint, endPoint

    if not secondPoint:
        glBegin(GL_LINES)
        glVertex2f(startPoint[0], startPoint[1] + 20)
        glVertex2f(endPoint[0], endPoint[1] + 20)
        glEnd()

        glBegin(GL_POINTS)
        for point in points:
            glVertex2f(point[0], point[1])
        glEnd()


def calculatePoints():
    global startPoint, endPoint

    def drawLineX(xs, ys, xe, ye):
        points = []

        dy = ye - ys
        a = abs(dy)/(xe-xs)
        korekcija = 0
        if dy < 0:
            korekcija = -1
        else:
            korekcija = 1

        yc = ys
        yf = -0.5

        for x in range(xs, xe + 1):
            points.append((x, yc))
            yf = yf+a
            if(yf >= 0.0):
                yf = yf-1.0
                yc = yc+korekcija
        return points

    def drawLineY(xs, ys, xe, ye):
        points = []
        dx = xe-xs
        a = abs(dx)/(ye-ys)
        korekcija = 0
        if dx < 0:
            korekcija = -1
        else:
            korekcija = 1
        xc = xs
        xf = -0.5

        for y in range(ys, ye + 1):
            points.append((xc, y))
            xf = xf+a

            if(xf >= 0.0):
                xf = xf-1.0
                xc = xc+korekcija

        return points

    xs, ys, xe, ye = startPoint[0], startPoint[1], endPoint[0], endPoint[1]

    dx = xe-xs
    dy = ye-ys
    if(abs(dx) >= abs(dy)):
        if(dx >= 0):
            return drawLineX(xs, ys, xe, ye)
        else:
            return drawLineX(xe, ye, xs, ys)
    else:
        if(dy >= 0):
            return drawLineY(xs, ys, xe, ye)
        else:
            return drawLineY(xe, ye, xs, ys)


if __name__ == "__main__":
    pyglet.app.run()
