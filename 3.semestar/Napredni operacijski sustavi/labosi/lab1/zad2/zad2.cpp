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
#define MESSAGE_SIZE 50 // najveca duljina poruke
#define REQUEST "0"
#define RESPONSE "1"

struct shmseg {
   int pid;
   int logic_clock;
   int ko_counter;
};

int N = 5;
shmseg *SHM_POINTER;

void storeInDb(shmseg *data){
    memcpy(SHM_POINTER, data, sizeof(shmseg) * N);
}

string createMyRequest(int i, int clock){
    string p=to_string(i);
    string c=to_string(clock);
    char *message;
    return p + REQUEST + c;
}

string createMyResponse(int i, int clock){
    string p=to_string(i);
    string c=to_string(clock);
    return p + RESPONSE + c;
}

void sendMessage(int *pfd, string message){
    char const *m = message.c_str();
    (void) write(pfd[1], m, MESSAGE_SIZE);
}

string readMessage(int *pfd){
    char buf[MESSAGE_SIZE] = "";
    (void) read(pfd[0], buf, MESSAGE_SIZE);
    return string(buf);
}

void child(int myIndex, int *pipes){
    int pid = getpid();
    int localClock = 0;
    cout << "Stvoren proces dijete " << pid << endl;

    // zatvaram pisanje u svoj cijevovod
    int *myPipe = pipes + 2 * myIndex;
    close(myPipe[1]);

    // zatvaranje citanja za sve druge cjevovode
    for(int i = 0; i < N + 1; i++){
        if(!(i == myIndex)){
            close(pipes[i]);
        }
    }

    // posalji svoj pid roditelju
    sendMessage(pipes + 2 * N, to_string(pid));

    // inicijalno spavanje
    sleep(rand()%3);

    // zatrazi ulaz u KO
    string request = createMyRequest(myIndex, localClock);
    for(int i = 0; i < N; i++){
        cout << "Proces" <<  myIndex << " salje poruku procesu" << i << endl;
        if(i != myIndex){
            sendMessage(pipes + 2 * i, request);
        }
    }
    
    // cekaj poruke i obraduj ih, zelis uci u KO
    int responseCounter = 0;
    while(responseCounter < N){
        string message = readMessage(myPipe);
        cout <<  message << endl;
        responseCounter++;
        /* int i = message[0];
        int mType = message[1];
        message.erase(0, 1);
        int messageClock = stoi(message);

        cout << i << " " << mType << " " << messageClock << endl; */
    }

    // udi u KO

    // paralelno spavaj random sekundi i obraduj poruke

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
        string message = readMessage(pfds + N * 2);
        int temp = stoi(message, NULL, 10);
        data[i].pid = temp;
    }

    storeInDb(data);

    for(int i = 0; i < N; i++){
        wait(NULL);
        cout << "Zavrseno" << endl;
    }

    return 0;
}