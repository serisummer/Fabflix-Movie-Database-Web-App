import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        JsonObject responseJsonObject = new JsonObject();

        ArrayList<SalesRecord> salesRecords = (ArrayList<SalesRecord>) session.getAttribute("salesRecords");
        if (salesRecords == null) {
            salesRecords = new ArrayList<SalesRecord>();
        }

        JsonArray salesRecordJsonArray = new JsonArray();
        for (SalesRecord r : salesRecords) {
            JsonObject recordJsonObject = new JsonObject();
            recordJsonObject.addProperty("id", r.getSalesId());
            recordJsonObject.addProperty("title", r.getTitle());
            recordJsonObject.addProperty("quantity", r.getQuantity());
            recordJsonObject.addProperty("unitPrice", r.getUnitPrice());
            recordJsonObject.addProperty("totalPrice", r.getTotalPrice());
            salesRecordJsonArray.add(recordJsonObject);
        }
        responseJsonObject.add("salesRecords", salesRecordJsonArray);
        responseJsonObject.addProperty("totalSalesPrice", (String)session.getAttribute("totalSalesPrice"));

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

}