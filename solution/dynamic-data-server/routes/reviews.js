const express = require('express');
const router = express.Router();
const controller = require('../controllers/reviews');

/**
 * GET /by-movie-title/:movieTitle
 * Retrieves reviews for a specific movie based on its title.
 * Supports pagination using query parameters "limit" and "skip".
 */
router.get('/by-movie-title/:movieTitle', async (req, res) => {
    const { movieTitle } = req.params;
    const limit = parseInt(req.query.limit) || 10;
    const skip = parseInt(req.query.skip) || 0;

    // Validate that a movie title is provided.
    if (!movieTitle) {
        console.error('No title provided');
        return res.status(400).json({ error: 'No title provided' });
    }

    try {
        // Retrieve reviews from the controller.
        const reviews = await controller.getReviewsByMovieTitle(movieTitle, limit, skip);
        res.status(200).json(reviews);
    } catch (err) {
        console.error(`Error retrieving reviews for movie_title: ${movieTitle}, error: ${err}`);
        res.status(500).json({ error: err.message });
    }
});

/**
 * GET /by-rate-type
 * Returns an array of movie titles from the top 20 reviews
 * sorted by review_score in descending order for a given review type.
 * Expected review types are "Fresh" or "Rotten".
 */
router.get('/by-rate-type', async (req, res) => {
    const { type } = req.query;
    console.log('Review type:', type);

    // Validate the provided type.
    if (!type) {
        console.error('No type provided');
        return res.status(400).json({ error: 'No type provided' });
    } else if (type !== 'Fresh' && type !== 'Rotten') {
        console.error('Wrong type provided');
        return res.status(400).json({ error: 'Wrong type provided' });
    }

    try {
        // Retrieve the top rated movie titles from the controller.
        const movieTitles = await controller.getRatedMoviesByType(type);
        res.status(200).json(movieTitles);
    } catch (err) {
        console.error('Error fetching top rated movies:', err);
        res.status(500).json({ error: err.message });
    }
});

module.exports = router;