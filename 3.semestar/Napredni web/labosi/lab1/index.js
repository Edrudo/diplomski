const express = require('express');
const { auth, requiresAuth } = require('express-openid-connect');
var bodyParser = require('body-parser');

const app = express();
app.use(bodyParser.urlencoded({ extended: true }));
const dotenv = require('dotenv')
dotenv.config()
var https = require('https')

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
  clientID: process.env.CLIENT_ID,
  issuerBaseURL: 'https://dev-qtmnfsxrqexie5bo.us.auth0.com',
  clientSecret: process.env.CLIENT_SECRET
};


var cors = require('cors');
const e = require('express');
app.use(cors());

// auth router attaches /login, /logout, and /callback routes to the baseURL
app.use(auth(config));

const fs = require("fs");
const { parse } = require("csv-parse");

let results = []
let table = new Map()

function timeNow() {
  const date = new Date();

  return date.getDate() + "." + date.getMonth() + "." + date.getFullYear() + "."
}

let comments = [[{user: "pero.peric@gmail.com", timestamp: timeNow(), text:"Ovo je bilo super kolo"}],
                [{user: "ivo.ivic@icloud.com", timestamp: timeNow(), text:"Ovo je bilo super kolo"}],
                [], [],[],[],[],[],[],[],[],[],
]


fs.createReadStream("./results")
.pipe(parse({delimiter: ",", from_line: 2}))
.on("data", function (row) {
  const result = {
    team1: row[3],
    team2: row[4],
    scoreTeam1: row[5],
    scoreTeam2: row[6],
    matchNumber: row[8]
  }
  results.push(result)

  let team1Points, team2Points
  let scoreTeam1 = parseInt(result.scoreTeam1)
  let scoreTeam2 = parseInt(result.scoreTeam2)
  if(result.scoreTeam1 === ""){
    team1Points = 0
    team2Points = 0
    scoreTeam1 = 0
    scoreTeam2 = 0
  }else if (scoreTeam1 > scoreTeam2){
    team1Points = 3;
    team2Points = 0;
  }else if (scoreTeam1 === scoreTeam2) {
    team1Points = 1;
    team2Points = 1;
  }else{
    team1Points = 0;
    team2Points = 3;
  }

  if(table.get(result.team1)){
      table.set(result.team1, {points: table.get(result.team1).points + team1Points, diff: table.get(result.team1).diff + scoreTeam1-scoreTeam2})
  }else{
    table.set(result.team1, {points: team1Points, diff: scoreTeam1-scoreTeam2})
  }

  if(table.get(result.team2)){
      table.set(result.team2, {points: table.get(result.team2).points + team2Points, diff: table.get(result.team2).diff + scoreTeam2-scoreTeam1})
  }else{
    table.set(result.team2, {points: team2Points, diff: scoreTeam2-scoreTeam1})
  }
})
.on("error", function (error) {
  console.log(error.message);
})
.on("end", function () {
  console.log("finished");
});


app.get('/', function (req, res) {
  req.user = {
    isAuthenticated : req.oidc.isAuthenticated()
  };
  if (req.user.isAuthenticated) {
      req.user.name = req.oidc.user.name;
  }
  let url
  if(process.env.PORT){
    url = process.env.HOST
  }else{
    url = address + ":" + port
  }
  res.render('index', {
    data:{
      user: req.user,
      url: url,
      results: results,
      table: Object.fromEntries(table),
      comments: comments,
    }
  });
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
  for(var i = 0; i < registeredArray.length; i++) {
    
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

app.post('/changeResult', function (req, res) {
  if(req.oidc.user.name === "admin@lab1.com"){
    const team1Score = req.body.team1Score
    const team2Score = req.body.team2Score
    const matchNumber = parseInt(req.body.matchNumber)

    results.forEach((result, i) => {
      if(result.matchNumber == matchNumber){
        results[i] = {
          team1: result.team1,
          team2: result.team2,
          scoreTeam1: team1Score,
          scoreTeam2: team2Score,
          matchNumber: matchNumber
        }
      }
    })

    table = new Map()
    results.forEach((result) => {
      let team1Points, team2Points
      let scoreTeam1 = parseInt(result.scoreTeam1)
      let scoreTeam2 = parseInt(result.scoreTeam2)

      if(result.scoreTeam1 === ""){
        team1Points = 0
        team2Points = 0
        scoreTeam1 = 0
        scoreTeam2 = 0
      }else if (scoreTeam1 > scoreTeam2){
        team1Points = 3;
        team2Points = 0;
      }else if (scoreTeam1 === scoreTeam2) {
        team1Points = 1;
        team2Points = 1;
      }else{
        team1Points = 0;
        team2Points = 3;
      }

      if(table.get(result.team1)){
        table.set(result.team1, {points: table.get(result.team1).points + team1Points, diff: table.get(result.team1).diff + scoreTeam1-scoreTeam2})
      }else{
        table.set(result.team1, {points: team1Points, diff: scoreTeam1-scoreTeam2})
      }

      if(table.get(result.team2)){
        table.set(result.team2, {points: table.get(result.team2).points + team2Points, diff: table.get(result.team2).diff + scoreTeam2-scoreTeam1})
      }else{
        table.set(result.team2, {points: team2Points, diff: scoreTeam2-scoreTeam1})
      }
    })
  }
  res.redirect('/');
})

app.post('/addComment', function (req, res) {
  const commentText = req.body.commentText
  const gameweekIndex = req.body.gameweekIndex

  if(gameweekIndex < 12) {
    comments[gameweekIndex].push({
      user: req.oidc.user.name,
      timestamp: timeNow(),
      text:commentText,
    })
  }
  res.redirect('/');
})

app.post('/editComment', function (req, res) {
  const commentText = req.body.commentText
  const gameweekIndex = req.body.gameweekIndex
  const commentIndex = req.body.commentIndex

  if(gameweekIndex < 12) {
    comments[gameweekIndex][commentIndex] = {
      user: req.oidc.user.name,
      timestamp: timeNow(),
      text:commentText,
    }
  }
  res.redirect('/');
})

app.post('/deleteComment', function (req, res) {
  const gameweekIndex = req.body.gameweekIndex
  const commentIndex = req.body.commentIndex

  let newArray = []
  for (let i = 0; i < comments[gameweekIndex].length; i++){
    if (i != commentIndex) {
      newArray.push(comments[gameweekIndex][i])
    }
  }

  comments[gameweekIndex] = newArray

  res.redirect('/');
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