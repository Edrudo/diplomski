

var app = new Vue({
    el: '#app',
    data: {
        diskNumber: 3,
        positionA: [1, 2, 3],
        positionB: [],
        positionC: [],
        selectedDisk: null,
        selectedRod: null,
        logMessages: [],
        moves: 0
    },
    computed: {
        isGameOver: function() {
            return this.positionB.length === this.diskNumber 
                || this.positionC.length === this.diskNumber
        }
    },
    methods: {
        rodStyle(disk){
            return {
                width: (disk / this.diskNumber * 60 + 20) + '%',
                backgroundColor: this.getRodColor(disk)
            }
        },
        getRodColor(diskSize){
            const colors = ['green', 'red', 'blue', 'orange', 'yellow', 'purple'];
            return colors[diskSize % colors.length];
        },
        onSelectFrom(disk, rod) {
            if ( 
                (this.positionA[0] === disk)
                || (this.positionB[0] === disk)
                || (this.positionC[0] === disk)
            ) {
                this.selectedDisk = disk;
                this.selectedRod = rod;
                this.log("Selected disk: " + this.selectedDisk + " at rod " + this.selectedRod);
            } else {
               this.log("You can only select the topmost disk.");
            }
        },
        onSelectTo(rod) {
            if (this.selectedDisk && rod !== this.selectedRod) {
                let positionABC = [this.positionA, this.positionB, this.positionC];
                let idxTo = rod.charCodeAt(0) - 'A'.charCodeAt(0);
                let rodToArray = positionABC[idxTo];
                let idxFrom = this.selectedRod.charCodeAt(0) - 'A'.charCodeAt(0);
                let rodFromArray = positionABC[idxFrom];

                if ( 
                    rodToArray.length
                    && rodToArray[0] < this.selectedDisk
                ) {
                    this.log("No disk may be placed on top of a disk that is smaller than it.");
                } else {
                    rodFromArray.shift();
                    rodToArray.unshift(this.selectedDisk);
                    this.log("Moved disk " + this.selectedDisk + " from " + this.selectedRod + " to " + rod);
                    this.selectedRod = null;
                    this.selectedDisk = null;
                    this.moves++;
                }
            }            
        },
        log(msg) {
            this.logMessages.unshift({
                ts: (new Date()).toLocaleString('hr-HR'),
                text: msg
            });
        }
    }
})