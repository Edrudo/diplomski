import {Car, Owner, spottedCars, knownOwners} from "./data"

type Registration = Car | Owner;
type FullData = Car & Owner; 

function isCar(r : Registration) : r is Car {
    return "brand" in r; // && other necessery checks
}

function registrationSet(...registrations : Registration[]) : Set<Registration> {
    const set = new Set<Registration>();
    registrations.forEach(r => {        
        set.add(r);
        //what we have here?        
        //if ("brand" in r) 
        if (isCar(r))
            console.log(`${r.registration} from Car ${r.model}`);
        else  
            console.log(`${r.registration} from Owner ${r.name}`);
    });
    return set;
}

function spottedCarsDetails(cars: Car[], owners : Owner[]) : FullData[] {
    const defaults : {name:string, sex : 'M' | 'F'} = { name: "Unknown", sex: "M"};
    return cars.map(c => ({ ...c,
        ...owners.find(o => o.registration === c.registration) || { ...defaults, id: c.registration } }));
}

const cars = spottedCars();
const owners = knownOwners();
const set = registrationSet(...cars, ...owners);
console.log("----------------");
const full = spottedCarsDetails(cars, owners);
full.forEach(f => console.log(f));



