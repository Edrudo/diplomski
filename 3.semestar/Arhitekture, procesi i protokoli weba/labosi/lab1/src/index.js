const controllers = require('./controllers.js');

const express = require('express');
const {auth} = require('express-openid-connect');
const bodyParser = require('body-parser');

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

// reading configuration file
const dotenv = require('dotenv');
dotenv.config();

// generating documentation
const swaggerAutogen = require('swagger-autogen')();
const doc = {
  info: {
    title: 'My API',
    description: 'Description',
  },
  host: 'localhost:8000',
  schemes: ['http'],
};
const outputFile = './swagger.json';
const endpointsFiles = ['./index.js'];
swaggerAutogen(outputFile, endpointsFiles, doc);

// setting documentation route
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./swagger.json');
app.use('/api-doc', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

const address = process.env.RENDER_EXTERNAL_URL || 'http://localhost';
const port = process.env.PORT || 8000;


const config = {
  authRequired: false,
  auth0Logout: true,
  secret: process.env.SECRET,
  baseURL: process.env.RENDER_EXTERNAL_URL || `http://localhost:${port}`,
  clientID: process.env.CLIENT_ID,
  issuerBaseURL: 'https://dev-qtmnfsxrqexie5bo.us.auth0.com',
  clientSecret: process.env.CLIENT_SECRET,
};


const cors = require('cors');
app.use(cors());

// auth router attaches /login, /logout, and /callback routes to the baseURL
app.use(auth(config));

const userPass = new Map();
const isAuthorized = function(req, res, next) {
  if (req.query.local) {
    return next();
  }
  const username = req.body.username;
  const password = req.body.password;
  if (userPass.get(username) === password) {
    return next();
  }
  const err = new Error('Not authorized! Go back!');
  err.status = 401;
  return next(err);
};
app.post('/register', function(req, res) {
  const username = req.body.username;
  const password = req.body.password;
  userPass.set(username, password);
  res.send(200);
});

app.get('/gameweeks', isAuthorized, function(req, res) {
  res.write(JSON.stringify(controllers.getGameweeks()));
  res.end();
});

app.post('/gameweeks', isAuthorized, function(req, res) {
  const newGameweek = req.body.gameweek;
  controllers.newGameweek(newGameweek);
  res.send(200);
});

app.put('/gameweeks', isAuthorized, function(req, res) {
  const gameweek = req.body.gameweek;
  gameweekIndex = req.body.gameweekIndex;
  controllers.updateGameweek(gameweek, gameweekIndex);
  res.send(200);
});

app.delete('/gameweeks', isAuthorized, function(req, res) {
  gameweekIndex = req.body.gameweekIndex;
  controllers.deleteGameweek(gameweekIndex);
  res.send(200);
});

app.get('/gameweeks/comments', isAuthorized, function(req, res) {
  gameweekIndex = req.query.gameweek;
  res.write(JSON.stringify(controllers.getCommentsForGameweek(gameweekIndex)));
  res.end();
});

app.get('/comments', isAuthorized, function(req, res) {
  const user = req.body.username;
  const userComments = controllers.getComments(user);
  res.write(JSON.stringify({
    data: {
      comments: userComments,
    }}));
  res.end();
});

app.post('/comments', isAuthorized, function(req, res) {
  const commentText = req.body.commentText;
  const gameweekIndex = req.body.gameweekIndex;

  controllers.newComment(req.body.username, commentText, gameweekIndex);
  res.send('OK');
});

app.put('/comments', isAuthorized, function(req, res) {
  const commentText = req.body.commentText;
  const gameweekIndex = req.body.gameweekIndex;
  const commentIndex = req.body.commentIndex;

  controllers.updateComment(req.body.username, commentText, gameweekIndex, commentIndex);
  res.send('OK');
});

app.delete('/comments', isAuthorized, function(req, res) {
  const gameweekIndex = req.body.gameweekIndex;
  const commentIndex = req.body.commentIndex;

  controllers.deleteComment(gameweekIndex, commentIndex);

  res.send('OK');
});

app.listen(port, () => {
  console.log(`Server running at ${address}/`);
});
