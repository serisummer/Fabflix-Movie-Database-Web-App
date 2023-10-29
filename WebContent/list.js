$(document).ready(function (){

    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("genre")){
        console.log("genres!")
        //execute the MoviesByGenreServlet
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/moviesbygenre",
            data: {
                genre: searchParams.get("genre")
            },
            success: (resultData) => handleMovieList(resultData)
        });
    }else if (searchParams.has("prefix")){
        console.log("prefix!");
        //execute the MoviesByTitleServlet
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/moviesbytitle",
            data: {
                prefix: searchParams.get("prefix")
            },
            success: (resultData) => handleMovieList(resultData)
        });
    }else if (searchParams.has("title") && searchParams.has("year")
        && searchParams.has("director") && searchParams.has("star")){
        console.log("movieList!");

        //execute the MovieListServlet
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/form",
            data: {
                title: searchParams.get("title"),
                year: searchParams.get("year"),
                director: searchParams.get("director"),
                star: searchParams.get("star")
            },
            success: (resultData) => handleMovieList(resultData)
        });
    }else{
        // this is a case that is not accounted for.
    }

})

function handleMovieList(resultData) {
    console.log("handleMovieList v2 called");
    // Populate the table with movie data
    let moviesTableBodyElement = $("#movies_table_body");

    for (let i = 0; i < resultData.length; i++) {
        let movie = resultData[i];
        let rowHTML = "<tr>";
        rowHTML += ("<th>" + movie.title +"</th>")
        rowHTML += ("<th>" + movie.year +"</th>")
        rowHTML += ("<th>" + movie.director +"</th>")
        rowHTML += ("<th>" + movie.genres +"</th>")
        rowHTML += ("<th>" + movie.stars +"</th>")
        rowHTML += ("<th>" + movie.rating +"</th>")
        rowHTML += ('<th>Add To Cart</th>')
        rowHTML += ("</tr>")
        moviesTableBodyElement.append(rowHTML);
        console.log(rowHTML)
    }
}






