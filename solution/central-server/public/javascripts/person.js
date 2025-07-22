// Initialize the Oscar toggle functionality after the DOM is loaded.
const initializeOscarToggle = () => {
    const oscarsContainer = document.getElementById('oscars-container');
    if (!oscarsContainer) return;
    const oscarItems = oscarsContainer.getElementsByClassName('oscar-item');
    const toggleButton = document.getElementById('toggle-oscars');
    setupToggle(oscarItems, toggleButton, 4, '');
};

// Setup toggle for items: only display defaultVisibleCount items initially.
const setupToggle = (items, toggleButton, defaultVisibleCount, visibleDisplay) => {
    const showMoreLabel = 'Show More <i class="bi bi-chevron-down"></i>';
    const showLessLabel = 'Show Less <i class="bi bi-chevron-up"></i>';
    if (!toggleButton) return;

    // Initialize items' visibility.
    Array.from(items).forEach((item, index) => {
        item.style.display = index < defaultVisibleCount ? visibleDisplay : 'none';
    });

    // Show toggle button only if there are extra items.
    if (items.length > defaultVisibleCount) {
        toggleButton.style.display = 'inline-block';
        toggleButton.innerHTML = showMoreLabel;
    } else {
        toggleButton.style.display = 'none';
    }

    let isExpanded = false;
    toggleButton.addEventListener('click', () => {
        // Toggle the display of additional items.
        Array.from(items).forEach((item, index) => {
            if (index >= defaultVisibleCount) {
                item.style.display = isExpanded ? 'none' : visibleDisplay;
            }
        });
        toggleButton.innerHTML = isExpanded ? showMoreLabel : showLessLabel;
        isExpanded = !isExpanded;
    });
};

document.addEventListener('DOMContentLoaded', initializeOscarToggle);