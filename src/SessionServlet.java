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
import jakarta.servlet.http.HttpSession;


@WebServlet(name = "SessionServlet", urlPatterns = "/api/session")
public class SessionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Always remember to close db connection after usage. Here it's done by try-with-resources
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);
        String lastListURL = (String) session.getAttribute("lastListURL");

        JsonObject json = new JsonObject();
        if (lastListURL != null) {
            json.addProperty("lastListURL", lastListURL);
        } else {
            json.addProperty("lastListURL", "list.html");
        }

        out.write(json.toString());
    }
}