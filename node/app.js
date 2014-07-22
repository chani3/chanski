var express = require('express');
var https = require('https');
var fs = require('fs');

var PORT = 3000; //TODO make option

var options = {
  key: fs.readFileSync('keys/key.pem'),
  cert: fs.readFileSync('keys/cert.pem')
};

var app = express();
app.engine('.html', require('ejs').__express);
app.set('view engine', 'html');


app.get('/', function(req, res){
  res.render('hello');
});


https.createServer(options, app).listen(PORT);
