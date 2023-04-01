from pyglet.gl import *

epsilon = 100
limit = 16
c = complex(0.32, 0.043)    #povezani skup
# c = complex(-2, 0)        #segment linije
#c = complex(0.11, 0.65)     #nepovezani skup
# c = complex(0.179180, 0.588843)     #iz knjige
x_min, x_max = 0, 500
y_min, y_max = 0, 500
u_min, u_max = -1, 1
v_min, v_max = -1.2, 1.2

window = pyglet.window.Window(x_max, y_max, caption=f'Julia - limit={limit}')
batch = pyglet.graphics.Batch()

def divergence_test(z0:complex, c:complex, limit:int, epsilon_square:int) -> int:
    '''Checks whether a given complex number 'c' diverges.'''
    z = complex(z0.real, z0.imag)
    modul2 = z.real**2 + z.imag**2
    if modul2 > epsilon_square:
        return 0

    for i in range(limit):
        next_re = z.real**2 - z.imag**2 + c.real
        next_im = 2 * z.real*z.imag + c.imag
        z = complex(next_re, next_im)
        modul2 = z.real**2 + z.imag**2
        if modul2 > epsilon_square:
            return i
    return -1


@window.event
def on_draw():
    global c, epsilon
    window.clear()
    glPointSize(1)
    glBegin(GL_POINTS)
    for y in range(0, y_max+1):
        for x in range(0, x_max+1):
            z0_re = (x-x_min) / float( (x_max-x_min) ) * (u_max-u_min) + u_min
            z0_im = (y-y_min) / float( (y_max-y_min) ) * (v_max-v_min) + v_min
            z0 = complex(z0_re, z0_im)
            n = divergence_test(z0,c,limit,epsilon**2)
            if n == -1:
                glColor3f(0,0,0)
            else:
                glColor3f(float(n)/limit, 1-float(n)/ limit/2.0, 0.8-float(n)/limit/3.0)
            glVertex2i(x,y)
    glEnd()


def main():
    #global radius, limit, x_max, x_min, u_min, u_max, v_min, v_max
    #q = input("Use default data? (y/n): ")
    #if q != 'y':
    #    radius = int(input("Radijus: "))
    #    limit = int(input("Max broj iteracija: "))
#
    #    area = input("u_min, u_max, v_min, v_max: ")
    #    area = list(map(float, area.split(',')))
    #    u_min, u_max, v_min, v_max = area[0], area[1], area[2], area[3]

    pyglet.app.run()

if __name__=='__main__':
    main()
