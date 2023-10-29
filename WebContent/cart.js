/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let itemsTableBodyElement = $("#items_table_body");
    for (let i = 0; i < resultArray.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultArray[i]["title"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["unitPrice"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["totalPrice"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["quantity"] + "</th>";

        rowHTML += "<th>";
        rowHTML += "<form class='cart' method='post'>" +
                        "<input type='hidden' name='itemId' value='" + resultArray[i]["id"] + "'>" +
                        "<input type='hidden' name='itemTitle' value='" + resultArray[i]["title"] + "'>" +
                        "<input type='hidden' name='actionType' value='add'>" +
                        "<input type='submit' value='+'>" +
                    "</form>";
        rowHTML += "<form class='cart' method='post'>" +
                        "<input type='hidden' name='itemId' value='" + resultArray[i]["id"] + "'>" +
                        "<input type='hidden' name='itemTitle' value='" + resultArray[i]["title"] + "'>" +
                        "<input type='hidden' name='actionType' value='remove'>" +
                        "<input type='submit' value='-'>" +
                    "</form>";
        rowHTML += "</th>";

        rowHTML += "<th>";
        rowHTML += "<form class='cart' method='post'>" +
            "<input type='hidden' name='itemId' value='" + resultArray[i]["id"] + "'>" +
            "<input type='hidden' name='itemTitle' value='" + resultArray[i]["title"] + "'>" +
            "<input type='hidden' name='actionType' value='delete'>" +
            "<input type='submit' value='Delete'>" +
            "</form>";
        rowHTML += "</th>";

        rowHTML += "</tr>";
        itemsTableBodyElement.append(rowHTML);
    }
    $('.cart').submit(handleCartSubmitShoppingCart);
}

$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});