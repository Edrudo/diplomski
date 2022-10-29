const express = require('express');
const { auth, requiresAuth } = require('express-openid-connect');
var bodyParser = require('body-parser');

const app = express();
const dotenv = require('dotenv')
dotenv.config()
var https = require('https')
var fs = require('fs')

const address = process.env.HOST || 'https://localhost';
const port = process.env.PORT || 5000;

//app.use(express.static('public'));
app.set('view engine', 'pug')
app.use(bodyParser.json());

const config = {
  authRequired: false,
  auth0Logout: true,
  secret: process.env.SECRET,
  baseURL: process.env.HOST || `https://localhost:${port}`,
  clientID: 'fDxuun5xngEqtP3s8VUXY7R0JzeZjMvm',
  issuerBaseURL: 'https://dev-8gh205m3.us.auth0.com',
  clientSecret: process.env.CLIENT_SECRET
};


var cors = require('cors');
const e = require('express');
app.use(cors());

// auth router attaches /login, /logout, and /callback routes to the baseURL
app.use(auth(config));

var registeredArray = []

app.get('/', function (req, res) {
  req.user = {
    isAuthenticated : req.oidc.isAuthenticated()
  };
  if (req.user.isAuthenticated) {
      req.user.name = req.oidc.user.name;
      
  }
  var url
  if(process.env.PORT){
    url = process.env.HOST
  }else{
    url = address + ":" + port
  }
  res.render('index', {user:req.user, url:url})
})

app.get('/registeredUserLocation', function (req, res) {
  req.user = {
    isAuthenticated : req.oidc.isAuthenticated()
  };
  res. setHeader('Content-Type', 'application/json')
  res.end(JSON.stringify(registeredArray));
})

app.post('/registeredUserLocation', function (req, res) {
  var exists = false
  var index
  for(var i = 0; i < registeredArray.length; i++){
    
    if(registeredArray[i].name === req.oidc.user.name){
      index = i
      exists = true
    }
  }
  
  if(exists){
    registeredArray[index] = {name:req.oidc.user.name, timestamp:Date.now(), longitude:req.body.longitude, latitude:req.body.latitude}
  }else{
    if(registeredArray.length < 5){
      registeredArray.push({name:req.oidc.user.name, timestamp:Date.now(), longitude:req.body.longitude, latitude:req.body.latitude})
    }else{
      var minimum = 0
  
      for(var i = 1; i < registeredArray.length; i++){
        if(registeredArray[i].timestamp < registeredArray[minimum].timestamp){
          minimum = i
        }
      } 
  
      registeredArray[minimum] = {name:req.oidc.user.name, timestamp:Date.now(), longitude:req.body.longitude, latitude:req.body.latitude}
    }
  }
})

if(process.env.PORT){
  app.listen(port, () => {
    console.log(`Server running at ${address}/`);
  }); 
}else{
  https.createServer({
    key: fs.readFileSync('server.key'),
    cert: fs.readFileSync('server.cert')
  }, app).listen(port, function () {
    console.log(`Server running at https://localhost:${port}/`);
  });
}