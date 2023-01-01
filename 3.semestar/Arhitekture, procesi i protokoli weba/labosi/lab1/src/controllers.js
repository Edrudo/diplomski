import fs from 'fs';
import {parse} from 'csv-parse';

const gameweeks = [];
const table = new Map();

function timeNow() {
  const date = new Date();

  return date.getDate() + '.' + date.getMonth() + '.' + date.getFullYear() + '.';
}

const comments = [[{
  commentText: 'Ovo je komentar',
  user: 'Eduard Duras',
  timestamp: timeNow(),
}]];
let gameweekNum = 6;

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
    .on('finish', function() {
      gameweekNum = gameweeks.length;
      console.log(gameweekNum);
      console.log('Data init finished');
      gameweeks.forEach(() => {
        comments.push([{
          user: 'Eduard Duras',
          timestamp: timeNow(),
          text: 'Ovo je bilo super kolo'}]);
      });
    });

function getGameweeks() {
  return {
    data: {
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
  };
}

function newGameweek(newGameweek) {
  gameweeks.push(newGameweek);
  gameweekNum += 1;
}

function updateGameweek(gameweek, gameweekIndex) {
  gameweeks[gameweekIndex] = gameweek;
}

function deleteGameweek(gameweekIndex) {
  gameweeks.splice(gameweekIndex, 1);
  gameweekNum -= 1;
}

function getCommentsForGameweek(gameweekIndex) {
  return comments[gameweekIndex];
}

function getComments(user) {
  const userComments = [];
  comments.forEach((gameweekComments) => {
    gameweekComments.forEach((comment) => {
      if (comment.user === user) {
        userComments.push(comment);
      }
    });
  });
  return userComments;
}

function newComment(user, commentText, gameweekIndex) {
  if (gameweekIndex < gameweekNum) {
    comments[gameweekIndex].push({
      user: user,
      timestamp: timeNow(),
      text: commentText,
    });
  }
}

function updateComment(user, commentText, gameweekIndex, commentIndex) {
  if (gameweekIndex < gameweekNum) {
    comments[gameweekIndex][commentIndex] = {
      user: user,
      timestamp: timeNow(),
      text: commentText,
    };
  }
}

function deleteComment(gameweekIndex, commentIndex) {
  if (gameweekIndex < gameweekNum) {
    const newArray = [];
    for (let i = 0; i < comments[gameweekIndex].length; i++) {
      if (i != commentIndex) {
        newArray.push(comments[gameweekIndex][i]);
      }
    }

    comments[gameweekIndex] = newArray;
  }
}

export default {
  gameweekNum,
  getGameweeks,
  newGameweek,
  updateGameweek,
  deleteGameweek,
  getCommentsForGameweek,
  getComments,
  newComment,
  updateComment,
  deleteComment,
};
