export default class Observer {
    constructor(dataObject, listener) {
        this.observersSet = [];
        if (listener) this.observersSet.push(listener);

        let self = this;
        this.proxyObject = new Proxy(dataObject, {
            set: (target, key, value, receiver) => {
                console.log("Proxy set: ", target, key, value, receiver);
                const result = Reflect.set(target, key, value, receiver);
                self.dataObjectClone = {...dataObject};  // used in isDirty()
                self.observersSet.forEach(observer => observer(key, value));
                return result;
            }
        });
    }
    getProxy() {
        return this.proxyObject;
    }
    isDirty() { // poor man's micro-optimization
        for (const key in this.dataObjectClone) {
            console.log(this.dataObjectClone[key], "!==",  this.proxyObject[key]);
            if (this.dataObjectClone[key] !== this.proxyObject[key]) {
                return true;
            }
        }
        return false;
    }
}

// test
// SCENARIO 1:

let obj = {
    x: 1,
    y : 2
}

let foo = new Observer(obj, (key, value) => {
    console.log("Changed (key, value): ", key, value);
});

obj.x = 20;
obj.y = 21; 

foo.getProxy().x = 10;
foo.getProxy().y = 11;

console.log("isDirty = ", foo.isDirty());

// SCENARIO 2:
// change y when x changes - what happens to isDirty?

foo = new Observer(obj, (key, value) => {
    if (key === "x") obj.y = 2 * value;
    console.log("Changed (key, value): ", key, value);
});

foo.getProxy().x = 10;
console.log("isDirty = ", foo.isDirty());

// so, we will use isDirty to re-render the page bcs some other (dependant) value changed (except the one that initiated the change)


