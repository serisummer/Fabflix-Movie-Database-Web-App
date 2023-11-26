import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()){
            JsonArray jsonArray = new JsonArray();

            String query = request.getParameter("query");

            if (query == null || query.trim().isEmpty()) {
                out.write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // in project 4, you should do full text search with MySQL to find the matches on movies and stars

            String query_string = "SELECT id, title from movies WHERE match(title) against (? IN BOOLEAN " +
                    "MODE) LIMIT 10;";

            PreparedStatement statement = conn.prepareStatement(query_string);
            String filter = "";
            String [] words = query.split(" ");
            for(String word: words ){
                filter += '+' + word + "* ";
            }

            statement.setString(1, filter);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                jsonArray.add(generateJsonObject(movie_id, movie_title));
            }

            out.write(jsonArray.toString());
            response.setStatus(200);
            rs.close();
            statement.close();

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.sendError(500, e.getMessage());
        } finally {
            out.close();
        }
    }

    private static JsonObject generateJsonObject(String id, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", id);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}
