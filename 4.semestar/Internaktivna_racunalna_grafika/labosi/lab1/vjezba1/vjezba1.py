from ipaddress import v4_int_to_packed
import numpy as np

print("Zadatak 1")

v1 = np.array([2, 3, -4])+np.array([-1, 4, -1])
print(f'{v1=}')
s=np.dot(v1,np.array([-1, 4, -1]))
print(f'{s=}')
v2=np.cross(v1,np.array([2, 2, 4]))
print(f'{v2=}')
v3=np.linalg.norm(v2)
print(f'{v3=}')
v4=-v2
print(f'{v4=}')

def mnoziMatrice(A, B):
    m2=np.transpose(B)
    return np.dot(A,m2) # mnozenje matrica

m2=np.transpose(np.array([[-1,2,-3], [5,-2,7], [-4,-1,3]]))
M1=np.dot(np.array([[1,2,3], [2,1,3], [4,5,1]]),m2) # mnozenje matrica
print(f'{M1=}')


m22=np.linalg.inv(np.array([[-1,2,-3], [5,-2,7], [-4,-1,3]])) # invertiraj matricu
M2=np.dot(np.array([[1,2,3], [2,1,3], [4,5,1]]),m22)
print(f'{M2=}')


print("Zadatak 2")

A=np.array([[0,0,0],
            [0,0,0],
            [0,0,0]])
B=np.array([0,0,0])
j=0
for i in range(0,9):
    i1=i%3
    A[j][i1]=input("Upisite broj A:\n")
    print(A)   
    
    if i%3==2: 
        B[j]=input("Upisite broj B:\n")
        print(B)
        j=j+1

A1 = np.array(A)
B1 = np.array(B)
X = np.linalg.inv(A1).dot(B1)
print("x, y, z = ", X)

print('Zadatak 3:')

A=[0,0,0]
B=[0,0,0]
C=[0,0,0]
D=[0,0,0]

A[0]=int(input("x tocke A:"))
A[1]=int(input("z tocke A:"))
A[2]=int(input("y tocke A:"))

B[0]=int(input("x tocke B:"))
B[1]=int(input("z tocke B:"))
B[2]=int(input("y tocke B:"))

C[0]=int(input("x tocke C:"))
C[1]=int(input("z tocke C:"))
C[2]=int(input("y tocke C:"))

D[0]=int(input("x tocke D:",))
D[1]=int(input("z tocke D:"))
D[2]=int(input("y tocke D:"))

M=np.transpose(np.array([A,B,C]))
print(M)
Minv=np.linalg.inv(np.array(M))
t=mnoziMatrice(Minv, np.array(D))

print(t)


