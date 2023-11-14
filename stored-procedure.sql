drop procedure if exists add_movie;
-- Change DELIMITER to $$ 
DELIMITER $$ 

CREATE PROCEDURE add_movie(
    IN p_title VARCHAR(100),
    IN p_year INT,
    IN p_director VARCHAR(100),
    IN p_star_name VARCHAR(100),
    IN p_star_birth_year INT,
    IN p_genre_name VARCHAR(32)
)
BEGIN
    DECLARE starId VARCHAR(10);
    DECLARE genreId INT;
    DECLARE movieId VARCHAR(10);

    -- Check if the movie already exists
    IF EXISTS (SELECT * FROM movies WHERE title = p_title AND year = p_year AND director = p_director) THEN
        SELECT 'Movie already exists.' AS message;
    ELSE
        -- Check if the star exists
        IF EXISTS (SELECT id FROM stars WHERE name = p_star_name AND (birthYear = p_star_birth_year OR (birthYear IS NULL AND p_star_birth_year = -1) OR (p_star_birth_year = -1 AND birthYear IS NOT NULL))) THEN
            -- If the star exists, get the starId
            SELECT id INTO starId FROM stars WHERE name = p_star_name LIMIT 1;
        ELSE
            -- If the star doesn't exist, create a new star
            SET starId = CONCAT('nm', (SELECT MAX(SUBSTRING(id, 3) + 1) FROM stars));
            IF p_star_birth_year = -1 THEN
				Set p_star_birth_year = NULL;
			end if;
            INSERT INTO stars VALUES (starId, p_star_name, p_star_birth_year);
        END IF;

        -- Check if the genre exists
        IF EXISTS (SELECT id FROM genres WHERE name = p_genre_name) THEN
            -- If the genre exists, get the genreId
            SELECT id INTO genreId FROM genres WHERE name = p_genre_name LIMIT 1;
        ELSE
            -- If the genre doesn't exist, create a new genre
            SET genreId = (SELECT MAX(id) + 1 FROM genres);
            INSERT INTO genres VALUES (genreId, p_genre_name);
        END IF;

        -- Add the movie
        SET movieId = CONCAT('tt', (SELECT MAX(SUBSTRING(id, 3) + 1) FROM movies));
        INSERT INTO movies (id, title, year, director) VALUES (movieID, p_title, p_year, p_director);

        -- Link star to the movie
        INSERT INTO stars_in_movies (starId, movieId) VALUES (starId, movieId);

        -- Link genre to the movie
        INSERT INTO genres_in_movies (genreId, movieId) VALUES (genreId, movieId);

        SELECT CONCAT('Movie added successfully. Movie ID: ', movieId, ', Star ID: ', starId, ', Genre ID: ', genreId) AS message;
    
    END IF;
END
$$

-- Change back DELIMITER to ; 
DELIMITER ; 