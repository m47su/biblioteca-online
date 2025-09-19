window.COLOR_PRIMARY = window.COLOR_PRIMARY || "#2f3559"; // azul
window.COLOR_DANGER = window.COLOR_DANGER || "#d94a4a"; // vermelho

window.swalConfirmDelete =
  window.swalConfirmDelete ||
  function swalConfirmDelete(opts = {}) {
    return Swal.fire({
      icon: "warning",
      showCancelButton: true,
      reverseButtons: true,
      confirmButtonText: "Sim, excluir!",
      cancelButtonText: "Cancelar",
      confirmButtonColor: window.COLOR_DANGER,
      cancelButtonColor: window.COLOR_PRIMARY,
      buttonsStyling: true,
      ...opts,
    });
  };

// (avaliar + reviews)
const API_BASE = window.ELIVROS_API_BASE || "/api";
const HEADERS = { "Content-Type": "application/json" };
const STORAGE_KEY = "liked_ratings_fallback";

const starsWrap = document.getElementById("rating-stars");
const sendBtn = document.getElementById("send-review");
const reviewText = document.getElementById("review-text");
const listaAvaliacoes = document.getElementById("lista-avaliacoes");

const livro = {
  id: "esta-chovendo-estrelas",
  capa: "assets/img/capa (6).png",
  tags: ["Romance", "Drama"],
  titulo: "Está chovendo estrelas",
  sinopse:
    "Está chovendo estrelas é um livro que retrata a história de Laura, ela é uma garota autista que sonha em ser uma astronauta...",
  autor: {
    nome: "Ana Soares",
    foto: "assets/img/autor-anasoares.png",
    descricao: "Ana sempre foi uma garota criativa e sonhadora...",
  },
  avaliacoes: [
    {
      estrelas: 5,
      autor: "Ana Souza",
      foto: "assets/img/avaliacoes2.png",
      texto: "Que livro FODA! Já quero ler novamente.",
      editado: false,
    },
    {
      estrelas: 1,
      autor: "Clara Costa",
      foto: "assets/img/avaliacoes1.png",
      texto:
        "Já li melhores, não achei a escrita boa, além disso, o plot é fraco.",
      editado: true,
    },
  ],
};

function getFallback() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {};
  } catch {
    return {};
  }
}
function setFallback(m) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(m));
}

function carregarDadosDoLivro() {
  document.getElementById("capa-livro").src = livro.capa;

  const tagsContainer = document.getElementById("tags");
  tagsContainer.innerHTML = "";
  livro.tags.forEach((tagText) => {
    const tagElement = document.createElement("span");
    tagElement.className = "tag-item";
    tagElement.textContent = tagText;
    tagsContainer.appendChild(tagElement);
  });

  document.getElementById("titulo-livro").textContent = livro.titulo;
  document.getElementById("sinopse-livro").textContent = livro.sinopse;

  document.getElementById("foto-autor").src = livro.autor.foto;
  document.getElementById("nome-autor").textContent = livro.autor.nome;
  document.getElementById("descricao-autor").textContent =
    livro.autor.descricao;

  listaAvaliacoes.innerHTML = "";
  livro.avaliacoes.forEach((a) =>
    listaAvaliacoes.appendChild(makeReviewCard(a))
  );

  const mine = getMyReviewFromStorage();
  if (mine) {
    renderMyReview(mine);
    setRating(mine.estrelas);
  }
}

//estrelas
let selectedRating = 0;

function starEl(value) {
  const i = document.createElement("i");
  i.className = "fa-solid fa-star";
  i.style.cursor = "pointer";
  i.dataset.value = value;
  i.setAttribute("role", "button");
  i.setAttribute("tabindex", "0");
  i.ariaLabel = `Dar ${value} estrela(s)`;
  i.addEventListener("click", () => setRating(value));
  i.addEventListener("keydown", (e) => {
    if (e.key === "Enter" || e.key === " ") setRating(value);
    if (e.key === "ArrowRight" || e.key === "ArrowUp")
      setRating(Math.min(value + 1, 5));
    if (e.key === "ArrowLeft" || e.key === "ArrowDown")
      setRating(Math.max(value - 1, 1));
  });
  return i;
}
function paintStars(value = 0) {
  [...starsWrap.querySelectorAll("i")].forEach((el) => {
    el.classList.toggle("filled", Number(el.dataset.value) <= Number(value));
    el.style.color = el.classList.contains("filled") ? "#ffd700" : "#c9c9c9";
  });
}
function setRating(v) {
  selectedRating = v;
  paintStars(selectedRating);
}
function renderStarsBar() {
  starsWrap.innerHTML = "";
  for (let s = 1; s <= 5; s++) starsWrap.appendChild(starEl(s));
  paintStars(0);
}

//review usuario
const MY_REVIEW_KEY = "my_review_" + livro.id;
function getMyReviewFromStorage() {
  try {
    return JSON.parse(localStorage.getItem(MY_REVIEW_KEY));
  } catch {
    return null;
  }
}
function saveMyReviewToStorage(r) {
  localStorage.setItem(MY_REVIEW_KEY, JSON.stringify(r));
}
function clearMyReviewFromStorage() {
  localStorage.removeItem(MY_REVIEW_KEY);
}

function makeReviewCard({ autor, foto, estrelas, texto, editado }) {
  const wrap = document.createElement("div");
  wrap.className = "avaliacao-card";
  wrap.innerHTML = `
    <img src="${foto}" alt="Foto de ${autor}" class="avaliacao-foto">
    <div class="avaliacao-conteudo">
      <div class="avaliacao-header">
        <strong>${autor}${
    editado ? " <span class='edit-tag'>(editado)</span>" : ""
  }</strong>
      </div>
      <div class="avaliacao-estrelas">
        ${"★".repeat(estrelas)}${"☆".repeat(5 - estrelas)}
      </div>
      <p>"${texto}"</p>
    </div>
  `;
  return wrap;
}

function renderMyReview({ estrelas, texto, editado }) {
  let mine = document.getElementById("my-review");
  const html = `
    <img src="assets/img/logohome.png" alt="Sua foto" class="avaliacao-foto">
    <div class="avaliacao-conteudo">
      <div class="avaliacao-header">
        <strong>Você ${
          editado ? "<span class='edit-tag'>(editado)</span>" : ""
        }</strong>
        <div class="review-actions">
          <button id="btn-edit-review" class="btn ghost sm">Editar</button>
          <button id="btn-remove-review" class="btn ghost danger sm">Remover</button>
        </div>
      </div>
      <div class="avaliacao-estrelas">${"★".repeat(estrelas)}${"☆".repeat(
    5 - estrelas
  )}</div>
      <p>"${texto}"</p>
    </div>
  `;
  if (!mine) {
    mine = document.createElement("div");
    mine.id = "my-review";
    mine.className = "avaliacao-card";
    listaAvaliacoes.prepend(mine);
  }
  mine.innerHTML = html;

  document
    .getElementById("btn-edit-review")
    .addEventListener("click", onEditReview);
  document
    .getElementById("btn-remove-review")
    .addEventListener("click", onRemoveReview);
}

//API (pronto p/ backend)
async function postReview(bookId, rating, comment) {
  return fetch(`${API_BASE}/books/${encodeURIComponent(bookId)}/reviews`, {
    method: "POST",
    headers: HEADERS,
    body: JSON.stringify({ rating, comment }),
  });
}
async function patchReview(bookId, rating, comment) {
  return fetch(`${API_BASE}/books/${encodeURIComponent(bookId)}/reviews/me`, {
    method: "PATCH",
    headers: HEADERS,
    body: JSON.stringify({ rating, comment, edited: true }),
  });
}
async function deleteReview(bookId) {
  return fetch(`${API_BASE}/books/${encodeURIComponent(bookId)}/reviews/me`, {
    method: "DELETE",
    headers: HEADERS,
  });
}
async function syncCurtidosRating(bookId, rating) {
  try {
    await fetch(
      `${API_BASE}/liked-books/${encodeURIComponent(bookId)}/rating`,
      {
        method: "PATCH",
        headers: HEADERS,
        body: JSON.stringify({ rating }),
      }
    );
  } catch {
    const fb = getFallback();
    fb[bookId] = rating;
    setFallback(fb);
  }
}

async function handleSend() {
  const comment = (reviewText.value || "").trim();
  if (!selectedRating) {
    Swal.fire({
      icon: "warning",
      title: "Escolha as estrelas",
      text: "Selecione de 1 a 5 estrelas.",
      confirmButtonColor: window.COLOR_PRIMARY,
    });
    return;
  }
  if (!comment) {
    Swal.fire({
      icon: "warning",
      title: "Escreva um comentário",
      text: "Conte um pouco sobre o que achou do livro.",
      confirmButtonColor: window.COLOR_PRIMARY,
    });
    return;
  }

  const existing = getMyReviewFromStorage();
  try {
    if (existing) {
      await patchReview(livro.id, selectedRating, comment);
    } else {
      await postReview(livro.id, selectedRating, comment);
    }
  } catch {}

  await syncCurtidosRating(livro.id, selectedRating);

  const my = { estrelas: selectedRating, texto: comment, editado: !!existing };
  saveMyReviewToStorage(my);
  renderMyReview(my);

  reviewText.value = "";
  setRating(0);

  Swal.fire({
    icon: "success",
    title: "Avaliação enviada!",
    confirmButtonColor: window.COLOR_PRIMARY,
  });
}

// Editar simples
function onEditReview() {
  const mine = getMyReviewFromStorage();
  if (!mine) return;

  reviewText.value = mine.texto;
  setRating(mine.estrelas);
  reviewText.focus();
}

async function onRemoveReview() {
  const result = await swalConfirmDelete({
    title: "Excluir sua avaliação?",
    text: "Essa ação não poderá ser desfeita.",
    confirmButtonText: "Sim, excluir!",
  });
  if (!result.isConfirmed) return;

  try {
    await deleteReview(livro.id);
  } catch {}
  try {
    await syncCurtidosRating(livro.id, 0);
  } catch {}

  clearMyReviewFromStorage();
  document.getElementById("my-review")?.remove();
  setRating(0);
  reviewText.value = "";

  Swal.fire({
    icon: "success",
    title: "Avaliação removida.",
    confirmButtonColor: window.COLOR_PRIMARY,
  });
}

document.addEventListener("DOMContentLoaded", () => {
  carregarDadosDoLivro();
  renderStarsBar();
  sendBtn.addEventListener("click", handleSend);
});

let menuIcon = document.querySelector("#menu-icon");
let navbar = document.querySelector(".navbar");
menuIcon &&
  (menuIcon.onclick = () => {
    menuIcon.classList.toggle("bx-x");
    navbar.classList.toggle("active");
  });
