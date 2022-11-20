const express = require("express");
const path = require("path");
const { readdirSync } = require('fs');

const getDirectories = source =>
    readdirSync(source, { withFileTypes: true })
        .filter(dirent => dirent.isDirectory() && dirent.name.startsWith("ver"))
        .map(dirent => dirent.name);
const arrVersionDirs = getDirectories(__dirname);
console.log(arrVersionDirs);
const app = express();

arrVersionDirs.forEach(dirname => {
    app.use(`/${dirname}/assets`, express.static(path.resolve(__dirname, dirname, "assets")));
});

// app.use("/assets", express.static(path.resolve(__dirname, "assets")));

// app.use((req, res, next) => {
//     console.log(req.path);
//     next();
// });
// include file system module

const fs = require('fs');
let jsonRecipes;
fs.readFile('recipes.json',
    function (err, data) {
        var jsonData = data;
        jsonRecipes = JSON.parse(jsonData);
        console.log(jsonRecipes.length + " recipes parsed...");
    });


app.get('/recipes', function (req, res) {
    res.json(jsonRecipes);
});


app.get('/ver:vernum*', function (req, res) {
    console.log("Requested (v=%s) (%s): %s", req.params.vernum, new Date(), req.path);
    res.sendFile(path.resolve(__dirname, "ver" + req.params.vernum, "assets", "index.html"));
});

app.get('/', function (req, res) {
    res.send(`Please select version: <ul>${arrVersionDirs.map(name => `<li><a href="/${name}">${name}</a></li>`).join("")} </ul>`);
});


app.listen(9999, 'localhost', function () {
    console.log("Server listening on http://%s:%d in %s mode...", this.address().address, this.address().port, app.settings.env);
});