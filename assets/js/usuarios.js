// ./assets/js/usuarios.js
(() => {
  const MODAL_ID = "editar-usuario";
  const LS_KEY = "biblioteca.users";

  const els = {};
  const state = {
    users: [], // [{id, name, email, avatar}]
    currentId: null, // id do usuário sendo editado
  };

  document.addEventListener("DOMContentLoaded", init);

  function init() {
    cacheEls();
    hydrateFromDOM();
    hydrateFromStorage();
    wireModalOpen();
    wireModalClose();
    wireForm();
    wireDelete();
    wireSearch();
    wireOverlayClose();
    wireEscToClose();
  }

  // ---------- Cache de elementos ----------
  function cacheEls() {
    els.userList = document.querySelector(".user-list");
    els.modal = document.getElementById(MODAL_ID);
    els.form = document.getElementById("form-editar-usuario");
    els.inputId = document.getElementById("user-id");
    els.inputNome = document.getElementById("user-nome");
    els.inputEmail = document.getElementById("user-email");
    els.inputSenha = document.getElementById("user-senha");
    els.inputFoto = document.getElementById("user-foto");
    els.btnSave = document.getElementById("saveUserBtn");
    els.btnDelete = document.getElementById("deleteUserBtn");
    els.searchInput = document.querySelector(".search-input");
    els.searchBtn = document.querySelector(".search-btn");
  }

  // ---------- Leitura inicial dos cards do DOM ----------
  function hydrateFromDOM() {
    state.users = [...document.querySelectorAll(".user-card")].map((card) => {
      const id = card.getAttribute("data-user-id");
      const name = card.querySelector(".name")?.textContent.trim() || "";
      const email = card.querySelector(".email")?.textContent.trim() || "";
      const avatar =
        card.querySelector(".avatar img")?.getAttribute("src") ||
        "assets/img/user.png";
      return { id, name, email, avatar };
    });
  }

  // ---------- LocalStorage (opcional) ----------
  function hydrateFromStorage() {
    try {
      const raw = localStorage.getItem(LS_KEY);
      if (!raw) return;
      const saved = JSON.parse(raw);
      // Faz um merge suave: atualiza cards existentes e ignora ids desconhecidos
      saved.forEach((u) => {
        const card = getCardById(u.id);
        if (card) applyUserToCard(u, card);
      });
      // Atualiza state com o que está visível
      state.users = getUsersFromDOM();
    } catch {}
  }
  function persist() {
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(state.users));
    } catch {}
  }

  function getUsersFromDOM() {
    return [...document.querySelectorAll(".user-card")].map((card) => ({
      id: card.getAttribute("data-user-id"),
      name: card.querySelector(".name")?.textContent.trim() || "",
      email: card.querySelector(".email")?.textContent.trim() || "",
      avatar:
        card.querySelector(".avatar img")?.getAttribute("src") ||
        "assets/img/user.png",
    }));
  }

  // ---------- Abrir modal (preencher) ----------
  function wireModalOpen() {
    document.querySelectorAll(".edit-btn").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        e.preventDefault();
        const id = btn.getAttribute("data-user-id");
        const user = state.users.find((u) => u.id === id);
        if (!user) return;

        state.currentId = id;
        els.inputId.value = user.id;
        els.inputNome.value = user.name;
        els.inputEmail.value = user.email;
        els.inputSenha.value = "";
        if (els.inputFoto) els.inputFoto.value = "";

        openModal();
        // foco no primeiro campo para acessibilidade
        setTimeout(() => els.inputNome?.focus(), 50);
      });
    });
  }

  function openModal() {
    els.modal?.classList.add("active");
    trapFocus(els.modal);
  }
  function closeModal() {
    els.modal?.classList.remove("active");
    releaseFocusTrap();
  }

  // ---------- Fechar modal (botão [x]) ----------
  function wireModalClose() {
    document.querySelectorAll("[data-close]").forEach((btn) => {
      btn.addEventListener("click", () => {
        const targetId = btn.getAttribute("data-close");
        if (targetId === MODAL_ID) closeModal();
      });
    });
  }

  // ---------- Fechar clicando fora (overlay) ----------
  function wireOverlayClose() {
    els.modal?.addEventListener("click", (e) => {
      if (e.target === els.modal) closeModal();
    });
  }

  // ---------- Fechar com ESC ----------
  function wireEscToClose() {
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && els.modal?.classList.contains("active")) {
        closeModal();
      }
    });
  }

  // ---------- Formulário: salvar ----------
  function wireForm() {
    if (!els.form) return;

    // Preview de foto imediata (no card) quando o usuário escolhe arquivo
    els.inputFoto?.addEventListener("change", () => {
      const file = els.inputFoto.files?.[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (ev) => {
        const src = ev.target?.result;
        if (!src) return;
        const card = getCardById(state.currentId || els.inputId.value);
        if (card) {
          card.querySelector(".avatar img").setAttribute("src", src);
          // Atualiza no state
          updateUserInState(state.currentId, { avatar: src });
          persist();
        }
      };
      reader.readAsDataURL(file);
    });

    els.form.addEventListener("submit", (e) => {
      e.preventDefault();

      const id = els.inputId.value.trim();
      const name = els.inputNome.value.trim();
      const email = els.inputEmail.value.trim();
      // senha é opcional no layout

      if (!name || !email) {
        Swal.fire({
          icon: "warning",
          title: "Campos obrigatórios",
          text: "Digite um nome e um e-mail válidos.",
        });
        return;
      }

      const card = getCardById(id);
      if (!card) return;

      // aplica no card
      applyUserToCard({ id, name, email }, card);

      // atualiza state + persiste
      updateUserInState(id, { name, email });
      persist();

      Swal.fire({
        icon: "success",
        title: "Usuário atualizado!",
        text: "As informações foram salvas com sucesso.",
      }).then(() => {
        closeModal();
        els.form.reset();
      });
    });
  }

  // ---------- Botão Apagar usuário ----------
  function wireDelete() {
    if (!els.btnDelete) return;

    els.btnDelete.addEventListener("click", () => {
      const id = els.inputId.value.trim();
      const card = getCardById(id);
      if (!card) return;

      Swal.fire({
        title: "Apagar este usuário?",
        text: "Essa ação não poderá ser desfeita.",
        icon: "warning",
        showCancelButton: true,
        reverseButtons: true,
        confirmButtonColor: "#2f3559",
        cancelButtonColor: "#d94a4a",
        confirmButtonText: "Sim, apagar",
        cancelButtonText: "Cancelar",
      }).then((result) => {
        if (!result.isConfirmed) return;

        // remove do DOM
        card.remove();
        // remove do state
        state.users = state.users.filter((u) => u.id !== id);
        persist();

        closeModal();

        Swal.fire({
          icon: "success",
          title: "Usuário apagado!",
          text: "O usuário foi removido da lista.",
        });

        // mostra mensagem se lista ficar vazia
        ensureEmptyState();
      });
    });
  }

  function ensureEmptyState() {
    if (![...document.querySelectorAll(".user-card")].length) {
      const empty = document.createElement("div");
      empty.className = "empty-state";
      empty.style.cssText =
        "text-align:center;color:var(--muted);padding:2rem;border:1px dashed var(--line);border-radius:12px;background:#fff";
      empty.innerHTML = `
        <p>Nenhum usuário encontrado.</p>
      `;
      els.userList?.appendChild(empty);
    } else {
      const empty = els.userList?.querySelector(".empty-state");
      if (empty) empty.remove();
    }
  }

  // ---------- Busca ----------
  function wireSearch() {
    if (!els.searchInput) return;

    const doFilter = () => {
      const q = els.searchInput.value.trim().toLowerCase();
      const cards = [...document.querySelectorAll(".user-card")];
      let visibleCount = 0;

      cards.forEach((card) => {
        const name =
          card.querySelector(".name")?.textContent.toLowerCase() || "";
        const email =
          card.querySelector(".email")?.textContent.toLowerCase() || "";
        const match = name.includes(q) || email.includes(q);
        card.style.display = match ? "" : "none";
        if (match) visibleCount++;
      });

      // estado vazio condicional (apenas visual, não remove cards)
      if (visibleCount === 0) {
        if (!els.userList.querySelector(".empty-state")) {
          const empty = document.createElement("div");
          empty.className = "empty-state";
          empty.style.cssText =
            "grid-column:1/-1;text-align:center;color:var(--muted);padding:2rem;border:1px dashed var(--line);border-radius:12px;background:#fff";
          empty.innerHTML = `<p>Nenhum usuário corresponde à busca.</p>`;
          els.userList.appendChild(empty);
        }
      } else {
        const empty = els.userList.querySelector(".empty-state");
        if (empty) empty.remove();
      }
    };

    const debounced = debounce(doFilter, 150);
    els.searchInput.addEventListener("input", debounced);
    els.searchInput.addEventListener("keydown", (e) => {
      if (e.key === "Enter") {
        e.preventDefault();
        doFilter();
      }
    });
    els.searchBtn?.addEventListener("click", doFilter);
  }

  // ---------- Helpers ----------
  function getCardById(id) {
    return document.querySelector(
      `.user-card[data-user-id="${CSS.escape(id)}"]`
    );
  }

  function applyUserToCard(user, card) {
    if (user.name) card.querySelector(".name").textContent = user.name;
    if (user.email) card.querySelector(".email").textContent = user.email;
    if (user.avatar) {
      const img = card.querySelector(".avatar img");
      if (img) img.setAttribute("src", user.avatar);
    }
  }

  function updateUserInState(id, patch) {
    const idx = state.users.findIndex((u) => u.id === id);
    if (idx >= 0) {
      state.users[idx] = { ...state.users[idx], ...patch };
    } else {
      // se não existir (algum card novo dinamicamente), cria
      state.users.push({
        id,
        name: patch.name || "",
        email: patch.email || "",
        avatar: patch.avatar || "assets/img/user.png",
      });
    }
  }

  function debounce(fn, wait = 200) {
    let t;
    return (...args) => {
      clearTimeout(t);
      t = setTimeout(() => fn.apply(this, args), wait);
    };
  }

  // --- Focus trap simples no modal (acessibilidade) ---
  let prevFocus = null;
  let focusables = [];
  function trapFocus(container) {
    if (!container) return;
    prevFocus = document.activeElement;
    focusables = [
      ...container.querySelectorAll(
        'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
      ),
    ].filter((el) => !el.hasAttribute("disabled"));

    const first = focusables[0];
    const last = focusables[focusables.length - 1];

    container.addEventListener("keydown", onKeyDown);
    function onKeyDown(e) {
      if (e.key !== "Tab") return;
      if (focusables.length === 0) return;

      if (e.shiftKey) {
        if (document.activeElement === first) {
          e.preventDefault();
          last.focus();
        }
      } else {
        if (document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    }
    container._onKeyDown = onKeyDown;
  }
  function releaseFocusTrap() {
    if (els.modal?._onKeyDown) {
      els.modal.removeEventListener("keydown", els.modal._onKeyDown);
      delete els.modal._onKeyDown;
    }
    if (prevFocus && document.body.contains(prevFocus)) {
      prevFocus.focus();
    }
  }
})();
