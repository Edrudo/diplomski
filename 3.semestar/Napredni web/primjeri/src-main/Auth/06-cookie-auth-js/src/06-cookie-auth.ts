import * as auth from './06-auth-middleware';
import express from 'express';
import path from 'path';

const app = express();
app.use(express.json());
app.use(express.static('public'));
app.set("views", path.join(__dirname, "views"));
app.set('view engine', 'pug');

auth.initCookieAuth(app, 'login');

app.get('/',  function (req, res) {
    res.render('index', {user : req.user});
});

app.get('/login', function (req, res) {
    res.render('login');
});

app.post('/login',  function (req, res) {    
    const username = req.body.username;
	const password = req.body.password;
    if (password !== 'some password') {
        res.status(401).send("Invalid username or password");
    }
    else {        
        auth.signInUser(res, username);
        res.sendStatus(204);
    }
});

app.post('/logout',   function (req, res) {
    auth.signOutUser(res);
    res.sendStatus(204);
});

app.get('/protected', function (req, res) {     
    if (!req.user)      
        res.status(401).send("Invalid or missing cookie");
     
    else {
        const username = req.user.username;  
                    
        if (username.toLowerCase() === 'alice' || username.toLowerCase() === 'bob') {
            const data = {       
                'CurrentTime' : Date.now(),     
                'Message' : `Welcome ${username}`
            };
            res.json(data);
        }
        else {
            res.status(403);
            res.end('Forbidden for ' + username);
        }     
    }         
});

const hostname = '127.0.0.1';
const port = 4060;
app.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});