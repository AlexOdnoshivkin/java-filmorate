INSERT INTO users (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES (1, 'john.doe@example.com', 'john.doe', 'John', '2001-01-21'),
       (2, 'jane.doe@example.com', 'jane.doe', 'Jane', '2002-02-22'),
       (3, 'bob.doe@example.com', 'bob.doe', 'Bob', '2003-03-23');

INSERT INTO films (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES (1, 'Trainspotting', 'Absolutely amazing film!', '1996-2-23', 93, 4),
       (2, 'Big Fish', 'A brilliant experience', '2003-12-25', 125, 2),
       (3, 'Doctor Sleep', 'Wow. Absolutely incredible film.', '2019-10-31', 152, 4),
       (4, 'Doctor Strange', 'Incredible film.', '2020-10-31', 133, 4);

INSERT INTO FILM_RATINGS (film_id,user_id,user_rating)
VALUES (1,1,4), (1,2,6), (2,2,4),(3,1,4),(3,3,3),(2,3,8),(4,2,8);