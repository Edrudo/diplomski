import  Vector from "./vector"
import  {IHasNorm, IComparable} from "./interfaces"

function ToSortedArray<T extends IComparable<T>>(...items : T[]) : T[]{
    let arr = [...items];
    arr.sort((a, b) => a.compare(b));
    return arr;
}

let a = new Vector(2, 3, 5);
console.log(a);
console.log(`a=${a}`); //calls a.toString()
let b = new Vector([-1, 4, 6]);
console.log(`b=${b}`);

let c = a.cross(b);
console.log(`c=${c}`);
console.log(`|c|=${c.norm}`);

let s = a.scalar(c);
console.log(s);

s = b.scalar(c);
console.log(s);

s = a.scalar(b);
console.log(s);

let normed : IHasNorm = new Vector(a);
console.log(normed.toString());

normed = new Vector({x : 7, y : 9, z : 13});
console.log(normed);
console.log(normed.norm);

let arr = ToSortedArray(a, b, c); //but not normed!
console.log(arr);
