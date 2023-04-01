import sys
from pyglet.gl import *
import numpy as np

window = pyglet.window.Window(width=1280, height=720, resizable=True, caption="Sjencanje")
window.set_minimum_size(300,200)
window.projection = pyglet.window.Projection3D()
pyglet.gl.glClearColor(0.6,0.6,0.5,1)
batch = pyglet.graphics.Batch()

vertices, polygons = [], []
transform_matrix = np.array
with_v_up = False
center, eye_coordinates, light_source = np.array, np.array, np.array
col_min, col_max = 0, 1

#lighting type for polygons
# False = constant lighting
# True = Gouraud
lighting_type = False

class Polygon():
    def __init__(self, v_1, v_2, v_3, visible:bool):
        self.v_1=v_1
        self.v_2=v_2
        self.v_3=v_3
        self._intensity = 1
        self._visible=visible
        self.n_hat=np.array

    def set_intensity(self, intensity) -> None:
        self._intensity = intensity

    def get_intensity(self) -> float:
        return self._intensity

    def compute_normal(self) -> None:
        v_1 = vertices[self.v_1-1]
        v_2 = vertices[self.v_2-1]
        v_3 = vertices[self.v_3-1]
        v1 = np.array([v_1[0], v_1[1], v_1[2]])
        v2 = np.array([v_2[0], v_2[1], v_2[2]])
        v3 = np.array([v_3[0], v_3[1], v_3[2]])
        n = np.cross( np.subtract(v1,v2), np.subtract(v1,v3))
        self.n_hat = n / np.linalg.norm(n)

    def is_visible(self) -> bool:
        return self._visible

    def set_visible(self, visible:bool) -> None:
        self._visible=visible

@window.event
def on_draw():
    compute_lighting()
    glClear(GL_COLOR_BUFFER_BIT)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glMatrixMode(GL_MODELVIEW)
    glBegin(GL_TRIANGLES)
    r,g,b = 0.6,0.75,0.3
    for polygon in polygons:
        if polygon.is_visible():
            glColor3f(r * polygon.get_intensity(),g * polygon.get_intensity(),b * polygon.get_intensity())
            glVertex3f(vertices[polygon.v_1-1][0], vertices[polygon.v_1-1][1], vertices[polygon.v_1-1][2])
            glVertex3f(vertices[polygon.v_2-1][0], vertices[polygon.v_2-1][1], vertices[polygon.v_2-1][2])
            glVertex3f(vertices[polygon.v_3-1][0], vertices[polygon.v_3-1][1], vertices[polygon.v_3-1][2])
    glEnd()

@window.event
def on_mouse_drag(x, y, dx, dy, buttons, modifiers):
    glRotatef(1, dx, dy, 0)

@window.event
def on_key_press(symbol, modifier):
    #object coordinates adjustment
    if symbol == pyglet.window.key.T:
        transformation()
    if symbol == pyglet.window.key.A:
        change_object_coordinates(0,-1)
        #rotate_x(-0.02)
    if symbol == pyglet.window.key.D:
        change_object_coordinates(0,1)
        #rotate_x(0.02)
    if symbol == pyglet.window.key.S:
        change_object_coordinates(1,-1)
    if symbol == pyglet.window.key.W:
        change_object_coordinates(1,1)
    if symbol == pyglet.window.key.Q:
        change_object_coordinates(2,-1)
    if symbol == pyglet.window.key.E:
        change_object_coordinates(2,1)
    #lighting type change
    if symbol == pyglet.window.key.SPACE:
        change_lighting_type()
    #eye coordinates adjustment
    if symbol == pyglet.window.key.UP:
        change_light_source_coordinate(0,5)
        #rotate_x(100)
        #change_eye_coordinates(0,1)
    if symbol == pyglet.window.key.DOWN:
        change_light_source_coordinate(0,-5)
        #rotate_x(-100)
        #change_eye_coordinates(0,-1)
    if symbol == pyglet.window.key.RIGHT:
        change_light_source_coordinate(1,5)
        #rotate_y(100)
        #change_eye_coordinates(1,1)
    if symbol == pyglet.window.key.LEFT:
        change_light_source_coordinate(1,-5)
        #rotate_y(-100)
        #change_eye_coordinates(1,-1)


#============================================
#polygon visibility and lighting calculation
def change_lighting_type() -> None:
    global lighting_type
    lighting_type = not lighting_type
    if lighting_type:
        print('Changed lighting type to Gouraud.')
    else:
        print('Changed lighting type to constant.')

def check_visible_polygons() -> None:
    '''Checks what polygons are visible to the user.'''
    global center, vertices, polygons, eye_coordinates
    n_p = np.subtract(eye_coordinates, center)
    n_p = np.array([n_p[0], n_p[1], n_p[2]])
    n_p_hat = n_p / np.linalg.norm(n_p)
    

    for polygon in polygons:
        polygon.compute_normal()
        dot_product = np.dot(polygon.n_hat, n_p_hat)
        angle = np.arccos(dot_product)
        #angle = np.arccos(np.dot(n,n_p) / (np.linalg.norm(n) * np.linalg.norm(n_p) ) )
        if np.degrees(angle) < 90:
            polygon.set_visible(True)

def compute_lighting() -> None:
    '''Computes lighting for visible polygons.'''

    # constants for ambient component
    # assures that back polygons are not completely black
    k_a = 1     # 0 <= k_a <= 1
    i_a = 10    # ambient light intensity
    

    # constants for diffuse reflection calculation
    k_d = 0.78125   # difuse reflection coefficient
    i_p = 256       # light source intensity
    k_c = 0.1       # constant to assure no division with zero


    #constants for specular reflection calculation
    k_s = 0.17578125   # specular reflection coefficient (0 <= k_s <= 1)
    #n describes reflective property of the object
    n = 5      # small n => plastic ..... large n => metal
    
    check_visible_polygons()
    if lighting_type:
        compute_gouraud_lighting(k_a,i_a,k_d,i_p,k_c,k_s,n)
    else:
        compute_constant_lighting(k_a,i_a,k_d,i_p,k_c,k_s,n)    

def compute_gouraud_lighting(k_a,i_a,k_d,i_p,k_c,k_s,n) -> None:
    '''Compute constant lighting for each visible polygon.'''
    for polygon in polygons:
        if polygon.is_visible():
            pass

def compute_constant_lighting(k_a,i_a,k_d,i_p,k_c,k_s,n) -> None:
    '''Compute constant lighting for each visible polygon.'''
    global light_source, eye_coordinates, col_min, col_max
    for polygon in polygons:
        if polygon.is_visible():
            v_1 = vertices[polygon.v_1-1]
            v_2 = vertices[polygon.v_2-1]
            v_3 = vertices[polygon.v_3-1]
            x_c = ( v_1[0]+v_2[0]+v_3[0] ) / 3
            y_c = ( v_1[1]+v_2[1]+v_3[1] ) / 3
            z_c = ( v_1[2]+v_2[2]+v_3[2] ) / 3
            p_center = np.array([x_c, y_c, z_c, 1])
            # vector spanning from the center of polygon to the light source
            L = np.subtract(light_source,p_center)
            L_hat = L / np.linalg.norm(L)
            #vector spanning from the center of polygon to the eye
            V = np.subtract(eye_coordinates,p_center)
            V_hat = V / np.linalg.norm(V)
            
            #ambient component
            ambient_total = k_a * i_a
            
            #diffuse component
            dot_product = np.dot(polygon.n_hat, np.array([L_hat[0], L_hat[1], L_hat[2]]))
            d_l = np.linalg.norm(L)
            diffuse_total = k_d*i_p*max(0,dot_product) / (d_l + k_c)
            
            #specular component
            #reflected = L_hat - 2*np.dot(L_hat,polygon.n_hat)*polygon.n_hat     #reflection of light source 'bouncing' from polygon
            #specular_total = k_s*i_p* (np.dot(reflected,V_hat)**n) / (d_l+k_c)
            h = (L_hat + V_hat) / 2
            #TODO check if h_hat is needed
            specular_total = k_s*i_p* (np.dot(np.array([h[0],h[1],h[2]]), polygon.n_hat)**n) / (d_l+k_c)
            #specular_total = 0
            
            #total = ambient_total + diffuse_total + specular_total
            total = ambient_total + diffuse_total
            #total = 1
            if total < col_min:
                col_min = total
            elif total > col_max:
                col_max = total
            #print(f'{total=}')
            polygon.set_intensity(total)

    for polygon in polygons:
        intensity = polygon.get_intensity()
        scaled_intensity = (intensity - col_min) / (col_max - col_min)
        polygon.set_intensity(scaled_intensity)
        #print(scaled_intensity)



#============================================
#coordinate adjustment via keyboard
def change_light_source_coordinate(pos, dt):
    '''Adjust coordinate of light source in position 'pos' for shift 'dt'.'''
    global light_source
    light_source[pos] += dt

def change_eye_coordinates(pos, dt):
    '''Adjusts eye coordinates on given position 'pos' with change 'dt'.'''
    global eye_coordinates
    eye_coordinates[pos] += dt

def change_object_coordinates(pos, dt):
    '''Adjust given coordinate of object for shift 'dt'. '''
    global vertices
    for i in range(len(vertices)):
        vertices[i][pos] += dt



def load_object(filename):
    '''Loads vertices and polygons from given .obj file.'''
    global vertices, polygons
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('v'):
                coords = line.strip().split(' ')
                vertices.append(np.array([float(coords[1]), float(coords[2]), float(coords[3]), 1]) )
            if line.startswith('f'):
                polygon_indexes = line.strip().split(' ')
                p = Polygon(int(polygon_indexes[1]), int(polygon_indexes[2]), int(polygon_indexes[3]), False)
                polygons.append(p)

def load_data(filename) -> np.array:
    '''
    Loads coordinate systems and vertices of polygon from given file.
    Default values for coord systems are (0,0,0).
    '''
    global vertices, with_v_up
    O, G, U = np.zeros(3), np.zeros(3), np.zeros(3)
    with open(filename) as f:
        for line in f.readlines():
            if line.startswith('#'):
                continue
            coords = line.strip().split(' ')
            coords = np.array([float(coords[1]), float(coords[2]), float(coords[3])])
            if line.upper().startswith('O'):
                O = coords
            elif line.upper().startswith('G'):
                G = coords
            elif line.upper().startswith('UP'):
                U = coords
                with_v_up = True
    return O, G, U


def rotate(dt):
    glRotatef(0.5, dt, dt, dt)

def draw_vertex(coordinates):
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v3f', (coordinates[0],coordinates[1], coordinates[2] ) ), ('c3B', (1,1,0)) )


def get_t1_matrix(local_coordinates) -> np.array:
    '''Calculates translation matrix which maps coordinates
    from eye's coordinate system to origin.'''
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,1,0],
                    [-local_coordinates[0], -local_coordinates[1], -local_coordinates[2], 1]])

def get_z_mirror_matrix() -> np.array:
    '''Returns a matrix which mirrors elements by the 'z' axis.'''
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,-1,0],
                    [0,0,0,1]])

def get_t2_matrix(sin_a, cos_a) -> np.array:
    return np.array([[cos_a, -sin_a, 0, 0],
                    [sin_a, cos_a, 0, 0],
                    [0, 0, 1, 0],
                    [0, 0, 0, 1]])

def get_t3_matrix(sin_b, cos_b) -> np.array:
    return np.array([[cos_b, 0, sin_b, 0],
                    [0, 1, 0, 0],
                    [-sin_b, 0, cos_b, 0],
                    [0, 0, 0, 1]])

def transformation() -> None:
    '''Peforms tranformation on vertices.'''
    global transform_matrix, vertices
    
    for i in range(len(vertices)):
        vertices[i] = vertices[i] @ transform_matrix



def main():
    global vertices, center, eye_coordinates, light_source, transform_matrix, with_v_up
    filename = sys.argv[1]

    load_object(filename)

    #============================================
    #scale object
    #============================================
    x_values = [x[0] for x in vertices]
    y_values = [x[1] for x in vertices]
    z_values = [x[2] for x in vertices]

    #find smallest and largest coordinates
    x_min, x_max = min(x_values), max(x_values)
    y_min, y_max = min(y_values), max(y_values)
    z_min, z_max = min(z_values), max(z_values)

    #find center of the body
    x_center = (x_min+x_max) / 2
    y_center = (y_min+y_max) / 2
    z_center = (z_min+z_max) / 2
    center = np.array([x_center, y_center, z_center, 1])

    M = max(x_max-x_min, y_max-y_min, z_max-z_min)
    #translate each vertex for (-x_center, -y_center, -z_center)
    glTranslatef(-x_center, -y_center, -z_center)

    #scale vertices for 2/M
    for i, v in enumerate(vertices):
        vertices[i] = np.array([v[0] * 2/M, v[1] * 2/M, v[2] * 2/M, 1])

    

    #O = input('Unesite 3D koordinate ocista (odvojeno zarezima): ')
    #O = O.split(',')
    #eye_coordinates = np.array([float(O[0]), float(O[1]), float(O[2]), 1])
    #TODO for testing
    #eye_coordinates = np.array([10,10,-5,1])
    #O = input('Unesite 3D koordinate izvora svijetlosti (odvojeno zarezima): ')
    #O = O.split(',')
    #light_source = np.array([float(O[0]), float(O[1]), float(O[2]), 1])
    light_source = np.array([10,20,-23,1])
    draw_vertex(light_source)

    #===========
    #5vjezba dodatak
    #===========
    obj_name = sys.argv[1].split('.')[0]
    f2 = obj_name + '.txt'
    O, G, v_up = load_data(f2)
    eye_coordinates = np.append(O,1)
    t_1 = get_t1_matrix(O)
    z = np.subtract(G,O)


    '''First way of calculating transformation matrix.'''
    if (with_v_up):

        #z-axis calculation
        z_hat = z / np.linalg.norm(z)
        v_up_hat = v_up / np.linalg.norm(v_up)
        #axis calculation using the view-up vector
        x = np.cross(z_hat, v_up_hat)
        x_hat = x / np.linalg.norm(x)
        y = np.cross(x_hat, z_hat)
        y_hat = y / np.linalg.norm(y)

        rotation_matrix = np.transpose( np.vstack( (np.append(x_hat,0), np.append(y_hat,0), np.append(z_hat,0), np.array([0,0,0,1])) ) )

        t_z = get_z_mirror_matrix()

        transform_matrix = t_1 @ rotation_matrix @ t_z

    else:
        '''Second way of calculating transformation matrix.'''
        g_1 = np.subtract(G,O)
        g_1 = np.append(g_1,1)
        sin_a = g_1[1] / np.sqrt(g_1[0]**2+g_1[1]**2)
        cos_a = g_1[0] / np.sqrt(g_1[0]**2+g_1[1]**2)

        t_2 = get_t2_matrix(sin_a,cos_a)

        g_2 = g_1 @ t_2
        sin_b = g_2[0] / np.sqrt(g_2[0]**2+g_2[2]**2)
        cos_b = g_2[2] / np.sqrt(g_2[0]**2+g_2[2]**2)

        t_3 = get_t3_matrix(sin_b,cos_b)

        t_4 = np.array([[0,-1,0,0],
                        [1,0,0,0],
                        [0,0,1,0],
                        [0,0,0,1]])
                        
        t_5 = np.array([[-1,0,0,0],
                        [0,1,0,0],
                        [0,0,1,0],
                        [0,0,0,1]])

        t_2_5 = t_2 @ t_3 @ t_4 @t_5
        transform_matrix = t_1 @ t_2_5

    print(f'Transform matrix:\n{transform_matrix}')


    glTranslatef(0, 0, eye_coordinates[2]/2)
    #glRotatef(1,10,10,0)
    #pyglet.clock.schedule_interval(rotate, 1/60)
    pyglet.app.run()


if __name__=='__main__':
    main()
