import path from 'path';
import gulp from 'gulp';
import gulpsync from 'gulp-sync';
import gutil from 'gulp-util';
import babel from 'gulp-babel';
import sourcemaps from 'gulp-sourcemaps';
import concat from 'gulp-concat';
import webdriver from 'gulp-webdriver';
import gulpdocker from 'gulp-docker';
import webpack from 'webpack';
import webpackstream from 'webpack-stream';
import WebpackDevServer from 'webpack-dev-server';
import webpackConfig from './webpack.config.js';
import R from 'ramda';
import pkg from './package.json';
const sync = gulpsync(gulp).sync;
let seleniumServer;

function onDone(taskName, done) {
  return (err, stats) => {
    if (err) throw new gutil.PluginError(taskName, err);
    else gutil.log('[' + taskName + ']', stats.toString());

    if (done) done();
  };
}

gulp.task('build', (done) => {
  webpack(webpackConfig.client).run(onDone('build', done));
});

gulp.task('watch', (done) => {
  return gulp.src('src/main/react/index.js')
    .pipe(webpackstream(webpackConfig.client))
    .pipe(gulp.dest('src/main/resources/static/js/'));
});
