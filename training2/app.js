const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const mongoose = require("mongoose");

const routes = require("./routes");

require("dotenv").config();

const server = express();

server.use(bodyParser.json());
server.use(cors());

mongoose.connect(
  process.env.DB_URI,
  {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  },
  () => console.log("connected to DB")
);

server.use("/", routes);

// server.use(
//   "/users",
//   (req, res, next) => {
//     if (req.method !== "GET") {
//       res.status(401).json({
//         success: false,
//       });
//     }

//     next();
//   },
//   (req, res) => {
//     res.json({
//       success: true,
//     });
//   }
// );

server.listen(
  process.env.PORT,
  console.log(`server started at port ${process.env.PORT}`)
);
