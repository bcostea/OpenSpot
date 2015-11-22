'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import { GoogleMap, Marker, DirectionsRenderer } from 'react-google-maps';
import SockJS from 'sockjs-client';
import { Stomp } from './stomp.js';
import { find, pipe, indexOf, __, propEq, always, prop, curry } from 'ramda';
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
window.spots = [];

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
        if (spot.status !== 'FREE') return;

        var mlat = spot.position[0];
        var mlng = spot.position[1];
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

    return {
      from: point,
      to: spots[closest]
    }
}
var directionsDisplay = new google.maps.DirectionsRenderer;
var directionsService = new google.maps.DirectionsService;

function traceRouteTo(points) {
  directionsDisplay.setMap(map);
  directionsDisplay.setOptions({
    preserveViewport: true,
    polylineOptions: {
      strokeWeight: 10,
      strokeColor: '#660099'
    }
  });

  directionsService.route({
    origin: points.from,
    destination: { lat: points.to.position[0], lng: points.to.position[1] },
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

  let spot;
  spots.forEach((x) => {
    if (x.id === update.id) {
      spot = x;
    }
  });
  if (spot) {
    spot.status = update.status;
    spot.marker.setIcon(icons[update.status]);
  }
}

function addSpots(rawSpots) {
  spots = rawSpots
    .filter(pipe(prop('id'), indexOf(__, spots.map(prop('id')))))
    .map(newSpot(map))
    .concat(spots);
  return spots;
}

var start = {
  lat: 44.418854247735524,
  lng: 26.096949577331543
};

var stop = {
  lat: 44.42636349533249,
  lng: 26.11201286315918
};

let carPos;

function move(car, points) {
  if (0 === points.length) {
    return;
  }

  carPos = points.shift();

  car.setPosition(carPos);
  setTimeout(() => {
    move(car, points);
  }, 3000);
}

let step = 1 / 1000;

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

      let car = new google.maps.Marker({
         position: {
           lat: points[0].lat,
           lng: points[0].lng
         },
         key: 'car',
         icon: icons['CAR'],
         map: map
      });

      let smoothPoints = [];

      function increment(cur, next) {
        cur = {
          lat: cur.lat + step,
          lng: cur.lng + step
        }

        let diffLat = next.lat - cur.lat;
        let diffLng = next.lng - cur.lng;
        if (diffLat > step || diffLng > step) {
          smoothPoints.push(cur);
          increment(cur, next);
        }
      }

      points.forEach((point, index, points) => {
        smoothPoints.push(point);

        let next = points[index + 1];
        if (next) {
          // increment(point, next);
        }
      });

      setInterval(() => pipe(findClosest, traceRouteTo)(carPos), 1000);
      move(car, smoothPoints);

    } else {
      window.alert('Directions request failed due to ' + status);
    }
  });


}

function changeLocation(event) {
  console.log(event.latLng.lat(), event.latLng.lng());
  // curLocation = { lat: event.latLng.lat(), lng: event.latLng.lng() };
}

function init() {

  var $map = $('<div id="map"></div>');

  $map.css({
    width: '100%',
    height: 600
  });

  var $closest = $('<button id="closest">Find closest</button>');
  // $closest.on('click', pipe(always(curLocation), findClosest, traceRouteTo));

  var $panel = $('<div id="panel"></div>');

  $('body').append($closest);
  $('body').append($map);
  $('body').append($panel);

  map = new google.maps.Map(document.getElementById('map'), { 
    zoom: 15,
    styles: require('./mapStyles.js')
  });

  navigator.geolocation.getCurrentPosition(function (position) {
    curLocation.lat = position.coords.latitude;
    curLocation.lng = position.coords.longitude;
    setCenter(map, curLocation);
  });

  map.addListener('click', recordMarker);

  stomp = Stomp.over(new SockJS('/api/public/websocket/spots/all'));
    stomp.connect({}, function(frame) {
      stomp.subscribe('/topic/parkingSpots', pipe(prop('body'), JSON.parse, pipe(addSpots, startCar)));
      stomp.subscribe('/topic/spots/update', pipe(prop('body'), JSON.parse, updateSpot));

      setTimeout(function () {
        stomp.send('/api/public/websocket/spots/all', {}, JSON.stringify({}));
      }, 1000);
    });
};

$(init);
