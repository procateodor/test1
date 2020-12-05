const http = require("http");

const server = http.createServer((req, res) => {
  switch (req.url) {
    case "/":
      switch (req.method) {
        case "GET":
          res.setHeader("Content-Type", "text/html");
          res.end(`
          <html>
          <body>
          <h1>asdasd</h1>
          </body>
          </html>
          `);
          break;

        case "POST":
          res.setHeader("Content-Type", "application/json");

          let body = "";

          req.on("data", (data) => {
            body += data;
          });

          req.on("end", () => {
            res.statusCode = 201;
            res.end(body);
          });
          break;

        default:
          break;
      }
      break;

    case "/users":
      res.setHeader("Content-Type", "application/json");
      res.end(
        JSON.stringify({
          data: [],
        })
      );
      break;

    default:
      break;
  }
});

server.listen(8000, () => console.log("server started at port 8000"));
