/**
 * Global collapse instances for the navbar links and the search form.
 */
let bsNavbarCollapse = null;
let bsSearchCollapse = null;

/**
 * Initializes all navbar functionalities:
 *  - Navbar collapse toggle.
 *  - Search form collapse toggle.
 *  - Search form submission handling and validation.
 */
function initNavbar() {
    initNavbarToggle();
    initSearchCollapse();
    initSearchHandler();
}

/**
 * Initializes the navbar toggle functionality for the navigation links.
 * Checks if the toggler and the pages-links collapse container exist.
 */
function initNavbarToggle() {
    const navbarToggler = document.getElementById("navbar-toggler");
    const navbarCollapse = document.getElementById("pages-links");

    if (!navbarToggler || !navbarCollapse) {
        console.error("Navbar toggler or collapse element not found.");
        return;
    }

    // Initialize Bootstrap's collapse for navigation links with manual control.
    bsNavbarCollapse = new bootstrap.Collapse(navbarCollapse, { toggle: false });

    // When the toggler is clicked, toggle both the navigation links and the search form.
    navbarToggler.addEventListener("click", function (event) {
        event.preventDefault();
        bsNavbarCollapse.toggle();
        if (bsSearchCollapse) {
            bsSearchCollapse.toggle();
        }
    });
}

/**
 * Initializes the collapse functionality for the search form.
 * Ensures that the search collapse container exists.
 */
function initSearchCollapse() {
    const searchCollapse = document.getElementById("search-collapse");

    if (!searchCollapse) {
        console.error("Search collapse element not found.");
        return;
    }

    // Initialize Bootstrap's collapse for the search form.
    bsSearchCollapse = new bootstrap.Collapse(searchCollapse, { toggle: false });
}

/**
 * Initializes the search form submission handler.
 * Validates that the search query is non-empty and,
 * if searching by year, that the input is exactly 4 digits.
 * Also collapses the navbar sections when the form is submitted.
 */
function initSearchHandler() {
    const searchForm = document.getElementById("search-form");
    const searchInput = document.getElementById("search-input");
    const searchFieldElement = document.getElementById("search-field");

    if (!searchForm || !searchInput || !searchFieldElement) {
        console.error("Search form or related elements not found.");
        return;
    }

    // Attach a submit event listener to validate the search query.
    searchForm.addEventListener("submit", function (event) {
        const searchField = searchFieldElement.value;
        const searchQuery = searchInput.value.trim();

        // Prevent submission if the search query is empty.
        if (searchQuery === "") {
            event.preventDefault();
            return;
        }

        // If searching by year, validate that the input is exactly a 4-digit number.
        if (searchField === "year" && !/^\d{4}$/.test(searchQuery)) {
            event.preventDefault();
            alert("Please enter a valid 4-digit year.");
            return;
        }

        // Collapse both the navigation links and the search form if they are open.
        if (bsNavbarCollapse) {
            bsNavbarCollapse.hide();
        }
        if (bsSearchCollapse) {
            bsSearchCollapse.hide();
        }
    });
}

document.addEventListener("DOMContentLoaded", initNavbar);
