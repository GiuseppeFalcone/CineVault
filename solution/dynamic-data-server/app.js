const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const mongoose = require('mongoose');

const swaggerUi = require('swagger-ui-express');
const openApiDocumentation = require('./swagger/swaggerDocumentation.json');

const reviewsRouter = require('./routes/reviews');
const oscarsRouter = require('./routes/oscars');

const {request} = require("express");

// Used to require the MongoDB database connection
const database = require("./databases/database");

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/reviews', reviewsRouter);
app.use('/oscars', oscarsRouter);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(openApiDocumentation,
    {
        customSiteTitle: 'Dynamic Data Server API Documentation',
        swaggerOptions: {
            persistAuthorization: true,
            displayRequestDuration: true,
            docExpansion: 'none',
            filter: true
        }
    }
));

module.exports = app;
