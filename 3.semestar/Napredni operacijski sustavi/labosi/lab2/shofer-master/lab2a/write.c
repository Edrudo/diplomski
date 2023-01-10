#include <poll.h>
#include <fcntl.h>
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>

/*
Pisi u nasumicno odabrane naprave periodicki.
*/

#define errExit(msg)        \
    do                      \
    {                       \
        perror(msg);        \
        exit(EXIT_FAILURE); \
    } while (0)

int main(int argc, char *argv[]) {

    if (argc < 2) {
        fprintf(stderr, "Usage: %s file...\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    int nfds, num_open_fds;
    struct pollfd *pfds;


    nfds = argc - 1;
    pfds = calloc(nfds, sizeof(struct pollfd));
    if (pfds == NULL)
        errExit("malloc");

    /* Open each file on command line, and add it 'pfds' array. */

    for (int j = 0; j < nfds; j++)
    {
        pfds[j].fd = open(argv[j + 1], O_WRONLY);
        if (pfds[j].fd == -1)
            errExit("open");

        printf("Opened %s on fd %d\n", argv[j + 1], pfds[j].fd);

        pfds[j].events = POLLIN;
    }

    srand((unsigned) time(NULL));


    //napravi polje razlicitih znakova i svaki put salji sljedeci znak
    char znak[11] = "abcdefghij";
    //write forever
    int i = 0;
    while(1) {

        //take random device
        int random = ( rand() % nfds );
        //which is ready to take data
        if (pfds[random].revents != 0) {
            printf("Device %d is not ready to take data.\n", pfds[random].fd);
            continue;
        }


        printf("About to write to a device.\n");

        //write to a device
        if (write(pfds[random].fd, &znak[i], 1) == -1) {
            errExit("write");
        }
        i = (i+1) % 10;

        printf("Wrote to a device. Sleep for 5 sec...\n");
        sleep(5);
    }

}
