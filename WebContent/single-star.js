$(document).ready(function (){
    let searchForm = $("#search-form"); // Use jQuery selector to select the form
    searchForm.on("submit", function(event) {
        event.preventDefault();
        var title = $("#search-title").val(); // Use jQuery to get input values
        var year = $("#search-year").val();
        var director = $("#search-director").val();
        var star = $("#search-star").val();

        // Construct the URL
        var url = "list.html?title=" + encodeURIComponent(title) +
            "&year=" + encodeURIComponent(year) +
            "&director=" + encodeURIComponent(director) +
            "&star=" + encodeURIComponent(star);

        // Redirect the user to the new URL
        console.log(url)
        window.location.href = url;
    })
    $.ajax({
        url: 'api/session', // Replace with the actual URL of your SessionServlet
        dataType: 'json',
        success: function(data) {
            const lastListURL = data.lastListURL;
            console.log(data)
            // Update the href attribute of the movie list button
            $('#movie-list').attr('href', lastListURL);
        },
        error: function(xhr, status, error) {
            console.error('Error fetching last accessed URL:', error);
        }
    });

})

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult (single-star.js)");
    console.log(resultData)

    let starInfoElement = jQuery("#star_name");
    let starDobElement = jQuery("#star_dob");
    let dob = resultData[0]["birthYear"];
    if(dob==null){
        dob = "N/A";
    }
    starDobElement.text("Date of Birth: "+dob);


    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["name"] + "</p>");


    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>";
        rowHTML += '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
            + resultData[i]["title"] + '</a>';
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";

        rowHTML += "<th>";
        rowHTML += "<form class='cart' method='post'>" +
            "<input type='hidden' name='itemId' value='" + resultData[i]["id"] + "'>" +
            "<input type='hidden' name='itemTitle' value='" + resultData[i]["title"] + "'>" +
            "<input type='hidden' name='actionType' value='add'>" +
            "<input type='submit' value='Add'>" +
            "</form>";
        rowHTML += "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    $('.cart').submit(handleCartSubmit);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});