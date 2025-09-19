const API_BASE = window.ELIVROS_API_BASE || "/api";
const HEADERS = { "Content-Type": "application/json" };
const STORAGE_KEY = "liked_ratings_fallback";

const grid = document.getElementById("liked-grid");

const getFallback = () => {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {};
  } catch {
    return {};
  }
};
const setFallback = (m) => localStorage.setItem(STORAGE_KEY, JSON.stringify(m));

// ===== estrelas fixas =====
function makeStar(filled) {
  const i = document.createElement("i");
  i.className = "fa-solid fa-star star";
  if (filled) i.classList.add("filled");
  return i;
}

async function fetchLikedBooks() {
  const res = await fetch(`${API_BASE}/liked-books`, { headers: HEADERS });
  if (!res.ok) throw new Error("Falha ao carregar livros curtidos");
  return res.json();
}

async function deleteLikedBook(id) {
  const res = await fetch(`${API_BASE}/liked-books/${encodeURIComponent(id)}`, {
    method: "DELETE",
    headers: HEADERS,
  });
  if (!res.ok) throw new Error("Falha ao remover curtida");
  return res.json();
}

function renderBooks(books) {
  grid.innerHTML = "";
  const fallback = getFallback();

  books.forEach((b) => {
    const rating = b.user_rating ?? fallback[b.id] ?? 0;

    const card = document.createElement("article");
    card.className = "book-card";
    card.dataset.book = b.id;

    const cover = b.cover_url || "assets/img/capas/placeholder.jpg";

    card.innerHTML = `
      <div class="book-cover">
        <img src="${cover}" alt="Capa do livro ${b.title}">
        <button class="heart-btn active" title="Curtido">
          <i class="bx bxs-heart"></i>
        </button>
      </div>
      <div class="stars" aria-label="Avaliação do livro"></div>
      <h3 class="book-title">${b.title}</h3>
      <p class="book-author">${b.author}</p>
    `;

    // estrelas apenas leitura
    const starsWrap = card.querySelector(".stars");
    for (let s = 1; s <= 5; s++) {
      starsWrap.appendChild(makeStar(s <= rating));
    }

    // coração animado + remover
    const heartBtn = card.querySelector(".heart-btn");
    heartBtn.addEventListener("click", async () => {
      heartBtn.classList.toggle("active");
      heartBtn.classList.add("animating");
      setTimeout(() => heartBtn.classList.remove("animating"), 300);

      if (!heartBtn.classList.contains("active")) {
        card.classList.add("removing");
        setTimeout(() => card.remove(), 300);

        try {
          await deleteLikedBook(b.id);
        } catch (e) {
          console.warn("Erro ao remover do backend:", e);
        }
      }
    });

    grid.appendChild(card);
  });
}

(async function init() {
  try {
    const books = await fetchLikedBooks();
    renderBooks(books);
  } catch (e) {
    console.error(e);
    const fb = getFallback();
    const ids = Object.keys(fb);
    const offlineBooks = ids.map((id) => ({
      id,
      title: id,
      author: "—",
      cover_url: "assets/img/capas/placeholder.jpg",
      user_rating: fb[id],
    }));
    renderBooks(offlineBooks);
  }
})();
