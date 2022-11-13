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
#define MESSAGE_SIZE 10 // najveca duljina poruke
#define REQUEST '0'
#define RESPONSE '1'
#define READ 0
#define WRITE 1
#define MESSAGE_ID 0
#define MESSAGE_TYPE 1
#define MESSAGE_CLOCK 2

struct shmseg {
   int pid;
   int localClock;
   int ko_counter;
};

struct myMessage {
    int id;
    char m_type;
    int logic_clock;
    myMessage(int id, char m_type, int logic_clock) : 
        id(id), m_type(m_type), logic_clock(logic_clock) {}
    myMessage() :  id(-1) {}
};

int N = 5;
shmseg *SHM_POINTER;

void storeInDb(shmseg *data){
    memcpy(SHM_POINTER, data, sizeof(shmseg) * N);
}

void updateAndPrintDb(int index, int localClock){
    string dbDump = string("|   ");
    for(int i = 0; i < N; i++){
        if(index == i){
            SHM_POINTER[i].localClock = localClock;
            SHM_POINTER[i].ko_counter++;
        }
        dbDump += to_string(SHM_POINTER[i].pid) + " " + to_string(SHM_POINTER[i].localClock) + " " + to_string(SHM_POINTER[i].ko_counter) + "   |    ";
    }
    cout << dbDump << endl;
}

string createMyRequest(int id, int clock){
    string index=to_string(id);
    string c=to_string(clock);
    return index + REQUEST + c;
}

string createMyResponse(int id, int clock){
    string index=to_string(id);
    string c=to_string(clock);
    return index + RESPONSE+ c;
}

void sendMessage(int *pfd, string message){
    char const *m = message.c_str();
    (void) write(pfd[WRITE], m, MESSAGE_SIZE);
}

string readMessage(int *pfd){
    char buf[MESSAGE_SIZE] = "";
    (void) read(pfd[READ], buf, MESSAGE_SIZE);
    return string(buf);
}

myMessage toMyMessage(string message){
    int pid = message[MESSAGE_ID] - '0';
    char m_type = message[MESSAGE_TYPE];
    message.erase(0, 1);
    int messageClock = stoi(message);
    cout << "Iz poruke " << message << " dobio sam strukturu " << myMessage(pid, m_type, messageClock) << endl;
    return myMessage(pid, m_type, messageClock);
}

void child(int myIndex, int *pipes){
    int pid = getpid();
    srand(pid);
    int localClock = 0;
    cout << "Stvoren proces dijete " << pid << endl;

    // zatvaram pisanje u svoj cijevovod
    int *myPipe = pipes + 2 * myIndex;
    close(myPipe[WRITE]);

    // zatvaranje citanja za sve druge cjevovode
    for(int i = 0; i < N + 1; i++){
        if(i != myIndex){
            close(pipes[2*N + READ]);
        }
    }

    // posalji svoj pid roditelju
    sendMessage(pipes + 2 * N, to_string(pid));

    // inicijalno spavanje
    sleep(rand()%3);

    int counter = 0;

    while(true) {
        // zatrazi ulaz u KO
        string request = createMyRequest(myIndex, localClock);
        for(int i = 0; i < N; i++){
            if(i != myIndex){
                //cout << "Proces" <<  myIndex << " salje zahtev procesu" << i << " message: " << request << endl;
                sendMessage(pipes + 2 * i, request);
                //cout << "Proces" << myIndex << " poslao je poruku " << request << endl;
            }
        }

        myMessage pendingRequests[N];
    
        // cekaj poruke i obraduj ih, zelis uci u KO
        int responseCounter = 0;
        while(responseCounter < N - 1){
            string m = readMessage(myPipe);
            //cout << "Proces" << myIndex << " primio je poruku " << m << endl;
            myMessage message = toMyMessage(m);

            if(message.m_type == REQUEST){
                //cout << "Proces" << myIndex << " primio je zahtjev od " << message.id << endl;

                if(localClock > message.logic_clock || (localClock == message.logic_clock && myIndex > message.id)){
                    // posalji odgovor
                    //cout << "Proces" <<  myIndex << " salje odgovor procesu" << message.id << " satovi: " << localClock << " " << message.logic_clock << endl;
                    sendMessage(pipes + 2 * message.id, createMyResponse(myIndex, localClock));
                    //cout << "Proces" << myIndex << " poslao je poruku " << createMyResponse(myIndex, localClock) << endl;
                    continue;
                }else{
                    pendingRequests[message.id] = message;
                }
            }else if(message.m_type == RESPONSE){
                responseCounter++;
                //cout << "Proces" << myIndex << " primio je odgovor od " << message.id << " counter je " << responseCounter << endl;
            }

            // uskladi logicki sat
            if(localClock < message.logic_clock){
                localClock= message.logic_clock;
            }
            localClock++;
        }

        // udi u KO
        //cout << "Proces" << myIndex << " je usao u KO po " << ++counter << " puta" << endl;
        //updateAndPrintDb(myIndex, localClock);
        sleep(2);
        // odgovori svima koji cekaju
        for(int i = 0; i < N; i++){
            if(i != myIndex && pendingRequests[i].id != -1){
                sendMessage(pipes + 2 * pendingRequests[i].id, createMyResponse(myIndex, localClock));
                //cout << "Proces" << myIndex << " poslao je poruku " << createMyResponse(myIndex, localClock) << endl;
            }
        }
        sleep(rand() % 4);
    }
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
        }
    }

    // zatvaranje citanja za sve druge cijevovode
    for(int i = 0; i < N; i++){
        close(pfds[i*2 + READ]); 
    }

    int *mainPipe = pfds + 2 * N;
    close(mainPipe[WRITE]);

    sleep(1); // a little bit of waiting for tension

    // get all pids from children
    for(int i = 0; i < N; i++){
        string message = readMessage(pfds + N * 2 + READ);
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