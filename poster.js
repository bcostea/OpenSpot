//Load the request module
var request = require('request');

var spots =
["56511e6c715cb5a4b236bc64",
"56511e6c715cb5a4b236bc65",
"56511e6c715cb5a4b236bc66",
"56511e6c715cb5a4b236bc67",
"56511e6c715cb5a4b236bc68",
"56511e6c715cb5a4b236bc69",
"56511e6c715cb5a4b236bc6a",
"56511e6c715cb5a4b236bc6b"];

var client = function(){
    var spotNum =  Math.floor(Math.random() * 4);
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

setInterval(client, 1000);