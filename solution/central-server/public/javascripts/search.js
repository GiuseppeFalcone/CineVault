function initLoadMore() {
    const loadMoreButton = document.getElementById('load-more-btn');
    const moviesContainer = document.getElementById('movies-container');
    loadMoreButton.addEventListener('click', (event) => {
        event.preventDefault();
        loadMoreMovies(loadMoreButton, moviesContainer);
    });
};

async function loadMoreMovies(loadMoreButton, moviesContainer) {
    try {
        const field = loadMoreButton.getAttribute('data-field');
        const value = loadMoreButton.getAttribute('data-value');
        const offset = moviesContainer.children.length;
        const response = await axios.get("/search", {
            params: {
                field: field,
                value: value,
                offset: offset
            }
        });
        if (response.data) {
            moviesContainer.innerHTML += response.data;
        } else {
            loadMoreButton.style.display = 'none';
        }
    } catch (error) {
        console.error("Error loading more movies:", error.message);
        moviesContainer.innerHTML +=
            '<div class="alert alert-danger">Error loading more movies</div>';
    }
};


document.addEventListener('DOMContentLoaded', initLoadMore);