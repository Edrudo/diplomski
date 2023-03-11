from ast import Global
from asyncio import events
from lib2to3.refactor import get_all_fix_names
from re import L
from turtle import width
from sqlalchemy import true
import pyglet
from pyglet.gl import *
from pyglet import shapes


qtrCol = [1, 0, 0, 1]
window = pyglet.window.Window()
batch = pyglet.graphics.Batch()
@window.event
def on_mouse_press(x, y, button, modifiers):
    global qtrCol
    if button:
        if qtrCol[0] == 1 or qtrCol[2] != 0:
            qtrCol[0]=x
            qtrCol[1]=y
            qtrCol[2]=0
            qtrCol[3]=0
        else:
            qtrCol[2]=x
            qtrCol[3]=y
            
            
@window.event                        
def on_draw():
    def drawLineX(x1, y1, x2, y2):
        dy = y2 - y1;
        a = abs(dy)/(x2-x1);
        korekcija = 0
        if dy<0 : korekcija = -1 
        else: korekcija = 1;
        yc=y1; yf = -0.5;
        for x in range(x1, x2 + 1):
            pyglet.graphics.draw(1, GL_POINTS, ('v2i',(x,yc)))
            yf = yf+a;
            if(yf>=0.0) :
                yf=yf-1.0;
                yc=yc+korekcija;
            
        
    def drawLineY(x1, y1, x2, y2):
        dx = x2-x1;
        a = abs(dx)/(y2-y1);
        korekcija = 0
        if dx<0 :  korekcija = -1 
        else: korekcija = 1;
        xc=x1; xf = -0.5;

        for y in range(y1, y2 + 1):
            pyglet.graphics.draw(1, GL_POINTS, ('v2i',(xc,y)))
            xf = xf+a;

            if(xf>=0.0):
                xf=xf-1.0;
                xc=xc+korekcija
                
                
    x1=qtrCol[0]
    y1=qtrCol[1]
    x2=qtrCol[2]
    y2=qtrCol[3]
    
    dx = x2-x1;
    dy = y2-y1;
    if x2!=0:
        if(abs(dx) >= abs(dy)):
            if(dx>=0):
                drawLineX(x1, y1, x2, y2);
            else:
                drawLineX(x2, y2, x1, y1);
        else:
            if(dy >= 0):
                drawLineY(x1, y1, x2, y2);
            else:
                drawLineY(x2, y2, x1, y1);
                    
        line = shapes.Line(qtrCol[0], qtrCol[1] + 20, qtrCol[2], qtrCol[3] + 20, width=3, batch=batch)
        line.opacity = 250
        if(qtrCol[2] != 0) : batch.draw()
pyglet.app.run()