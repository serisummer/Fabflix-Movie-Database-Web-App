import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        DomParser domParserMains = new DomParser("stanford-movies/mains243.xml");
        domParserMains.parseMains();
        DomParser domParserCasts = new DomParser("stanford-movies/casts124.xml");
        domParserCasts.parseCasts();
        DomParser domParserActors = new DomParser("stanford-movies/actors63.xml");
        domParserActors.parseActors();

        //Report
        //Film(String id, String title, Integer year, String director, List<String> genres)
        List<Film> movies = domParserMains.films;
        int originalMoviesLen = movies.size();

        //String starName: List<String> movieIds
        Map<String, List<String>> stars_in_movies = domParserCasts.stars_in_movies;

        //String starName: Integer birthYear
        Map<String, Integer> stars = domParserActors.stars;
        int originalStarsLen = stars.size();

        //genres
        List<String> genres = new ArrayList<>();
        //starName-movieId
        List<List<String>> starName_movieId = new ArrayList<>();
        //genreId-movieId
        List<List<String>> genreName_movieId = new ArrayList<>();


        //movie is inconsistent if it has no genre or no star
        List<Film> inconsistentMovies = new ArrayList<>();
        for (Film movie: movies) {
            if (movie.getGenres() == null || !domParserCasts.moviesWithStars.contains(movie.getId())) {
                inconsistentMovies.add(movie);
            }
            else {
                //get all genres
                for (String genre: movie.getGenres()) {
                    if (!genres.contains(genre)) {
                        genres.add(genre);
                    }
                    List<String> e = new ArrayList<>();
                    e.add(genre);
                    e.add(movie.getId());
                    genreName_movieId.add(e);
                }
            }
        }
        for (Film f: inconsistentMovies) {
            movies.remove(f);
        }


        List<String> castsNotInStars = new ArrayList<>();
        List<String> castsNotInMovies = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry: stars_in_movies.entrySet()) {
            if (!stars.containsKey(entry.getKey())) {
                //get casts that are not found in stars
                castsNotInStars.add(entry.getKey());
            }
            else {
                //remove movies that are not found in movies
                entry.getValue().removeIf(movieId -> inconsistentMovies.stream().map(Film::getId).collect(Collectors.toList()).contains(movieId));
                //get casts that are not in any movie
                if (entry.getValue().isEmpty()) {
                    castsNotInMovies.add(entry.getKey());
                }
            }
        }
        //remove casts that are not found in stars
        for (String star: castsNotInStars) {
            stars_in_movies.remove(star);
        }
        //remove casts that are not in any movie
        for (String star: castsNotInMovies) {
            stars_in_movies.remove(star);
        }
        for (Map.Entry<String, List<String>> entry: stars_in_movies.entrySet()) {
            for (String movieId: entry.getValue()) {
                List<String> e = new ArrayList<>();
                e.add(entry.getKey());
                e.add(movieId);
                starName_movieId.add(e);
            }
        }

        //remove star from stars if it is not in stars_in_movies
        List<String> starsNotInMovies = new ArrayList<>();
        for (Map.Entry<String, Integer> entry: stars.entrySet()) {
            if (!stars_in_movies.containsKey(entry.getKey())) {
                starsNotInMovies.add(entry.getKey());
            }
        }
        for (String star: starsNotInMovies) {
            stars.remove(star);
        }

        System.out.println(stars.size() + " stars are inserted");
        System.out.println(genres.size() + " genres are inserted");
        System.out.println(movies.size() + " movies are inserted");
        System.out.println(genreName_movieId.size() + " genres_in_movies are inserted");
        System.out.println(starName_movieId.size() + " stars_in_movies are inserted");
        /* TODO: insert the above data into database*/

        System.out.println(originalMoviesLen-movies.size() + " movies are inconsistent (no genre or no star)");
        System.out.println(originalStarsLen-stars.size() + " stars are inconsistent (not in both casts and actors)");
    }
}