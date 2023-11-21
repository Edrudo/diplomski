import numpy as np

if __name__ == "__main__":
    print("Zadatak 1")

    print("Zbroj vektora")
    v1 = np.add(np.array([2, 3, -4]), np.array([-1, 4, -1]))
    print(f'{v1=}')

    print("Ovo je skalarni produkt zadanih vektora")
    s = np.dot(v1, np.array([2, 3, 5]))
    print(f'{s=}')

    print("Ovo je vektorski produkt zadanih vektora")
    v2 = np.cross(v1, np.array([2, 3, 5]))
    print(f'{v2=}')

    v3 = np.linalg.norm(v2)
    print(f'{v3=}')

    v4 = -v2
    print(f'{v4=}')

    Mx = np.array([
        [1, 2, 3],
        [2, 1, 3],
        [4, 5, 1],
    ])
    My = np.array([
        [-1, 2, -3],
        [5, -2, 7],
        [-4, -1, 3]
    ])

    M1 = np.add(Mx, My)  # zbrajanje matrica
    print(f'{M1=}')

    print("Ovo je mnozenje zadanih matrica (transponirana)")
    M2 = np.dot(Mx, np.transpose(My))  # mnozenje matrica
    print(f'{M2=}')

    print("Ovo je mnozenje zadanih matrica (invertirana)")
    M3 = np.dot(Mx, np.linalg.inv(My))
    print(f'{M3=}')

    print("Zadatak 2")

    A = np.array([
        [0, 0, 0],
        [0, 0, 0],
        [0, 0, 0]
    ])
    B = np.array([0, 0, 0])
    j = 0

    for i in range(0, 9):
        i1 = i % 3
        A[j][i1] = input("Upisite broj A:\n")
        print(A)

        if i % 3 == 2:
            B[j] = input("Upisite broj B:\n")
            print(B)
            j = j+1

    print("\n\n\nRjesenja x, y, z = ", np.linalg.inv(A).dot(B))

    print('Zadatak 3:')

    A = [0, 0, 0]
    B = [0, 0, 0]
    C = [0, 0, 0]
    T = [0, 0, 0]

    A[0] = int(input("x tocke A:"))
    A[1] = int(input("z tocke A:"))
    A[2] = int(input("y tocke A:"))

    B[0] = int(input("x tocke B:"))
    B[1] = int(input("z tocke B:"))
    B[2] = int(input("y tocke B:"))

    C[0] = int(input("x tocke C:"))
    C[1] = int(input("z tocke C:"))
    C[2] = int(input("y tocke C:"))

    T[0] = int(input("x tocke T:",))
    T[1] = int(input("z tocke T:"))
    T[2] = int(input("y tocke T:"))

    M = np.transpose(np.array([A, B, C]))
    print(M)

    t = np.dot(np.linalg.inv(np.array(M)), T)
    print(t)
