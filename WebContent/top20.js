/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

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
        success: function (data) {
            const lastListURL = data.lastListURL;
            console.log(data)
            // Update the href attribute of the movie list button
            $('#movie-list').attr('href', lastListURL);
        },
        error: function (xhr, status, error) {
            console.error('Error fetching last accessed URL:', error);
        }
    })
})

function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movies table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let moviesTableBodyElement = jQuery("#movies_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['id'] + '">'
            + resultData[i]["title"] +     // display title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";

        const starStrings = resultData[i]["stars"].split(", ");
        const starIdStrings = resultData[i]["starIds"].split(", ");
        rowHTML += "<th>";
        for (let j = 0; j < starStrings.length; j++) {
            rowHTML +=
                '<a href="single-star.html?id=' + starIdStrings[j] + '">'
                + starStrings[j] + '</a>';
            if (j < starStrings.length-1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";

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
        moviesTableBodyElement.append(rowHTML);
    }
    $('.cart').submit(handleCartSubmit);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMoviesResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});