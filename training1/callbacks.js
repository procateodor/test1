function readFile(path, cb) {
  try {
    const data = {};
    cb(null, data);
  } catch (error) {
    cb(error);
  }
}

function callback(error, data) {
  if (error) {
    console.error(error);
    return;
  }

  console.log(data);
}

readFile("/main.js", callback);
