function addQuestion() {
    const questionId = crypto.randomUUID();
    const questionContainer = document.getElementById(
            "questions-container"
            );

    const questionHTML = `
                  <div class="tile mt-3" id="question-${questionId}">
                      <div class="tile-body">
                          <input type="text" class="form-control mb-3 question-input" name="question-${questionId}" placeholder="Enter question" required>
                          <div id="choices-container-${questionId}"></div>
                          <div class="error-message text-danger mt-2"></div>
                          <div class="d-flex mt-2">
                              <button class="btn btn-sm btn-success me-2" type="button" onclick="addChoice('${questionId}')" style="width: 150px;">
                                  <i class="bi bi-plus-circle me-1"></i> Add Choice
                              </button>
                              <button class="btn btn-sm btn-danger" type="button" onclick="removeQuestion('${questionId}')" style="width: 150px;">
                                  <i class="bi bi-trash-fill me-1"></i> Remove Question
                              </button>
                          </div>
                      </div>
                  </div>`;

    questionContainer.insertAdjacentHTML("beforeend", questionHTML);
}

function addChoice(questionId) {
    const choiceContainer = document.getElementById(
            `choices-container-${questionId}`
            );
    const choiceId = crypto.randomUUID();

    const choiceHTML = `
                  <div class="d-flex align-items-center mt-2" id="choice-${choiceId}">
                      <input type="text" class="form-control me-2 choice-input" name="choice-${questionId}-${choiceId}" placeholder="Enter choice" required>
                      <div class="form-check me-2">
                          <input type="checkbox" class="form-check-input" name="correct-${questionId}-${choiceId}">
                      </div>
                      <button class="btn btn-sm btn-outline-danger ms-2" type="button" onclick="removeChoice('${choiceId}')" style="width: 75px;">Remove</button>
                  </div>`;

    choiceContainer.insertAdjacentHTML("beforeend", choiceHTML);
}

function removeQuestion(questionId) {
    document.getElementById(`question-${questionId}`).remove();
}

function removeChoice(choiceId) {
    document.getElementById(`choice-${choiceId}`).remove();
}

function validateQuiz() {
    const questions = document.querySelectorAll("[id^=question-]");
    let firstError = null;
    const formError = document.getElementById("form-error");
    formError.textContent = "";

    if (questions.length === 0) {
        formError.textContent = "You must add at least one question.";
        return false;
    }

    questions.forEach((question) => {
        const choices = question.querySelectorAll(".choice-input");
        const checkboxes = question.querySelectorAll(".form-check-input");
        const errorDiv = question.querySelector(".error-message");
        errorDiv.textContent = "";

        // At least 2 choices 
        if (choices.length < 2) {
            errorDiv.textContent = "Each question must have at least two choices.";
            if (!firstError) firstError = question;
        }
        
        // At least 1 correct choice
        const isChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
        if (!isChecked) {
            errorDiv.textContent = "Each question must have at least one correct answer.";
            if (!firstError) firstError = question;
        }
    });

    if (firstError) {
        firstError.scrollIntoView({ behavior: "smooth", block: "center" });
        return false;
    }

    return true;
}