import { Request, Response, NextFunction } from 'express';
import jwt, { JwtPayload } from 'jsonwebtoken'
import dotenv from 'dotenv'
dotenv.config();

type User = {
  username : string,
  isAdmin : boolean
}

//using additional type allows us to add something is User that is not part of the token
type TokenData = {
  username : string,
  role? : string
}
function containsTokenData(data : string | JwtPayload) : data is TokenData {
  return typeof data !== 'string' && 'username' in data;
}

//user(information) should be set using setUserInfo method below
declare global {
  namespace Express {
      export interface Request {
          user? : User
      }
  }
}

declare global {
  namespace NodeJS {
    interface ProcessEnv {
      TOKEN_KEY : string
    }
  }
}

function setUserInfo(req : Request, res : Response, next : NextFunction) {  
  const bearerToken = req.headers.authorization?.replace(/^Bearer /, '');   
  if (bearerToken) {    
    try {         
      const token = jwt.verify(bearerToken, process.env.TOKEN_KEY);    
      if (containsTokenData(token)) {          
        req.user = { 
          username : token.username, 
          isAdmin : token.role === 'admin'
        }       
      }   
    } 
    catch (err) {         
      const message = err instanceof jwt.TokenExpiredError ? "Expired token" : "Invalid token";
      return res.status(401).send(message);      
    }
  }
  next();
}

function requiresAuthentication(req : Request, res : Response, next : NextFunction)  {
  if (req.user) next();
  else {
    res.writeHead(401, { 'WWW-Authenticate': 'Bearer'});    
    res.end('Authentication is needed');
  }  
}


function createToken(payload : object) : string {
    return jwt.sign(payload, process.env.TOKEN_KEY, { expiresIn : "2m"}); //2 minutes
}

export {setUserInfo, createToken, requiresAuthentication, TokenData };