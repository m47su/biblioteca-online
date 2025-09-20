document.addEventListener("DOMContentLoaded", () => {
  const tituloEtapa = document.getElementById("titulo-etapa");
  
  const etapaEmail = document.getElementById("etapa-email");
  const etapaCodigo = document.getElementById("etapa-codigo");
  const etapaNovaSenha = document.getElementById("etapa-nova-senha");

  etapaEmail.addEventListener("submit", (event) => {
    event.preventDefault();
    console.log("Formulário de e-mail enviado. Simulando envio...");

    Swal.fire({
      title: "E-mail enviado!",
      text: "Enviamos um código de verificação para o seu e-mail.",
      icon: "success",
     customClass: {
        popup: 'meu-popup-estilizado',
        confirmButton: 'meu-botao-confirmar' 
      },
      buttonsStyling: false,
      showClass: {
        popup: 'animate__animated animate__fadeInDown'
      },
      hideClass: {
        popup: 'animate__animated animate__fadeOutUp'
      }
    });

    tituloEtapa.innerText = "Digite o Código";
    etapaEmail.style.display = "none";
    etapaCodigo.style.display = "block";
  });

  etapaCodigo.addEventListener("submit", (event) => {
    event.preventDefault();
    const codigoInput = document.getElementById("codigo").value;
    console.log(`Verificando código: ${codigoInput}`);
    
    if (codigoInput === "123456") {
      tituloEtapa.innerText = "Crie uma Nova Senha";
      etapaCodigo.style.display = "none";
      etapaNovaSenha.style.display = "block";
    } else {
      Swal.fire({
        title: "Código Incorreto",
        text: "O código digitado não é válido. Tente novamente.",
        icon: "error",
        customClass: {
          popup: 'meu-popup-estilizado',
          confirmButton: 'meu-botao-confirmar'
        }, buttonsStyling: false,
      });
    }
  });

  etapaNovaSenha.addEventListener("submit", (event) => {
    event.preventDefault();
    const novaSenha = document.getElementById("nova-senha").value;
    const confirmarSenha = document.getElementById("confirmar-senha").value;

    if (novaSenha !== confirmarSenha) {
      Swal.fire({
        title: "As senhas não coincidem!",
        text: "Por favor, digite a mesma senha nos dois campos.",
        icon: "warning",
        customClass: {
          popup: 'meu-popup-estilizado',
          confirmButton: 'meu-botao-confirmar'
        }, buttonsStyling: false,
      });
      return; 
    }
    
    console.log("Senhas coincidem. Salvando...");

    Swal.fire({
      title: "Senha alterada com sucesso!",
      icon: "success",
      customClass: {
        popup: 'meu-popup-estilizado',
        confirmButton: 'meu-botao-confirmar'
      }, buttonsStyling: false,
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = "index.html"; 
      }
    });
  });
});