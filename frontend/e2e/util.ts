import {sign} from 'jsonwebtoken';

export const createJwt = () => {
  const secret = process.env.JWT_SECRET || 'apples';

  const expires = new Date();
  expires.setDate((new Date()).getDate() + 1);

  return sign({
    iss: 'LSD',
    UUID: 'f5e0e6f1-1786-4702-b2b0-800774e5021b',
    exp: expires.getTime(),
    iat: new Date().getTime()
  }, secret, {algorithm: 'HS384'});
};
