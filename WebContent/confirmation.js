/**
 * Handle the data returned by PaymentServlet
 * @param resultDataString jsonObject
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    $("#totalSalesPrice").text("Total Sales Price: $" + resultDataJson["totalSalesPrice"]);
    handleSalesArray(resultDataJson["salesRecords"]);
}

function handleSalesArray(resultArray) {
    console.log(resultArray);
    let recordsTableBodyElement = $("#records_table_body");

    for (let i = 0; i < resultArray.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultArray[i]["id"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["title"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["quantity"] + "</th>";
        rowHTML += "<th>$" + resultArray[i]["unitPrice"] + "</th>";
        rowHTML += "<th>$" + resultArray[i]["totalPrice"] + "</th>";
        rowHTML += "</tr>";
        recordsTableBodyElement.append(rowHTML);
    }
}

$.ajax(
    "api/confirmation", {
        method: "GET",
        success: handleSessionData
    }
);