const oscarsModel = require('../models/oscars');

/**
 * Retrieves Oscar winning data for a given actor.
 *
 * @param {string} actorName - The actor's name.
 * @returns {Promise<Array>} - Promise resolving with an array of Oscar records.
 */
function getActorsWinner(actorName) {
    return oscarsModel.find({ name: actorName });
}

/**
 * Retrieves Oscar data for a specific film by its title.
 *
 * @param {string} movieTitle - The title of the movie.
 * @returns {Promise<Array>} - Promise resolving with an array of Oscar records.
 */
function getOscarsByFilm(movieTitle) {
    return oscarsModel.find({ film: movieTitle });
}

/**
 * Retrieves Oscar data for a person by their name.
 *
 * @param {string} name - The name of the person.
 * @returns {Promise<Array>} - Promise resolving with an array of Oscar records.
 */
function getOscarsByName(name) {
    return oscarsModel.find({ name: name });
}

module.exports = {
    getActorsWinner,
    getOscarsByFilm,
    getOscarsByName
};