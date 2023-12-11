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

@WebServlet(name = "NewStarServlet", urlPatterns = "/_dashboard/api/newstar")
public class NewStarServlet extends HttpServlet {
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
        String name = request.getParameter("name");
        String form_year = request.getParameter("year");
        // 1.) empty year -> pass as null
        // 2.) valid year (int) -> just pass year
        // 3.) invalid year -> return error message

        // if a non empty string was passed
        int year = -1;
        if(!form_year.isEmpty()){
            try {
                year = Integer.parseInt(form_year);
            } catch (NumberFormatException nfe) {
                // they put something not a year and we'll just ignore this request
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("fail");
                responseJsonObject.addProperty("message", "invalid year");
                out.write(responseJsonObject.toString());
                out.close();
                return;
            }
        }

        try (Connection conn = dataSource.getConnection()) {
            // getting max id
            String query = "SELECT id from stars order by id desc limit 1";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // need to get id
                String id = rs.getString("id");
                // need to extract the number part and increment
                String id_num = id.substring(2);
                int idint = Integer.parseInt(id_num);
                idint+=1;
                id = "nm" + idint;

                //insert into star table
                String insertQuery = "insert into stars VALUE( ?, ?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                insertStatement.setString(1, id);
                insertStatement.setString(2, name);
                if(year==-1){
                    //if year was null
                    insertStatement.setString(3, null);
                }else{
                    insertStatement.setInt(3, year);
                }
                insertStatement.executeUpdate();

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("id", id);
                request.getServletContext().log("success");

            }
            else {
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("fail");
                responseJsonObject.addProperty("message", "sql error getting max id");
            }
            out.write(responseJsonObject.toString());
            rs.close();
            statement.close();

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
    }
}