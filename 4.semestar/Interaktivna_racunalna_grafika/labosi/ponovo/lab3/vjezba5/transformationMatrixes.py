import numpy as np


def getT1(O) -> np.array:
    return np.array([[1, 0, 0, 0],
                     [0, 1, 0, 0],
                     [0, 0, 1, 0],
                     [-O[0], -O[1], -O[2], 1]])


def getT2(O, G) -> np.array:
    g = np.subtract(G, O)
    g = np.append(g, 1)
    sina = g[1] / np.sqrt(g[0]**2+g[1]**2)
    cosa = g[0] / np.sqrt(g[0]**2+g[1]**2)

    return np.array([[cosa, -sina, 0, 0],
                     [sina, cosa, 0, 0],
                     [0, 0, 1, 0],
                     [0, 0, 0, 1]])


def getT3(O, G) -> np.array:
    g = np.subtract(G, O)
    g = np.append(g, 1)

    g1 = g @ getT2(O, G)

    sinb = g1[0] / np.sqrt(g1[0]**2+g1[2]**2)
    cosb = g1[2] / np.sqrt(g1[0]**2+g1[2]**2)

    return np.array([[cosb, 0, sinb, 0],
                     [0, 1, 0, 0],
                     [-sinb, 0, cosb, 0],
                     [0, 0, 0, 1]])


def getT4() -> np.array:
    return np.array([[1, 0, 0, 0],
                     [0, 1, 0, 0],
                     [0, 0, -1, 0],
                     [0, 0, 0, 1]])


def getT5():
    return np.array([[-1, 0, 0, 0],
                     [0, 1, 0, 0],
                     [0, 0, 1, 0],
                     [0, 0, 0, 1]])
