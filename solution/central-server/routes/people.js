const express = require('express');
const router = express.Router();
const axios = require('axios');

// Base URLs for the API endpoints.
const mongoDBAPI = 'http://localhost:3001';
const springBootAPI = 'http://localhost:8080/api';

// Create Axios instances for cleaner API calls.
// springClient is used for Spring Boot API endpoints.
// mongoClient is used for MongoDB API endpoints.
const springClient = axios.create({ baseURL: springBootAPI });
const mongoClient = axios.create({ baseURL: mongoDBAPI });

/**
 * GET /actor/:actorName
 * Retrieves the actor's page displaying:
 *  - Movies the actor appears in (from Spring API)
 *  - Associated Oscar awards (from Mongo API)
 */
router.get('/actor/:actorName', async (req, res) => {
    const { actorName } = req.params;
    if (!actorName) {
        return res.render('pages/error', { status: 400, message: 'Actor Name is required' });
    }
    try {
        const { movies, oscars } = await fetchPersonDetails('actor', actorName);
        res.render('pages/person', {
            name: actorName,
            movies: {
                category: "Appears On",
                movies: movies
            },
            oscars: oscars
        });
    } catch (error) {
        console.error('Error retrieving actor details:', error.status || '', error.message || error);
        const status = error.status || 500;
        const errorMsg = error.message || 'An unexpected error occurred.';
        res.render('pages/error', { status, message: errorMsg });
    }
});

/**
 * GET /crew/:crewName
 * Retrieves the crew member's page displaying:
 *  - Movies the crew member worked on (from Spring API)
 *  - Associated Oscar awards (from Mongo API)
 */
router.get('/crew/:crewName', async (req, res) => {
    const { crewName } = req.params;
    if (!crewName) {
        return res.render('pages/error', { status: 400, message: 'Crew Name is required' });
    }
    try {
        const { movies, oscars } = await fetchPersonDetails('crew', crewName);
        res.render('pages/person', {
            name: crewName,
            movies: {
                category: "Worked On",
                movies: movies
            },
            oscars: oscars
        });
    } catch (error) {
        console.error('Error retrieving crew details:', error.status || '', error.message || error);
        const status = error.status || 500;
        const errorMsg = error.message || 'An unexpected error occurred.';
        res.render('pages/error', { status, message: errorMsg });
    }
});

/**
 * Fetches details for a person (actor or crew) by concurrently retrieving:
 *  - Movie data from the Spring Boot API.
 *  - Oscar data from the MongoDB API.
 *
 * @param {string} personType - Either "actor" or "crew".
 * @param {string} personName - The name of the person.
 * @returns {Promise<Object>} - An object containing movies and oscars data.
 */
async function fetchPersonDetails(personType, personName) {
    try {
        // Build query parameters.
        const params = { personType, personName };

        // Execute API calls concurrently using Promise.all.
        const [springResponse, mongoResponse] = await Promise.all([
            springClient.get('/movies/by-name', { params }),
            mongoClient.get('/oscars/by-name/' + personName )
        ]);

        return {
            movies: springResponse.data,
            oscars: mongoResponse.data
        };
    } catch (error) {
        if (error.response) {
            console.error('Error fetching person details:', error.response.status, error.response.data);
            const err = new Error(error.response.data || 'Error fetching person details');
            err.status = error.response.status;
            throw err;
        } else if (error.request) {
            console.error('No response received from the server for person details:', error.request);
            const err = new Error('No response received from the server');
            err.status = 503;
            throw err;
        } else {
            console.error('Error setting up request for person details:', error.message);
            const err = new Error('Error setting up request');
            err.status = 500;
            throw err;
        }
    }
}

module.exports = router;