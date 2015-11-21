/** @jsx React.DOM */
'use strict';

var App = React.createClass({displayName: "App",
  render() {
    return React.createElement("h1", null, "Hello, world from React auto!");
  }
});

ReactDOM.render(React.createElement(App, null), document.getElementById('react-demo-container'));
