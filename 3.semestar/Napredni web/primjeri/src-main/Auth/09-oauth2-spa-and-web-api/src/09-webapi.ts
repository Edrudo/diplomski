import express from 'express'
import cors from 'cors'
import { auth } from 'express-oauth2-jwt-bearer'; //claimCheck, requireScope, ...
import axios from 'axios'

const app = express();
app.use(cors());

const authServer = 'https://fer-web2.eu.auth0.com';

const checkJwt = auth({
  audience: 'FER-Web2 WebAPI',
  issuerBaseURL: `${authServer}`,
});
app.use(checkJwt);

app.get('/protected',  async function (req, res) { 
  const accesstoken = req.auth!.token; //because of app.use(checkJwt)
  
  try{
    const userInfoResponse = await axios.post(`${authServer}/userinfo`, {},  {
                                                    headers : {
                                                        Authorization : `Bearer ${accesstoken}`
                                                    }}); 
     const user = userInfoResponse.data;    
     res.json(JSON.stringify(user));               
  }
  catch(err) {
    console.log(err);
  } 
});

const hostname = '127.0.0.1';
const port = 4091; 
app.listen(port, hostname, () => {
  console.log(`Web API running at http://${hostname}:${port}/`);
});