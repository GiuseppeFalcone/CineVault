/**
 * Initializes scroll functionality for each movie row.
 * Attaches click event listeners to left and right arrow buttons.
 */
function initializeMovieRowScrolling() {
    // Constant: number of pixels to scroll on each arrow click.
    const SCROLL_AMOUNT = 300;

    // Retrieve all movie row sections.
    const movieRows = document.getElementsByClassName('movie-row');

    for (const row of movieRows) {
        // Retrieve the left and right arrow buttons within the row.
        const leftArrow = row.getElementsByClassName('left-arrow')[0];
        const rightArrow = row.getElementsByClassName('right-arrow')[0];
        // Retrieve the container that holds the movie cards.
        const cardsContainer = row.getElementsByClassName('cards-container')[0];

        // If the cards container is missing, skip this row.
        if (!cardsContainer) continue;

        // Attaching scroll to left and right arrows on click event
        if (leftArrow) {
            leftArrow.addEventListener('click', () => {
                // Scroll left by the defined amount with smooth animation.
                cardsContainer.scrollBy({ left: -SCROLL_AMOUNT, behavior: 'smooth' });
            });
        }
        if (rightArrow) {
            rightArrow.addEventListener('click', () => {
                // Scroll right by the defined amount with smooth animation.
                cardsContainer.scrollBy({ left: SCROLL_AMOUNT, behavior: 'smooth' });
            });
        }
    }
}

// Initialize scrolling functionality once the DOM is fully loaded.
document.addEventListener("DOMContentLoaded", initializeMovieRowScrolling);