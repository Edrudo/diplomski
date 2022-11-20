function f(v) {
    let sum = 0;
    v.forEach(a => sum += a * a);
    return sum;
}

let x = f([1, 2, 3]);
console.log(x);
x = f(["1", "2", "3"]);
console.log(x);
x = f("abc");