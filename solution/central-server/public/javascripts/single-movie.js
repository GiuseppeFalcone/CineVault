// Global variable to store the original order of review cards.
let originalReviewCards = [];

/**
 * Initializes all functionalities on the single movie page:
 * - Toggling for cast, crew, and oscars sections.
 * - Sorting and filtering of reviews.
 * - Loading additional reviews.
 */
const init = () => {
    initCastToggle();
    initCrewToggle();
    initOscarToggle();
    initReviewsSorting();
    initLoadMoreReviews();
};

/**
 * Generic helper to set up a toggle for a list of items.
 * Only a specified number of items are visible by default.
 *
 * @param {HTMLElement[]|HTMLCollection} items - List of items to toggle.
 * @param {HTMLElement} toggleBtn - Button used to trigger the toggle.
 * @param {number} defaultVisibleCount - Number of items to show initially.
 * @param {string} visibleDisplay - CSS display value for visible items.
 */
const setupToggle = (items, toggleBtn, defaultVisibleCount, visibleDisplay) => {
    const toggleMoreLabel = 'Show More <i class="bi bi-chevron-down"></i>';
    const toggleLessLabel = 'Show Less <i class="bi bi-chevron-up"></i>';
    if (!toggleBtn) return;
    const itemArray = Array.from(items);

    // Set initial visibility: show only the first defaultVisibleCount items.
    itemArray.forEach((item, i) => {
        item.style.display = i < defaultVisibleCount ? visibleDisplay : 'none';
    });

    // Display toggle button only if there are more items than the default visible count.
    toggleBtn.style.display = itemArray.length > defaultVisibleCount ? 'inline-block' : 'none';
    toggleBtn.innerHTML = itemArray.length > defaultVisibleCount ? toggleMoreLabel : '';

    // Attaching event on click to set visible or hidden style
    let isExpanded = false;
    toggleBtn.addEventListener('click', () => {
        itemArray.forEach((item, i) => {
            if (i >= defaultVisibleCount) {
                item.style.display = isExpanded ? 'none' : visibleDisplay;
            }
        });
        toggleBtn.innerHTML = isExpanded ? toggleMoreLabel : toggleLessLabel;
        isExpanded = !isExpanded;
    });
};

/**
 * Initializes toggle functionality for the cast section.
 */
const initCastToggle = () => {
    const castContainer = document.getElementById('cast-container');
    if (!castContainer) return;
    const castMembers = castContainer.getElementsByClassName('cast-member');
    const toggleCastBtn = document.getElementById('toggle-cast');
    setupToggle(castMembers, toggleCastBtn, 4, 'block');
};

/**
 * Initializes toggle functionality for the crew section.
 */
const initCrewToggle = () => {
    const crewContainer = document.getElementById('crew-container');
    if (!crewContainer) return;
    const crewMembers = crewContainer.getElementsByClassName('crew-member');
    const toggleCrewBtn = document.getElementById('toggle-crew');
    setupToggle(crewMembers, toggleCrewBtn, 4, 'block');
};

/**
 * Initializes toggle functionality for the oscars section.
 */
const initOscarToggle = () => {
    const oscarsContainer = document.getElementById('oscars-container');
    if (!oscarsContainer) return;
    const oscarItems = oscarsContainer.getElementsByClassName('oscar-item');
    const toggleOscarsBtn = document.getElementById('toggle-oscars');
    setupToggle(oscarItems, toggleOscarsBtn, 4, '');
};

/**
 * Re-appends review cards to the container in the provided order.
 *
 * @param {HTMLElement} container - Container holding the review cards.
 * @param {HTMLElement[]} reviewsArray - Array of review card elements.
 */
const reappendReviews = (container, reviewsArray) => {
    reviewsArray.forEach(card => {
        card.style.display = '';
        container.appendChild(card);
    });
};

/**
 * Returns a new sorted array of review cards based on the comparator function.
 *
 * @param {HTMLElement[]} reviewsArray - Array of review card elements.
 * @param {Function} comparator - Comparison function for sorting.
 * @returns {HTMLElement[]} Sorted array of review cards.
 */
const sortReviews = (reviewsArray, comparator) => {
    return reviewsArray.slice().sort(comparator);
};

/**
 * Initializes review sorting and filtering functionalities.
 * Sets up event listeners on buttons to sort by date or filter by review type.
 */
const initReviewsSorting = () => {
    const container = document.getElementById('critics-reviews-container');
    if (!container) return;

    // Save the original order of review cards.
    const reviewCards = Array.from(container.getElementsByClassName('review-card'));
    originalReviewCards = reviewCards.slice();

    // Get filter and sort buttons.
    const btnLatestToOldest = document.getElementById('btn-latest-to-oldest');
    const btnOldestToLatest = document.getElementById('btn-oldest-to-latest');
    const btnFresh = document.getElementById('btn-fresh');
    const btnRotten = document.getElementById('btn-rotten');
    const btnTopCritic = document.getElementById('btn-top-critic');
    const btnReset = document.getElementById('btn-reset');
    const btnList = [btnLatestToOldest, btnOldestToLatest, btnFresh, btnRotten, btnTopCritic];

    // Helper to remove active styling from all buttons.
    const resetBtnStyle = (btns) => {
        btns.forEach(btn => btn && btn.classList.remove('active-btn'));
    };

    // "Latest to Oldest": Restore the original order.
    btnLatestToOldest && btnLatestToOldest.addEventListener('click', (event) => {
        event.preventDefault();
        resetBtnStyle(btnList);
        reappendReviews(container, originalReviewCards);
        btnLatestToOldest.classList.add('active-btn');
    });

    // "Oldest to Latest": Sort reviews in ascending order by review date.
    btnOldestToLatest && btnOldestToLatest.addEventListener('click', (event) => {
        event.preventDefault();
        resetBtnStyle(btnList);
        originalReviewCards.forEach(card => card.style.display = '');
        const sorted = sortReviews(originalReviewCards, (a, b) =>
            new Date(a.getAttribute('data-review-date')) - new Date(b.getAttribute('data-review-date'))
        );
        sorted.forEach(card => container.appendChild(card));
        btnOldestToLatest.classList.add('active-btn');
    });

    // "Fresh": Show only reviews with type "Fresh".
    btnFresh && btnFresh.addEventListener('click', (event) => {
        event.preventDefault();
        resetBtnStyle(btnList);
        originalReviewCards.forEach(card => {
            const type = card.getAttribute('data-review-type');
            card.style.display = (type === 'Fresh') ? '' : 'none';
        });
        btnFresh.classList.add('active-btn');
    });

    // "Rotten": Show only reviews with type "Rotten".
    btnRotten && btnRotten.addEventListener('click', (event) => {
        event.preventDefault();
        resetBtnStyle(btnList);
        originalReviewCards.forEach(card => {
            const type = card.getAttribute('data-review-type');
            card.style.display = (type === 'Rotten') ? '' : 'none';
        });
        btnRotten.classList.add('active-btn');
    });

    // "Top Critic": Show only reviews flagged as top critic.
    btnTopCritic && btnTopCritic.addEventListener('click', (event) => {
        event.preventDefault();
        resetBtnStyle(btnList);
        originalReviewCards.forEach(card => {
            const isTopCritic = card.getAttribute('data-top-critic') === 'true';
            card.style.display = isTopCritic ? '' : 'none';
        });
        btnTopCritic.classList.add('active-btn');
    });

    // "Reset": Show all reviews and restore the original order.
    btnReset && btnReset.addEventListener('click', (event) => {
        event.preventDefault();
        resetBtnStyle(btnList);
        reappendReviews(container, originalReviewCards);
        btnLatestToOldest && btnLatestToOldest.classList.add('active-btn');
    });
};

/**
 * Creates a review card element from the given review data.
 *
 * @param {Object} review - Review data object.
 * @returns {HTMLElement} The review card element.
 */
const createReviewCard = (review) => {
    const colDiv = document.createElement("div");
    colDiv.className = "col-12 col-md-6 col-lg-4 mb-3 review-card";
    colDiv.setAttribute("data-review-type", review.review_type);
    colDiv.setAttribute("data-top-critic", review.top_critic);
    colDiv.setAttribute("data-review-score", review.review_score);
    colDiv.setAttribute("data-review-date", review.review_date);
    colDiv.innerHTML = `
        <div class="card h-100">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="card-title mb-0">${review.critic_name}</h5>
                    ${review.top_critic ? `<span class="badge badge-yellow"><i class="bi bi-star-fill"></i> TOP CRITIC</span>` : ""}
                </div>
                <h6 class="card-subtitle mb-2 text-warning">${review.publisher_name}</h6>
                <p class="card-text mb-2 text-white">${review.review_content}</p>
            </div>
            <div class="card-footer d-flex justify-content-between align-items-center">
                <span class="text-muted">${new Date(review.review_date).toLocaleDateString()}</span>
                ${review.review_type === "Fresh"
        ? `<span class="badge bg-success">${review.review_type}</span>`
        : `<span class="badge bg-danger">${review.review_type}</span>`}
            </div>
        </div>
    `;
    return colDiv;
};

/**
 * Initializes the "Load More Reviews" functionality.
 * Fetches additional reviews from the server and appends them to the reviews container.
 */
const initLoadMoreReviews = () => {
    const loadMoreButton = document.getElementById("load-more-reviews");
    const reviewsContainer = document.getElementById("critics-reviews-container");
    if (!loadMoreButton || !reviewsContainer) return;

    let skipCount = reviewsContainer.children.length;
    const limit = 20;
    const movieTitle = document.title;

    // Retrieving additional reviews on btn click
    loadMoreButton.addEventListener("click", async (event) => {
        event.preventDefault();
        try {
            const response = await axios.get('/movies/reviews-by-movie-title', {
                params: {
                    movieTitle: movieTitle,
                    limit: limit,
                    skip: skipCount
                }
            });
            const newReviews = response.data;
            newReviews.forEach(review => {
                const reviewCard = createReviewCard(review);
                reviewsContainer.appendChild(reviewCard);
                originalReviewCards.push(reviewCard);
            });
            skipCount += newReviews.length;
            if (newReviews.length < limit) {
                loadMoreButton.style.display = "none";
            }
        } catch (error) {
            console.error("Error loading more reviews: ", error.message);
        }
    });
};

document.addEventListener('DOMContentLoaded', init);