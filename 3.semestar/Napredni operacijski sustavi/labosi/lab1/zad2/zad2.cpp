#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h> 
#include <sys/types.h> 
#include <unistd.h>
#include <iostream>
#include <cstring>
#include <sys/shm.h>
#include <cstdlib>
#include <signal.h>
using namespace std;

#define SHM_KEY 0x1234
#define MAXREAD 50 // najveća duljina poruke

struct shmseg {
   int pid;
   int logic_clock;
   int ko_counter;
};

int N;
shmseg *SHM_POINTER;

void storeInDb(shmseg *data){
    memcpy(SHM_POINTER, data, sizeof(shmseg) * N);
}

void child(int myIndex, int *pipes){
    int pid = getpid();
    cout << "Stvoren proces dijete " << pid << endl;

    // zatvaram pisanje u svoj cijevovod
    int myPipe = 2 * myIndex;
    close(pipes[myPipe + 1]);

    // zatvaranje citanja za sve druge cjevovode
    for(int i = 0; i < N + 1; i++){
        if(!(i == 2 * myIndex)){
            close(pipes[i]);
        }
    }

    // posalji svoj pid roditelju
    string temp_str=to_string(pid);
    char const *message = temp_str.c_str();
    (void) write(pipes[2 * N + 1], message, strlen(message) + 1);

}

int main(int argc, char *argv[]){
    if(argc < 2){
        cout << "Program treba jedan argument" << endl;
        return 0;

    }
    
    int n = strtol(argv[1], NULL, 10);

    if(n < 2 || n > 10){
        cout << "Programu je potrebno dati broj procesa u intervalu [3, 10]" << endl;
        return 0;
    }

    N = n;

    int shmid;
    struct shmseg *shmp;

    // creating shared memory
    int sharedMemKey = ftok("shmfile",65);
    shmid = shmget(SHM_KEY, sizeof(struct shmseg) * N, 0644|IPC_CREAT);
    if (shmid == -1) {
      perror("Shared memory");
      return 1;
    }

    // Attach to the segment to get a pointer to it.
    shmp = (shmseg *)shmat(shmid, NULL, 0);
    if (shmp == (void *) -1) {
        perror("Shared memory attach");
        return 1;
    }
    SHM_POINTER = shmp;

    // creating pipes, children and parent
    int pfds[(N+1) * 2];
    for(int i = 0; i < N + 1; i++){
        if (pipe(pfds + i * 2) == -1){
            exit(1);
        }
    }

    // init data that will be stored in db
    shmseg* data = (shmseg*)malloc(sizeof(shmseg) * N);

    int pid;
    for(int i = 0; i < N; i++){
        switch(pid = fork()){
            case -1:    cout << "Ne mogu stvoriti " << i << ". proces" << endl;
                        exit(1);
            case 0:     child(i, pfds);
                        exit (0);
            default:    close(pfds[i*2]); // zatvaranje citanja za sve druge cijevovode
        }
    }

    close(pfds[2*N + 1]); // zatvaranje pisanja za moj cijevovod

    sleep(1); // a little bit of waiting for tension

    // get all pids from children
    for(int i = 0; i < N; i++){
        char buf[MAXREAD] = "";
        (void) read(pfds[N * 2], buf, MAXREAD);
        cout << "Buffer je procitao " << buf << endl;
        int temp = strtol(buf, NULL, 10);
        //cout << temp << endl;
    }

    storeInDb(data);


    for(int i = 0; i < N; i++){
        cout << shmp[i].pid << endl;
    }

    return 0;
}