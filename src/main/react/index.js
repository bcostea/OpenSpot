'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import {google, GoogleMap, Marker} from 'react-google-maps';

const mapProps = {
  style: {
    height: '100%',
    width: '100%'
  }
};

var App = React.createClass({
  getInitialState: function () {
    return {
      markers: [{
        position: {
          lat: 25.0112183,
          lng: 121.52067570000001,
        },
        key: 'Taiwan',
        defaultAnimation: 2
      }]
    }
  },
  render: function () {
    return (
      <section style={mapProps.style}>
        <GoogleMap containerProps={mapProps}
          ref='map'
          defaultZoom={3}
          defaultCenter={{lat: -25.363882, lng: 131.044922}}>
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
ReactDOM.render(<App />, document.getElementById('react-container'));
}, 500);
