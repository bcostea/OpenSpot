'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import { GoogleMap, Marker, DirectionsRenderer } from 'react-google-maps';
import SockJS from 'sockjs-client';
import { Stomp } from './stomp.js';
import { pipe, always, prop, curry } from 'ramda';
import $ from 'jquery';

const mapProps = {
  style: {
    height: '100%',
    width: '100%'
  }
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

function recordMarker(event) {
  if (!window.parkingLots) {
    window.parkingLots = "";
  }
  window.parkingLots += 'parkingSpotRepository.save(new ParkingSpot(' + event.latLng.lat() + ', ' + event.latLng.lng() + '))' + "\n";
}

function findClosest() {
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
    console.log('received', response);
    if (status == google.maps.DirectionsStatus.OK) {
      directionsDisplay.setDirections(response);
    }
  });
}

const appStyle = {
  width: '500px',
  height: '300px',
  background: 'red'
};

var map;
var curLocation = { lat: 0, lng: 0 };
var stomp;
var spots = [];

function setCenter(map, point) {
  return map.setCenter({ lat: point.lat, lng: point.lng });
}

function updateSpot(spot) {
  console.log(spot);
  spots.filter(function (x) {
    if (x.key === spot.id) {
      x.icon = statuses[rawMarker.status];
    }
    return x;
  });
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
