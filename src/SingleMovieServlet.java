import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT m.title, m.id, m.year, m.director, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC) AS genres, " +
                    "GROUP_CONCAT(DISTINCT CONCAT('(', s.name, '$', s.id, ')') ORDER BY star_freq.frequency DESC, s.name ASC) AS stars, r.rating " +
                    "FROM movies m " +
                    "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                    "LEFT JOIN genres g ON gm.genreId = g.id " +
                    "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                    "LEFT JOIN stars s ON sm.starId = s.id " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "LEFT JOIN (" +
                    "SELECT si.starId, COUNT(*) AS frequency " +
                    "FROM stars_in_movies si " +
                    "GROUP BY si.starId" +
                    ") AS star_freq ON s.id = star_freq.starId " +
                    "WHERE m.id = ? " +
                    "GROUP BY m.title, m.id, m.year, m.director, r.rating";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            String genres = "";
            String stars = "";
            String starIds = "";
            JsonObject jsonObject = new JsonObject();
            while (rs.next()) {
                JsonObject movieJson = new JsonObject();
                movieJson.addProperty("title", rs.getString("title"));
                movieJson.addProperty("year", rs.getInt("year"));
                movieJson.addProperty("director", rs.getString("director"));
                movieJson.addProperty("genres", rs.getString("genres"));
                movieJson.addProperty("stars", rs.getString("stars"));
                movieJson.addProperty("rating", rs.getFloat("rating"));

                jsonArray.add(movieJson);
            }


            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
