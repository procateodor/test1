const router = require("express").Router();

const { authController } = require("../controllers");
const { payloadValidation } = require("../middlewares");
const { auth } = require("../schemas");

router.post("/register", payloadValidation(auth), authController.register);
router.post("/login", payloadValidation(auth), authController.login);

module.exports = router;
