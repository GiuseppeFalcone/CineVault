const createError = require('http-errors');
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');

const swaggerUi = require('swagger-ui-express');
const openApiDocumentation = require('./swagger/swaggerDocumentation.json');

const indexRouter = require('./routes/index');
const usersRouter = require('./routes/users');
const moviesRouter = require('./routes/movies');
const peopleRouter = require('./routes/people');

const app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
const { engine } = require('express-handlebars');
app.engine(
  'hbs',
  engine({
    extname: '.hbs',
    defaultLayout: 'layout',
    layoutsDir: path.join(__dirname, 'views/layouts'),
    partialsDir: path.join(__dirname, 'views/partials'),
    helpers: {
      eq: (a, b) => a === b,
      formatDate: (isoDate) => {
        const date = new Date(isoDate);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
      },
      hasOscar: (personName, oscars) => {
        if (!oscars || !personName) return false;
        return oscars.some((oscar) => oscar.name.includes(personName));
      },
      oscarWinner: (personName, oscars) => {
        if (!oscars || !personName) return false;
        const found = oscars.find((oscar) => oscar.name.includes(personName));
        return found ? found.winner : false;
      },
      length(array) {
        return Array.isArray(array) ? array.length : 0;
      },
      gt(a, b) {
        return a > b;
      },
      lt(a, b) {
        return a < b;
      },
      subtract(a, b) {
        return a - b;
      },
    },
  })
);
app.set('view engine', 'hbs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/movies', moviesRouter);
app.use('/person', peopleRouter);
app.use(
  '/api-docs',
  swaggerUi.serve,
  swaggerUi.setup(openApiDocumentation, {
    customSiteTitle: 'Central Server API Documentation',
    swaggerOptions: {
      docExpansion: 'none',
      displayRequestDuration: true,
      filter: true,
      showExtensions: true,
      showCommonExtensions: true,
    },
  })
);

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('pages/error');
});

module.exports = app;
