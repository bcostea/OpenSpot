//Load the request module
var request = require('request');


    request({
        url: 'http://localhost:8080/api/public/spots/565113787c276a810296376b/update',
        method: 'POST',
        json: {id: '565113787c276a810296376b', free:false}
    }, function(error, response, body){
        if(error) {
            console.log(error);
        } else {
            console.log(response.statusCode, body);
    }
    });
