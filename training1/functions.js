function name(a, b, c) {}

const func = (a, b, c) => {};

class MyClass {
  myprop = 5;

  // constructor(a, b) {
  //   this.name = this.name.bind(this);
  // }

  name(params) {
    console.log(this.myprop);
  }

  func = () => {
    console.log(this.myprop);
  };
}

function MyClass2(a, b) {
  this.myprop = 5;
}

const myc = new MyClass();
myc.name()

const a = myc.func;

a()