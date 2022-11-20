let obj = {
  name: "Milivoj",
  age: 45,
};

let pObj = new Proxy(obj, {
  get: function (target, prop, receiver) {
      console.log("Netko želi znati ", prop);
    return target[prop];
  },
  set: function (obj, prop, value) {
    console.log("Netko postavlja ", prop, "na", value);
    if (prop === "age") {
      if (!Number.isInteger(value)) {
        throw new TypeError("Godine moraju biti cijeli broj!");
      }
    }
    obj[prop] = value;
    return true; // Indicate success
  },
});

console.log(obj.name, obj.age);
console.log(pObj.name, pObj.age);
try {
    pObj.age = "33a";    
} catch (error) {
    console.error(error);
}

pObj.age = 33;
console.log(obj);