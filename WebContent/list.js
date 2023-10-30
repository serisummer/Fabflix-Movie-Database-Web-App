$(document).ready(function (){

    const searchParams = new URLSearchParams(window.location.search);
    let sort  = 1;
    if(searchParams.has("sort")){
        sort = searchParams.get("sort")
        $("#sort-by").val(parseInt(searchParams.get("sort")))
    }

    let n = 10 // default
    if(searchParams.has("n")){
        n = searchParams.get("n")
        $("#movies-per-page").val(parseInt(searchParams.get("n")))

    }

    let page = 1 // default
    if(searchParams.has("page")){
        page = searchParams.get("page")
    }

    if (searchParams.has("genre")){
        console.log("genres!")
        //execute the MoviesByGenreServlet
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/moviesbygenre",
            data: {
                genre: searchParams.get("genre"),
                sort: sort,
                n: n,
                page: page
            },
            success: (resultData) => {check(resultData),
                handleMovieList(resultData)}
        });
    }else if (searchParams.has("prefix")){
        console.log("prefix!");
        //execute the MoviesByTitleServlet
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/moviesbytitle",
            data: {
                prefix: searchParams.get("prefix"),
                sort: sort,
                n: n,
                page: page
            },
            success: (resultData) => {check(resultData),
                handleMovieList(resultData)}
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
                star: searchParams.get("star"),
                sort: sort,
                n: n,
                page: page
            },
            success: (resultData) => {check(resultData),
                handleMovieList(resultData)}

        });
    }else{
        // this is a case that is not accounted for.
    }

    function check(resultData){
        const searchParams = new URLSearchParams(window.location.search);
        let currentPage = searchParams.get("page") || 1;
        let maxRecords = searchParams.get("n") || 10;
        let totalRecords = resultData.length;
        if (totalRecords<maxRecords) {
            $("#next-button").prop("disabled", true);
        } else {
            $("#next-button").prop("disabled", false);
        }
        if(currentPage == 1){
            $("#prev-button").prop("disabled", true);
        }else{
            $("#prev-button").prop("disabled", false);
        }
    }


    $("#filter-form").on("submit", function (event) {
        event.preventDefault();

        const sort = $("#sort-by").val();
        const n = $("#movies-per-page").val();

        // Get the current URL and append the selected parameters
        const searchParams = new URLSearchParams(window.location.search);

        //if the current url doesnt have sorting parameters jst append then
        if (!searchParams.has("n")){
            var currentURL = window.location.href;
            var newURL = `&sort=${sort}&n=${n}`;
            window.location.href = currentURL + newURL;
        }else{
            //need to replace the parameters and idk how else to do it but to rebuild th url
            if(searchParams.has("title")){
                title = searchParams.get("title");
                year = searchParams.get("year");
                director = searchParams.get("director");
                star = searchParams.get("star")
                window.location.href=`list.html?title=${title}&year=${year}&director=${director}&star=${star}&sort=${sort}&n=${n}&page=1`
            }
            if (searchParams.has("genre")){
                genre = searchParams.get("genre")
                window.location.href=`list.html?genre=${genre}&sort=${sort}&n=${n}&page=1`

            }
            if (searchParams.has("prefix")) {
                prefix = searchParams.get("prefix")
                window.location.href=`list.html?prefix=${prefix}&sort=${sort}&n=${n}&page=1`

            }
        }
    })

    $("#next-button").on("click", function (event) {
        event.preventDefault();
        const searchParams = new URLSearchParams(window.location.search);
        let url = ""
        const currentPage = parseInt(searchParams.get("page")) || 1;
        const nextPage = currentPage + 1;
        let n = searchParams.get("n") || 10;
        let sort = searchParams.get("sort") || 1;

        if (searchParams.has("genre")){
            url = `list.html?genre=${searchParams.get("genre")}&sort=${sort}&n=${n}&page=${nextPage}`
        }
        else if (searchParams.has("prefix")){
            url = `list.html?prefix=${searchParams.get("prefix")}&sort=${sort}&n=${n}&page=${nextPage}`
        }
        else {
            title = searchParams.get("title");
            year = searchParams.get("year");
            director = searchParams.get("director");
            star = searchParams.get("star");

            url = `list.html?title=${title}&year=${year}&director=${director}&star=${star}&sort=${sort}&n=${n}&page=${nextPage}`

        }
       window.location.href= url
    });

    $("#prev-button").on("click", function (event) {
        event.preventDefault();
        const searchParams = new URLSearchParams(window.location.search);
        let url = ""
        const currentPage = parseInt(searchParams.get("page")) || 1;
        const nextPage = currentPage - 1;
        let n = searchParams.get("n") || 10;
        let sort = searchParams.get("sort") || 1;

        if (searchParams.has("genre")){
            url = `list.html?genre=${searchParams.get("genre")}&sort=${sort}&n=${n}&page=${nextPage}`
        }
        else if (searchParams.has("prefix")){
            url = `list.html?prefix=${searchParams.get("prefix")}&sort=${sort}&n=${n}&page=${nextPage}`
        }
        else {
            title = searchParams.get("title");
            year = searchParams.get("year");
            director = searchParams.get("director");
            star = searchParams.get("star");

            url = `list.html?title=${title}&year=${year}&director=${director}&star=${star}&sort=${sort}&n=${n}&page=${nextPage}`

        }
        window.location.href= url
    });

})

function handleMovieList(resultData) {

    console.log("handleMovieList (list.js) called");
    // Populate the table with movie data
    let moviesTableBodyElement = $("#movies_table_body");
    moviesTableBodyElement.empty()

    for (let i = 0; i < resultData.length; i++) {
        let movie = resultData[i];
        let rowHTML = "<tr>";
        rowHTML += ("<th>" + parseMovieID(movie.title, movie.id) +"</th>")
        rowHTML += ("<th>" + movie.year +"</th>")
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
    }
    $('.cart').submit(handleCartSubmit);
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


function parseMovieID(movieTitle, movieId){
    return `<a href=single-movie.html?id=${movieId}>${movieTitle}</a> `
}
function parseGenres(genres){
    let genresArray = genres.split(",")
    let finalHTML = ""
    for (let i = 0; i < genresArray.length; i++) {
        g = genresArray[i]
        html = `<a href=list.html?genre=${g}>${g}</a> `
        finalHTML+=html
    }
    return finalHTML;
};





