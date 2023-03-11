import numpy as np
def mnoziMatrice(A, B):
    m2=np.transpose(B)
    return np.dot(A,m2)
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
t= mnoziMatrice(Minv, np.array(D))

print(t)
