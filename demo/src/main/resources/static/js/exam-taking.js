document.addEventListener("DOMContentLoaded", function () {
    let startTime = new Date().getTime();

    // Lấy thời gian làm bài từ thẻ meta
    var quizDurationElement = document.getElementById("quizDurationMeta");
    if (!quizDurationElement) {
        console.error("Không tìm thấy phần tử quizDurationMeta!");
        return;
    }

    var quizDuration = parseInt(quizDurationElement.getAttribute("data-duration"), 10);
    var countDownDate = startTime + quizDuration * 60 * 1000;

    // Bắt đầu chế độ toàn màn hình khi vào trang
    function enterFullScreen() {
        let elem = document.documentElement;
        if (elem.requestFullscreen) {
            elem.requestFullscreen();
        } else if (elem.mozRequestFullScreen) {
            elem.mozRequestFullScreen();
        } else if (elem.webkitRequestFullscreen) {
            elem.webkitRequestFullscreen();
        } else if (elem.msRequestFullscreen) {
            elem.msRequestFullscreen();
        }
    }

    // Thêm sự kiện click vào body để yêu cầu chế độ toàn màn hình
    document.body.addEventListener('click', function() {
        enterFullScreen();
    });

    // Đếm ngược thời gian làm bài
    var countdownInterval = setInterval(function () {
        var now = new Date().getTime();
        var distance = countDownDate - now;

        var hours = Math.floor(distance / (1000 * 60 * 60));
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000);

        var timeString =
            hours > 0
                ? `${hours}:${minutes < 10 ? "0" : ""}${minutes}:${seconds < 10 ? "0" : ""}${seconds}`
                : `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;

        var countdownElement = document.getElementById("countdown");
        if (countdownElement) {
            countdownElement.innerHTML = timeString;
        }

        if (distance <= 0) {
            clearInterval(countdownInterval);
            console.log("Time is up, submitting form!");
            submitQuiz();
        }
    }, 1000);

    // Kiểm tra force submit mỗi 2 giây
    var forceSubmitInterval = setInterval(function () {
        if (typeof submissionID === "undefined" || submissionID === null) {
            console.error("submissionID is not defined!");
            return;
        }

        fetch(`/online_quiz/exam/check_submission?submissionID=${submissionID}`, { cache: "no-store" })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Force submit check:", data);
                if (data.isSubmit) {
                    console.log("Force submit triggered!");
                    submitQuiz();
                }
            })
            .catch(error => console.error("Error checking submission:", error));
    }, 2000);

    // Nếu thoát fullscreen thì tự động submit
    document.addEventListener("fullscreenchange", function () {
        if (!document.fullscreenElement) {
            alert("Bạn đã thoát toàn màn hình! Bài làm sẽ được nộp.");
            submitQuiz();
        }
    });

    // Nếu chuyển tab hoặc ẩn trang thì tự động submit
    document.addEventListener("visibilitychange", function () {
        if (document.hidden) {
            alert("Bạn đã rời khỏi trang! Bài làm sẽ được nộp.");
            submitQuiz();
        }
    });

    // Nếu nhấn ESC hoặc F11 thì tự động submit
    document.addEventListener("keydown", function (event) {
        if (event.key === "F11" || event.key === "Escape") {
            alert("Bạn đã thoát toàn màn hình! Bài làm sẽ được nộp.");
            submitQuiz();
        }
    });

    // Xử lý khi nhấn nút "Submit"
    var submitBtn = document.getElementById("submitBtn");
    if (submitBtn) {
        submitBtn.addEventListener("click", function () {
            console.log("Submit button clicked");
            submitQuiz();
        });
    }

    // Hàm nộp bài
    function submitQuiz() {
        clearInterval(forceSubmitInterval);
        clearInterval(countdownInterval);

        let endTime = new Date().getTime();
        let duration = Math.round((endTime - startTime) / 60000); // Tính thời gian làm bài theo phút
        
        var durationInput = document.getElementById("hiddenDuration");
        if (durationInput) {
            durationInput.value = duration; // Gán vào input ẩn
        }

        console.log("Submitting quiz... Duration:", duration);

        var quizForm = document.getElementById("quizForm");
        if (quizForm) {
            quizForm.submit(); // Nộp form
        } else {
            console.error("Không tìm thấy form quizForm!");
        }
    }

    // Gán submitQuiz vào window để gọi từ mọi nơi
    window.submitQuiz = submitQuiz;
});

