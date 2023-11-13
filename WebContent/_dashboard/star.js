$(document).ready(function(){
    let star_form = $("#star_form");
    star_form.on("submit", function (event){
        console.log("submit new star form");
        event.preventDefault();
        $.ajax(
            "api/newstar", {
                method: "POST",
                // Serialize the login form to the data sent by POST request
                data: star_form.serialize(),
                success: handleNewStarResult
            }
        );
    })
})

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleNewStarResult(resultDataString) {
    console.log(resultDataString);
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    //
    if (resultDataJson["status"] === "success") {
        $("#login_error_message").text(resultDataJson["id"]);
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}
