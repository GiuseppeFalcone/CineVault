const express = require("express");
const router = express.Router();
const axios = require("axios");

// Set base API endpoints
const mongoDBAPI = "http://localhost:3001";
const springBootAPI = "http://localhost:8080/api";

// Create Axios instances for cleaner API calls.
// springClient interacts with the Spring Boot API endpoints.
// mongoClient interacts with the MongoDB API endpoints.
const springClient = axios.create({ baseURL: springBootAPI });
const mongoClient = axios.create({ baseURL: mongoDBAPI });

/**
 * GET /
 * Renders the homepage with:
 *  - Genre categories (from Spring API)
 *  - Newest movies (from Spring API)
 *  - Top reviewed movies (from Mongo API)
 */
router.get("/", async (req, res) => {
  try {
    const [genreCategories, newest, topRated] = await Promise.all([
      fetchGenreRows(),
      fetchNewestMovies(),
      fetchTopReviewedMovies(),
    ]);
    res.render("pages/index", {
      activeHome: true,
      genreCategories,
      newest,
      topRated,
    });
  } catch (error) {
    console.error(
      "Error fetching homepage data:",
      error.status || "",
      error.message || error
    );
    const status = error.status || 500;
    const errorMsg = error.message || "An unexpected error occurred.";
    res.render("pages/error", { status, message: errorMsg });
  }
});

/**
 * GET /search
 * Searches for movies using provided query parameters:
 *  - field: The search field (e.g., title, actor, genre)
 *  - value: The search keyword
 * Renders the search results page.
 */
router.get("/search", async (req, res) => {
  const { field, value, offset } = req.query;
  try {
    const { data: movies } = await springClient.get("/movies/search", {
      params: { field, value, offset },
    });
    if (parseInt(offset) > 0) {
      res.render("partials/movie-list", { movies, layout: false });
    } else {
      res.render("pages/search-results", {
        movies,
        field: field.toUpperCase(),
        value,
      });
    }
  } catch (error) {
    console.error(
      "Error performing search:",
      error.status || "",
      error.message || error
    );
    const status = error.status || 500;
    const errorMsg = error.message || "An unexpected error occurred.";
    res.render("pages/error", { status, message: errorMsg });
  }
});

/**
 * GET /chat
 * -----------
 * Renders the chat page.
 */
router.get("/chat", (req, res) => {
  res.render("pages/chat", { activeChat: true });
});

/**
 * Fetch genre rows from the Spring Boot API.
 *
 * @returns {Promise<Object>} Data returned from the API.
 */
async function fetchGenreRows() {
  try {
    const { data } = await springClient.get("/movies/random-genre-based");
    if (!data || Object.keys(data).length === 0) {
      throw new Error("No genre data found");
    }
    return data;
  } catch (error) {
    if (error.response) {
      // Server responded with a status code outside 2xx
      console.error(
        "Error fetching genre rows:",
        error.response.status,
        error.response.data
      );
      const err = new Error(error.response.data || "Error fetching genre rows");
      err.status = error.response.status;
      throw err;
    } else if (error.request) {
      // No response received from server
      console.error("No response received from the server:", error.request);
      const err = new Error("No response received from the server");
      err.status = 503;
      throw err;
    } else {
      // Error setting up the request
      console.error("Error setting up request:", error.message);
      const err = new Error("Error setting up request");
      err.status = 500;
      throw err;
    }
  }
}

/**
 * Fetch top reviewed movies.
 * Retrieves movie titles from the MongoDB API based on "Fresh" reviews,
 * then fetches detailed movie data from the Spring Boot API.
 *
 * @returns {Promise<Array>} Array of top reviewed movies.
 */
async function fetchTopReviewedMovies() {
  try {
    // Get movie titles with "Fresh" reviews.
    const { data: movieTitles } = await mongoClient.get(
      "/reviews/by-rate-type",
      {
        params: { type: "Fresh" },
      }
    );
    // Fetch movies by titles from the Spring Boot API.
    const { data: movies } = await springClient.post(
      "/movies/get-by-titles",
      movieTitles
    );
    return movies;
  } catch (error) {
    if (error.response) {
      // Server responded with a status code outside 2xx
      console.error(
        "Error fetching top-reviewed movies:",
        error.response.status,
        error.response.data
      );
      const err = new Error(
        error.response.data || "Error fetching top-reviewed movies"
      );
      err.status = error.response.status;
      throw err;
    } else if (error.request) {
      // No response received from server
      console.error("No response received from the server:", error.request);
      const err = new Error("No response received from the server");
      err.status = 503;
      throw err;
    } else {
      // Error setting up the request
      console.error("Error setting up request:", error.message);
      const err = new Error("Error setting up request");
      err.status = 500;
      throw err;
    }
  }
}

/**
 * Fetch the newest movies from the Spring Boot API.
 *
 * @returns {Promise<Array>} Array of newest movies.
 */
async function fetchNewestMovies() {
  try {
    const { data } = await springClient.get("/movies/get-latest");
    return data;
  } catch (error) {
    if (error.response) {
      // Server responded with a status code outside 2xx
      console.error(
        "Error fetching newest movies:",
        error.response.status,
        error.response.data
      );
      const err = new Error(
        error.response.data || "Error fetching newest movies"
      );
      err.status = error.response.status;
      throw err;
    } else if (error.request) {
      // No response received from server
      console.error("No response received from the server:", error.request);
      const err = new Error("No response received from the server");
      err.status = 503;
      throw err;
    } else {
      // Error setting up the request
      console.error("Error setting up request:", error.message);
      const err = new Error("Error setting up request");
      err.status = 500;
      throw err;
    }
  }
}

module.exports = router;
