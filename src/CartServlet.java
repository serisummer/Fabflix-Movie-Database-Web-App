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
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<Item> previousItems = (ArrayList<Item>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<Item>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(item -> {
            JsonObject itemJsonObject = new JsonObject();
            itemJsonObject.addProperty("id", item.getId());
            itemJsonObject.addProperty("title", item.getTitle());
            itemJsonObject.addProperty("quantity", item.getQuantity());
            itemJsonObject.addProperty("unitPrice", item.getUnitPrice().toString());
            itemJsonObject.addProperty("totalPrice", item.getTotalPrice().toString());
            previousItemsJsonArray.add(itemJsonObject);
        });
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemId = request.getParameter("itemId");
        String itemTitle = request.getParameter("itemTitle");
        String actionType = request.getParameter("actionType");

        //System.out.println(itemId);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        ArrayList<Item> previousItems = (ArrayList<Item>) session.getAttribute("previousItems");
        if (actionType.equals("add")) {
            if (previousItems == null) {
                previousItems = new ArrayList<Item>();
                Item item = new Item(itemId, itemTitle);
                previousItems.add(item);
                session.setAttribute("previousItems", previousItems);
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                boolean dup = false;
                synchronized (previousItems) {
                    for (Item i : previousItems) {
                        if (i.getId().equals(itemId)) {
                            i.change(1);
                            dup = true;
                            break;
                        }
                    }
                    if (!dup) {
                        previousItems.add(new Item(itemId, itemTitle));
                    }
                }
            }
        }

        if (actionType.equals("remove")) {
            synchronized (previousItems) {
                for (Item i : previousItems) {
                    if (i.getId().equals(itemId)) {
                        i.change(-1);
                        break;
                    }
                }
            }
        }

        if (actionType.equals("delete")) {
            synchronized (previousItems) {
                Item item = null;
                for (Item i : previousItems) {
                    if (i.getId().equals(itemId)) {
                        item = i;
                        break;
                    }
                }
                previousItems.remove(item);
            }
        }
    }
}