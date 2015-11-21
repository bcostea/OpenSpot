'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import { GoogleMap, Marker } from 'react-google-maps';
import SockJS from 'sockjs-client';
import { Stomp } from './stomp.js';
import { pipe, always } from 'ramda';

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

function makeMarker(item) {
   return {
     position: {
       lat: item.position[0],
       lng: item.position[1]
     },
     key: item.id,
     defaultAnimation: 2,
     icon: statuses[item.status]
   }
}

var App = React.createClass({

  getInitialState: always({ markers: [] }),

  componentDidMount: function () {
    var that = this;
    let stompClient = Stomp.over(new SockJS('/api/public/websocket/spots/all'));
    this.stompClient = stompClient;

    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/parkingSpots', function(response){
          let markers = JSON.parse(response.body);
          let list = that.state.markers.map(function (x) {
            return x.id;
          });
          markers = markers
            .filter(function (x) {
              return -1 === list.indexOf(x.id);
            })
            .map(makeMarker);
          markers = markers.concat(that.state.markers);
          that.setState({ markers: markers });
        });

        stompClient.subscribe('/topic/spots/update', function (response) {
          let rawMarker = JSON.parse(response.body);
          let markers = that.state.markers.map(function (x) {
            if (x.key === rawMarker.id) {
              x.icon = statuses[rawMarker.status];
            }
            return x;
          });
          that.setState({ markers: markers });
        });

        setTimeout(function () {
          stompClient.send('/api/public/websocket/spots/all', {}, JSON.stringify({}));
        }, 1000);
    });

  },

  componentWillUnmount: function () {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
  },

  handleMapClick: function (event) {
    if (!window.parkingLots) {
      window.parkingLots = "";
    }
    window.parkingLots += 'parkingSpotRepository.save(new ParkingSpot(' + event.latLng.lat() + ', ' + event.latLng.lng() + '))' + "\n";
  },

  render: function () {
    return (
      <section style={mapProps.style}>
        <GoogleMap containerProps={mapProps}
          onClick={this.handleMapClick}
          ref='map'
          defaultZoom={18}
          defaultCenter={{lat: this.props.lat, lng: this.props.lng}}>
          {this.state.markers.map((marker, index) => {
            return (
              <Marker {...marker} />
            )
          })}
         </GoogleMap>
      </section>
    );
  }
});

const appStyle = {
  width: '500px',
  height: '300px',
  background: 'red'
};



setTimeout(function () {
    navigator.geolocation.getCurrentPosition(function (position) {
      ReactDOM.render(<App lat={position.coords.latitude} lng={position.coords.longitude} />, document.getElementById('react-container'));
      console.log(position);
    })
}, 500);
