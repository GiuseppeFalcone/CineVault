const initIndex = () => {
    const carouselElement = document.getElementById('movieCarousel');
    if (!carouselElement) return;
    carouselHandling(carouselElement);
};

const carouselHandling = (carouselElement) => {
    // Update footer on initialization
    updateCarouselFooter(carouselElement);

    // Listen for slide change events to update caption
    carouselElement.addEventListener('slid.bs.carousel', updateCarouselFooter);

    // Initialize pause/resume functionality
    pauseResumeCarousel(carouselElement);
};

const pauseResumeCarousel = (carouselElement) => {
    const carouselInstance = new bootstrap.Carousel(carouselElement);
    const pauseButton = document.getElementById('carouselPauseButton');
    let isPaused = false;

    pauseButton.addEventListener('click', (event) => {
        event.preventDefault();
        if (isPaused) {
            carouselInstance.cycle();
            pauseButton.innerHTML = '<i class="bi bi-pause-fill"></i>';
        } else {
            carouselInstance.pause();
            pauseButton.innerHTML = '<i class="bi bi-play-fill"></i>';
        }
        isPaused = !isPaused;
    });
};

const updateCarouselFooter = (eventOrElement) => {
    let carouselElement;
    let activeIndex;

    // Determine the carousel element and active slide index
    if (eventOrElement instanceof Event && typeof eventOrElement.to !== 'undefined') {
        activeIndex = eventOrElement.to;
        carouselElement = eventOrElement.target;
    } else {
        carouselElement = eventOrElement;
        const itemsArray = Array.from(carouselElement.getElementsByClassName('carousel-item'));
        activeIndex = itemsArray.findIndex(item => item.classList.contains('active'));
    }

    // Exit if no active slide is found
    if (activeIndex < 0) return;

    const itemsArray = Array.from(carouselElement.getElementsByClassName('carousel-item'));
    const activeSlide = itemsArray[activeIndex];
    const imgs = activeSlide.getElementsByTagName('img');
    if (imgs.length === 0) return;

    const activeSlideImg = imgs[0];
    const title = activeSlideImg.getAttribute('data-title') || '';
    const category = activeSlideImg.getAttribute('data-category') || '';
    const movieId = activeSlideImg.getAttribute('data-movie-id') || '';

    const captionTitle = document.getElementById('carouselCaptionTitle');
    const captionCategory = document.getElementById('carouselCaptionCategory');

    captionTitle.innerHTML = `<a href="/movies/${movieId}" class="text-decoration-none text-white">${title}</a>`;
    captionCategory.innerHTML = `<a href="/movies/${encodeURIComponent(category)}" class="text-decoration-none text-warning">${category}</a>`;
};


document.addEventListener('DOMContentLoaded', initIndex);