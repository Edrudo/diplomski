import * as auth from './02-auth-middleware';
import express from 'express';
import path from 'path';

const app = express();
app.use(auth.setUserInfo);
app.set("views", path.join(__dirname, "views"));
app.set('view engine', 'pug');

app.get('/',  function (req, res) {
    res.render('index', {user : req.user});
});

app.get('/private', auth.requiresAuthentication,  function (req, res) {  
    const username = req.user!.username; //using ! as we expect that auth.requiresAuthentication had done the job

    if (username.toLowerCase() === 'alice' || username.toLowerCase() === 'bob') {
        res.render('private', {user : req.user}); 
    }
    else {
        res.status(403);
        res.end('Forbidden for ' + username);
    }             
});

const hostname = '127.0.0.1';
const port = 4020;
app.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});