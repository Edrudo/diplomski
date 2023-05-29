from pyglet.gl import *

radius = 2
k = 10
c = complex(0, 0)    
#povezani skup 0.32, 0.043
#segment linije -2, 0  
#nepovezani skup  0.11, 0.65

x_min, x_max = 0, 500
y_min, y_max = 0, 500
u_min, u_max = -1, 1
v_min, v_max = -1.2, 1.2

window = pyglet.window.Window(x_max, y_max)
batch = pyglet.graphics.Batch()

@window.event
def on_draw():
    global c, radius, k
    
    eps = radius**2
    
    window.clear()
    glPointSize(1)
    glBegin(GL_POINTS)
    for x in range(0, x_max+1):
        for y in range(0, y_max+1):
            z0_re = (x-x_min) / float( (x_max-x_min) ) * (u_max-u_min) + u_min
            z0_im = (y-y_min) / float( (y_max-y_min) ) * (v_max-v_min) + v_min
            z0 = complex(z0_re, z0_im)
            
            n = -1
            z = complex(z0.real, z0.imag)
            modul = z.real**2 + z.imag**2
            if modul > eps:
                n = 0
            
            else:
                for i in range(k):
                    next_re = z.real**2 - z.imag**2 + c.real
                    next_im = 2 * z.real*z.imag + c.imag
                    z = complex(next_re, next_im)
                    modul = z.real**2 + z.imag**2
                    if modul > eps:
                        n = i
                        break
                    

            if n == -1:
                glColor3f(0,0,0)
            else:
                glColor3f(float(n)/k, 1-float(n)/ k/2.0, 0.8-float(n)/k/3.0)
            glVertex2i(x,y)
    glEnd()


def main():
    global radius, k, c

    radius=int(input("Upisite radijus:\n"))
    k=int(input("Upisite k (limit):\n"))
    
    c_input=input("Upisite c(real imag):\n")
    c_numbers = c_input.split(" ")
    c = complex(float(c_numbers[0]), float(c_numbers[1]))
    
    
    pyglet.app.run()

if __name__=='__main__':
    main()
