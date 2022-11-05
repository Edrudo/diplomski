#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h> 
#include <string.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <iostream>
#include <sys/wait.h>
#include <sys/shm.h>
#include <signal.h>
using namespace std;
#define MAXREAD 4/* najveća duljina poruke*/

void dretvaFilozof(int id, int pfds[20][2]){
    int localClock = 0;

    int leftN = (20 + id - 1) % 20;
    int rightN = (id + 1) % 20;
 
    int myLeftRead=id*4;
    int myLeftWrite=(id*4 - 1 + 10) % 10;
    int myRightRead=id*4 + 1;
    int myRightWrite=(id*4 + 2) % 10;


    while(true){ 
        // thinking time
        int think = (rand() % 1900) + 100;
        cout << "Filozof " << id << " misli." << endl;
        usleep(think * 1000);

        // check if there are some Reads waiting
        char buf1[MAXREAD] = ""; 
        (void) read(pfds[myLeftRead][0], buf1, MAXREAD); // provjera zahtjeva lijevog susjeda
        

        mesg_buffer message2;
        recvRet = msgrcv(msgids[myRightRead], &message2, sizeof(message2), 0, IPC_NOWAIT); // ima li zahtjev od desnog filozofa
        if(recvRet != -1) { // ima zahtjeva, odgovori mu
            //cout << "Filozof " << id<< " salje odgovor desnom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[rightNWrite], &message, sizeof(message), 0);
        }

        // salji zahtjev za ulaz u ko
        //cout << "Filozof " << id<< " salje zahtjeve" << endl;

        mesg_buffer message3={1, "n"};
        msgsnd(msgids[leftNRead], &message3, sizeof(message3), 0);

        mesg_buffer message4={1, "n"};
        msgsnd(msgids[rightNRead], &message4, sizeof(message4), 0);

        // cekaj odgovore na zahtjeve i provjeravaj zahtjeve
        bool leftOk = false;
        bool rightOk = false;
        //cout << "Filozof " << id<< " ceka odgovore" << endl;
        while(!leftOk && !rightOk){
            if(!leftOk){
                mesg_buffer message5;
                int rcvRet = msgrcv(msgids[myLeftWrite], &message5, sizeof(message5), 0, IPC_NOWAIT); // cekaj odgovor lijevog filozofa
                if(rcvRet != -1){
                    leftOk = true;
                }
            }
            if(!rightOk){
                mesg_buffer message6;
                int rcvRet = msgrcv(msgids[myRightWrite], &message6, sizeof(message6), 0, IPC_NOWAIT); // cekaj odgovor desnog filozofa
                if(rcvRet != -1){
                    rightOk = true;
                }
            }
            
            // provjeri zeli li filozof s manjim indeksom uci u ko
            if(leftN < id){
                mesg_buffer message7;
                int recvRet = msgrcv(msgids[myLeftRead], &message7, sizeof(message7), 0, IPC_NOWAIT); // ima li zahtjev od lijevog filozofa
                if(recvRet != -1) { // ima zahtjeva, odgovori mu
                    mesg_buffer message={1, "n"};
                    msgsnd(msgids[leftNWrite], &message, sizeof(message), 0);
                }   
            }
            if(rightN < id){
                mesg_buffer message10;
                int recvRet = msgrcv(msgids[myRightWrite], &message10, sizeof(message10), 0, IPC_NOWAIT); // ima li zahtjev od lijevog filozofa
                if(recvRet != -1) { // ima zahtjeva, odgovori mu
                    mesg_buffer message={1, "n"};
                    msgsnd(msgids[leftNWrite], &message, sizeof(message), 0);
                }   
            }
        }

        // udi u ko
        int sleep = (rand() % 1900) + 100;
        cout << "Filozof " << id << " jede." << endl;
        usleep(sleep * 1000);
        cout << "Filozof " << id << " gotov s jelom." << endl;

        // check if there are some Reads waiting
        //cout << "Filozof " << id<< " provjerava zahtjeve" << endl;
        mesg_buffer message8;
        recvRet = msgrcv(msgids[myLeftRead], &message8, sizeof(message8), 0, IPC_NOWAIT); // ima li zahtjev od lijevog filozofa
        if(recvRet != -1) { // ima zahtjeva, odgovori mu
            //cout << "Filozof " << id<< " salje odgovor lijevom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[leftNWrite], &message, sizeof(message), 0);
        }

        mesg_buffer message9;
        recvRet = msgrcv(msgids[myRightRead], &message9, sizeof(message9), 0, IPC_NOWAIT); // ima li zahtjev od desnog filozofa
        if(recvRet != -1) { // ima zahtjeva, odgovori mu
            //cout << "Filozof " << id<< " salje odgovor desnom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[rightNWrite], &message, sizeof(message), 0);
        }

        localClock = (localClock + 1) % 10;
    }
}

int main(void){

    int pfds[20][2];

     int pfds[5][2];

    for(int i = 0; i < 5; i++){
        if (pipe(pfds[i*4]) == -1) // stvaranje cjevovoda
            cout << "Greska u stvaranju cjevovoda" << endl;
            exit(1);
        if (pipe(pfds[i*4+1]) == -1) // stvaranje cjevovoda
            cout << "Greska u stvaranju cjevovoda" << endl;
            exit(1);
        if (pipe(pfds[i*4+2]) == -1) // stvaranje cjevovoda
            cout << "Greska u stvaranju cjevovoda" << endl;
            exit(1);
        if (pipe(pfds[i*4+3]) == -1) // stvaranje cjevovoda
            cout << "Greska u stvaranju cjevovoda" << endl;
            exit(1);
    }


    for(int i = 0; i < 5; i++){
        switch (fork()) {

            case -1:
                cout << "Greska kod stvaranja procesa" << endl;
                exit(1);

            case 0:
                dretvaFilozof(i, pfds);
                exit(0);
        }
    }
    for(int i = 0; i < 5; i++){
        wait(NULL);
    }
    return 0;
}