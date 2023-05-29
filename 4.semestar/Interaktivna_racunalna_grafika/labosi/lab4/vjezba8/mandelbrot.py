from pyglet.gl import *

radius = 100
k = 16
x_min, x_max = 0, 600
y_min, y_max = 0, 480
u_min, u_max = -1.5, 0.5
v_min, v_max = -1, 1

window = pyglet.window.Window(x_max, y_max)
batch = pyglet.graphics.Batch()

def divergence_test(c:complex) -> int:
    global k
    z = 0 + 0j
    for i in range(k):
        next_re = z.real**2 - z.imag**2 + c.real
        next_im = 2 * z.real*z.imag + c.imag
        z = complex(next_re, next_im)
        modul2 = z.real**2 + z.imag**2
        if modul2 > radius**2:
            return i
    return -1


@window.event
def on_draw():
    window.clear()
    glPointSize(1)
    glBegin(GL_POINTS)
    for x in range(0, x_max+1):
        for y in range(0, y_max+1):
            c_real = (x-x_min) / float( (x_max-x_min) ) * (u_max-u_min) + u_min
            c_imag = (y-y_min) / float( (y_max-y_min) ) * (v_max-v_min) + v_min
            c = complex(c_real, c_imag)
            n = divergence_test(c)
            if n == -1:
                glColor3f(0,0,0)
            else:
                glColor3f(float(n)/k, 1-float(n)/ k/2.0, 0.8-float(n)/k/3.0)
            glVertex2i(x,y)
    glEnd()

def main():
    pyglet.app.run()

if __name__=='__main__':
    main()
