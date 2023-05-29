import numpy as np

def get_t1_matrix(coordinates) -> np.array:
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,1,0],
                    [-coordinates[0], -coordinates[1], -coordinates[2], 1]])

def get_z_mirror_matrix() -> np.array:
    '''Returns a matrix which mirrors elements by the 'z' axis.'''
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,-1,0],
                    [0,0,0,1]])
    
def get_transformation_matrix(O, G, UP) -> np.array:
    eye_coordinates = np.append(O,1)
    t_1 = get_t1_matrix(O)
    z = np.subtract(G,O)

    #z-axis calculation
    z_hat = z / np.linalg.norm(z)
    v_up_hat = UP / np.linalg.norm(UP)
    #axis calculation using the view-up vector
    x = np.cross(z_hat, v_up_hat)
    x_hat = x / np.linalg.norm(x)
    y = np.cross(x_hat, z_hat)
    y_hat = y / np.linalg.norm(y)

    rotation_matrix = np.transpose( np.vstack( (np.append(x_hat,0), np.append(y_hat,0), np.append(z_hat,0), np.array([0,0,0,1])) ) )

    t_z = get_z_mirror_matrix()

    return t_1 @ rotation_matrix @ t_z

def get_projection_matrix(O, G) -> np.array:
    z = np.subtract(G,O)
    H = np.linalg.norm(z)
    
    return np.array([[1,0,0,0],
                    [0,1,0,0],
                    [0,0,0,1/H],
                    [0,0,0,0]])
    
    