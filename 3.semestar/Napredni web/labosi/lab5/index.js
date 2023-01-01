const express = require('express');

const app = express();
const dotenv = require('dotenv');
dotenv.config();

const address = process.env.RENDER_EXTERNAL_URL || 'http://localhost';
const port = process.env.PORT || 5000;

app.use(express.static('public'));

app.use(express.static('./'));

app.get('/', function(req, res) {
  res.sendFile(__dirname + '/public/html/index.html');
});

app.get('*', function(req, res) {
  res.status(404);
  res.sendFile(__dirname + '/public/html/404.html');
});


app.listen(port, () => {
  console.log(`Server running at ${address}/ \n port ${port}`);
});
