const express = require('express');
const router = express.Router();
const controller = require('../controllers/oscars');

/**
 * GET /winner-by-name-actor/:nameActor
 * Retrieves the Oscar-winning data for an actor based on the provided actor name.
 */
router.get('/winner-by-name-actor/:nameActor', async (req, res) => {
    const { nameActor } = req.params;

    // Check that actor name was provided.
    if (!nameActor) {
        console.error('No actor name provided');
        return res.status(400).json({ error: 'No actor name provided' });
    }

    try {
        // Fetch the winner details for the actor.
        const oscars = await controller.getActorsWinner(nameActor);
        return res.status(200).json(oscars);
    } catch (err) {
        console.error('Error retrieving actor winner details:', err);
        return res.status(500).json({ error: err.message || err });
    }
});

/**
 * GET /by-movie-title/:movieTitle
 * Retrieves Oscar details for a specific movie based on its title.
 */
router.get('/by-movie-title/:movieTitle', async (req, res) => {
    const { movieTitle } = req.params;

    // Validate that a movie title was provided.
    if (!movieTitle) {
        console.error('No movie title provided');
        return res.status(400).json({ error: 'No movie title provided' });
    }

    try {
        // Fetch the Oscars data associated with the movie.
        const oscars = await controller.getOscarsByFilm(movieTitle);
        return res.status(200).json(oscars);
    } catch (err) {
        console.error('Error retrieving Oscars by movie title:', err);
        return res.status(500).json({ error: err.message || err });
    }
});

/**
 * GET /by-name/:name
 * Retrieves Oscar details for a person (actor or crew) based on the provided name.
 */
router.get('/by-name/:name', async (req, res) => {
    const { name } = req.params;

    // Validate that a name was provided.
    if (!name) {
        console.error('No name provided');
        return res.status(400).json({ error: 'No name provided' });
    }

    try {
        // Fetch the Oscars data for the given name.
        const oscars = await controller.getOscarsByName(name);
        return res.status(200).json(oscars);
    } catch (err) {
        console.error('Error retrieving Oscars by name:', err);
        return res.status(500).json({ error: err.message || err });
    }
});

module.exports = router;
