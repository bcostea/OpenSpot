const path = require('path');
const fs = require('fs');
const DeepMerge = require('deep-merge');
const deepmerge = DeepMerge(function(target, source, key) {
  if(target instanceof Array) {
    return [].concat(target, source);
  }
  return source;
});
const excludes = /node_modules/;

var defaultConfig = {
  module: {
    preloaders: [
    ],
    loaders: [
      { test: /\.js$/, exclude: excludes, loader: "eslint-loader" },
      { test: /\.js$/, exclude: excludes, loader: 'babel?presets[]=es2015&presets[]=react' }
    ]
  },
  devtool: 'source-map', // #eval-source-map to avoid creating source maps
  debug: true
};

const config = overrides => deepmerge(defaultConfig, overrides || {});

var nodeModules = fs.readdirSync('node_modules')
  .filter(x => ['.bin'].indexOf(x) === -1)
  .reduce((acc, x) => {
    acc[x] = 'commonjs ' + x;
    return acc;
  }, {});

module.exports = {

  client: config({
    watch: true,
    entry: './src/main/react/index.js',
    output: {
      path: path.join(__dirname, './src/main/resources/static/js/'),
      filename: 'index.js'
    },
    module: {
      loaders: [
        { test: /\.css$/, loader: 'style-loader!css-loader!postcss-loader' }
      ]
    },
    postcss: function () {
      return [require('autoprefixer'), require('precss')];
    }
  })
};
