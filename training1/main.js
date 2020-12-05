const a = {
  key1: "asd",
  key2: 24,
};

const { key2: newkey2 } = a;

// const b = {
//   ...a,
//   ...c,
//   ...d, 
//   key1: 'new value'
// };

const person = {
  name: "teo",
  age: 5,
  height: 150,
};

const newPerson = {
  ...person,
  age: 15,
};

const arr = [1, 2, "3", [4, 5]];

const newArr = [1, 2, 3, [4, 5]];
