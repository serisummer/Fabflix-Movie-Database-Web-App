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
}
