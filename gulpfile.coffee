gulp = require 'gulp'

coffee      = require 'gulp-coffee'
coffeelint  = require 'gulp-coffeelint'
uglify      = require 'gulp-uglify'
concat      = require 'gulp-concat'
sass        = require 'gulp-sass'
minifyCSS   = require 'gulp-minify-css'
clean       = require 'gulp-clean'
sourcemaps  = require 'gulp-sourcemaps'
rename      = require 'gulp-rename'
runSequence = require 'run-sequence'
gulpif      = require 'gulp-if'
imagemin    = require 'gulp-imagemin'
livereload  = require 'gulp-livereload'
notify      = require 'gulp-notify'

config = {
  imagesPattern: 'src/images/**/*.{png,gif,jpg,jpeg}'
  fontsPattern: './assets/bower_components/bootstrap-sass/assets/fonts/**/*.{eot,svg,ttf,woff,woff2,otf}'
}

#
# Report potential coffeescript code violations.
#
gulp.task 'lint', ->
  gulp.src './assets/javascripts/**/*.coffee'
    .pipe coffeelint()
    .pipe coffeelint.reporter()

#
# Clear out the destination folder.
#
gulp.task 'clean', ->
  gulp.src './public/*'
    .pipe clean force: true

#
# Compile and minify stylesheets.
#
gulp.task 'stylesheets', ->
  sass_opts =
    includePaths: [
      './assets/bower_components/bootstrap-sass/assets/stylesheets/'
      './assets/stylesheets/'
    ]

  min_opts =
    comments: true
    spare: true

  gulp.src ['./assets/stylesheets/application.sass']
    .pipe(sass(sass_opts)).on 'error', notify.onError (error) ->
      "Sass error: #{error.message}"
    .pipe minifyCSS min_opts
    .pipe concat('app.css')
    .pipe rename('app.min.css')
    .pipe gulp.dest './public/stylesheets'
    .pipe livereload()

#
# Concatenate vendor scripts.
#
gulp.task 'vendor-scripts', ->
  gulp.src ['./assets/bower_components/jquery/dist/jquery.js',
            './assets/bower_components/bootstrap-sass/assets/javascripts/bootstrap.js']
    .pipe sourcemaps.init()
    .pipe concat 'vendor.js'
    .pipe uglify()
    .pipe sourcemaps.write('.')
    .pipe gulp.dest './public/javascripts'
    .pipe rename 'vendor.min.js'
    .pipe gulp.dest './public/javascripts'
    .pipe livereload()

#
# Compile and concatenate app scripts.
#
gulp.task 'app-scripts', ->
  gulp.src ['./assets/javascripts/**/*.coffee']
    .pipe sourcemaps.init()
    .pipe(coffee()).on 'error', notify.onError (error) ->
      "CoffeeScript error: #{error.message}"
    .pipe concat 'app.js'
    .pipe uglify()
    .pipe sourcemaps.write('.')
    .pipe gulp.dest './public/javascripts'
    .pipe rename 'app.min.js'
    .pipe gulp.dest './public/javascripts'
    .pipe livereload()

#
# Copy images.
#
gulp.task 'copy-images', ->
  gulp.src config.imagesPattern
    .pipe imagemin()
    .pipe gulp.dest './public/images'
    .pipe livereload()

#
# Copy fonts.
#
gulp.task 'copy-fonts', ->
  gulp.src config.fontsPattern
    .pipe gulp.dest './public/fonts'
    .pipe livereload()

#
# Watch for changes to compiled / concatenated files.
#
gulp.task 'watch', ->
  livereload.listen()
  gulp.watch ['assets/javascripts/**/*.coffee'], ['lint', 'app-scripts']
  gulp.watch ['assets/stylesheets/**/*.sass'], ['stylesheets']
  gulp.watch [config.imagesPattern], ['copy-images']
  gulp.watch [config.fontsPattern], ['copy-fonts']

#
# Build the assets.
#
gulp.task 'build', ->
  runSequence(
    ['clean'],
    ['lint',
     'stylesheets',
     'vendor-scripts',
     'app-scripts',
     'copy-fonts',
     'copy-images']
  )

#
# By defualt, build all files & run a development server.
#
gulp.task 'default', ->
  runSequence ['build', 'watch']
