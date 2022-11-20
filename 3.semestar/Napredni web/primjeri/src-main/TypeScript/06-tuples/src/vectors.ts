type vector = [number, number, number];
function norm(v : vector) : number  {    
    let sum = 0;
    v.forEach(a => sum += a * a);
    return Math.sqrt(sum);
}

function scalar(a : vector, b : vector) : number {          
    let val = 0;
    for(let i=0; i<a.length; i++) {
        val += a[i]! * b[i];
    }
    return val;
}

function cross(a : vector, b : vector) : vector {    
   let x = a[1] * b[2] - a[2] * b[1];
   let y = a[2] * b[0] - a[0] * b[2];
   let z = a[0] * b[1] - a[1] * b[0];
   return [x, y, z];
}

export {vector, norm, scalar, cross}
