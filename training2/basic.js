const http = require("http");

const server = http.createServer((req, res) => {
  switch (req.method) {
    case 'GET':
      switch (req.url) {
        case '/users':
          
          break;
      
        default:
          break;
      }
      break;

    case 'POST':
      switch (req.url) {
        case '/users':
            let body = '';

            req.on('data', data => {
              body += data;
            })

            req.on('end', () => {
              console.log(body);
            })
          break;
      
        default:
          break;
      }
      break;
  
    default:
      break;
  }
});



server.listen(8000, () => console.info("server started"));
