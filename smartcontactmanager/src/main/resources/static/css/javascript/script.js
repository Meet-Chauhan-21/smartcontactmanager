console.log("This is a javaScript file.");

const toggleSidebar = () => {

    if($(".sidebar").is(":visible")){

        //true

        $(".sidebar").css("display","none");
        $(".content").css("margin-left","2%");


    } else {

        //false

        $(".sidebar").css("display","block");
        $(".content").css("margin-left","22%");

    }

};

function validatePhoneNumber(event) {
            // Prevent form submission
            event.preventDefault();

            // Regular expression for a 10-digit phone number
            const phoneRegex = /^\d{10}$/;

            // Get the phone number input value
            const phoneInput = document.getElementById("phone").value;

            // Check if the phone number matches the regex
            if (!phoneRegex.test(phoneInput)) {
                alert("Please enter a valid 10-digit phone number!");
                return false;
            } else {
                alert("Phone number is valid!");
                event.target.submit(); // Submit the form
            }
}
