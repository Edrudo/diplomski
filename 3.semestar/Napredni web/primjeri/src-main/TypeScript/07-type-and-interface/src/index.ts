interface Vector { x:number; y:number; z:number; }
type vector = { x:number; y:number; z:number; };

function t_print(v: vector) {
    console.log(v);
}

function i_print(v: Vector) {
    console.log(v);
    //console.log(`(${v.x}, ${v.y}, ${v.z})`);
}

function a_print(v: {x:number, y:number, z:number}) {
    console.log(v);
    //console.log(`(${v.x}, ${v.y}, ${v.z})`);
}

let iv : Vector = {x : 3, y : 5, z : 2};
let tv : vector = {x : 1, y : 2, z : 3};
t_print(iv);
i_print(tv);
a_print(iv);

let temp = iv;
iv = tv;
tv = temp;

let w = {x : 9, y : 8, z : 7, w : 6};
iv = w;
i_print(iv);

