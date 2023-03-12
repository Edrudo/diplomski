import pyglet
from pyglet.gl import *
from pyglet import shapes


def bresenham_line(x0, y0, x1, y1):
    # Return a list of points that make up the line between (x0, y0) and (x1, y1) using Bresenham's algorithm
    points = []
    dx = abs(x1 - x0)
    dy = abs(y1 - y0)
    x, y = x0, y0
    sx = -1 if x0 > x1 else 1
    sy = -1 if y0 > y1 else 1
    if dx > dy:
        err = dx / 2.0
        while x != x1:
            points.append((x, y))
            err -= dy
            if err < 0:
                y += sy
                err += dx
            x += sx
    else:
        err = dy / 2.0
        while y != y1:
            points.append((x, y))
            err -= dx
            if err < 0:
                x += sx
                err += dy
            y += sy
    points.append((x, y))
    return points

point1=(0,0)
point2=(0,0)
drawingInProgress=False
linePoints=[]
    
def main():
    window = pyglet.window.Window()
    batch = pyglet.graphics.Batch()
    
    @window.event
    def on_mouse_press(x, y, button, modifiers):
        global drawingInProgress, point1, point2, linePoints
        
        if button:
            if not drawingInProgress:
                point1=(x, y)
                drawingInProgress = True
            else:
                point2=(x, y)
                linePoints=bresenham_line(point1[0], point1[1], x, y)
                drawingInProgress=False
                
                    
    @window.event                        
    def on_draw():
        global drawingInProgress, linePoints, point1, point2
        
        if(not drawingInProgress):
            line=shapes.Line(point1[0], point1[1] + 20, point2[0], point2[1] + 20, width=3, batch=batch)
            for point in linePoints:
                pyglet.graphics.draw(1, pyglet.gl.GL_POINTS,
                          ('v2i', point),
                          ('c3B', (255, 255, 255)))
            batch.draw()
            
    pyglet.app.run()
    
if __name__ == "__main__":
    main()