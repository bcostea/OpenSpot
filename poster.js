//Load the request module
var request = require('request');

var spots =
['5650b0bc7c275a476117e4d2',
'5650b0bc7c275a476117e4d1',
'5650b0bc7c275a476117e4d0',
'5650b0bc7c275a476117e4d3',
'5650b0bc7c275a476117e4d4'];

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