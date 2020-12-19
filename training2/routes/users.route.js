const router = require("express").Router();

const { usersController } = require("../controllers");
const { requireAuth } = require("../middlewares");

router.get("/", requireAuth, usersController.getUser);

module.exports = router;
