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

});


