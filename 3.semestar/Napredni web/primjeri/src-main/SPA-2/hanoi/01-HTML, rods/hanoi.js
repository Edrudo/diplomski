

var app = new Vue({
    el: '#app',
    data: {
        diskNumber: 3,
        positionA: [1, 2, 3],
        positionB: [],
        positionC: [],
    },
    methods: {
        rodStyle(diskSize){
            return {
                width: (diskSize / this.diskNumber * 60 + 20) + '%',
                backgroundColor: this.getRodColor(diskSize)
            }
        },
        getRodColor(diskSize){
            const colors = ['green', 'red', 'blue', 'orange', 'yellow', 'purple'];
            return colors[diskSize % colors.length];
        }
    }
})