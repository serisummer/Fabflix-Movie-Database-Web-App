$(document).ready(function () {

    /* this code uses jQuery to populate the character list since it's easier than individually creating all the <li> for each character */
    var characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789*";
    var charList = $("#browse-by-title-list");

    for (var i = 0; i < characters.length; i++) {
        var prefix = characters[i];
        // need to make
        let url = `list.html?prefix=${prefix}`
        var letterHTML = "<li><a href=" + url + ">" + prefix + "</a></li>"
        charList.append(letterHTML);
    }

    function handleGenres(resultData){
        console.log("handleGenres: population genre block from resultData");

        let genreList = $("#browse-by-genre-list");
        for (let i = 0; i < resultData.length; i++) {
            var genre = resultData[i];
            let url = `list.html?genre=${genre}`
            var genreHTML = "<li><a href=" + url + ">" + genre + "</a></li>"
            genreList.append(genreHTML);
        }
    }

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

    });

    // Makes the HTTP GET request and registers the success callback function handleMoviesResult
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/genres", // Setting request URL, which is mapped by GenresServlet in GenreServlet.java
        success: (resultData) => handleGenres(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
    });

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


    // bind pressing enter key to a handler function
    $('#autocomplete').keypress(function(event) {
        // keyCode 13 is the enter key
        if (event.keyCode == 13) {
            // pass the value of the input box to the handler function
            handleNormalSearch($('#autocomplete').val())
        }
    })

    // $('#autocomplete') is to find element by the ID "autocomplete"
    $('#autocomplete').autocomplete({
        // documentation of the lookup function can be found under the "Custom lookup function" section
        lookup: function (query, doneCallback) {
            handleLookup(query, doneCallback)
        },
        onSelect: function(suggestion) {
            handleSelectSuggestion(suggestion)
        },
        // set delay time
        deferRequestBy: 300,
        // there are some other parameters that you might want to use to satisfy all the requirements
        // TODO: add other parameters, such as minimum characters
        minChars: 3,
    });

});

// AUTOCOMPLETE / FULL TEXT STUFFS
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")

    // check past query results first
    let cache = localStorage.getItem(query)
    if(cache){
        console.log("Using results previously stored in localStorage cache");
        handleLookupAjaxSuccess(JSON.parse(cache),query,doneCallback);
        return;
    }

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    console.log("sending AJAX request to backend Java Servlet")
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/autocomplete?query=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    //console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    //cache the result into a global variable
    localStorage.setItem(query, JSON.stringify(data));

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    window.location.href = `single-movie.html?id=${suggestion["data"]["id"]}`;
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */



/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}





