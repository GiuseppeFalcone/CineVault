const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const OscarSchema = new Schema({
    year_film: {type: Number, required: true},
    year_ceremony: {type: Number, required: true},
    ceremony: {type: Number, required: true},
    category: {type: String, required: true},
    name: {type: String, required: true},
    film: {type: String},
    winner: {type: Boolean, required: true},
});

module.exports = mongoose.model('Oscar', OscarSchema,'the-oscar-awards');