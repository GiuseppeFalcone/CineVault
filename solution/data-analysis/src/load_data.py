import pandas as pd
from pathlib import Path
import gc

DATA_PATH = Path(__file__).parent.parent.parent / 'data' / 'data-cleaned'


def load_movies():
    return pd.read_csv(
        DATA_PATH / 'movies_cleaned.csv',
        dtype={
            'id': 'int64',
            'title': 'string',
            'tagline': 'string',
            'plot': 'string',
        }
    ).astype({
        'global_release_year': 'Int64',
        'runtime': 'Int64',
        'critique_rating': 'float64',
    })


def load_actors():
    return pd.read_csv(
        DATA_PATH / 'actors_cleaned.csv',
        dtype={
            'id': 'int64',
            'actor_name': 'category',
            'actor_role': 'category',
        }
    )


def load_crew():
    return pd.read_csv(
        DATA_PATH / 'crew_cleaned.csv',
        dtype={
            'id': 'int64',
            'crew_name': 'category',
            'crew_role': 'category',
        }
    )


def load_countries():
    return pd.read_csv(
        DATA_PATH / 'countries_cleaned.csv',
        dtype={
            'id': 'int64',
            'production_country': 'category',
        }
    )


def load_genres():
    return pd.read_csv(
        DATA_PATH / 'genres_cleaned.csv',
        dtype={
            'id': 'int64',
            'genre_category': 'category',
        }
    )


def load_languages():
    return pd.read_csv(
        DATA_PATH / 'languages_cleaned.csv',
        dtype={
            'id': 'int64',
            'language_type': 'category',
            'language': 'category',
        }
    )


def load_oscars():
    return pd.read_csv(
        DATA_PATH / 'oscars_cleaned.csv',
        dtype={
            'year_film': 'int64',
            'year_ceremony': 'int64',
            'ceremony': 'int64',
            'category': 'category',
            'name': 'category',
            'film': 'category',
            'winner': 'boolean',
        }
    )


def load_posters():
    return pd.read_csv(
        DATA_PATH / 'posters_cleaned.csv',
        dtype={
            'id': 'int64',
            'poster_link': 'string',
        }
    )


def load_releases():
    return pd.read_csv(
        DATA_PATH / 'releases_cleaned.csv',
        dtype={
            'id': 'int64',
            'release_country': 'category',
            'release_type': 'category',
            'release_age_rating': 'category',
        },
        parse_dates=['release_date']
    )


def load_reviews():
    return pd.read_csv(
        DATA_PATH / 'rotten-tomatoes-reviews-cleaned.csv',
        dtype={
            'rotten_tomatoes_link': 'string',
            'movie_title': 'string',
            'critic_name': 'string',
            'top_critic': 'boolean',
            'publisher_name': 'string',
            'review_type': 'category',
            'review_text': 'string',
        },
        parse_dates=['review_date']
    )


def load_studios():
    return pd.read_csv(
        DATA_PATH / 'studios_cleaned.csv',
        dtype={
            'id': 'int64',
            'production_studio': 'category',
        }
    )


def load_themes():
    return pd.read_csv(
        DATA_PATH / 'themes_cleaned.csv',
        dtype={
            'id': 'int64',
            'theme': 'category',
        }
    )

def load_all_dataframes():
    """Returns a dict of all loaded dataframes, keyed by name."""
    return {
        'movies': load_movies(),
        'actors': load_actors(),
        'crew': load_crew(),
        'countries': load_countries(),
        'genres': load_genres(),
        'languages': load_languages(),
        'oscars': load_oscars(),
        'posters': load_posters(),
        'releases': load_releases(),
        'reviews': load_reviews(),
        'studios': load_studios(),
        'themes': load_themes(),
    }

def check_all_dataframes():
    """Loads all dataframes and prints their info to check loading correctness."""
    dfs = load_all_dataframes()
    for name, df in dfs.items():
        print(f'--- {name} ---')
        print(df.info())
        print(df.head(2))
        print()
    print('All dataframes loaded and checked.')
    # Clean up memory
    del dfs
    gc.collect()

if __name__ == '__main__':
    check_all_dataframes()
