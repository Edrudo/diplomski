import { Request, Response, NextFunction } from 'express';
import crypto from 'crypto'
export {setUserInfo, requiresAuthentication };

type User = {
    username : string
    //latter we can add additional properties as isAdmin, groups, ...
}

//user(information) should be set using setUserInfo method below
declare global {
    namespace Express {
        export interface Request {
            user? : User
        }
    }
}

const realm = "FER-Web2 Examples";
const hash = md5Digest(realm);

function requiresAuthentication(req : Request, res : Response, next : NextFunction) {
    if (req.user) next();
    else {
        res.writeHead(401, { 'WWW-Authenticate': `Digest realm="${realm}",qop="auth",nonce="${Math.random()}",opaque="${hash}"` });   
        res.end('Authentication is needed');
    }
}

function setUserInfo(req : Request, res : Response, next : NextFunction) {
   if (req.headers.authorization) {
        const authString = req.headers.authorization.replace(/^Digest /, '');        
        const authData = parseAuthenticationString(authString);
        if ('username' in authData) {
            const username = authData.username;    
            //all users has 'some password' as password
            const ha1 = md5Digest(`${username}:${realm}:some password`);
            const ha2 = md5Digest(`${req.method}:${authData.uri}`);
            const calculatedResponse = md5Digest(`${ha1}:${authData.nonce}:${authData.nc}:${authData.cnonce}:${authData.qop}:${ha2}`);
            
            if (calculatedResponse !== authData.response) { //invalid digest
                requiresAuthentication(req, res, next);
                return;
            }         
            else{
                req.user = {
                    username : username
                };                                            
            }
        }
    }        
    next();
}

function md5Digest(data : string) {
    return crypto.createHash('md5').update(data).digest('hex');
}

function parseAuthenticationString(authString : string) : {[key:string] : string} {
    const authData : {[key:string] : string} = {};
    authString.split(", ").forEach(function(pair) {
        const arr = pair.split('=');
        authData[arr[0]] = arr[1].replace(/"/g, '');
    });    
    return authData;
}

