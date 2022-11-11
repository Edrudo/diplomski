#include <stdio.h>
#include <semaphore.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/shm.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <iostream>
#include <stdlib.h>

using namespace std;


#define BROJ_PUSACA 3
sem_t *KO;
sem_t *p1;
sem_t *p2;
sem_t *p3;
sem_t *stol_prazan;
char *proizvodi[] = {"papir", "duhan", "sibice"};
int *proizvod1;
int *proizvod2;
bool *kraj;

void trgovac(int id){
    while(*kraj){

        *proizvod1 = rand() % 3;
        *proizvod2 = rand() % 3;
        while(*proizvod1 == *proizvod2){
            *proizvod2 = rand() % 3;
        }
        sem_wait(KO);
        cout << "Trgovac postavlja: " << proizvodi[*proizvod1] << " i " << proizvodi[*proizvod2] << endl;
        sem_post(KO);
        sem_post(p1);
        sem_post(p2);
        sem_post(p3);
        sem_wait(stol_prazan);
    }
    sem_post(KO);
    sem_post(p1);
    sem_post(p2);
    sem_post(p3);

}

void pusac(int proizvod){
    while(*kraj){
        sem_wait(p1 + proizvod);
        sem_wait(KO);
        if(*proizvod1 != proizvod && *proizvod2 != proizvod){
            sem_post(KO);
            cout << "Pusac " << proizvod + 1 << " uzima sastojke i ..." << endl;
            cout << endl;
            usleep(1000); //smota, zapali, pusi
            sem_post(stol_prazan);
        }
        else{
            sem_post(KO);
        }
    }

    sem_post(stol_prazan);
    sem_post(KO);

}

void signalInt(int i){
    *kraj = false;
}

int main(){
    srand (time(NULL));
    int ID;
    pid_t pid;
    sigset(SIGINT, signalInt);

	ID = shmget (IPC_PRIVATE, sizeof(sem_t)* 4 + 2 * sizeof(int) + sizeof(bool), 0600);
	KO = (sem_t *) shmat(ID, NULL, 0);
    shmctl(ID, IPC_RMID, NULL);

    p1 = (sem_t *) (KO + 1);
    p2 = (sem_t *) (KO + 2);
    p3 = (sem_t *) (KO + 3);
    stol_prazan = (sem_t *) (KO + 4);
    proizvod1 = (int *) (KO + 5);
    proizvod2 = (int *) (KO + 6);

    kraj = (bool *)(KO + 7);
    *kraj = true;

	sem_init(KO, 1, 1);
	sem_init(p1, 1, 0);
	sem_init(p2, 1, 0);
	sem_init(p3, 1, 0);
	sem_init(stol_prazan, 1, 0);

    for(int i = 0; i < 3; i++){
        switch(pid = fork()){
            case -1: cout << "Ne mogu stvoriti " << i << ". pusaca" << endl;
                     exit (1);
            case 0: cout << "Pusac " << i + 1 << ": ima " <<  proizvodi[i] << endl; 
                    pusac(i);
                    exit (0);
           
        }
    }
    sleep(1);
    switch(pid = fork()){
            case -1: cout << "Ne mogu stvoriti trgovca" << endl;
                     exit (1);
            case 0: trgovac(3);
                    exit (0);
           
        }
    for(int i = 0; i < 4; i++)
     wait(NULL);


    sem_destroy(KO);
    sem_destroy(p1);
    sem_destroy(p2);
    sem_destroy(p3);
    sem_destroy(stol_prazan);
	shmdt(KO);

	return 0;
}