const reviewsModel = require('../models/reviews');

/**
 * Retrieves reviews for a given movie title, sorted by review_date in descending order,
 * with support for pagination using limit and skip.
 *
 * @param {string} movieTitle - The title of the movie.
 * @param {number} limit - Maximum number of reviews to return.
 * @param {number} skip - Number of reviews to skip (for pagination).
 * @returns {Promise<Array>} - A promise that resolves with an array of reviews.
 */
async function getReviewsByMovieTitle(movieTitle, limit, skip) {
    return reviewsModel.find({ movie_title: movieTitle })
        .sort({ review_date: -1 })
        .limit(limit)
        .skip(skip);
}

/**
 * Retrieves an array of movie titles from the top 20 reviews of a specified review type.
 * The reviews are filtered by a valid movie title, non-null review_score, and the given review_type.
 * The movies are grouped by title and sorted in descending order by the highest review_score.
 *
 * @param {string} type - The review type (e.g., "Fresh" or "Rotten").
 * @returns {Promise<Array<string>>} - A promise that resolves with an array of movie titles.
 */
async function getRatedMoviesByType(type) {
    // Perform an aggregation pipeline to get the top 20 movies by review score.
    const results = await reviewsModel.aggregate([
        // Match documents with a valid movie title, non-null review_score, and the specified review_type.
        {
            $match: {
                movie_title: { $type: "string" },
                review_score: { $ne: null },
                review_type: type
            }
        },
        // Group reviews by movie title and compute the maximum review_score per movie.
        { $group: { _id: "$movie_title", highestReviewScore: { $max: "$review_score" } } },
        // Sort movies in descending order by highestReviewScore.
        { $sort: { highestReviewScore: -1 } },
        // Limit the result to the top 20 movies.
        { $limit: 20 },
        // Project the movie title, renaming _id to movie_title.
        { $project: { _id: 0, movie_title: "$_id" } }
    ]);

    // Return only the movie_title from each document.
    return results.map(item => item.movie_title);
}

module.exports = {
    getReviewsByMovieTitle,
    getRatedMoviesByType
};
