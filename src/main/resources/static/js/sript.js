// Attach reCAPTCHA token to hidden field before submit
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registerForm");
    if (form) {
        form.addEventListener("submit", function () {
            const tokenField = document.querySelector("[name='g-recaptcha-response']");
            const hiddenField = document.getElementById("captchaResponse");
            if (tokenField && hiddenField) {
                hiddenField.value = tokenField.value;
				<script src="https://www.google.com/recaptcha/api.js" async defer></script>

            }
        });
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const fileInput = document.querySelector("input[type='file']");
    if (fileInput) {
        const previewImg = document.createElement("img");
        previewImg.classList.add("profile-pic");
        previewImg.style.display = "none";
        fileInput.insertAdjacentElement("afterend", previewImg);

        fileInput.addEventListener("change", (event) => {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewImg.src = e.target.result;
                    previewImg.style.display = "block";
                };
                reader.readAsDataURL(file);
            }
        });
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const fileInputs = document.querySelectorAll("input[type='file']");
    fileInputs.forEach(input => {
        input.addEventListener("change", (event) => {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    let preview = input.nextElementSibling;
                    if (!preview || preview.tagName !== "IMG") {
                        preview = document.createElement("img");
                        preview.classList.add("post-img");
                        input.insertAdjacentElement("afterend", preview);
                    }
                    preview.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    });
});

function ConfrimDelete()
{
	return confirm("Are you sure you want to delete your post?");
}

function ConfirmUpdate()
{
	return confirm("Are you sure to update your post?");
}

document.addEventListener("DOMContentLoaded", function () {
      document.querySelectorAll(".delete-comment").forEach(dot => {
          dot.addEventListener("click", function () {
              const commentId = this.getAttribute("data-id");

              fetch("/comments/delete/" + commentId, {
                  method: "DELETE"
              })
              .then(res => {
                  if (res.ok) {
                      this.closest(".comment").remove(); // Remove from UI
                  } else {
                      alert("Failed to delete comment");
                  }
              })
              .catch(() => alert("Error deleting comment"));
          });
      });
  });
