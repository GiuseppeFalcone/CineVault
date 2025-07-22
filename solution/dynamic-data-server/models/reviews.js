const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ReviewSchema = new Schema({
    rotten_tomatoes_link: { type: String, required: true },
    movie_title:          { type: String, required: true },
    critic_name:          { type: String, required: true },
    top_critic:           { type: Boolean, required: true },
    publisher_name:       { type: String, required: true },
    review_type:          { type: String, required: true },
    review_score:         { type: Number },
    review_date:          { type: Date },
    review_content:       { type: String }
});

module.exports = mongoose.model('Review', ReviewSchema, 'rotten-tomatoes-reviews');