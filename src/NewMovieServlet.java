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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet(name = "NewMovieServlet", urlPatterns = "/_dashboard/api/newmovie")
public class NewMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();


        //validating new star info
        String title = request.getParameter("title");
        int movie_year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String star_name = request.getParameter("name");
        String star_year_string = request.getParameter("birth_year");
        String genre = request.getParameter("genre");

        int star_year = -1;
        if(!star_year_string.isEmpty()){
            try{
                star_year = Integer.parseInt(star_year_string);
            } catch (Exception e){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("response", e.getMessage());
                out.write(jsonObject.toString());
                out.close();
                return;
            }
        }

        try (Connection conn = dataSource.getConnection()) {


            CallableStatement statement = conn.prepareCall("call add_movie(?, ?, ?, ?, ?, ?);");
            statement.setString(1, title);
            statement.setInt(2, movie_year);
            statement.setString(3, director);
            statement.setString(4, star_name);
            statement.setInt(5, star_year);
            statement.setString(6, genre);

            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("response", rs.getString("message"));
                request.getServletContext().log("success");

            }else{
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("response", rs.getString("call failed"));
                request.getServletContext().log("fail");

            }

            out.write(responseJsonObject.toString());
            rs.close();
            statement.close();

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("response", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}