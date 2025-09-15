document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".toggle").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-target");
      const input = document.getElementById(id);
      const isPass = input.type === "password";
      input.type = isPass ? "text" : "password";
      btn.innerHTML = isPass
        ? '<i class="bx bx-show"></i>'
        : '<i class="bx bx-hide"></i>';
    });
  });

  const senhaAtual = document.getElementById("senha-atual");
  const novaSenha = document.getElementById("nova-senha");
  const confirma = document.getElementById("confirma-senha");
  const atualizarBtn = document.querySelector(".section:not(.danger) .btn");

  if (atualizarBtn) {
    atualizarBtn.addEventListener("click", (e) => {
      e.preventDefault();

      if (!senhaAtual.value || !novaSenha.value || !confirma.value) {
        Swal.fire({
          icon: "warning",
          title: "Preencha todos os campos",
          text: "Para alterar a senha, complete os três campos.",
        });
        return;
      }

      if (novaSenha.value !== confirma.value) {
        Swal.fire({
          icon: "error",
          title: "As senhas não coincidem",
          text: "Verifique a confirmação da nova senha.",
        });
        return;
      }

      Swal.fire({
        icon: "success",
        title: "Senha atualizada!",
        text: "Sua senha foi alterada com sucesso.",
      }).then(() => {
        senhaAtual.value = "";
        novaSenha.value = "";
        confirma.value = "";
      });
    });
  }

  // Apagar conta
  const chk = document.getElementById("confirmar-exclusao");
  const apagarBtn = document.getElementById("apagar-btn");

  if (chk && apagarBtn) {
    apagarBtn.disabled = false;
    apagarBtn.addEventListener("click", () => {
      if (!chk.checked) {
        Swal.fire({
          icon: "warning",
          title: "Confirmação necessária",
          text: "Marque a opção 'Desejo apagar minha conta e todos meus dados' para continuar.",
        });
        return;
      }

      Swal.fire({
        title: "Tem certeza?",
        text: "Essa ação não poderá ser revertida!",
        icon: "warning",
        showCancelButton: true,
        reverseButtons: true,
        confirmButtonColor: "#2f3559",
        cancelButtonColor: "#d94a4a",
        confirmButtonText: "Sim, apagar conta!",
        cancelButtonText: "Cancelar",
      }).then((result) => {
        if (result.isConfirmed) {
          Swal.fire({
            title: "Apagada!",
            text: "Sua conta foi excluída com sucesso.",
            icon: "success",
            confirmButtonColor: "#2f3559",
            confirmButtonText: "OK",
          }).then(() => {
            window.location.href = "home.html";
          });
        }
      });
    });
  }

  document.querySelectorAll("[data-open]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const targetId = btn.getAttribute("data-open");
      const el = document.getElementById(targetId);
      if (el) el.classList.add("active");
    });
  });

  document.querySelectorAll("[data-close]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const targetId = btn.getAttribute("data-close");
      const el = document.getElementById(targetId);
      if (el) el.classList.remove("active");
    });
  });

  // Salvar edição de nome e e-mail
  const formEditar = document.getElementById("form-editar-detalhes");
  if (formEditar) {
    formEditar.addEventListener("submit", (e) => {
      e.preventDefault();

      const nome = document.getElementById("det-nome").value.trim();
      const email = document.getElementById("det-email").value.trim();

      if (!nome || !email) {
        Swal.fire({
          icon: "warning",
          title: "Preencha todos os campos",
          text: "Digite nome e e-mail válidos.",
        });
        return;
      }

      Swal.fire({
        icon: "success",
        title: "Informações atualizadas!",
        text: "Seus dados foram salvos com sucesso.",
      }).then(() => {
        const nomeField = document.querySelector(
          ".details .field:nth-child(1) div"
        );
        const emailField = document.querySelector(
          ".details .field:nth-child(2) div"
        );
        if (nomeField) nomeField.textContent = nome;
        if (emailField) emailField.textContent = email;

        const popup = document.getElementById("editar-detalhes");
        if (popup) popup.classList.remove("active");

        formEditar.reset();
      });
    });
  }

  const fileInput = document.getElementById("upload-foto");
  const profileAvatar = document.getElementById("profile-avatar");
  const headerAvatar = document.getElementById("header-avatar");

  function setAvatar(src) {
    if (profileAvatar) {
      profileAvatar.innerHTML = `<img src="${src}" alt="Foto do perfil" class="avatar-img">`;
    }
    if (headerAvatar) {
      headerAvatar.innerHTML = `<img src="${src}" alt="Avatar" class="img-logo">`;
    }
  }

  if (fileInput) {
    fileInput.addEventListener("change", () => {
      const file = fileInput.files && fileInput.files[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (e) => {
        const src = e.target.result;
        setAvatar(src);

        Swal.fire({
          icon: "success",
          title: "Foto atualizada!",
          text: "Sua imagem de perfil foi alterada com sucesso.",
        });
      };
      reader.readAsDataURL(file);
    });
  }
});
