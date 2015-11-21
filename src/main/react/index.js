'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import { GoogleMap, Marker } from 'react-google-maps';

import SockJS from 'sockjs-client';
import { Stomp } from './stomp.js';

const mapProps = {
  style: {
    height: '100%',
    width: '100%'
  }
};
function makeMarker (item) {
   return {
     position: {
       lat: item.position[0] + Math.random() / 5,
       lng: item.position[1] + Math.random() / 5
     },
     key: item.id + Math.random(),
     defaultAnimation: 2,
     icon: {
       path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
       fillOpacity: 0.8,
       scale: 1,
       strokeColor: item.status === 'FREE' ? 'green' : 'red',
       strokeWeight: 14
     }
   }
}
var App = React.createClass({
  getInitialState: function () {
    return {
      markers: []
    }
  },
  handleEvent: function () {
    this.setState();
  },

  componentDidMount: function () {
    var that = this;
    let stompClient = Stomp.over(new SockJS('/api/public/websocket/spots/all'));
    this.stompClient = stompClient;
    console.log(that);

    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/parkingSpots', function(parkingSpots){
          let markers = JSON.parse(parkingSpots.body)
          if (typeof markers !== 'array') {
            console.log(makeMarker(markers));
            markers = that.state.markers.concat([makeMarker(markers)]);
          } else {
            markers = markers.map(makeMarker);
          }
          that.setState({ markers: markers });
        });

        setTimeout(function () {
          stompClient.send("/api/public/websocket/spots/all", {}, JSON.stringify({}));
        }, 1000);
    });

  },

  componentWillUnmount: function () {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
  },

  render: function () {
    return (
      <section style={mapProps.style}>
        <GoogleMap containerProps={mapProps}
          ref='map'
          defaultZoom={10}
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
