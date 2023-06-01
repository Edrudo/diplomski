import numpy as np

def get_t1_matrix(coordinates) -> np.array:
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,1,0],
                    [-coordinates[0], -coordinates[1], -coordinates[2], 1]])

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

def get_t4_matrix() -> np.array:
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,-1,0],
                    [0,0,0,1]])
    
def get_transformation_matrix(O, G, ) -> np.array:
    t_1 = get_t1_matrix(O)

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
    return t_1 @ t_2_5

def get_projection_matrix(O, G) -> np.array:
    z = np.subtract(G,O)
    H = np.linalg.norm(z)
    
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,0,1/H],
                    [0,0,0,0]])
    
    