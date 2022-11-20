const enum CarShape {
    Sedan, Coupe, StationWagon, Hatchback, SUV, Other
}

type Car = {
    registration: string;
    brand: string;
    model: string;    
    shape: CarShape;    
}

type Owner = {
    name: string;
    registration: string;
    sex: 'M' | 'F'
}

function spottedCars() : Car[] {    
    return [
        { registration : 'ZG-243-AA', brand: 'VW', model: 'Golf', shape: CarShape.Hatchback },
        { registration : 'ŠI-111-C', brand: 'Toyota', model: 'Hilux', shape: CarShape.Other },
        { registration : 'ST-508-P', brand: 'Peugeot', model: '508 SW',  shape: CarShape.StationWagon },
        { registration : 'DA-123-AG', brand: 'Audi', model: 'A6',  shape: CarShape.Sedan },
        { registration : 'VT-906-TR', brand: 'BMW', model: '320', shape: CarShape.Sedan },
        { registration : 'ZG-1234-A', brand: 'Honda', model: 'Prelude', shape: CarShape.Coupe },
        { registration : 'KC-722-HA', brand: 'Mazda', model: 'CX5', shape: CarShape.SUV  }
    ];
}

function knownOwners() : Owner[] {
    return [
        { registration : 'ZG-243-AA', name : "Ana Anić", sex : "F" },
        { registration : 'ŠI-111-C', name : "Renata Horvat", sex : "F"  },
        { registration : 'ST-508-P', name : "Marin Marić", sex : "M"  },
        { registration : 'DA-123-AG',name : "Vinko Topić", sex : "M"  },
        { registration : 'ZG-6565-E',name : "Matilda Brlić", sex : "F"  }
    ];
}

export {Car, Owner, knownOwners, spottedCars}