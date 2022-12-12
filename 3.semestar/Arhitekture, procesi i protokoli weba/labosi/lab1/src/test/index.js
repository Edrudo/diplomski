import {expect} from 'chai';
const url = 'http://localhost:8000/';

import fetch, {Headers} from 'node-fetch';

const authHeaders = new Headers({
  'Authorization': `Basic ${btoa('edo' + ':' + 'edo')}`,
});

describe('Register', async function() {
  it('registers user edo', async function() {
    const response = await fetch(url + 'register', {
      method: 'POST',
      headers: authHeaders,
    });
    expect(response.status).to.equal(200);
  });
});

describe('Gameweeks', async function() {
  let gameweekNum = 0;
  describe('Get gameweeks', async function() {
    it('returns gameweeks', async function() {
      const response = await fetch(url + 'gameweeks', {
        headers: authHeaders,
      });
      const body = await response.json();
      expect(body.data.gameweeks.length).to.greaterThan(gameweekNum);
      gameweekNum = body.data.gameweeks.length;
    });
  });

  describe('Post gameweeks', async function() {
    it('creates a geameweek', async function() {
      const response = await fetch(url + 'gameweeks', {
        method: 'POST',
        headers: authHeaders,
        body: {
          'gameweek': [
            {
              'team1': 'VPS',
              'team2': 'AC Oulu',
              'scoreTeam1': '1',
              'scoreTeam2': '2',
              'matchNumber': '1',
            },
            {
              'team1': 'SJK',
              'team2': 'FC Haka',
              'scoreTeam1': '3',
              'scoreTeam2': '4',
              'matchNumber': '2',
            },
            {
              'team1': 'KuPS',
              'team2': 'FC Honka',
              'scoreTeam1': '2',
              'scoreTeam2': '4',
              'matchNumber': '3',
            },
            {
              'team1': 'Ilves',
              'team2': 'FC Inter',
              'scoreTeam1': '2',
              'scoreTeam2': '5',
              'matchNumber': '4',
            },
            {
              'team1': 'IFK Mariehamn',
              'team2': 'FC Lahti',
              'scoreTeam1': '2',
              'scoreTeam2': '5',
              'matchNumber': '5',
            },
            {
              'team1': 'HJK',
              'team2': 'HIFK',
              'scoreTeam1': '1',
              'scoreTeam2': '5',
              'matchNumber': '6',
            },
          ],
        },
      });
      expect(response.status).to.equal(200);
    });
  });

  describe('Get gameweeks', async function() {
    it('checks if post work', async function() {
      const response = await fetch(url + 'gameweeks', {
        headers: authHeaders,
      });
      const body = await response.json();
      expect(body.data.gameweeks.length).to.equal(gameweekNum + 1);
      gameweekNum = body.data.gameweeks.length;
    });
  });

  describe('Delete gameweek', async function() {
    it('deletes a geameweek', async function() {
      const response = await fetch(url + 'gameweeks', {
        method: 'DELETE',
        headers: authHeaders,
        body: {
          'gameweekIndex': 0,
        },
      });
      expect(response.status).to.equal(200);
    });
  });

  describe('Get gameweeks', async function() {
    it('checks if delete work', async function() {
      const response = await fetch(url + 'gameweeks', {
        headers: authHeaders,
      });
      const body = await response.json();
      expect(body.data.gameweeks.length).to.equal(gameweekNum - 1);
      gameweekNum = body.data.gameweeks.length;
    });
  });

  describe('Delete gameweek', async function() {
    it('deletes a geameweek', async function() {
      const response = await fetch(url + 'gameweeks', {
        method: 'DELETE',
        headers: authHeaders,
        body: {
          'gameweekIndex': 0,
        },
      });
      expect(response.status).to.equal(200);
    });
  });
});

describe('Comments', async function() {
  let commentNum = 0;
  describe('Get comments', async function() {
    it('returns comments', async function() {
      const response = await fetch(url + 'comments', {
        headers: authHeaders,
      });
      expect(response.status).to.equal(200);
      const body = await response.json();
      commentNum = body.data.comments.length;
    });
  });

  describe('Post comment', async function() {
    it('creates a comment', async function() {
      const response = await fetch(url + 'comments', {
        method: 'POST',
        headers: authHeaders,
        body: {
          'commentText': 'Ovo je komentar',
          'gameweekIndex': 4,
        },
      });
      expect(response.status).to.equal(200);
    });
  });

  describe('Get comments', async function() {
    it('checks if post work', async function() {
      const response = await fetch(url + 'comments', {
        method: 'GET',
        headers: authHeaders,
      });
      const body = await response.json();
      expect(body.data.comments.length).to.equal(commentNum + 1);
      commentNum = body.data.comments.length;
    });
  });

  describe('Delete comment', async function() {
    it('deletes a comment', async function() {
      const response = await fetch(url + 'comments', {
        method: 'DELETE',
        headers: authHeaders,
        body: {
          'gameweekIndex': 4,
          'commentIndex': 1,
        },
      });
      expect(response.status).to.equal(200);
    });
  });

  describe('Get comments', async function() {
    it('checks if delete work', async function() {
      const response = await fetch(url + 'comments', {
        headers: authHeaders,
      });
      const body = await response.json();
      expect(body.data.comments.length).to.equal(commentNum - 1);
      commentNum = body.data.comments.length;
    });
  });
});
