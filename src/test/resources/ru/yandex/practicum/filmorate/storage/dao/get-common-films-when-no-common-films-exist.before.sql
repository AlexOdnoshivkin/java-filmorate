INSERT INTO users (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES (1, 'john.doe@example.com', 'john.doe', 'John', '2001-01-21'),
       (2, 'jane.doe@example.com', 'jane.doe', 'Jane', '2002-02-22');

INSERT INTO films (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES (1, 'Trainspotting', 'Absolutely amazing film!', '1996-2-23', 93, 4),
       (2, 'Big Fish', 'A brilliant experience', '2003-12-25', 125, 2);

INSERT INTO likes (FILM_ID, USER_ID)
VALUES (1, 1),
       (2, 2);
