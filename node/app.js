var express = require('express');
var https = require('https');
var fs = require('fs');
var bodyParser = require('body-parser');

var PORT = 3000; //TODO make option

var options = {
  key: fs.readFileSync('keys/key.pem'),
  cert: fs.readFileSync('keys/cert.pem')
};

var app = express();
app.engine('.html', require('ejs').__express);
app.set('view engine', 'html');
app.use(bodyParser.json());


var helloData = {
  world: 'World'
}

app.get('/', function(req, res){
  res.render('hello', helloData);
});
app.post('/', function(req, res){
  console.log("post:");
  console.log(req.body);
  res.render('hello', {world:'post'});
});


https.createServer(options, app).listen(PORT);
