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
import java.util.Arrays;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/form")
public class MovieListServlet extends HttpServlet {
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

        //response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // getting request parameters
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            String sort = request.getParameter("sort");

            // pagination
            int moviesPerPage = Integer.parseInt(request.getParameter("n"));
            int currentPage = Integer.parseInt(request.getParameter("page"));
            int offset = (currentPage - 1) * moviesPerPage;

            Statement statement = conn.createStatement();

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
                    "WHERE 1=1";


            if (title != null && !title.isEmpty()) {
                query += " AND title LIKE '%" + title + "%'";
            }
            if (year != null && !year.isEmpty()) {
                query += " AND year = " + year;
            }
            if (director != null && !director.isEmpty()) {
                query += " AND director LIKE '%" + director + "%'";
            }
            if (star != null && !star.isEmpty()) {
                query += " AND m.id IN (SELECT si.movieId FROM stars_in_movies si WHERE si.starId IN (SELECT st.id FROM stars st WHERE st.name LIKE '%" + star + "%'))";
            }

            query += " GROUP BY m.title, m.id, m.year, m.director, r.rating ";
            query += parseSort(sort);
            query += " LIMIT " + moviesPerPage + " OFFSET " + offset;

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs

            while (rs.next()) {
                JsonObject movieJson = new JsonObject();
                movieJson.addProperty("title", rs.getString("title"));
                movieJson.addProperty("id", rs.getString("id"));
                movieJson.addProperty("year", rs.getInt("year"));
                movieJson.addProperty("director", rs.getString("director"));
                movieJson.addProperty("genres", rs.getString("genres"));
                String stars = rs.getString("stars");
                if (stars != null) {
                    // Split the concatenated stars string by comma and limit it to the first 3 elements
                    String[] starArray = stars.split(",");
                    if (starArray.length > 3) {
                        stars = String.join(",", Arrays.copyOf(starArray, 3));
                    }
                }
                movieJson.addProperty("stars", stars);
                movieJson.addProperty("rating", rs.getFloat("rating"));

                jsonArray.add(movieJson);
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

    public String parseSort(String sort){
        if (sort.equals("1")){
            return "ORDER BY m.title ASC, r.rating DESC";
        } else if (sort.equals("2")) {
            return "ORDER BY m.title ASC, r.rating ASC";
        } else if (sort.equals("3")) {
            return "ORDER BY m.title DESC, r.rating DESC";
        } else if (sort.equals("4")) {
            return "ORDER BY m.title DESC, r.rating ASC";
        } else if (sort.equals("5")) {
            return "ORDER BY r.rating DESC, m.title ASC";
        } else if (sort.equals("6")) {
            return "ORDER BY r.rating DESC, m.title DESC";
        } else if (sort.equals("7")) {
            return "ORDER BY r.rating ASC, m.title ASC";
        } else  {
            return "ORDER BY r.rating ASC, m.title DESC";
        }
    };
}
