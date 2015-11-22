//Load the request module
var request = require('request');

var spots =
['5650e704715c84e8b4a547d8',
'5650e704715c84e8b4a547d9',
'5650e704715c84e8b4a547da',
'5650e704715c84e8b4a547db',
'5650e704715c84e8b4a547dc'];

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