import java.util.List;

public class Film {
    private final String id;
    private final String title;
    private final Integer year;
    private final String director;
    private final List<String> genres;

    public Film(String id, String title, Integer year, String director, List<String> genres) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Integer getYear() {
        return this.year;
    }

    public String getDirector() {
        return this.director;
    }

    public List<String> getGenres() {
        return this.genres;
    }

    public String toString() {
        return this.id + ", "
                + this.title + ", "
                + this.year + ", "
                + this.director + ", "
                + this.genres;
    }
}
