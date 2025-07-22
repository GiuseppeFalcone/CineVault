# CineVault

CineVault is a comprehensive movie information platform that provides detailed film data, behind-the-scenes insights, reviews, and a vibrant community for cinema enthusiasts.

### Project Structure

```
CineVault/
├── Readme.md                   # Project documentation (this file)
├── web-pages-examples/           # Query examples with screenshots
│   ├── actor-page/            # Actor page query examples
│   ├── crew-page/             # Crew page query examples
│   ├── homepage/              # Homepage query examples
│   ├── movies-page/           # Movies page query examples
│   ├── search/                # Search functionality examples
│   └── single-movie-page/     # Single movie page examples
├── report/                    # Project report PDF
│   └── Technical Report.pdf
└── solution/                  # Main application code
    ├── macBootScript.sh       # macOS startup script
    ├── central-server/        # Main web application (Express.js)
    │   ├── app.js             # Express application setup
    │   ├── routes/            # Route handlers
    │   ├── views/             # Handlebars templates
    │   ├── public/            # Static assets (CSS, JS, images)
    │   └── socket.io/         # Socket.IO configuration
    ├── static-data-server/    # Spring Boot API for movie data
    │   └── src/main/java/com/falconevettorello/staticdataserver/
    │       ├── config/        # Spring configuration & data initialization
    │       ├── movies/        # Movie-related entities and services
    │       └── [other packages]
    ├── dynamic-data-server/   # Node.js API for reviews and Oscar data
    │   ├── app.js             # Express application
    │   ├── routes/            # API routes (reviews, oscars)
    │   ├── models/            # MongoDB schemas
    │   └── databases/         # Database configuration
    └── data-analysis/         # Data cleaning and analysis scripts
        ├── data-cleaning/     # Jupyter notebooks for data processing
        │   ├── themes.ipynb
        │   ├── studios.ipynb
        │   ├── ...
        │   └── dataframes-merging.ipynb
        ├── parquet-data/      # Directory containing cleaned data in Parquet format
        ├── src/               # Python module for data loading
        │   └── load-data.py   # Script for loading and processing data
        ├── actors-analysis.ipynb  # Jupyter notebook for actor data analysis
        └── movies-oscars-analysis.ipynb  # Jupyter notebook for movie and Oscar data analysis

```

### Key Directories Explained

- **`queries-examples/`** - Contains screenshots and examples of application queries organized by page type
- **`report/`** - Project documentation and analysis report
- **`solution/`** - Core application implementation with three main servers
- **`data-analysis/`** - Jupyter notebooks used for data cleaning and preprocessing

## Architecture

The project follows a microservices architecture with three main components:

### 1. Central Server (Port 3000)

- **Technology**: Express.js with Handlebars templating
- **Purpose**: Main web application serving the user interface
- **Features**:
  - Movie browsing and search
  - Real-time chat system with Socket.IO
  - Responsive web design
  - Integration with both data servers

### 2. Static Data Server (Port 8080)

- **Technology**: Spring Boot with JPA/Hibernate
- **Database**: PostgreSQL for persistent movie data storage
- **Purpose**: RESTful API for structured movie data
- **Data Sources**:
  - Movies, actors, crew members
  - Genres, languages, countries
  - Release information, studios, themes
- **Features**:
  - Comprehensive movie database
  - Advanced filtering and search
  - Swagger API documentation

### 3. Dynamic Data Server (Port 3001)

- **Technology**: Node.js with MongoDB
- **Purpose**: API for user-generated and external content
- **Data Sources**:
  - Rotten Tomatoes reviews
  - Oscar awards and nominations
- **Features**:
  - Review aggregation and filtering
  - Award information lookup
  - Swagger API documentation

## Key Features

### 🎬 Movie Discovery

- Browse movies by genre, year, language, and production country
- Advanced search functionality across multiple criteria
- Featured movie carousel on homepage
- Related movie recommendations

### 👥 People Information

- Actor and crew member profiles
- Oscar awards and nominations tracking
- Filmography for each person

### 📝 Reviews & Ratings

- Rotten Tomatoes critic reviews
- Review filtering (Fresh/Rotten, Top Critics)
- Sortable reviews by date and rating

### 💬 Community Chat

- Real-time chat system with multiple namespaces
- Topic-based chat rooms (General, Movies, Actors, Countries, Genres)
- Typing indicators and message notifications

### 🏆 Awards Information

- Oscar winners and nominees
- Award ceremony year tracking
- Integration with movie and people pages

## Technology Stack

### Frontend

- **Handlebars**: Server-side templating
- **Bootstrap 5**: Responsive UI framework
- **Socket.IO**: Real-time communication
- **Axios**: HTTP client for API calls

### Backend

- **Node.js/Express**: Central server and dynamic data API
- **Spring Boot**: Static data API with JPA/Hibernate
- **MongoDB**: Document database for reviews and awards
- **PostgreSQL**: Relational database for movie data

### Development Tools

- **Swagger**: API documentation

## Getting Started

### Prerequisites

- Node.js (v14 or higher)
- Java 11 or higher
- MongoDB instance
- PostgreSQL database

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd solution
   ```

2. **Start the Static Data Server (Spring Boot)**

   ```bash
   cd static-data-server
   gradle bootRun
   # Server will start on http://localhost:8080
   ```

3. **Start the Dynamic Data Server**

   ```bash
   cd dynamic-data-server
   npm install
   npm start
   # Server will start on http://localhost:3001
   ```

4. **Start the Central Server**

   ```bash
   cd central-server
   npm install
   npm start
   # Server will start on http://localhost:3000
   ```

5. **Access the application**
   - Main Application: http://localhost:3000
   - Static API Docs: http://localhost:8080/api-docs
   - Dynamic API Docs: http://localhost:3001/api-docs
   - Central API Docs: http://localhost:3000/api-docs

### Quick Start Script

For macOS users, use the provided boot script:

```bash
chmod +x macBootScript.sh
./macBootScript.sh
```

## Data Sources

The application uses several CSV files that need to be located in the `/data` directory:

### Download Required Data Files

The CSV data files can be downloaded from the following Google Drive links:

- **Primary Dataset**: https://drive.google.com/drive/folders/1Kie8cRbJRiljUGrP6v3nNYikFHjZWbMg?usp=drive_link
- **Additional Dataset**: https://drive.google.com/drive/folders/1pilJFEXVeNXT-wq098fY3WtOXB9BdwtN?usp=drive_link

After downloading, place all CSV files in the `/data` directory of the project.

### Data Files Description

- `movies.csv` - Core movie information
- `actors.csv` - Actor details and roles
- `crew.csv` - Crew members and positions
- `genres.csv` - Movie genre classifications
- `languages.csv` - Movie languages
- `countries.csv` - Production countries
- `releases.csv` - Release information
- `studios.csv` - Production studios
- `themes.csv` - Movie themes
- `posters.csv` - Movie poster URLs
- `rotten_tomatoes_reviews.csv` - Critic reviews
- `the_oscar_awards.csv` - Oscar awards data

## API Endpoints

### Central Server

- `GET /` - Homepage with featured content
- `GET /movies` - Movie browsing with filters
- `GET /movies/:id` - Detailed movie page
- `GET /person/actor/:name` - Actor profile
- `GET /person/crew/:name` - Crew member profile
- `GET /chat` - Real-time chat interface
- `GET /search` - Search functionality
- `GET /api-docs` - OpenAPI documentation

### Static Data Server

- `GET /api/movies` - Movie listings with pagination
- `GET /api/movies/{id}` - Single movie details
- `GET /api/genres/distinct` - Available genres
- `GET /api/languages/distinct` - Available languages
- And many more endpoints for comprehensive data access
- `GET /api-docs` - OpenAPI documentation

### Dynamic Data Server

- `GET /reviews/by-movie-title` - Reviews for specific movies
- `GET /oscars/by-name` - Oscar information for people
- `GET /reviews/by-rate-type` - Reviews filtered by rating type
- `GET /api-docs` - OpenAPI documentation

## Development

### File Structure

```
central-server/
├── app.js                  # Express application setup
├── routes/                 # Route handlers
├── views/                  # Handlebars templates
├── public/                 # Static assets (CSS, JS, images)
├── socket.io/              # Socket.IO configuration
└── swagger/                # API documentation

static-data-server/
├── src/main/java/com/falconevettorello/staticdataserver/
├── config/                 # Spring configuration
├── movies/                 # Movie-related entities and services
├── genres/                 # Genre-related functionality
└── [other entity packages]

dynamic-data-server/
├── app.js                  # Express application
├── routes/                 # API routes
├── models/                 # MongoDB schemas
└── databases/              # Database configuration
```

### Key Features Implementation

#### Real-time Chat

- Socket.IO namespaces for topic-based rooms
- Message persistence and history
- Typing indicators
- User presence management

#### Movie Filtering

- Client-side and server-side filtering
- Multiple filter criteria support
- Pagination for large datasets

#### Review System

- Dynamic loading of additional reviews
- Client-side sorting and filtering
- Integration with Rotten Tomatoes data

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Authors

- **Giuseppe Falcone**
  [LinkedIn](https://www.linkedin.com/in/giuseppefalcone01/)
  [giuseppe001falcone@gmail.com](mailto:giuseppe001falcone@gmail.com)
- **Gabriele Vettorello**

## License

MIT License

Copyright (c) 2025 Giuseppe Falcone and Gabriele Vettorello

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

For detailed API documentation, visit the Swagger endpoints when the servers are running.
