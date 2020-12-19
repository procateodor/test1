const { StatusCodes } = require("http-status-codes");
const { UserModel } = require("../models");

const getUser = async (req, res) => {
  try {
    const { userId } = req.user;

    const user = await UserModel.findOne({
      _id: userId,
    });

    if (!user) {
      res.status(StatusCodes.NOT_FOUND).json({
        success: false,
        message: "user not found",
      });
    }

    return res.status(StatusCodes.OK).json({
      success: true,
      user,
    });
  } catch (error) {
    console.error(error);
    return res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Something bad happened",
    });
  }
};

module.exports = {
  getUser,
};
