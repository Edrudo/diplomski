#include <stdio.h>

#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>
#include<unistd.h>

#include <stdio.h>

#include<time.h>

#include <stdio.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#define MAX 10


// structure for message queue
struct mesg_buffer {
    long mesg_type;
    int mesg_text;
} message;

void trgovac() {
    int n1;
    int n2;
    int sastojakKojiFali;

    key_t key;
    int msgid;

    printf("krece trgovac \n");
    // sleep(1);

    while(1) {
        srand(time(0));
        n1=rand()%3;
        n2=rand()%3;
        while(n1==n2)
        {
            n2=rand()%3;
        }

        // n1 i n2 predstavljaju sto trgovac ima
        // ako su n1 i n2 jednaki 2 i 3, salje pusacu koji ima papir

        if (n1 + n2 == 1) { // fali 2
            printf("ide papir i duhan \n");
            sastojakKojiFali = 2;
        }

        if (n1 + n2 == 2) { // fali 1
            printf("idu papir i sibice \n");
            sastojakKojiFali = 1;
        }

        if (n1 + n2 == 3) { // fali 0
            printf("idu duhan i sibice \n");
            sastojakKojiFali = 0;
        }

        message.mesg_text = sastojakKojiFali;
        message.mesg_type = 0;

        printf("trgovac salje da fali sastojak: %d \n", message.mesg_text);


        // key = ftok(0, 65);
        key = 0; // slanje u topic 0, tamo komuniciraju trgovac i nulti proces
        msgid = msgget(key, 0666 | IPC_CREAT);
        msgsnd(msgid, &message, sizeof(message), 0);

        printf("trgovac poslao da fali sastojak: %d \n", message.mesg_text);

        // key = ftok(3, 65);
        key = 3; // zaprimanje odgovora od procesa broj 2, komuniciraju u topicu broj 3
        msgid = msgget(key, 0666 | IPC_CREAT);
        msgrcv(msgid, &message, sizeof(message), 1, 0);

        // display the message
        printf("trgovac uspjesno zaprimio od procesa 2 da je falio sastonak: %d \n", message.mesg_text);

        sleep(2);
    }

}

// svak ima svoj queue, svak salje svome

void pusac(int broj) {
    sleep(2);
    printf("krece pusac %d \n", broj);
    // broj predstavlja sto tocno pusac ima kod sebe
    // pusac 0 ima papir
    // pusac 1 ima duhan
    // pusac 2 ima sibice

    key_t key;
    int msgid;

    while(1) {
        // ftok to generate unique key
        // key = ftok(broj, 65); // kada je broj queue "0", to je trgovac nultom procesu
        key = broj; // kada je broj queue "0", to je trgovac nultim procesom
        msgid = msgget(key, 0666 | IPC_CREAT);

        // zaprimanje poruke
        msgrcv(msgid, &message, sizeof(message), 1, 0);

        // display the message
        printf("proces %d zaprimio da fali sastojak: %d \n", broj, message.mesg_text);


        // SLANJE ZNACKE SLJEDECEM PROCESU
        // key = ftok(broj + 1, 65);
        key = (broj + 1); // kada je queue "3", to biti topic komunikacije procesa 2 i trgovca

        msgid = msgget(key, 0666 | IPC_CREAT);
        message.mesg_type = 0;

        printf("proces %d salje iducem procesu, da fali sastojak: %d \n", broj, message.mesg_text);
        fgets(message.mesg_text, MAX, stdin);

        msgsnd(msgid, &message, sizeof(message), 0);
        printf("proces %d poslao iducem procesu, da fali sastojak: %d \n", broj, message.mesg_text);
    }
}

int main() {
    int pid;

    // 2 forka stvaraju 4 procesa, naj parnet je trgovac ostali su pusaci
    for(int i = 0; i <= 2; i++){
        pid = fork();
        // error
        if (pid == -1) {
            exit(1);
        }

        // nije error, bit ce pusac
        if (pid == 0) {
            pusac(i);
            exit(0);
        }
    }

    trgovac();
    return 0;
}