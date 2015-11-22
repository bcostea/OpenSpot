'use strict';

import { GoogleMap, Marker, DirectionsRenderer } from 'react-google-maps';
import SockJS from 'sockjs-client';
import { Stomp } from './stomp.js';
import { find, pipe, memoize, indexOf, __, propEq, always, prop, curry } from 'ramda';
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
     scale: 7,
     strokeColor: 'green',
     fillColor: 'green',
     strokeWeight: 1
   },
   'OCCUPIED': {
     path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
     fillOpacity: 0.8,
     scale: 7,
     strokeColor: 'red',
     fillColor: 'red',
     strokeWeight: 1
   },
  'TARGET': {
     path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
     fillOpacity: 0.8,
     scale: 1,
     strokeColor: 'yellow',
     strokeWeight: 14
   },
  'CARS': {
     path: google.maps.SymbolPath.CIRCLE,
     fillOpacity: 0.8,
     scale: 3,
     strokeColor: 'black',
     strokeWeight: 30
   },
   'CAR': {
     url: 'http://icons.iconarchive.com/icons/fasticon/freestyle/128/car-icon.png',
     scaledSize: new google.maps.Size(60, 60),
     origin: new google.maps.Point(0, 0),
     anchor: new google.maps.Point(30, 30)
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

  item.infowindow = new google.maps.InfoWindow({
    content: '<div>Type: ' + item.type + '</div><div>Price: ' + item.price + 'lei/hour</div>'
  })

  item.marker.addListener('click', function() {
    if (openInfoWindow) {
        openInfoWindow.close()
    }
    item.infowindow.open(map, item.marker)
    openInfoWindow = item.infowindow
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
    suppressMarkers: true,
    polylineOptions: {
      strokeWeight: 5,
      strokeColor: 'green'
    }
  });

  if (openInfoWindow) {
      openInfoWindow.close()
  }
  points.to.infowindow.open(map, points.to.marker);

  openInfoWindow = points.to.infowindow;

  directionsService.route({
    origin: points.from,
    destination: { lat: points.to.position[0], lng: points.to.position[1] },
    travelMode: google.maps.TravelMode.DRIVING
  }, function(response, status) {
    if (status === google.maps.DirectionsStatus.OK) {
      directionsDisplay.setDirections(null);
      directionsDisplay.setDirections(response);
    } else {
      // window.alert('Directions request failed due to ' + status);
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
  lat: 44.417536198659185,
  lng: 26.081897020339966
};

var stop = {
  lat: 44.42636349533249,
  lng: 26.11201286315918
};

let carPos;

function move(car, points) {
  if (0 === points.length) {
    clearInterval(carInterval);
    return;
  }

  carPos = points.shift();

  car.setPosition(carPos);
  map.setCenter(carPos);
  setTimeout(() => {
    move(car, points);
  }, 25);
}

let step = 1 / 30000;
 var distance = memoize(function(from, to) {
  if (!to || !from ) return 0; 
          return Math.abs(Math.sqrt(Math.pow(from.lat - to.lat, 2) + Math.pow(from.lng - to.lng, 2)))
 });

let smoothPoints = [];
let carInterval;

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

      let smooth = points.reduce((acc, point, index, points) => {
        acc.push(point);

        let next = points[index + 1];

        if (next) {
          let diffLat = next.lat - point.lat;
          let diffLng = next.lng - point.lng;
          let dist = distance(point, next);

          let count = dist / step;
          let inc;
          let newPoint = {
            lat: point.lat,
            lng: point.lng
          };

          let diffLatStep = diffLat / count;
          let diffLngStep = diffLng / count;

          for (inc = 0; inc < count; inc += 1) {
            newPoint = {
              lat: newPoint.lat + diffLatStep,
              lng: newPoint.lng + diffLngStep
            }
            if (newPoint.lat > next.lat) {
              newPoint.lat = next.lat;
            }
            if (newPoint.lng > next.lng) {
              newPoint.lng = next.lng;
            }
            acc.push(newPoint);
          }
        }

        return acc;
      }, []);

      carInterval = setInterval(() => pipe(findClosest, traceRouteTo)(carPos), 1000);
      move(car, smooth);

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
  var $mapContainer = $('<div></div>');

  $mapContainer.css({
    width: '100%',
    height: $(window).height() - $('nav').height(),
    marginTop: $('nav').height() - 10,
    overflow: 'hidden'
  });
  $mapContainer.append($map);
  $map.css({
    width: '100%',
    height: '100%'
  });
  var $panel = $('<div id="panel"></div>');

  $('body').append($mapContainer);
  $('body').append($panel);
  $(window).on('resize', function () {
    $mapContainer.css({
      height: $(window).height() - $('nav').height() - 10,
    });
    google.maps.event.trigger(map, "resize");
  });

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
