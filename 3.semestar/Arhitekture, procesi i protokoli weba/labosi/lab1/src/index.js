const express = require('express');
const {auth} = require('express-openid-connect');
const bodyParser = require('body-parser');

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

// reading configuration file
const dotenv = require('dotenv');
dotenv.config();

const https = require('https');

// generating documentation
const swaggerAutogen = require('swagger-autogen')();
const doc = {
  info: {
    title: 'My API',
    description: 'Description',
  },
  host: 'localhost:8000',
  schemes: ['https'],
};
const outputFile = './swagger.json';
const endpointsFiles = ['./index.js'];
swaggerAutogen(outputFile, endpointsFiles, doc);

// setting documentation route
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./swagger.json');
app.use('/api-doc', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

const address = process.env.RENDER_EXTERNAL_URL || 'https://localhost';
const port = process.env.PORT || 8000;


const config = {
  authRequired: false,
  auth0Logout: true,
  secret: process.env.SECRET,
  baseURL: process.env.RENDER_EXTERNAL_URL || `https://localhost:${port}`,
  clientID: process.env.CLIENT_ID,
  issuerBaseURL: 'https://dev-qtmnfsxrqexie5bo.us.auth0.com',
  clientSecret: process.env.CLIENT_SECRET,
};


const cors = require('cors');
app.use(cors());

// auth router attaches /login, /logout, and /callback routes to the baseURL
app.use(auth(config));

const fs = require('fs');
const {parse} = require('csv-parse');
const {ok} = require('assert');

let gameweekNum = 6;
const gameweeks = [];
const table = new Map();

function timeNow() {
  const date = new Date();

  return date.getDate() + '.' + date.getMonth() + '.' + date.getFullYear() + '.';
}

const comments = [
  [{
    user: 'pero.peric@gmail.com',
    timestamp: timeNow(),
    text: 'Ovo je bilo super kolo'}],
  [{
    user: 'ivo.ivic@icloud.com',
    timestamp: timeNow(),
    text: 'Ovo je bilo super kolo'}],
  [], [], [], [], [], [], [], [], [], [],
];


fs.createReadStream('./results')
    .pipe(parse({delimiter: ',', from_line: 2}))
    .on('data', function(row) {
      const result = {
        team1: row[3],
        team2: row[4],
        scoreTeam1: row[5],
        scoreTeam2: row[6],
        matchNumber: row[8],
      };
      if (gameweeks[Math.floor((result.matchNumber - 1) / gameweekNum)]) {
        gameweeks[Math.floor((result.matchNumber - 1) / gameweekNum)].push(result);
      } else {
        gameweeks.push([result]);
      }

      let team1Points; let team2Points;
      let scoreTeam1 = parseInt(result.scoreTeam1);
      let scoreTeam2 = parseInt(result.scoreTeam2);
      if (result.scoreTeam1 === '') {
        team1Points = 0;
        team2Points = 0;
        scoreTeam1 = 0;
        scoreTeam2 = 0;
      } else if (scoreTeam1 > scoreTeam2) {
        team1Points = 3;
        team2Points = 0;
      } else if (scoreTeam1 === scoreTeam2) {
        team1Points = 1;
        team2Points = 1;
      } else {
        team1Points = 0;
        team2Points = 3;
      }

      if (table.get(result.team1)) {
        table.set(result.team1, {
          points: table.get(result.team1).points + team1Points,
          diff: table.get(result.team1).diff + scoreTeam1-scoreTeam2});
      } else {
        table.set(result.team1, {points: team1Points, diff: scoreTeam1-scoreTeam2,
        });
      }

      if (table.get(result.team2)) {
        table.set(result.team2, {
          points: table.get(result.team2).points + team2Points,
          diff: table.get(result.team2).diff + scoreTeam2-scoreTeam1,
        });
      } else {
        table.set(result.team2, {points: team2Points, diff: scoreTeam2-scoreTeam1});
      }
    })
    .on('error', function(error) {
      console.log(error.message);
    })
    .on('end', function() {
      console.log('finished');
    });

const isAuthorized = function(req, res, next) {
  if (req.oidc.isAuthenticated()) {
    return next();
  } else {
    const err = new Error('Not authorized! Go back!');
    err.status = 401;
    return next(err);
  }
};

app.get('/gameweeks', isAuthorized, function(req, res) {
  req.user = {
    isAuthenticated: req.oidc.isAuthenticated(),
  };
  if (req.user.isAuthenticated) {
    req.user.name = req.oidc.user.name;
  }
  res.write(JSON.stringify({
    data: {
      user: req.user,
      gameweeks: gameweeks,
      table: Object.fromEntries(Array.from(table.entries()).sort((a, b) => {
        const pointsTeam1 = a[1].points;
        const pointsTeam2 = b[1].points;
        const diffTeam1= a[1].diff;
        const diffTeam2 = b[1].diff;

        if (pointsTeam1 > pointsTeam2) {
          return -1;
        } else if (pointsTeam1 === pointsTeam2) {
          if (diffTeam1 >= diffTeam2) {
            return -1;
          }
        }
        return 1;
      })),
      comments: comments,
    },
  }));
  res.end();
});

app.post('/gameweeks', isAuthorized, function(req, res) {
  const newGameweek = req.body.gameweek;
  gameweeks.push(newGameweek);
  gameweekNum += 1;
  res.send(200);
});

app.put('/gameweeks', isAuthorized, function(req, res) {
  const gameweek = req.body.gameweek;
  gameweekIndex = req.body.gameweekIndex;
  gameweeks[gameweekIndex] = gameweek;
  res.send(200);
});

app.delete('/gameweeks', isAuthorized, function(req, res) {
  gameweekIndex = req.body.gameweekIndex;
  gameweeks.splice(gameweekIndex, 1);
  gameweekNum -= 1;
  res.send(200);
});

app.get('/gameweeks/comments', isAuthorized, function(req, res) {
  req.user = {
    isAuthenticated: req.oidc.isAuthenticated(),
  };
  if (req.user.isAuthenticated) {
    req.user.name = req.oidc.user.name;
  }
  gameweekIndex = req.query.gameweek;
  gameweekComments = comments[gameweekIndex];
  res.write(JSON.stringify({
    data: {
      comments: gameweekComments,
    },
  }));
  res.end();
});

app.get('/comments', isAuthorized, function(req, res) {
  const user = req.oidc.user.name;
  const userComments = [];
  comments.forEach((gameweekComments) => {
    gameweekComments.forEach((comment) => {
      if (comment.user === user) {
        userComments.push(comment);
      }
    });
  });
  res.write(JSON.stringify({
    data: {
      comments: userComments,
    }}));
  res.end();
});

app.post('/comments', isAuthorized, function(req, res) {
  const commentText = req.body.commentText;
  const gameweekIndex = req.body.gameweekIndex;

  if (gameweekIndex < gameweekNum) {
    comments[gameweekIndex].push({
      user: req.oidc.user.name,
      timestamp: timeNow(),
      text: commentText,
    });
  }
  res.send('OK');
});

app.put('/comments', isAuthorized, function(req, res) {
  const commentText = req.body.commentText;
  const gameweekIndex = req.body.gameweekIndex;
  const commentIndex = req.body.commentIndex;

  if (gameweekIndex < 12) {
    comments[gameweekIndex][commentIndex] = {
      user: req.oidc.user.name,
      timestamp: timeNow(),
      text: commentText,
    };
  }
  res.redirect('/');
});

app.delete('/comments', isAuthorized, function(req, res) {
  const gameweekIndex = req.body.gameweekIndex;
  const commentIndex = req.body.commentIndex;

  const newArray = [];
  for (let i = 0; i < comments[gameweekIndex].length; i++) {
    if (i != commentIndex) {
      newArray.push(comments[gameweekIndex][i]);
    }
  }

  comments[gameweekIndex] = newArray;

  res.send('OK');
});

if (process.env.PORT) {
  app.listen(port, () => {
    console.log(`Server running at ${address}/`);
  });
} else {
  https.createServer({
    key: fs.readFileSync('server.key'),
    cert: fs.readFileSync('server.crt'),
  }, app).listen(port, function() {
    console.log(`Server running at https://localhost:${port}/`);
  });
}
