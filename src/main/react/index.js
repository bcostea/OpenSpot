'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import { GoogleMap, Marker, DirectionsRenderer } from 'react-google-maps';
import SockJS from 'sockjs-client';
import { Stomp } from './stomp.js';
import { find, pipe, propEq, always, prop, curry } from 'ramda';
import $ from 'jquery';

const mapProps = {
  style: {
    height: '100%',
    width: '100%'
  }
};
const appStyle = {
  width: '500px',
  height: '300px',
  background: 'red'
};


const icons = {
  'FREE': {
     path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
     fillOpacity: 0.8,
     scale: 1,
     strokeColor: 'green',
     strokeWeight: 14
   },
   'OCCUPIED': {
     path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
     fillOpacity: 0.8,
     scale: 1,
     strokeColor: 'red',
     strokeWeight: 14
   },
  'TARGET': {
     path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
     fillOpacity: 0.8,
     scale: 1,
     strokeColor: 'yellow',
     strokeWeight: 14
   },
  'CAR': {
     path: google.maps.SymbolPath.CIRCLE,
     fillOpacity: 0.8,
     scale: 3,
     strokeColor: 'black',
     strokeWeight: 30
   }
}

let openInfoWindow;

let newSpot = curry(function (map, item) {
  item.marker = new google.maps.Marker({
     position: {
       lat: item.position[0],
       lng: item.position[1]
     },
     key: item.id,
     defaultAnimation: 2,
     icon: icons[item.status],
     map: map
  })

  let infowindow = new google.maps.InfoWindow({
    content: '<div>Type: ' + item.type + '</div><div>Price: ' + item.price + 'lei/hour</div>'
  })
  item.marker.addListener('click', function() {
    if (openInfoWindow) {
        openInfoWindow.close()
    }
    infowindow.open(map, item.marker)
    openInfoWindow = infowindow
  })
  return item;
});

let map;
let curLocation = { lat: 0, lng: 0 };
let stomp;
let spots = [];

function recordMarker(event) {
  if (!window.parkingLots) {
    window.parkingLots = "";
  }
  window.parkingLots += 'parkingSpotRepository.save(new ParkingSpot(' + event.latLng.lat() + ', ' + event.latLng.lng() + '))' + "\n";
}
function rad(x) {return x*Math.PI/180;}

function findClosest(point) {
    var lat = point.lat;
    var lng = point.lng;
    var R = 6371; // radius of earth in km
    var distances = [];
    var closest = -1;
    var i;
    spots.forEach((spot, i) => {
        var mlat = spot.position[0];
        var mlng = spot.position[1];
        console.log(lat, lng, mlat, mlng);
        var dLat  = rad(mlat - lat);
        var dLong = rad(mlng - lng);
        var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(rad(lat)) * Math.cos(rad(lat)) * Math.sin(dLong/2) * Math.sin(dLong/2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        var d = R * c;
        distances[i] = d;
        if ( closest == -1 || d < distances[closest] ) {
            closest = i;
        }
    });

    console.log(closest);

    return spots[closest];
}
var directionsDisplay = new google.maps.DirectionsRenderer;
var directionsService = new google.maps.DirectionsService;

function traceRouteTo(spot) {
  console.log(spot);
  directionsDisplay.setMap(map);
  directionsDisplay.setOptions({
    polylineOptions: {
      strokeWeight: 30,
      strokeColor: '#660099'
    }
  });

  directionsDisplay.setPanel(document.getElementById('panel'));

  directionsService.route({
    origin: curLocation,
    destination: { lat: spot.position[0], lng: spot.position[1] },
    travelMode: google.maps.TravelMode.DRIVING
  }, function(response, status) {
    if (status === google.maps.DirectionsStatus.OK) {
      directionsDisplay.setDirections(null);
      directionsDisplay.setDirections(response);
    } else {
      window.alert('Directions request failed due to ' + status);
    }
  });
}

function setCenter(map, point) {
  return map.setCenter({ lat: point.lat, lng: point.lng });
}

function updateSpot(update) {
  let spot = find(propEq('id', update.id))(spots);
  if (spot) {
    spot.marker.setIcon(icons[update.status]);
  }
}

function addSpots(rawSpots) {
  let list = spots.map(function (x) {
    return x.id;
  });
  let newSpots = rawSpots
    .filter(function (x) {
      return -1 === list.indexOf(x.id);
    })
    .map(newSpot(map));

  spots = spots.concat(newSpots);
  startCar();
}

var start = {
  lat: 44.425535992887916,
  lng: 26.127891540527344
};

var stop = {
  lat: 44.42670062259965,
  lng: 26.104846000671387
};

function startCar() {
  directionsService.route({
    origin: start,
    destination: stop,
    travelMode: google.maps.TravelMode.DRIVING
  }, function(response, status) {
    if (status === google.maps.DirectionsStatus.OK) {
      let points = response.routes[0].overview_path.map(function (point) {
        return {
          lat: point.lat(),
          lng: point.lng()
        };
      });
      let marker = new google.maps.Marker({
         position: {
           lat: points[0].lat,
           lng: points[0].lng
         },
         key: 'car',
         icon: icons['CAR'],
         map: map
      })

      let point;

      setInterval(function () {
        point = points.shift();
        if (point) {
          marker.setPosition(point);
        }
      }, 100);
      setTimeout(function () {
        pipe(findClosest, traceRouteTo)(point);
      }, 500);
    } else {
      window.alert('Directions request failed due to ' + status);
    }
  });


}

function changeLocation(event) {
  debugger;
  curLocation = { lat: event.latLng.lat(), lng: event.latLng.lng() };
}

function init() {

  var $map = $('<div id="map"></div>');

  $map.css({
    width: '100%',
    height: 600
  });

  var $closest = $('<button id="closest">Find closest</button>');
  $closest.on('click', pipe(always(curLocation), findClosest, traceRouteTo));

  var $panel = $('<div id="panel"></div>');

  $('body').append($closest);
  $('body').append($map);
  $('body').append($panel);

  map = new google.maps.Map(document.getElementById('map'), { 
    zoom: 15,
    styles:[
    {
        "featureType": "landscape",
        "stylers": [
            {
                "hue": "#F600FF"
            },
            {
                "saturation": 0
            },
            {
                "lightness": 0
            },
            {
                "gamma": 1
            }
        ]
    },
    {
        "featureType": "road.highway",
        "stylers": [
            {
                "hue": "#DE00FF"
            },
            {
                "saturation": -4.6000000000000085
            },
            {
                "lightness": -1.4210854715202004e-14
            },
            {
                "gamma": 1
            }
        ]
    },
    {
        "featureType": "poi",
        "elementType": "labels",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "poi.business",
        "elementType": "all",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "road.arterial",
        "stylers": [
            {
                "hue": "#FF009A"
            },
            {
                "saturation": 0
            },
            {
                "lightness": 0
            },
            {
                "gamma": 1
            }
        ]
    },
    {
        "featureType": "road.local",
        "stylers": [
            {
                "hue": "#FF0098"
            },
            {
                "saturation": 0
            },
            {
                "lightness": 0
            },
            {
                "gamma": 1
            }
        ]
    },
    {
        "featureType": "water",
        "stylers": [
            {
                "hue": "#EC00FF"
            },
            {
                "saturation": 72.4
            },
            {
                "lightness": 0
            },
            {
                "gamma": 1
            }
        ]
    },
    {
        "featureType": "poi",
        "stylers": [
            {
                "hue": "#7200FF"
            },
            {
                "saturation": 49
            },
            {
                "lightness": 0
            },
            {
                "gamma": 1
            }
        ]
    }
]
  });

  navigator.geolocation.getCurrentPosition(function (position) {
    curLocation.lat = position.coords.latitude;
    curLocation.lng = position.coords.longitude;
    setCenter(map, curLocation);
  });

  map.addListener('click', changeLocation);


  stomp = Stomp.over(new SockJS('/api/public/websocket/spots/all'));
    stomp.connect({}, function(frame) {
      stomp.subscribe('/topic/parkingSpots', pipe(prop('body'), JSON.parse, addSpots));
      stomp.subscribe('/topic/spots/update', pipe(prop('body'), JSON.parse, updateSpot));

      setTimeout(function () {
        stomp.send('/api/public/websocket/spots/all', {}, JSON.stringify({}));
      }, 1000);
    });
};

$(init);
