import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT * FROM genres as g, genres_in_movies as gim, " +
                    "stars as s, stars_in_movies as sim, " +
                    "(SELECT * from movies as m, ratings as r WHERE m.id = r.movieId " +
                    "ORDER by r.rating DESC LIMIT 20) as a " +
                    "WHERE gim.movieId = a.id AND gim.genreId = g.id " +
                    "AND sim.movieId = a.id AND sim.starId = s.id " +
                    "ORDER by a.rating DESC, a.id";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            int count = 0;
            int genreCount = 0;
            int starCount = 0;
            int starIdCount = 0;
            String prev = "";
            String genres = "";
            String stars = "";
            String starIds = "";
            JsonObject jsonObject = new JsonObject();
            while (rs.next()) {
                String id = rs.getString("movieId");
                if (count != 0) {
                    if (id.equals(prev)) {
                        String genre = rs.getString("g.name") + ", ";
                        if (!genres.contains(genre) && genreCount < 3) {
                            genreCount++;
                            genres += genre;
                        }
                        String star = rs.getString("s.name") + ", ";
                        if (!stars.contains(star) && starCount < 3) {
                            starCount++;
                            stars += star;
                        }
                        String starId = rs.getString("starId") + ", ";
                        if (!starIds.contains(starId) && starIdCount < 3) {
                            starIdCount++;
                            starIds += starId;
                        }
                        prev = id;
                        continue;
                    }
                    else {
                        genreCount = 1;
                        starCount = 1;
                        starIdCount = 1;
                        jsonObject.addProperty("genres", genres.substring(0, genres.length()-2));
                        jsonObject.addProperty("stars", stars.substring(0, stars.length()-2));
                        jsonObject.addProperty("starIds", starIds.substring(0, starIds.length()-2));
                        jsonArray.add(jsonObject);
                        jsonObject = new JsonObject();
                        genres = rs.getString("g.name") + ", ";
                        stars = rs.getString("s.name") + ", ";
                        starIds = rs.getString("starId") + ", ";
                        prev = id;
                    }
                }
                else {
                    count++;
                    genreCount++;
                    starCount++;
                    starIdCount++;
                    prev = id;
                    genres = rs.getString("g.name") + ", ";
                    stars = rs.getString("s.name") + ", ";
                    starIds = rs.getString("starId") + ", ";
                }
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String rating = rs.getString("rating");

                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
