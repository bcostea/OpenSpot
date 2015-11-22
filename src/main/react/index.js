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


const statuses = {
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
   }
}

let newSpot = curry(function (map, item) {
  item.marker = new google.maps.Marker({
     position: {
       lat: item.position[0],
       lng: item.position[1]
     },
     key: item.id,
     defaultAnimation: 2,
     icon: statuses[item.status],
     map: map
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

function findClosest() {
    var lat = curLocation.lat;
    var lng = curLocation.lng;
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

    return spots[closest];
}

function traceRouteTo(spot) {
  var directionsDisplay = new google.maps.DirectionsRenderer;
  var directionsService = new google.maps.DirectionsService;

  directionsDisplay.setMap(map);
  directionsDisplay.setPanel(document.getElementById('panel'));

  /*
  var control = document.getElementById('floating-panel');
  control.style.display = 'block';
  map.controls[google.maps.ControlPosition.TOP_CENTER].push(control);
  */

  directionsService.route({
    origin: curLocation,
    destination: { lat: spot.position[0], lng: spot.position[1] },
    travelMode: google.maps.TravelMode.DRIVING
  }, function(response, status) {
    console.log('respnse', response);
    if (status === google.maps.DirectionsStatus.OK) {
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
    spot.marker.setIcon(statuses[update.status]);
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
}

function changeLocation(event) {
  console.log(event);
  curLocation = { lat: event.latLng.lat(), lng: event.latLng.lng() };
}

function init() {

  var $map = $('<div id="map"></div>');

  $map.css({
    width: '100%',
    height: 500
  });

  var $closest = $('<button id="closest">Find closest</button>');
  $closest.on('click', pipe(findClosest, traceRouteTo));

  var $panel = $('<div id="panel"></div>');

  $('body').append($closest);
  $('body').append($map);
  $('body').append($panel);

  map = new google.maps.Map(document.getElementById('map'), { zoom: 15 });

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
