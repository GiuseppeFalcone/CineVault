// MovieManager class encapsulates all movie-related operations.
class MovieManager {
    constructor() {
        // --- Global State ---
        this.offset = 20;               // Tracks the current offset for pagination.
        this.activeFilters = {};        // Stores active filter parameters.

        // --- DOM Elements ---
        this.moviesContainer = document.getElementById("movies-container");
        this.loadMoreBtn = document.getElementById("loadMoreButton");
        this.applyFiltersBtn = document.getElementById("applyFiltersBtn");
        this.resetFiltersBtn = document.getElementById("resetFiltersBtn");
        this.genreFilter = document.getElementById("genreFilter");
        this.languageFilter = document.getElementById("languageFilter");
        this.countryFilter = document.getElementById("countryFilter");
        this.releaseYearFilter = document.getElementById("releaseYearFilter");
        this.typeReviews = document.getElementById("typeReviews");

        // --- Initialize Event Listeners ---
        this.addEventListeners();
    }

    /**
     * Fetches and displays movies based on the provided filter parameters.
     * @param {Object} params - Filter parameters including offset.
     */
    async getMovies(params) {
        try {
            const response = await axios.get('/movies/get-filtered-movies', { params });
            this.moviesContainer.innerHTML = response.data;
        } catch (error) {
            console.error("Error retrieving movies:", error.message);
            this.moviesContainer.innerHTML =
                '<div class="alert alert-danger">Error retrieving movies</div>';
        }
    }

    /**
     * Loads default movies (without filters) starting from offset 0.
     */
    async resetMovies() {
        try {
            const response = await axios.get('/movies/load-more-movies', { params: { offset: 0 } });
            this.moviesContainer.innerHTML = response.data;
        } catch (error) {
            console.error("Error retrieving movies:", error.message);
            this.moviesContainer.innerHTML =
                '<div class="alert alert-danger">Error retrieving movies</div>';
        }
    }

    /**
     * Loads additional movies and appends them to the movie container.
     * Chooses the endpoint based on whether filters are active.
     */
    async loadMoreMovies() {
        try {
            let response;
            if (Object.keys(this.activeFilters).length > 0) {
                // Fetch filtered movies using the current offset.
                const params = { ...this.activeFilters, offset: this.offset };
                response = await axios.get('/movies/get-filtered-movies', { params });
            } else {
                // Otherwise, use the default "load more" endpoint.
                response = await axios.get('/movies/load-more-movies', { params: { offset: this.offset } });
            }
            // Append the new movies to the container.
            this.moviesContainer.innerHTML += response.data;
            // Update offset for the next batch.
            this.offset += 20;
        } catch (error) {
            console.error("Error loading more movies:", error.message);
            this.moviesContainer.innerHTML =
                '<div class="alert alert-danger">Error retrieving movies</div>';
        }
    }

    /**
     * Gathers filter values from inputs, resets the offset,
     * stores active filters, and fetches filtered movies.
     */
    handleApplyFilters() {
        const params = {
            genre: this.genreFilter.value,
            language: this.languageFilter.value,
            country: this.countryFilter.value,
            releaseYear: this.releaseYearFilter.value,
            typeReviews: this.typeReviews.value,
            offset: 0,
        };
        this.activeFilters = params;
        this.offset = 20;
        this.getMovies(params);
    }

    /**
     * Clears all filter inputs, resets filter state and offset,
     * then loads the default movies list.
     */
    async handleResetFilters() {
        // Clear filter input fields.
        this.genreFilter.value = "";
        this.languageFilter.value = "";
        this.countryFilter.value = "";
        this.releaseYearFilter.value = "";
        this.typeReviews.value = "";

        this.activeFilters = {};
        this.offset = 20;
        await this.resetMovies();
    }

    /**
     * Attaches event listeners to the buttons.
     */
    addEventListeners() {
        if (this.applyFiltersBtn) {
            this.applyFiltersBtn.addEventListener("click", () => this.handleApplyFilters());
        }
        if (this.resetFiltersBtn) {
            this.resetFiltersBtn.addEventListener("click", () => this.handleResetFilters());
        }
        if (this.loadMoreBtn) {
            this.loadMoreBtn.addEventListener("click", (event) => {
                event.preventDefault();
                this.loadMoreMovies();
            });
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    new MovieManager();
});