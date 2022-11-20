import express from 'express';
import path from 'path'
const app = express();
app.set("views", path.join(__dirname, "views"));
app.set('view engine', 'pug')

app.get('/', function (req, res) {    
    res.render('client');
});


const hostname = '127.0.0.1';
const port = 4042;
app.listen(port, hostname, () => {
  console.log(`Server for client application running at http://${hostname}:${port}/`);
});