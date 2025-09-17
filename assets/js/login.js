// wrap do login e do cadastro
let loginForm = document.querySelector(".login-wrap");
let signupForm = document.querySelector(".signup-wrap");
let title = document.querySelector("title");

let signupToggleBtn = document.querySelector("#toggle-signup");
let loginToggleBtn = document.querySelector("#toggle-login");

signupToggleBtn.onclick = () => {
  loginForm.classList.remove("active");
  signupForm.classList.add("active");
  title.textContent = "Cadastrar";
};

loginToggleBtn.onclick = () => {
  signupForm.classList.remove("active");
  loginForm.classList.add("active");
  title.textContent = "Entrar";
};
