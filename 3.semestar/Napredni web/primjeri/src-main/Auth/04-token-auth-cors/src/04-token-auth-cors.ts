import * as auth from './04-auth-middleware';
import express from 'express';
import cors from 'cors';

const app = express();

app.use(cors())
app.use(auth.setUserInfo);
app.use(express.json()); 
app.use(express.static('public'));

app.post('/login',  function (req, res) {    
    const username = req.body.username;
	const password = req.body.password;
    if (password !== 'some password') {
        res.status(401).send("Invalid username or password");
    }
    else {
        const payload = {
            username
        };
        const token = auth.createToken(payload);
        res.json(token);
    }
});

app.get('/protected', auth.requiresAuthentication, function (req, res) {      
    const username = req.user!.username;      
                   
    if (username.toLowerCase() === 'alice' || username.toLowerCase() === 'bob') {
        const data = {  
            'CurrentTime' : new Date().toLocaleString(), 
            'Message' : `Welcome ${username}`
        };
        res.json(data);
    }
    else {
        res.status(403);
        res.end('Forbidden for ' + username);
    }              
});

const hostname = '127.0.0.1';
const port = 4041;
app.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});