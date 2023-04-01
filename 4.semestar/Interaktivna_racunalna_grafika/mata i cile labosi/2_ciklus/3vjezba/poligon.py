import numpy as np
from pyglet.gl import *
from pyglet import shapes
from pyglet.window import mouse

config=pyglet.gl.Config(double_buffer=False)
window = pyglet.window.Window(1280, 720, config=config)
batch = pyglet.graphics.Batch()

vertices = []   #list of tuples containing polygon vertices
num_of_assigned_vertices = 0

edges = []      #list of edges and their character (left/right)
test_vertex = ()


@window.event
def on_mouse_press(x, y, button, modifiers):
    global test_vertex, vertices, num_of_assigned_vertices
    if button & mouse.LEFT:
        if num_of_assigned_vertices == num_of_vertices-1:
            vertices.append((x, y))
            draw_polygon()
        elif num_of_assigned_vertices >= num_of_vertices:
            print('Test vertex assigned!')
            test_vertex = (x, y)
        elif num_of_assigned_vertices < num_of_vertices:
            vertices.append((x, y))
        
        print(x,y)
        draw_vertex(x,y)
        num_of_assigned_vertices += 1


@window.event
def on_key_press(symbol, modiefier):
    if symbol == 32:
        fill_polygon_v2()
    elif symbol == 99:
        check_vertex_position(test_vertex)
    

def draw_vertex(x, y):
    pyglet.graphics.draw(1, pyglet.gl.GL_POINTS, ('v2i', (x,y)))

def draw_polygon():
    for i in range(num_of_vertices):
        character = 'R'     #character indicator
        line = shapes.Line(vertices[i][0], vertices[i][1], vertices[(i+1) % len(vertices)][0], vertices[(i+1) % len(vertices)][1], batch=batch)
        if vertices[i][1] < vertices[(i+1) % len(vertices)][1]:
            character = 'L'
        edges.append((line, character))
        batch.draw()

def fill_polygon_v1():
    x_values = [x[0] for x in vertices]
    y_values = [x[1] for x in vertices]
    x_min = min(x_values)
    x_max = max(x_values)
    y_min = min(y_values)
    y_max = max(y_values)

    for y in range(y_min, y_max+1):
        const_line = pyglet.shapes.Line(x_min, y, x_max, y)
        L = x_min
        D = x_max
        for edge_info in edges:
            edge = edge_info[0]
            character = edge_info[1]

            S = find_intersection_x(edge, const_line)
            if character == 'L' and S > L:
                L = S
            elif character == 'R' and S < D:
                D = S
        if L <= D:
            line = shapes.Line(L, y, D, y, batch=batch)
            batch.draw()

def fill_polygon_v2():
    x_values = [x[0] for x in vertices]
    y_values = [x[1] for x in vertices]
    x_min = min(x_values)
    x_max = max(x_values)
    y_min = min(y_values)
    y_max = max(y_values)

    left_edges, right_edges = [], []

    for edge_info in edges:
        print(edge_info)
        if edge_info[1] == 'L':
            left_edges.append(edge_info[0])
        else:
            right_edges.append(edge_info[0])
            
    left_edges = sorted(left_edges, key=lambda line: line.y)
    right_edges = sorted(right_edges, key=lambda line: line.y2)

    print(f'First left edge starting coord y is: {left_edges[0].y}')
    print(f'Second left edge starting coord y is: {left_edges[1].y}')

    adbi, albi = 0, 0

    for y in range(y_min, y_max+1):
        const_line = pyglet.shapes.Line(x_min, y, x_max, y)
        b = right_edges[adbi+1]
        if b.y <= y:
            adbi += 1
        b = left_edges[albi+1]
        if b.y2 <= y:
            albi += 1

        L = find_intersection_x(const_line, left_edges[albi])
        D = find_intersection_x(const_line, right_edges[adbi])

        L = max(x_min, L)
        D = min(x_max, D)

        line = shapes.Line(L, y, D, y, batch=batch)
        batch.draw()


def find_intersection_x(line1, line2):
    '''
    Finds the x coordinate of the intersection between two given lines.
    '''
    m1 = (line1.y2-line1.y) / (line1.x2-line1.x)
    b1 = -m1*line1.x + line1.y

    m2 = (line2.y2-line2.y) / (line2.x2-line2.x)
    b2 = -m2*line2.x + line2.y

    return (b1-b2) / (m2-m1)


def check_vertex_position(vertex):
    '''
    Checks if given vertex is inside the polygon.
    '''
    all_below = True    #bool value indicating if given vertex is below all edges of the polygon (right of them)
    v_t = np.array([vertex[0], vertex[1], 1])
    for i in range(len(vertices)):
        v_1 = np.array([vertices[i][0], vertices[i][1], 1])
        v_2 = np.array([vertices[(i+1) % len(vertices)][0], vertices[(i+1) % len(vertices)][1], 1])
        edge = np.cross(v_1, v_2)
        if np.dot(v_t, edge) > 0:
            all_below = False
            break

    if all_below:
        print('Testni vrh je u poligonu.')
    else:
        print('Testni vrh je izvan poligona.')


if __name__=='__main__':
    global num_of_vertices
    num_of_vertices = int(input("Broj vrhova: "))
    #num_of_vertices = 4
    pyglet.app.run()
    