const mongoose = require('mongoose');
const mongoDBUrl = 'mongodb://localhost:27017/dynamic-data';

connection = mongoose.connect(mongoDBUrl, {
    checkServerIdentity: false
})
    .then(() => {
        console.log('Successfully connected to MongoDB Database!');
    })
    .catch((err) => {
        console.error('Error connecting to MongoDB Server ' + err);
    });