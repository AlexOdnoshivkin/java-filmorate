CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL,
    login varchar(255),
    email varchar(255),
    birthday date
    );

CREATE TABLE IF NOT EXISTS friends (
    req_user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    resp_user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    is_friend boolean DEFAULT 0,
    PRIMARY KEY (req_user_id, resp_user_id)
    );

CREATE TABLE IF NOT EXISTS genre (
    genre_id integer PRIMARY KEY,
    name varchar(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id integer PRIMARY KEY,
    name varchar(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar (255) NOT NULL,
    description varchar (199),
    release_date date,
    duration integer,
    mpa_id integer REFERENCES mpa(mpa_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS likes (
    film_id integer REFERENCES films(film_id) ON DELETE CASCADE,
    user_id integer REFERENCES users(user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS films_genre (
    film_id integer REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id integer REFERENCES genre(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS events (
   event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   timestamp timestamp with time zone DEFAULT CURRENT_TIMESTAMP(),
   user_id integer REFERENCES users(user_id) ON DELETE CASCADE,
   event_type varchar (255) NOT NULL,
   operation varchar (255) NOT NULL,
   entity_id integer NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews(
    review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content varchar(255) NOT NULL,
    positive BOOLEAN NOT NULL,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_like(
    review_id integer REFERENCES reviews(review_id) ON DELETE CASCADE,
    user_id integer REFERENCES  users(user_id) ON DELETE CASCADE,
    utility integer not null,
    PRIMARY KEY (review_id, user_id)
);

create table if not exists DIRECTORS
(
    DIRECTOR_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    DIRECTOR_NAME CHARACTER VARYING(255)

);

create table if not exists FILMS_DIRECTORS
(
    film_id     integer REFERENCES films (film_id) ON DELETE CASCADE,
    DIRECTOR_ID integer REFERENCES DIRECTORS (DIRECTOR_ID),
    PRIMARY KEY (film_id, DIRECTOR_ID)
);
