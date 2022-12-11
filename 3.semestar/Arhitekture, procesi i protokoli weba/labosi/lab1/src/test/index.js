const expect = require('chai').expect;
const request = require('request');
const url = 'http://localhost:8000/';


describe('Gameweek handlers', function() {
  describe('Get gameweeks', function() {
    it('return more that one gameweek', function() {
      request(url + 'gameweeks?local=true', function(error, response, body) {
        const bodyObj = JSON.parse(body);
        expect(bodyObj.data.gameweeks.length).to.greaterThan(0);
      });
    });
    
    describe('Get comments', function() {
    it('return more that one gameweek', function() {
      request(url + 'gameweeks?local=true', function(error, response, body) {
        const bodyObj = JSON.parse(body);
        expect(bodyObj.data.gameweeks.length).to.greaterThan(0);
      });
    });
  });
});
