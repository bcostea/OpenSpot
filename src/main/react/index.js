/** @jsx React.DOM */
'use strict';

var App = React.createClass({
  render() {
    return <h1>Hello, world from React!</h1>;
  }
});

ReactDOM.render(<App />, document.getElementById('react-demo-container'));
