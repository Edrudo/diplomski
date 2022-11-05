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

#include <stdio.h>
#include <string.h>
#define MAXREAD 20/* najveća duljina poruke*/
int main(void)
{

int pfd[2];
char buf[MAXREAD] = "";
char message[] = "Kroz cijev!";/* poruka*/
if (pipe(pfd) == -1)/* stvaranje cjevovoda*/

exit(1);

switch (fork()) {

case -1: /* dijete nije kreirano*/

exit(1);

case 0:/* dijete čita */

close(pfd[1]);/* zato zatvara kraj za pisanje*/
(void) read(pfd[0], buf, MAXREAD);
puts(buf);
exit(0);

default:/* roditelj piše */

close(pfd[0]);/* zato zatvara kraj za čitanje*/
        //(void) write(pfd[1], message, strlen(message) + 1);
wait(NULL);/* roditelj čeka da dijete završi*/

}
exit(0);/* zatvara sve deskriptore */

}