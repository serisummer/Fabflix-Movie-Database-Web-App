/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartSubmit(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/cart", {
        method: "POST",
        data: $(this).serialize(),
        success: () => {
            window.alert("Success");
        }
    });
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartSubmitShoppingCart(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/cart", {
        method: "POST",
        data: $(this).serialize(),
        success: () => {
            window.alert("Success");
            window.location.reload();
        }
    });
}