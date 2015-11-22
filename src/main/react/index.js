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

function findClosest() {
    var lat = curLocation.lat;
    var lng = curLocation.lng;
    var R = 6371; // radius of earth in km
    var distances = [];
    var closest = -1;
    var i;
    spots.forEach((spot, i) => {
        var mlat = spot.position.lat;
        var mlng = spot.position.lng;
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
    return closest;
}

function traceRoute() {
  var directionsDisplay = DirectionsRenderer;
  var directionsService = new google.maps.DirectionsService();
  var panel = ReactDOM.findDOMNode(this.refs.panel);

  directionsDisplay.setMap(this.refs.map);
  directionsDisplay.setPanel(panel);

  var request = {
    origin: { lat: this.props.lat, lng: this.props.lng },
    destination: { lat: this.props.lat + 1, lng: this.props.lng },
    travelMode: google.maps.TravelMode.DRIVING
  };

  directionsService.route(request, function(response, status) {
    if (status == google.maps.DirectionsStatus.OK) {
      directionsDisplay.setDirections(response);
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

function init() {

  var $map = $('<div id="map"></div>');

  $map.css({
    width: '100%',
    height: 500
  });

  var $closest = $('<button id="closest">Find closest</button>');

  $closest.on('click', findClosest);

  $('body').append($closest);
  $('body').append($map);

  map = new google.maps.Map(document.getElementById('map'), { zoom: 15 });

  navigator.geolocation.getCurrentPosition(function (position) {
    curLocation.lat = position.coords.latitude;
    curLocation.lng = position.coords.longitude;
    setCenter(map, curLocation);
  });

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
