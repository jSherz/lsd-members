import { sign } from "jsonwebtoken";

export const createJwt = () => {
  const secret = process.env.JWT_SECRET || "apples";

  const options = {
    algorithm: "HS384",
    expiresIn: "1h"
  };

  return sign(
    {
      iss: "LSD",
      UUID: "5f89a942-2704-4442-9d68-f30408b51ca1"
    },
    secret,
    options
  );
};
