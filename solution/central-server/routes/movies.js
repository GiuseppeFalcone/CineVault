const express = require('express');
const router = express.Router();
const axios = require('axios');

// Base URLs for the API endpoints
const mongoAPI = 'http://localhost:3001';
const springAPI = 'http://localhost:8080/api';

// Create axios instances for cleaner API calls.
// springClient is used to call the Spring API endpoints.
// mongoClient is used to call the Mongo API endpoints.
const springClient = axios.create({ baseURL: springAPI });
const mongoClient = axios.create({ baseURL: mongoAPI });

/**
 * GET /
 * Renders the main movies page by retrieving:
 *  - Movie filters (from Spring API)
 *  - The first 20 movies (using offset=0)
 */
router.get('/', async (req, res) => {
    try {
        // Fetch movie filters from the Spring API.
        const { data: filters } = await springClient.get('/movies/filters');

        // Fetch the first 20 movies using offset=0.
        const { data: movies } = await springClient.get('/movies/get-20-movies', {
            params: { offset: 0 }
        });

        // Render the movies page with filters and movies.
        res.render('pages/movies', {
            title: 'Movies',
            activeMovies: true,
            movies,
            filters
        });
    } catch (error) {
        console.error('Error retrieving movies:', error.status || '', error.message || error);
        const status = error.status || 500;
        const errorMsg = error.message || 'An unexpected error occurred.';
        res.render('pages/error', { status, message: errorMsg });
    }
});

/**
 * GET /load-more-movies
 * Retrieves additional movies using the 'offset' query parameter
 * for pagination/infinite scrolling.
 */
router.get('/load-more-movies', async (req, res) => {
    try {
        // Validate and parse the offset parameter.
        let offset = parseInt(req.query.offset, 10);
        if (isNaN(offset) || offset < 0) {
            offset = 0;
        }

        // Fetch additional movies based on the provided offset.
        const { data: movies } = await springClient.get('/movies/get-20-movies', {
            params: { offset }
        });

        // Render the movie list partial without the layout.
        res.render('partials/movie-list', { movies, layout: false });
    } catch (error) {
        console.error('Error fetching more movies:', error.status || '', error.message || error);
        const status = error.status || 500;
        const errorMsg = error.message || 'An unexpected error occurred.';
        res.render('pages/error', { status, message: errorMsg });
    }
});

/**
 * GET /get-filtered-movies
 * Retrieves movies based on filters provided in the query:
 *  - typeReviews (Fresh or Rotten)
 *  - genre, year, and director
 * Also applies movie titles fetched from Mongo API (if review filter is used).
 */
router.get('/get-filtered-movies', async (req, res) => {
    try {
        // Clone the query parameters from the request.
        const params = { ...req.query };

        // If a review type is provided, validate and normalize it.
        if (params.typeReviews && params.typeReviews.trim() !== "") {
            let reviewType = params.typeReviews.trim().toLowerCase();
            if (reviewType === 'fresh') {
                reviewType = 'Fresh';
            } else if (reviewType === 'rotten') {
                reviewType = 'Rotten';
            } else {
                res.render('pages/error', { message: 'Invalid review type provided' });
            }

            // Retrieve movie titles from Mongo API based on the review type.
            const { data: movieTitles } = await mongoClient.get('/reviews/by-rate-type', {
                params: { type: reviewType }
            });
            params.movieTitles = movieTitles;
        }

        // Send filter parameters to the Spring API to get filtered movies.
        const { data: filteredMovies } = await springClient.post('/movies/get-filtered-movies', params);

        // Render the movie list partial without the layout.
        res.render('partials/movie-list', { movies: filteredMovies, layout: false });
    } catch (error) {
        console.error('Error fetching filtered movies:', error.status || '', error.message || error);
        const status = error.status || 500;
        const errorMsg = error.message || 'An unexpected error occurred.';
        res.render('pages/error', { status, message: errorMsg });
    }
});

/**
 * GET /reviews-by-movie-title
 * Returns a JSON array of reviews for the specified movie title.
 * Supports pagination via 'limit' and 'skip' query parameters.
 */
router.get('/reviews-by-movie-title', async (req, res) => {
    try {
        const movieTitle = req.query.movieTitle;
        if (!movieTitle) {
            return res.render('pages/error', { status: 400, message: 'movieTitle parameter is required' });
        }

        const skip = parseInt(req.query.skip, 10) || 0;
        const limit = parseInt(req.query.limit, 10) || 15;
        const reviews = await getMovieReviews(movieTitle, limit, skip);

        res.json(reviews);
    } catch (error) {
        console.error('Error fetching reviews:', error.status || '', error.message || error);
        const status = error.status || 500;
        const errorMsg = error.message || 'An unexpected error occurred.';
        res.render('pages/error', { status, message: errorMsg });
    }
});

/**
 * GET /:movieId
 * Renders a detailed movie page showing:
 *  - Movie details (from Spring API)
 *  - Reviews (from Mongo API)
 *  - Oscar awards (from Mongo API)
 *  - Related movies (from Spring API)
 */
router.get('/:movieId', async (req, res) => {
    const movieId = req.params.movieId;
    try {
        const movie = await getMovieDetails(movieId);
        const [reviews, oscars, related] = await Promise.all([
            getMovieReviews(movie.title),
            getMovieOscars(movie.title),
            getRelatedMovies(movieId)
        ]);
        res.render('pages/single-movie', {
            title: movie.title,
            activeMovies: true,
            ...movie,
            reviews,
            oscars,
            related
        });
    } catch (error) {
        console.error('Error rendering single movie page:', error.status || '', error.message || error);
        const status = error.status || 500;
        const errorMsg = error.message || 'An unexpected error occurred.';
        res.render('pages/error', { status, message: errorMsg });
    }
});

/**
 * Retrieves movie details from the Spring API by movie ID.
 * @param {string} movieId - The movie's ID.
 * @returns {Promise<Object>} - Movie details.
 */
async function getMovieDetails(movieId) {
    try {
        const { data } = await springClient.get(`/movies/${movieId}`);
        return data;
    } catch (error) {
        if (error.response) {
            console.error("Error retrieving movie details:", error.response.status, error.response.data);
            const err = new Error(error.response.data || 'Error retrieving movie details');
            err.status = error.response.status;
            throw err;
        } else if (error.request) {
            console.error("No response received from the server for movie details:", error.request);
            const err = new Error('No response received from the server');
            err.status = 503;
            throw err;
        } else {
            console.error("Error setting up request for movie details:", error.message);
            const err = new Error('Error setting up request');
            err.status = 500;
            throw err;
        }
    }
}

/**
 * Retrieves reviews for a movie by its title from the Mongo API.
 * @param {string} movieTitle - The movie's title.
 * @param {number} limit - Maximum number of reviews.
 * @param {number} skip - Number of reviews to skip (for pagination).
 * @returns {Promise<Array>} - Array of reviews.
 */
async function getMovieReviews(movieTitle, limit = 10, skip = 0) {
    try {
        const { data } = await mongoClient.get(`/reviews/by-movie-title/${movieTitle}`, {
            params: { limit, skip }
        });
        return data;
    } catch (error) {
        if (error.response) {
            console.error(`Error retrieving reviews for movie "${movieTitle}":`, error.response.status, error.response.data);
            const err = new Error(error.response.data || `Error retrieving reviews for movie "${movieTitle}"`);
            err.status = error.response.status;
            throw err;
        } else if (error.request) {
            console.error(`No response received from the server for reviews of movie "${movieTitle}":`, error.request);
            const err = new Error('No response received from the server');
            err.status = 503;
            throw err;
        } else {
            console.error(`Error setting up request for reviews of movie "${movieTitle}":`, error.message);
            const err = new Error('Error setting up request');
            err.status = 500;
            throw err;
        }
    }
}

/**
 * Retrieves Oscar awards data for a movie by its title from the Mongo API.
 * @param {string} movieTitle - The movie's title.
 * @returns {Promise<Object>} - Oscar data.
 */
async function getMovieOscars(movieTitle) {
    try {
        const { data } = await mongoClient.get(`/oscars/by-movie-title/${movieTitle}`);
        return data;
    } catch (error) {
        if (error.response) {
            console.error(`Error retrieving Oscars for movie "${movieTitle}":`, error.response.status, error.response.data);
            const err = new Error(error.response.data || `Error retrieving Oscars for movie "${movieTitle}"`);
            err.status = error.response.status;
            throw err;
        } else if (error.request) {
            console.error(`No response received from the server for Oscars of movie "${movieTitle}":`, error.request);
            const err = new Error('No response received from the server');
            err.status = 503;
            throw err;
        } else {
            console.error(`Error setting up request for Oscars of movie "${movieTitle}":`, error.message);
            const err = new Error('Error setting up request');
            err.status = 500;
            throw err;
        }
    }
}

/**
 * Retrieves related movies from the Spring API using the movie ID.
 * @param {string} movieId - The movie's ID.
 * @returns {Promise<Array>} - Array of related movies.
 */
async function getRelatedMovies(movieId) {
    try {
        const { data } = await springClient.get('/movies/get-related', {
            params: { movieId }
        });
        return data;
    } catch (error) {
        if (error.response) {
            console.error("Error retrieving related movies:", error.response.status, error.response.data);
            const err = new Error(error.response.data || 'Error retrieving related movies');
            err.status = error.response.status;
            throw err;
        } else if (error.request) {
            console.error("No response received from the server for related movies:", error.request);
            const err = new Error('No response received from the server');
            err.status = 503;
            throw err;
        } else {
            console.error("Error setting up request for related movies:", error.message);
            const err = new Error('Error setting up request');
            err.status = 500;
            throw err;
        }
    }
}

module.exports = router;