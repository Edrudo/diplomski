#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h> 
#include <sys/types.h> 
#include <unistd.h>
#include <iostream>
#include <cstring>
using namespace std;

const int CHILDREN_NUMBER = 8;
const int DRIVE_OVER = 10;

struct mesg_buffer {
    long mesg_type;
    char mesg_text[2];
} message;
  

void dretvaPosjetitelj(int i, int msgidV, int msgidP, int msgidE, int pid) {
    srand(getpid() * time(NULL));
    for(int j = 0; j < 3; j++){
        // sleeping time
        int sleep = rand() % 1900 + 100;
        usleep(sleep* 1000); 
        //cout << "Proces " << i << " spava " << sleep << endl;

        // cekaj znacku
        mesg_buffer message5; 
        msgrcv(msgidP, &message5, sizeof(message5), 0, 0);

        // sjedni i dojavi vrtuljku
        cout << "Sjeo posjetitelj " << i << endl;
        mesg_buffer message2={1, "n"};
        msgsnd(msgidV, &message2, sizeof(message2), 0);


        // cekaj kraj voznje
        mesg_buffer message6;
        msgrcv(msgidE, &message6, sizeof(message6), 0, 0);
        cout << "Sisao posjetitelj " << i << endl;

        mesg_buffer message4 = {1, "3"};
        msgsnd(msgidP, &message4, sizeof(message4), 0); // slanje znacke dalje u red
    }
    cout << "Posjetitelj " << i << " zavrsio" << endl;
};

 
void dretvaVrtuljak(int msgidV, int msgidP, int msgidE) {
    srand(getpid() * time(NULL));
    mesg_buffer message = {1, "3"};
    // slanje znacke u red
    for(int i = 0; i < 4; i++){
        //cout << "Proces vrtuljak salje poruku u red " << msgidP << endl;
        msgsnd(msgidP, &message, sizeof(message), 0);
    }

    for(int i = 0; i < 6; i++){ // dok ima posjetitelja
        //cekaj 4 posjetitelja
        for(int i = 0; i < 4; i++){
            mesg_buffer message1;
            msgrcv(msgidV, &message1, sizeof(message1), 0, 0);
            //cout << "Vrtuljak primio poruku za sjest" << endl;
        }

        cout << "Pokrenuo vrtuljak" << endl;

        //spavanje
        int sleep = (rand() % 2000) + 1000;
        usleep(sleep * 1000);

        cout << "Vrtuljak zaustavljen" << endl;

        message.mesg_type = 1; // 10 oznacava kraj voznje
        for(int i = 0; i < 4; i++){
            //cout << "Proces " << i << "salje poruku u red " << msgidE << endl;
            msgsnd(msgidE, &message, sizeof(message), 0);
        }
    }


};

int main(){
    int key1;
    int key2;
    int key3;
    int msgid1;
    int msgid2;
    int msgid3;
    int pid;
    key1 = getuid();
    key2 = key1+1;
    key3 = key1+2;

    // brisi red poruka
    msgctl(key1, IPC_RMID, NULL);
    msgctl(key2, IPC_RMID, NULL);
    msgctl(key3, IPC_RMID, NULL);
    
    msgid1 = msgget(key1, 0666 | IPC_CREAT); // ulaz na vrtuljak
    msgid2 = msgget(key2, 0666 | IPC_CREAT); // znacka
    msgid3 = msgget(key3, 0666 | IPC_CREAT); // izlaz s vrtuljka

    cout << "msgidV: " << msgid1 << ", msgidP: " << msgid2 << ", msgidE: " << msgid3 << endl;
    
    for(int i = 1; i <= CHILDREN_NUMBER; i++){
        switch(pid = fork()){
            case -1:    cout << "Ne mogu stvoriti " << i << ". proces" << endl;
                        exit(1);
            case 0:     dretvaPosjetitelj(i, msgid1, msgid2, msgid3, pid);
                        exit (0);
        }
    }


    dretvaVrtuljak(msgid1, msgid2, msgid3);
    return 0;
}