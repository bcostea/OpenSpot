//Load the request module
var request = require('request');
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/test');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function (callback) {
  console.log('done');
});

var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var ObjectId = require('mongodb').ObjectID;
var url = 'mongodb://localhost/test';
var spots = [];

MongoClient.connect(url, function(err, db) {
    assert.equal(null, err);
    db.collection('parking_spots').find().each(function (err, doc) {
      assert.equal(err, null);
      if (doc) {
        spots.push(doc._id);
      } else {
        console.log(spots);
        startUpdates();
      }
    });
});


var client = function(){
    var spotNum =  Math.floor(Math.random() * spots.length - 1);
    var spot = spots[spotNum];
    var status = Math.floor(Math.random() * 10) > 5? true:false;

    request({
        url: 'http://localhost:8080/api/public/spots/' + spot + '/update',
        method: 'POST',
        json: {id: spot, free:status}
    }, function(error, response, body){
        if(error) {
            console.log(error);
        } else {
            console.log(response.statusCode, body);
    }
    });
}

function startUpdates() {
  setInterval(client, 100);
}