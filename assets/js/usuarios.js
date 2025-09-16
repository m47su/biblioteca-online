(() => {
  const MODAL_EDIT_ID = "editar-usuario";
  const MODAL_CREATE_ID = "criar-usuario";
  const LS_KEY = "biblioteca.users";

  const els = {};
  const state = { users: [], currentId: null };

  document.addEventListener("DOMContentLoaded", init);

  function init() {
    cacheEls();
    hydrateFromDOM();
    hydrateFromStorage();

    wireOpenEditModal();
    wireCardDelete();
    wireDeleteFromModal();
    wireEditForm();

    wireOpenCreateModal();
    wireCreateForm();

    wireSearch();
    wireCloseModal(MODAL_EDIT_ID);
    wireCloseModal(MODAL_CREATE_ID);
    wireOverlayClose();
    wireEscToClose();

    ensureEmptyState();
  }

  function cacheEls() {
    els.userList = document.querySelector(".user-list");

    // Editar
    els.modalEdit = document.getElementById(MODAL_EDIT_ID);
    els.formEdit = document.getElementById("form-editar-usuario");
    els.inputId = document.getElementById("user-id");
    els.inputNome = document.getElementById("user-nome");
    els.inputEmail = document.getElementById("user-email");
    els.inputFoto = document.getElementById("user-foto");
    els.btnDelete = document.getElementById("deleteUserBtn");

    // Criar
    els.modalCreate = document.getElementById(MODAL_CREATE_ID);
    els.formCreate = document.getElementById("form-criar-usuario");
    els.createName = document.getElementById("novo-nome");
    els.createEmail = document.getElementById("novo-email");

    // Busca
    els.searchInputHeader = document.querySelector(".search-input");
    els.searchBtnHeader = document.querySelector(".search-btn");
    els.searchInputLocal = document.getElementById("user-search-input");
  }

  function hydrateFromDOM() {
    state.users = getUsersFromDOM();
  }

  function hydrateFromStorage() {
    try {
      const raw = localStorage.getItem(LS_KEY);
      if (!raw) return;
      const saved = JSON.parse(raw);
      saved.forEach((u) => {
        const card = getCardById(u.id);
        if (card) {
          applyUserToCard(u, card);
        } else {
          els.userList?.appendChild(buildUserCard(u));
        }
      });
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

  //editar

  function wireOpenEditModal() {
    els.userList?.addEventListener("click", (e) => {
      const btn = e.target.closest(".edit-btn");
      if (!btn) return;

      e.preventDefault();
      const id = btn.getAttribute("data-user-id");
      const user =
        state.users.find((u) => u.id === id) ||
        getUsersFromDOM().find((u) => u.id === id);
      if (!user) return;

      state.currentId = id;
      els.inputId.value = user.id;
      els.inputNome.value = user.name;
      els.inputEmail.value = user.email;
      if (els.inputFoto) els.inputFoto.value = "";

      openModal(els.modalEdit);
      setTimeout(() => els.inputNome?.focus(), 50);
    });
  }

  function wireEditForm() {
    if (!els.formEdit) return;

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
          updateUserInState(state.currentId, { avatar: src });
          persist();
        }
      };
      reader.readAsDataURL(file);
    });

    els.formEdit.addEventListener("submit", (e) => {
      e.preventDefault();
      const id = els.inputId.value.trim();
      const name = els.inputNome.value.trim();
      const email = els.inputEmail.value.trim();
      if (!name || !email) {
        Swal.fire({
          icon: "warning",
          title: "Campos obrigatórios",
          text: "Digite um nome e um e-mail válidos.",
          confirmButtonColor: "#2f3559",
        });
        return;
      }
      const card = getCardById(id);
      if (!card) return;

      applyUserToCard({ id, name, email }, card);
      updateUserInState(id, { name, email });
      persist();

      Swal.fire({
        icon: "success",
        title: "Usuário atualizado!",
        text: "As informações foram salvas com sucesso.",
        confirmButtonColor: "#2f3559",
      }).then(() => {
        closeModal(els.modalEdit);
        els.formEdit.reset();
      });
    });
  }

  function wireDeleteFromModal() {
    if (!els.btnDelete) return;
    els.btnDelete.addEventListener("click", () => {
      const id = els.inputId.value.trim();
      const card = getCardById(id);
      if (!card) return;
      confirmDelete(() => {
        card.remove();
        state.users = state.users.filter((u) => u.id !== id);
        persist();
        closeModal(els.modalEdit);
        ensureEmptyState();
      });
    });
  }

  //criação

  function wireOpenCreateModal() {
    document
      .querySelectorAll('[data-open="criar-usuario"], .add-user-btn')
      .forEach((btn) => {
        btn.addEventListener("click", (e) => {
          e.preventDefault();
          openModal(els.modalCreate);
          setTimeout(() => els.createName?.focus(), 50);
        });
      });
  }

  function wireCreateForm() {
    if (!els.formCreate) return;

    els.formCreate.addEventListener("submit", (e) => {
      e.preventDefault();

      const name = els.createName.value.trim();
      const email = els.createEmail.value.trim();

      if (!name || !email) {
        Swal.fire({
          icon: "warning",
          title: "Campos obrigatórios",
          text: "Digite nome e e-mail válidos.",
          confirmButtonColor: "#2f3559",
        });
        return;
      }

      const id = String(Date.now());
      const newUser = { id, name, email, avatar: "assets/img/user.png" };
      const card = buildUserCard(newUser);
      els.userList?.appendChild(card);

      state.users.push(newUser);
      persist();
      ensureEmptyState();

      Swal.fire({
        icon: "success",
        title: "Usuário criado!",
        confirmButtonColor: "#2f3559",
      }).then(() => {
        closeModal(els.modalCreate);
        els.formCreate.reset();
      });
    });
  }

  function buildUserCard({ id, name, email, avatar }) {
    const article = document.createElement("article");
    article.className = "user-card";
    article.dataset.userId = id;

    article.innerHTML = `
      <div class="avatar">
        <img src="${
          avatar || "assets/img/user.png"
        }" alt="Avatar de ${escapeHTML(name)}" />
      </div>
      <div class="meta">
        <span class="name">${escapeHTML(name)}</span>
        <span class="email">${escapeHTML(email)}</span>
      </div>
      <div class="actions-card">
        <a href="#" class="edit-btn" data-open="editar-usuario" data-user-id="${id}" aria-label="Editar usuário">
          <i class="bx bx-edit"></i>
        </a>
        <button type="button" class="delete-btn-card" data-user-id="${id}" aria-label="Apagar usuário">
          <i class="bx bx-trash"></i>
        </button>
      </div>
    `;
    return article;
  }
  //delete

  function wireCardDelete() {
    els.userList?.addEventListener("click", (e) => {
      const btn = e.target.closest(".delete-btn-card");
      if (!btn) return;

      const id = btn.getAttribute("data-user-id");
      const card = getCardById(id);
      if (!card) return;

      confirmDelete(() => {
        card.remove();
        state.users = state.users.filter((u) => u.id !== id);
        persist();
        ensureEmptyState();
      });
    });
  }

  function confirmDelete(onConfirm) {
    Swal.fire({
      title: "Apagar este usuário?",
      text: "Essa ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      reverseButtons: true,
      buttonsStyling: false,
      customClass: {
        confirmButton: "swal-btn swal-btn--danger",
        cancelButton: "swal-btn swal-btn--primary",
        actions: "swal-actions-gap",
      },
      confirmButtonText: "Sim, deletar!",
      cancelButtonText: "Cancelar",
    }).then((result) => {
      if (result.isConfirmed) {
        onConfirm?.();
        Swal.fire({
          icon: "success",
          title: "Usuário apagado!",
          text: "O usuário foi removido da lista.",
          buttonsStyling: false,
          customClass: { confirmButton: "swal-btn swal-btn--primary" },
        });
      }
    });
  }
  function wireSearch() {
    const inputs = [els.searchInputHeader, els.searchInputLocal].filter(
      Boolean
    );

    const doFilter = () => {
      const q = (
        els.searchInputLocal?.value ||
        els.searchInputHeader?.value ||
        ""
      )
        .trim()
        .toLowerCase();

      const cards = [...document.querySelectorAll(".user-card")];
      let visible = 0;

      cards.forEach((card) => {
        const name =
          card.querySelector(".name")?.textContent.toLowerCase() || "";
        const email =
          card.querySelector(".email")?.textContent.toLowerCase() || "";
        const match = name.includes(q) || email.includes(q);
        card.style.display = match ? "" : "none";
        if (match) visible++;
      });

      if (visible === 0) {
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
    inputs.forEach((inp) => inp.addEventListener("input", debounced));
    els.searchBtnHeader?.addEventListener("click", doFilter);
  }

  function openModal(modalEl) {
    modalEl?.classList.add("active");
    trapFocus(modalEl);
  }

  function closeModal(modalEl) {
    modalEl?.classList.remove("active");
    releaseFocusTrap();
  }

  function wireCloseModal(targetId) {
    document.querySelectorAll("[data-close]").forEach((btn) => {
      btn.addEventListener("click", () => {
        const id = btn.getAttribute("data-close");
        if (id === targetId) {
          const modal = document.getElementById(targetId);
          closeModal(modal);
        }
      });
    });
  }

  function wireOverlayClose() {
    [els.modalEdit, els.modalCreate].forEach((m) => {
      m?.addEventListener("click", (e) => {
        if (e.target === m) closeModal(m);
      });
    });
  }

  function wireEscToClose() {
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") {
        if (els.modalEdit?.classList.contains("active"))
          closeModal(els.modalEdit);
        if (els.modalCreate?.classList.contains("active"))
          closeModal(els.modalCreate);
      }
    });
  }

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
      state.users.push({
        id,
        name: patch.name || "",
        email: patch.email || "",
        avatar: patch.avatar || "assets/img/user.png",
      });
    }
  }

  function ensureEmptyState() {
    if (![...document.querySelectorAll(".user-card")].length) {
      const empty = document.createElement("div");
      empty.className = "empty-state";
      empty.style.cssText =
        "text-align:center;color:var(--muted);padding:2rem;border:1px dashed var(--line);border-radius:12px;background:#fff";
      empty.innerHTML = `<p>Nenhum usuário encontrado.</p>`;
      els.userList?.appendChild(empty);
    } else {
      const empty = els.userList?.querySelector(".empty-state");
      if (empty) empty.remove();
    }
  }

  function debounce(fn, wait = 200) {
    let t;
    return (...args) => {
      clearTimeout(t);
      t = setTimeout(() => fn.apply(this, args), wait);
    };
  }

  // acessibilidade
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
    [els.modalEdit, els.modalCreate].forEach((m) => {
      if (m?._onKeyDown) {
        m.removeEventListener("keydown", m._onKeyDown);
        delete m._onKeyDown;
      }
    });
    if (prevFocus && document.body.contains(prevFocus)) prevFocus.focus();
  }

  function escapeHTML(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
})();
