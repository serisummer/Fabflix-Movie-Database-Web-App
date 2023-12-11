import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
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
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        //validating credit card info
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String ccnumber = request.getParameter("ccnumber");
        String expdate = request.getParameter("expdate");

        try (Connection conn = dataSource.getConnection()) {
            String query = "select * from creditcards " +
                    "where firstName like ? " +
                    "and lastName like ? " +
                    "and id like ? " +
                    "and expiration like ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, ccnumber);
            statement.setString(4, expdate);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                //validation success
                responseJsonObject.addProperty("status", "success");
                request.getServletContext().log("success");

                //get items in cart
                ArrayList<Item> previousItems = (ArrayList<Item>) session.getAttribute("previousItems");
                JsonArray salesJsonArray = new JsonArray();

                //get customerId
                String customerId = ((User)session.getAttribute("user")).getId();
                //get saleDate
                String saleDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                //initialize totalSalesPrice
                BigDecimal totalSalePrice = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);

                for (Item item : previousItems) {
                    //calculate totalSalesPrice
                    totalSalePrice = totalSalePrice.add(item.getTotalPrice()).setScale(2, RoundingMode.HALF_UP);

                    //generate sale id
                    String idQuery = "select id from sales order by id DESC limit 1";
                    Statement idStatement = conn.createStatement();
                    ResultSet idRs = idStatement.executeQuery(idQuery);
                    String id = "";
                    if (idRs.next()) {
                        String idString = idRs.getString("id");
                        int intValue = Integer.parseInt(idString) + 1;
                        id = Integer.toString(intValue);
                    }
                    //get movieId
                    String movieId = item.getId();

                    //insert into sales table
                    String insertQuery = "insert into sales(id, customerId, movieId, saleDate)" +
                            "values (?, ?, ?, ?)";
                    PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                    insertStatement.setString(1, id);
                    insertStatement.setString(2, customerId);
                    insertStatement.setString(3, movieId);
                    insertStatement.setString(4, saleDate);
                    insertStatement.executeUpdate();

                    //store confirmation info into session
                    SalesRecord salesRecord = new SalesRecord(
                            id,
                            item.getTitle(),
                            item.getQuantity(),
                            item.getUnitPrice().toString(),
                            item.getTotalPrice().toString());
                    ArrayList<SalesRecord> salesRecords = (ArrayList<SalesRecord>) session.getAttribute("salesRecords");
                    if (salesRecords == null) {
                        salesRecords = new ArrayList<SalesRecord>();
                        salesRecords.add(salesRecord);
                        session.setAttribute("salesRecords", salesRecords);
                    }
                    else {
                        salesRecords.add(salesRecord);
                    }
                    session.setAttribute("salesRecords", salesRecords);
                    session.setAttribute("totalSalesPrice", totalSalePrice.toString());
                }
            }
            else {
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("fail");
                responseJsonObject.addProperty("message", "Incorrect payment information");
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