package main.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DomParser {
    List<Film> films = new ArrayList<>();
    Map<String, List<String>> stars_in_movies = new HashMap<>();
    Map<String, Integer> stars = new HashMap<>();
    String file;
    Document dom;

    List<String> moviesWithStars = new ArrayList<>();

    public DomParser(String file) {
        this.file = file;
    }

    public void parseMains() {
        parseXmlFile();
        parseDocumentMains();
    }

    public void parseCasts() {
        parseXmlFile();
        parseDocumentCasts();
    }

    public void parseActors() {
        parseXmlFile();
        parseDocumentActors();
    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(new FileInputStream(this.file), "ISO-8859-1");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocumentMains() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get all directorfilms elements
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");

        //
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element directorfilms = (Element) nodeList.item(i);
            parseFilms(directorfilms);
        }
    }

    private String parseDirector(Element element) {
        //get director element
        Element directorElement = (Element) element.getElementsByTagName("director").item(0);
        //return director name
        return this.getTextValue(directorElement, "dirname");
    }

    private List<String> parseGenres(Element element) {
        NodeList catsElement = element.getElementsByTagName("cats");
        if (catsElement.getLength() == 0) {
            return null;
        } else {
            return getMultipleTextValues((Element) catsElement.item(0), "cat");
        }
    }

    private void parseFilms(Element element) {
        String id;
        String title;
        Integer year;
        String director = parseDirector(element);
        List<String> genres;
        //get films element
        Element filmsElement = (Element) element.getElementsByTagName("films").item(0);
        //get the list of films
        NodeList filmElements = filmsElement.getElementsByTagName("film");
        //create a film object for each film
        for (int i = 0; i < filmElements.getLength(); i++) {
            Element filmElement = (Element) filmElements.item(i);
            try {
                id = this.getTextValue(filmElement, "fid");
            } catch (Exception e) {
                id = null;
            }
            try {
                title = this.getTextValue(filmElement, "t");
            } catch (Exception e) {
                title = null;
            }
            try {
                year = this.getIntValue(filmElement, "year");
            } catch (Exception e) {
                year = null;
            }
            genres = parseGenres(filmElement);
            Film film = new Film(id, title, year, director, genres);
            this.films.add(film);
        }
    }

    private void parseDocumentCasts() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get all dirfilms elements
        NodeList nodeList = documentElement.getElementsByTagName("dirfilms");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element filmcElement = (Element) nodeList.item(i);
            NodeList mElements = filmcElement.getElementsByTagName("m");
            for (int j = 0; j < mElements.getLength(); j++) {
                Element mElement = (Element) mElements.item(j);
                if (mElement == null) {
                    continue;
                }
                String movieId = getTextValue(mElement, "f");
                String starName = getTextValue(mElement, "a");
                if (starName == null || starName.equals("s a") || starName.equals("sa") || starName.equals("s.a.")) {
                    continue;
                }
                if (!stars_in_movies.containsKey(starName)) {
                    stars_in_movies.put(starName, new ArrayList<>());
                }
                stars_in_movies.get(starName).add(movieId);
                moviesWithStars.add(movieId);
            }
        }
    }

    private void parseDocumentActors() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        NodeList nodeList = documentElement.getElementsByTagName("actor");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element actorElement = (Element) nodeList.item(i);
            String actorName = getTextValue(actorElement, "stagename");
            Integer birthYear = getIntValue(actorElement, "dob");
            stars.put(actorName, birthYear);
        }
    }


    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            try {
                textVal = nodeList.item(0).getFirstChild().getNodeValue().strip();
            } catch (Exception e) {
                return null;
            }
        }
        return textVal;
    }

    private List<String> getMultipleTextValues(Element element, String tagName) {
        List<String> textVal = new ArrayList<>();
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getFirstChild() != null) {
                    String s = nodeList.item(i).getFirstChild().getNodeValue();
                    if (s != null) {
                        textVal.add(s.strip().toLowerCase());
                    }
                }
            }
            if (!textVal.isEmpty()) {
                return textVal;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    private Integer getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        try {
            return Integer.parseInt(getTextValue(ele, tagName));
        } catch (Exception e) {
            return null;
        }
    }


    public void printDataMains(Boolean showDetails) {
        System.out.println("Total parsed " + films.size() + " movies");
        if (showDetails) {
            for (Film film : films) {
                System.out.println("\t" + film.toString());
            }
        }
    }

    public void printDataCasts(Boolean showDetails) {
        System.out.println("Total parsed " + stars_in_movies.size() + " stars in movies");
        if (showDetails) {
            for (Map.Entry<String, List<String>> entry : stars_in_movies.entrySet()) {
                System.out.println("\t" + entry.getKey() + ": " + entry.getValue().toString());
            }
        }
    }

    public void printDataActors(Boolean showDetails) {
        System.out.println("Total parsed " + stars.size() + " stars");
        if (showDetails) {
            for (Map.Entry<String, Integer> entry : stars.entrySet()) {
                System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        DomParser domParserMains = new DomParser("../stanford-movies/mains243.xml");
        domParserMains.parseMains();
        DomParser domParserCasts = new DomParser("../stanford-movies/casts124.xml");
        domParserCasts.parseCasts();
        DomParser domParserActors = new DomParser("../stanford-movies/actors63.xml");
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
