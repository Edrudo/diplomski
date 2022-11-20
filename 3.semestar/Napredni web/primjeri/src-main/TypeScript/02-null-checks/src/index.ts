function f(v : number[]) {
    let sum = 0;
    v.forEach(a => sum += a * a);
    return sum;
}

let x = f([1, 2, 3]);
let v : number[];
f(v);
//x = f(["1", "2", "3"]); //syntax error
//x = f("abc"); //syntax error