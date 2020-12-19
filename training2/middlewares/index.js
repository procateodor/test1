const { decrypt } = require("../utils");

const requireAuth = (req, res, next) => {
  try {
    const token =
      req.headers.authorization && req.headers.authorization.split(" ")[1];

    if (!token) {
      res.status(401).json({
        success: false,
      });
    }

    const claims = decrypt(token);

    req.user = claims;
    next();
  } catch (error) {
    res.status(500).json({
      success: false,
    });
  }
};

const payloadValidation = (schema) => async (req, res, next) => {
  try {
    const value = schema.validate(req.body);

    if (value.error) {
      res.status(400).json({
        success: false,
        message: value.error,
      });
    }

    next();
  } catch (error) {
    res.status(500).json({
      success: false,
    });
  }
};

module.exports = {
  requireAuth,
  payloadValidation,
};
