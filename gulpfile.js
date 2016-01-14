var
    gulp = require('gulp'),
    jade = require('gulp-jade'),
    sass = require('gulp-sass'),
    browserSync = require('browser-sync').create(),
    reload = browserSync.reload;


gulp.task('server', ['jade'], function() {
    browserSync.init({
        notify: false,
        port: 9000,
        server: {
            baseDir: 'app'
        }
    });
});

gulp.task('jade', function() {
    gulp.src('./app/jade/pages/*.jade')
        .pipe(jade({
            pretty: true
        }))
        .on('error', log)
        .pipe(gulp.dest('./app/'))
        .pipe(reload({stream: true}));
});

gulp.task('sass', function() {
    gulp.src('./app/scss/*.scss')
        .pipe(sass())
        .on('error', log)
        .pipe(gulp.dest("./app/css/"))
});

gulp.task('watch', function() {
    gulp.watch(['./app/jade/**/*.jade'], ['jade']);
    gulp.watch(['./app/scss/*.scss'], ['sass']);
    gulp.watch([
        './app/css/*.css',
        './app/js/*.js'
    ]).on('change', reload);
});

gulp.task('default', ['server', 'watch']);


function log(error) {
    console.log([
        '',
        "----------ERROR MESSAGE START----------",
        ("[" + error.name + " in " + error.plugin + "]"),
        error.message,
        "----------ERROR MESSAGE END----------",
        ''
    ].join('\n'));
    this.end();
}