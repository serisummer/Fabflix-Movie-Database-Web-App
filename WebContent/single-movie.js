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
    console.log("handleResult (single-movie): populating movie info from resultData");
    console.log(resultData)

    let movie_title = $("#movie-name")
    let movie = resultData[0];

    movie_title.text(`${movie.title}(${movie.year})`)

    let moviesTableBodyElement = $("#movie_table_body");

    let rowHTML = "<tr>";
    rowHTML += ("<th>" + movie.director +"</th>")
    rowHTML += ("<th>" + parseGenres(movie.genres) +"</th>")
    rowHTML += ("<th>" + parseStars(movie.stars) +"</th>")
    rowHTML += ("<th>" + movie.rating +"</th>")

    rowHTML += "<th>";
    rowHTML += "<form class='cart' method='post'>" +
        "<input type='hidden' name='itemId' value='" + movie.id + "'>" +
        "<input type='hidden' name='itemTitle' value='" + movie.title + "'>" +
        "<input type='hidden' name='actionType' value='add'>" +
        "<input type='submit' value='Add'>" +
        "</form>";
    rowHTML += "</th>";

    rowHTML += ("</tr>")
    moviesTableBodyElement.append(rowHTML);
    $('.cart').submit(handleCartSubmit);
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by SingleMovieServlet in single-movie.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});
function parseGenres(genres){
    let genresArray = genres.split(",")
    let finalHTML = ""
    for (let i = 0; i < genresArray.length; i++) {
        g = genresArray[i]
        html = `<a href=list.html?genre=${g}>${g}</a> `
        finalHTML+=html
    }
    return finalHTML;
}

function parseStars(stars){
    finalHTML = ""
    let starsArray = stars.split(",");
    for (let i = 0; i < starsArray.length; i++) {
        var pair = starsArray[i];
        pair = pair.substring(1, pair.length-1);
        var data = pair.split("$")
        var star = data[0];
        var id = data[1]
        finalHTML+= `<a href=single-star.html?id=${id}>${star}, </a> `
    }
    return finalHTML
}

