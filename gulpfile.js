var gulp = require('gulp'),
    jade = require('gulp-jade'),
    sass = require('gulp-sass'),
    clean = require('gulp-clean'),
    useref = require('gulp-useref'),
    gulpif = require('gulp-if'),
    uglify = require('gulp-uglify'),
    minifyCss = require('gulp-minify-css'),
    imagemin = require('gulp-imagemin'),
    size = require('gulp-size'),
    browserSync = require('browser-sync').create(),
    reload = browserSync.reload;

/*********************
** Local Development**
**********************/

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


/*********************
**** Dist building ***
**********************/

gulp.task('clean', function() {
    return gulp.src('dist')
        .pipe(clean());
});

gulp.task('useref', function() {
    return gulp.src('app/*.html')
        .pipe(gulpif('*.js', uglify()))
        .pipe(gulpif('*.css', minifyCss({
            compatibility: 'ie8'
        })))
        .pipe(useref())
        .pipe(gulp.dest('dist'));
});

gulp.task('images', function() {
    return gulp.src('app/img/**/*')
        .pipe(imagemin({
            progressive: true,
            interlaced: true
        }))
        .pipe(gulp.dest('dist/img'));
})

gulp.task('extras', function() {
    return gulp.src([
        'app/*.*',
        '!app/*.html'
        ]).pipe(gulp.dest('dist'));
});

gulp.task('dist', ['useref', 'images', 'extras'], function() {
    return gulp.src('dist/**/*').pipe(size({title: 'build'}));
})

gulp.task('build', ['clean', 'jade'], function() {
    gulp.start('dist');
})

gulp.task('server-dist', function() {
    browserSync.init({
        notify: false,
        port: 9000,
        server: {
            baseDir: 'dist'
        }
    });
});



/*********************
****** Functions *****
**********************/

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