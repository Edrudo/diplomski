import pyglet
from pyglet.gl import *
from pyglet import shapes


def bresenhamLine(point1, point2):
    def drawLineX(x1, y1, x2, y2):
        points = []

        dy = y2 - y1
        a = abs(dy)/(x2-x1)
        correction = 0
        if dy < 0:
            correction = -1
        else:
            correction = 1
        yc = y1
        yf = -0.5
        for x in range(x1, x2 + 1):
            points.append((x, yc))
            yf = yf+a
            if(yf >= 0.0):
                yf = yf-1.0
                yc = yc+correction

        return points

    def drawLineY(x1, y1, x2, y2):
        points = []

        dx = x2-x1
        a = abs(dx)/(y2-y1)
        correction = 0
        if dx < 0:
            correction = -1
        else:
            correction = 1
        xc = x1
        xf = -0.5

        for y in range(y1, y2 + 1):
            points.append((xc, y))
            xf = xf+a

            if(xf >= 0.0):
                xf = xf-1.0
                xc = xc+correction

        return points

    x1 = point1[0]
    y1 = point1[1]
    x2 = point2[0]
    y2 = point2[1]

    dx = x2-x1
    dy = y2-y1
    if x2 != 0:
        if(abs(dx) >= abs(dy)):
            if(dx >= 0):
                return drawLineX(x1, y1, x2, y2)
            else:
                return drawLineX(x2, y2, x1, y1)
        else:
            if(dy >= 0):
                return drawLineY(x1, y1, x2, y2)
            else:
                return drawLineY(x2, y2, x1, y1)


point1 = (0, 0)
point2 = (0, 0)
drawingInProgress = False
linePoints = []


def main():
    window = pyglet.window.Window()
    batch = pyglet.graphics.Batch()

    @window.event
    def on_mouse_press(x, y, button, modifiers):
        global drawingInProgress, point1, point2, linePoints

        if button:
            if not drawingInProgress:
                point1 = (x, y)
                drawingInProgress = True
            else:
                point2 = (x, y)
                linePoints = bresenhamLine(point1, (x, y))
                drawingInProgress = False

    @window.event
    def on_draw():
        global drawingInProgress, linePoints, point1, point2

        if(not drawingInProgress):
            line = shapes.Line(
                point1[0], point1[1] + 20, point2[0], point2[1] + 20, width=3, batch=batch)
            for point in linePoints:
                pyglet.graphics.draw(1, pyglet.gl.GL_POINTS,
                                     ('v2i', point),
                                     ('c3B', (255, 255, 255)))
            batch.draw()

    pyglet.app.run()


if __name__ == "__main__":
    main()
