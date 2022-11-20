function shuffle(data : string) : string;
function shuffle(data : any[]) : any[];

function shuffle(data : string | any[]) : string | any[]  {
    let arr = [...data];
        
    for (let i = arr.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        const temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    return typeof(data) == "string" ?  arr.join('') :  arr;
}

let word = "ABCDEFghijkl";
let numbers = [1, 2, 3, 4, 5, 6];

let shuffledWord = shuffle(word);
let shuffledNumbers = shuffle(numbers);

console.log(shuffledWord);
console.log(shuffledNumbers);





