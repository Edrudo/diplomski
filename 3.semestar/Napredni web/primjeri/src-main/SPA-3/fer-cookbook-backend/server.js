const express = require("express");
const path = require("path");
var cors = require("cors");

const fs = require("fs");
let jsonRecipes;
fs.readFile("recipes.json", function (err, data) {
  var jsonData = data;
  jsonRecipes = JSON.parse(jsonData);

  jsonRecipes = jsonRecipes
    .filter((x) => Math.random() > 0.9)
    .map(function (item, idx) {
      return {
        id: idx,
        ...item,
        ingredients: item.ingredients ? item.ingredients.split("\n") : [],
      };
    })
    .reverse();
  console.log(jsonRecipes.length + " recipes parsed... eg.", jsonRecipes[0]);
});

const app = express();
app.use(express.static('public'));
app.use(cors());

app.get("/recipes", function (req, res) {
  res.json(jsonRecipes);
});

app.listen(8888, "localhost", function () {
  console.log(
    "Server listening on http://%s:%d in %s mode...",
    this.address().address,
    this.address().port,
    app.settings.env
  );
});
