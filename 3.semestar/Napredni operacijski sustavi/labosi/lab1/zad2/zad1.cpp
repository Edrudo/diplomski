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

const int BROJ_PUSACA = 3;
const int PAPIR = 0;
const int DUHAN = 1;
const int SIBICE = 2;

char PROIZVODI[3][10] = {"papir", "duhan", "sibice"};

struct mesg_buffer {
    long mesg_type;
    char mesg_text[3];
};
  
// argumenti: broj pusaca, red trgovca, 
void dretvaPusac(int pusacIndex, int* redovi, int stol_id) {
    int mojRed = pusacIndex;
    int sljedeci = mojRed + 1;
    int trgovac = 3;
    int proizvod1;
    int proizvod2;
    int *stol;

    mesg_buffer messagePusac = {1, "12"};

    while(true){
        // cekaj znacku
        msgrcv(redovi[mojRed], &messagePusac, sizeof(messagePusac), 0, 0);

        // uzmi sastojke i provjeri trebaju li mi
        cout << "Pusac " << pusacIndex << " provjerava sastojke" << endl;

        //stol = (int*) shmat(stol_id, NULL, 0);
        proizvod1 = messagePusac.mesg_text[0];
        proizvod2 = messagePusac.mesg_text[1];
        cout << "Moj proizvod: " << PROIZVODI[pusacIndex] << ", trgovac: " << PROIZVODI[proizvod1] << " i " << PROIZVODI[proizvod2] << " usporedba: " << (proizvod1 != pusacIndex) << " : " << (proizvod2 != pusacIndex) << endl;
        //shmdt((void *) stol);
        sleep(1);


        // udi u KO ako su tvoji sastojci
        if(proizvod1 != pusacIndex && proizvod2 != pusacIndex){
            cout << "Pusac " << pusacIndex<< " uzima sastojke i mota cigaru" << endl;
            sleep(1); //smota, zapali, pusi
            cout << "Pusac " << pusacIndex << " je popusio cigaru" << endl;

            // salji znacku trgovcu
            cout << "Pusac " << pusacIndex << " salje zancku trgovcu" << endl;
            sleep(1);
            msgsnd(redovi[trgovac], &messagePusac, sizeof(messagePusac), 0);
        }else{
        // salji znacku dalje
            cout << "Pusac " << pusacIndex << " salje zancku dalje pusacu" << sljedeci << endl;
            sleep(1);
            messagePusac.mesg_text[0] = proizvod1;
            messagePusac.mesg_text[1] = proizvod2;
            msgsnd(redovi[sljedeci], &messagePusac, sizeof(messagePusac), 0);
        }
    }
    cout << "Pusac " << pusacIndex << " zavrsio" << endl;
};

 
void dretvaTrgovac(int* redovi, int stol_id) {
    int mojRed = 3;
    int sljedeci = 0;
    int proizvod1;
    int proizvod2;
    int *stol;

    srand(getpid() * time(NULL));
    mesg_buffer messageTrgovac = {1, "12"};

    while(true){
        sleep(1);

        // generiraj proizvode
        proizvod1 = rand() % 3;
        proizvod2 = rand() % 3;
        while(proizvod1 == proizvod2){
            proizvod2 = rand() % 3;
        }

        cout << "Trgovac postavlja: " << PROIZVODI[proizvod1] << " i " << PROIZVODI[proizvod2] << " na stol" << endl;

        /* stol = (int*) shmat(stol_id, NULL, 0);

        stol[0] = proizvod1;
        stol[1] = proizvod2; */

        messageTrgovac.mesg_text[0] = proizvod1;
        messageTrgovac.mesg_text[1] = proizvod2;
        sleep(1); // stavi na stol

        //shmdt((void *) stol);
        
        // salji znacku dalje
        msgsnd(redovi[sljedeci], &messageTrgovac, sizeof(messageTrgovac), 0);

        // cekaj znacku
        mesg_buffer messageTrgovac; 
        msgrcv(redovi[mojRed], &messageTrgovac, sizeof(messageTrgovac), 0, 0);
    }


};

int main(){
    int key1;
    int key2;
    int key3;
    int key4;

    int redTrgovac;
    int redPusac1;
    int redPusac2;
    int redPusac3;

    int stol_id;

    int pid;
    key1 = getuid();
    key2 = key1+1;
    key3 = key1+2;
    key4 = key1+3;

    int sharedMemKey = ftok("shmfile",65);
    stol_id = shmget(sharedMemKey, sizeof(int)*2, 0666|IPC_CREAT);

    // brisi red poruka
    msgctl(key1, IPC_RMID, NULL);
    msgctl(key2, IPC_RMID, NULL);
    msgctl(key3, IPC_RMID, NULL);
    msgctl(key4, IPC_RMID, NULL);
    
    redTrgovac = msgget(key1, 0666 | IPC_CREAT);
    redPusac1 = msgget(key2, 0666 | IPC_CREAT);
    redPusac2 = msgget(key3, 0666 | IPC_CREAT);
    redPusac3 = msgget(key4, 0666 | IPC_CREAT);

    int redovi[4] = { redPusac1, redPusac2, redPusac3, redTrgovac };


    for(int i = 0; i < BROJ_PUSACA; i++){
        switch(pid = fork()){
            case -1:    cout << "Ne mogu stvoriti " << i << ". proces" << endl;
                        exit(1);
            case 0:     dretvaPusac(i, redovi, stol_id);
                        exit (0);
        }
    }

    dretvaTrgovac(redovi, stol_id);
    return 0;
}