document.getElementById("createExamForm").addEventListener("submit", function (event) {
    let now = new Date();
    let examName = document.getElementById("examName");
    let startTime = document.getElementById("startTime");
    let endTime = document.getElementById("endTime");

    let isValid = true;

    // Reset validation styles
    examName.classList.remove("is-invalid");
    startTime.classList.remove("is-invalid");
    endTime.classList.remove("is-invalid");

    // Validate exam name
    if (!examName.value.trim()) {
        examName.classList.add("is-invalid");
        isValid = false;
    }

    // Function to check if the year has exactly 4 digits
    function isValidYear(dateString) {
        let year = dateString.split("-")[0];
        return year.length === 4 && !isNaN(year);
    }

    // Validate start time
    if (!startTime.value.trim()) {
        startTime.classList.add("is-invalid");
        startTime.nextElementSibling.textContent = "Start time is required.";
        isValid = false;
    } else if (!isValidYear(startTime.value)) {
        startTime.classList.add("is-invalid");
        startTime.nextElementSibling.textContent = "Year must have exactly 4 digits.";
        isValid = false;
    } else {
        let start = new Date(startTime.value);
        if (start < now) {
            startTime.classList.add("is-invalid");
            startTime.nextElementSibling.textContent = "Start time must be in the future.";
            isValid = false;
        }
    }

    // Validate end time
    if (!endTime.value.trim()) {
        endTime.classList.add("is-invalid");
        endTime.nextElementSibling.textContent = "End time is required.";
        isValid = false;
    } else if (!isValidYear(endTime.value)) {
        endTime.classList.add("is-invalid");
        endTime.nextElementSibling.textContent = "Year must have exactly 4 digits.";
        isValid = false;
    } else {
        let start = new Date(startTime.value);
        let end = new Date(endTime.value);
        if (end <= start) {
            endTime.classList.add("is-invalid");
            endTime.nextElementSibling.textContent = "End time must be later than start time.";
            isValid = false;
        }
    }

    if (!isValid) {
        event.preventDefault(); // Stop form submission if validation fails
    }
});
