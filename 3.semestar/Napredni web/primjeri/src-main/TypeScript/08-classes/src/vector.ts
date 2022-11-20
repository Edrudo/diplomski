import {IHasNorm, IComparable} from "./interfaces"
class Vector implements IHasNorm, IComparable<Vector> {
    x:number;
    y:number;
    z:number;
    constructor(x: number, y:number, z:number)   
    constructor(vector : {x : number, y : number, z : number});
    constructor(vector: [number, number, number]);

    constructor(data : number | {x : number, y : number, z : number} | [number, number, number], ...yz : number[]) {                
        if (typeof(data) === "number") {
            console.log('init from 3 numbers');
            this.x = data;
            this.y = yz[0];
            this.z = yz[1];
        }   
        else if (Array.isArray(data)) {
            console.log('init from tuple');
            [this.x, this.y, this.z] = data;
        }
        else {            
            console.log('init from an object');
            [this.x, this.y, this.z] = [data.x, data.y, data.z];            
        }
    }

    get norm() : number  {                   
        return Math.sqrt(this.x ** 2 + this.y ** 2 + this.z ** 2);
    }  

    scalar(other : Vector) : number {    
       return this.x * other.x + this.y * other.y + this.z * other.z
    }

    cross(other : Vector) : Vector {    
        let x = this.y * other.z - this.z * other.y;
        let y = this.z * other.x - this.x * other.z;
        let z = this.x * other.y - this.y * other.x;        
        return new Vector(x, y, z);
     }

     toString() {
         return `(${this.x}, ${this.y}, ${this.z})`;
     }   

     compare(other: Vector): number {
        let r = this.x - other.y;
        if (r === 0)
            r = this.y - other.y;
        if (r === 0)
            r = this.z - other.z;
        return r;
    }
          
}

export default Vector;