import sys
import os
import posix

def child():
    print("Ja sam dijete broj " + str(os.getpid()))

if __name__ == "__main__":
    N = int(sys.argv[1])
    if N < 3 or N > 10:
        print("Broj procesa N mora biti [3, 10]")
        sys.exit()
        
    for i in range(0, N):
        pid = os.fork()
        if(pid == 0):
            child()
            break
        
    
    