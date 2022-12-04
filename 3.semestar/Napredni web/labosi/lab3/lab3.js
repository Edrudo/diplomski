var myGamePieces = new Array;

const canvasWidth = 480 * 2;
const canvasHeight = 270 * 2;

const minGamePieces = 2;
const maxGamePieces = 7;
let numGamePieces = Math.floor(Math.random() * 100 % maxGamePieces + minGamePieces);

function* enumerate(iterable) {
    let i = 0;

    for (const x of iterable) {
        yield [i, x];
        i++;
    }
}


var myGameArea = {
    canvas : document.createElement("canvas"), 
    start : function() {
        this.canvas.id = "myGameCanvas";
        this.canvas.width = canvasWidth;
        this.canvas.height = canvasHeight;
        this.context = this.canvas.getContext("2d"); 
        document.body.insertBefore(this.canvas, document.body.childNodes[0]); 
        this.frameNo = 0;
        this.interval = setInterval(updateGameArea, 20); 
        this.context.font = 'italic 18px Arial';
        this.context.textAlign = 'right';
        this.context.fillStyle = 'black';  // a color name or by using rgb/rgba/hex values
        this.context.fillText("Komponente: " + numGamePieces, 150, 50); // text and position

        this.canvas.addEventListener('click', (event) => {
            const x = event.pageX;
            const y = event.pageY;

            for(const [index, component] of enumerate(myGamePieces)) {
                if(component.checkHit(x, y)){
                  numGamePieces--;  
                  myGamePieces.splice(index, 1)
                  break;
                }
            }
        })
    },
    stop : function() {
        clearInterval(this.interval);
    },
    clear : function() {
        this.context.clearRect(0, 0, this.canvas.width, this.canvas.height); 
        this.context.fillText("Komponente: " + numGamePieces, 150, 50); // text and position
    }

}

const randomPositionX = () => Math.floor(Math.random() * canvasWidth); 

const randomPositionY = () => Math.floor(Math.random() * canvasHeight);

function randomColor(){
    let maxVal = 0xFFFFFF; // 16777215
    let randomNumber = Math.random() * maxVal; 
    randomNumber = Math.floor(randomNumber);
    randomNumber = randomNumber.toString(16);
    let randColor = randomNumber.padStart(6, 0);   
    return `#${randColor.toUpperCase()}`
}

function startGame() {
    for(let i = 0; i < numGamePieces; i++){
        myGamePieces.push(new component(60, 60, randomColor(), randomPositionX(), randomPositionY()));
    }
    myGameArea.start();
}
    
function component(width, height, color, x, y, type) {
    this.type = type; 
    this.width = width; 
    this.height = height; 
    const speed = Math.random() * 5 + 1;
    this.speed_x = speed; 
    this.speed_y = speed; 
    this.x = x;
    this.y = y;
    this.update = function() {
        ctx = myGameArea.context;
        ctx.save();
        ctx.translate(this.x, this.y);
        ctx.fillStyle = color;
        ctx.fillRect(this.width / -2, this.height / -2, this.width, this.height); 
        ctx.restore();
    }
    this.newPos = function() {
        if (this.x - this.width / 2 < 0) 
            this.speed_x *= -1;
        else if ((this.x + this.width / 2) >= myGameArea.context.canvas.width){
            this.speed_x *= -1;
            if(randomPositionX() % 2 == 0){
                this.speed_x += 0.5;
                this.speed_y += 0.5;
            }else{
                this.speed_x -= 0.5;
                this.speed_y -= 0.5;
            }
        }

        if (this.y - this.height / 2 < 0) 
            this.speed_y *= -1;
        else if ((this.y + this.height / 2) >= myGameArea.context.canvas.height){
            this.speed_y *= -1;
            if(randomPositionX() % 2 == 0){
                this.speed_x += 0.5;
                this.speed_y += 0.5;
            }else{
                this.speed_x -= 0.5;
                this.speed_y -= 0.5;
            }

        } 
        
        this.x += this.speed_x;
        this.y -= this.speed_y;
    }

    this.checkHit = function(x, y) {
        if(x >= this.x && x - this.x <= this.width && y >= this.y && y - this.y <= this.height){
            return true;
        }
        return false
    }
}

function updateGameArea() {
    myGameArea.clear();

    for(let i = 0; i < myGamePieces.length; i++){
        myGamePieces[i].newPos();
        myGamePieces[i].update(); 
    }
}