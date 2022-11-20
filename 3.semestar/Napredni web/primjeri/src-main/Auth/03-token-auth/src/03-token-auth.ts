import * as auth from './03-auth-middleware';
import express from 'express';
import path from 'path';

const app = express();
app.use(auth.setUserInfo);
app.use(express.static('public'));
app.use(express.urlencoded({ extended: true })); 

app.set("views", path.join(__dirname, "views"));
app.set('view engine', 'pug');

app.get('/',  function (req, res) {
    res.render('index');
});

app.get('/login', function (req, res) {
    res.render('login');
});

app.post('/login',  function (req, res) {    
    const username = req.body.username;
	const password = req.body.password;
    if (password !== 'some password') {
        res.render('login');
    }
    else {
        const payload : auth.TokenData = {
            username 
        };
        if (username.toLowerCase() === 'alice') {
            payload.role = 'admin';
        }
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
const port = 4030;
app.listen(port, hostname, () => {    
  console.log(`Server running at http://${hostname}:${port}/`);
});