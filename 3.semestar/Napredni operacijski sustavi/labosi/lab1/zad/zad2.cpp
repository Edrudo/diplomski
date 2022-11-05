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

const int MAXREAD = 2;

struct mesg_buffer {
    long mesg_type;
    char mesg_text[2];
} message;

void dretvaFilozof(int id, int msgids[]){
    int localClock = 0;

    srand(getpid() * time(NULL));

    int leftN = (5 + id - 1) % 5;
    int rightN = (id + 1) % 5;
 
    int myLeftRequest=id*4;
    int myLeftResponse=id*4 + 1;
    int myRightRequest=id*4 + 2;
    int myRightResponse=id*4 + 3;

    int leftNRequest= id != 0 ? id*4 - 2: 18;
    int leftNResponse=id != 0 ? id*4 - 1: 19;
    
    int rightNRequest=(id*4 + 4) % 20;
    int rightNResponse=(id*4+ 5) % 20;

    while(true){ 
        // thinking time
        int think = (rand() % 1900) + 100;
        cout << "Filozof " << id << " misli." << endl;
        usleep(think * 1000);

        // provjeri jeli ti netko poslao zahtjev, ukoliko je odgovori mu da smije uci u odsjecak
        //cout << "Filozof " << id<< " provjerava zahtjeve" << endl;
        mesg_buffer message1;
        int recvRet = msgrcv(msgids[myLeftRequest], &message1, sizeof(message1), 0, IPC_NOWAIT); // ima li zahtjev od lijevog filozofa
        if(recvRet != -1) { // ima zahtjeva, odgovori mu
            //cout << "Filozof " << id<< " salje odgovor lijevom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[leftNResponse], &message, sizeof(message), 0);
        }

        mesg_buffer message2;
        recvRet = msgrcv(msgids[myRightRequest], &message2, sizeof(message2), 0, IPC_NOWAIT); // ima li zahtjev od desnog filozofa
        if(recvRet != -1) { // ima zahtjeva, odgovori mu
            //cout << "Filozof " << id<< " salje odgovor desnom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[rightNResponse], &message, sizeof(message), 0);
        }

        // salji zahtjev za ulaz u ko
        //cout << "Filozof " << id << " salje zahtjeve" << endl;

        mesg_buffer message3={1, '0' + localClock};
        msgsnd(msgids[leftNRequest], &message3, sizeof(message3), 0);

        mesg_buffer message4={1, '0' + localClock};
        msgsnd(msgids[rightNRequest], &message4, sizeof(message4), 0);

        // cekaj odgovore na zahtjeve i provjeravaj zahtjeve
        bool leftOk = false;
        bool rightOk = false;
        //cout << "Filozof " << id<< " ceka odgovore" << endl;
        bool leftPending = false;
        bool rightPending = false;
        while(!leftOk || !rightOk){
            if(!leftOk){
                mesg_buffer message5;
                int rcvRet = msgrcv(msgids[myLeftResponse], &message5, sizeof(message5), 0, IPC_NOWAIT); // cekaj odgovor lijevog filozofa
                if(rcvRet != -1){
                    //cout << "Filozof " << id << " procitao odgovor od lijevog filozofa " << endl;
                    leftOk = true;
                }
            }
            if(!rightOk){
                mesg_buffer message6;
                int rcvRet1 = msgrcv(msgids[myRightResponse], &message6, sizeof(message6), 0, IPC_NOWAIT); // cekaj odgovor desnog filozofa
                if(rcvRet1 != -1){
                    //cout << "Filozof " << id << " procitao odgovor od desnog filozofa" << endl;
                    rightOk = true;
                }
            }
            
            // provjeri zeli li lijevi ili desni filozof uci u ko ukoliko ti i dalje nemas pravo
            if(!leftOk || !rightOk){
                mesg_buffer message7;
                int recvRet = msgrcv(msgids[myLeftRequest], &message7, sizeof(message7), 0, IPC_NOWAIT); // ima li zahtjev od lijevog filozofa
                if(recvRet != -1) { // ima zahtjeva, odgovori mu
                    int lokalniSatZahtjeva = message7.mesg_text[0] - '0';

                    if(lokalniSatZahtjeva < localClock){
                        //cout << "Filozof " << id << " odgovara lijevom filozofu" << endl;
                        mesg_buffer message={1, "n"};
                        msgsnd(msgids[leftNResponse], &message, sizeof(message), 0);
                    }else if(localClock == lokalniSatZahtjeva && leftN < id){ // ako je lokalni sat isti onda pusti onog koji ima manji indeks
                        //cout << "Filozof " << id << " odgovara lijevom filozofu" << endl;
                        mesg_buffer message={1, "n"};
                        msgsnd(msgids[leftNResponse], &message, sizeof(message), 0);
                    }else{
                        //cout << "Filozof " << id << " je spremio zahtjev od lijevof filozofa" << endl;
                        leftPending = true;
                    }
                }   

                mesg_buffer message10;
                int recvRet1 = msgrcv(msgids[myRightRequest], &message10, sizeof(message10), 0, IPC_NOWAIT); // ima li zahtjev od lijevog filozofa
                if(recvRet1 != -1) { // ima zahtjeva, odgovori mu
                    int lokalniSatZahtjeva = message10.mesg_text[0] - '0';
                    ////cout << "Lokalni sat filozofa " << lokalniSatZahtjeva << endl;

                    if(lokalniSatZahtjeva < localClock){
                        //cout << "Filozof " << id << " odgovara desnom filozofu" << endl;
                        mesg_buffer message={1, "n"};
                        msgsnd(msgids[rightNResponse], &message, sizeof(message), 0);
                    }else if(localClock == lokalniSatZahtjeva && rightN < id){ // ako je lokalni sat isti onda pusti onog koji ima manji indeks
                        //cout << "Filozof " << id << " odgovara desnom filozofu" << endl;
                        mesg_buffer message={1, "n"};
                        msgsnd(msgids[rightNResponse], &message, sizeof(message), 0);
                    }else{
                        //cout << "Filozof " << id << " je spremio zahtjev od desnog filozofa" << endl;
                        rightPending = true;
                    }

                }
            }   
        }

        // udi u ko
        int sleep = (rand() % 1900) + 100;
        cout << "Filozof " << id << " jede." << endl;
        usleep(sleep * 1000);
        cout << "Filozof " << id << " gotov s jelom." << endl;

        // check if there are some requests waiting
        //cout << "Filozof " << id<< " provjerava zahtjeve" << endl;
        if(leftPending) {
            //cout << "Filozof " << id<< " salje odgovor lijevom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[leftNResponse], &message, sizeof(message), 0);
        }
        if(rightPending) {
            //cout << "Filozof " << id<< " salje odgovor desnom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[rightNResponse], &message, sizeof(message), 0);
        }

        mesg_buffer message8;
        recvRet = msgrcv(msgids[myLeftRequest], &message8, sizeof(message8), 0, IPC_NOWAIT); // ima li zahtjev od lijevog filozofa
        if(recvRet != -1) { // ima zahtjeva, odgovori mu
            //cout << "Filozof " << id<< " salje odgovor lijevom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[leftNResponse], &message, sizeof(message), 0);
        }

        mesg_buffer message9;
        recvRet = msgrcv(msgids[myRightRequest], &message9, sizeof(message9), 0, IPC_NOWAIT); // ima li zahtjev od desnog filozofa
        if(recvRet != -1) { // ima zahtjeva, odgovori mu
            //cout << "Filozof " << id<< " salje odgovor desnom filozofu" << endl;
            mesg_buffer message={1, "n"};
            msgsnd(msgids[rightNResponse], &message, sizeof(message), 0);
        }

        localClock = (localClock + 1) % 10;
    }

}

int main(){
    int key1;
    int msgids[20];
    int keys[20];
    int pid;
    key1 = getuid();
    keys[0] = key1;
    for(int i = 1; i < 20; i++){
        keys[i] = keys[i-1] + 1;
    } 

    // brisi red poruka
    for(int i = 0; i < 20; i++){
        msgctl(keys[i], IPC_RMID, NULL);
    }

    // brisi red poruka
    for(int i = 0; i < 20; i++){
        msgctl(keys[i], IPC_RMID, NULL);
        msgids[i] = msgget(keys[i], 0666 | IPC_CREAT); // ulaz na vrtuljak
    }

    for(int i = 0; i < 5; i++){
        switch (fork()) {
            case -1: /* dijete nije kreirano*/
                cout << "Ne mogu stvoriti " << i << ". proces" << endl;
                exit(1);

            case 0: //dretva filozof
                dretvaFilozof(i, msgids);
                exit(0);
        }
    }
    for(int i = 0; i < 5; i++){
        wait(NULL);
    }
    return 0;
}