let menuIcon = document.querySelector("#menu-icon");
let navbar = document.querySelector(".navbar");
let sections = document.querySelectorAll("section");
let navLinks = document.querySelectorAll("header nav a");

window.onscroll = () => {
  sections.forEach((sec) => {
    let top = window.scrollY;
    let offset = sec.offsetTop - 150;
    let height = sec.offsetHeight;
    let id = sec.getAttribute("id");

    if (top >= offset && top < offset + height) {
      navLinks.forEach((links) => {
        links.classList.remove("active");
        document
          .querySelector("header nav a[href*=" + id + "]")
          .classList.add("active");
      });
    }
  });
};

menuIcon.onclick = () => {
  menuIcon.classList.toggle("bx-x");
  navbar.classList.toggle("active");
};

document.addEventListener("DOMContentLoaded", function () {
  console.log("DOM carregado. Iniciando script dinâmico do carrossel.");

  // DADOS DOS LIVROS (ADICIONAR/REMOVER LIVROS)

  //carrossel dos livros em alta
  const livrosEmAlta = [
    {
      imagem: "assets/img/capa (6).png",
      titulo: "Está chovendo estrelas",
      autor: "Ana Santos",
      alt: "Capa do livro Está chovendo estrelas", //esses livros futuramente serão puxados de um banco de dados
    },
    {
      imagem: "assets/img/capa (5).png",
      titulo: "Viagem pelas estrelas",
      autor: "Matheus Zara",
      alt: "Capa do livro Viagem pelas estrelas",
    },
    {
      imagem: "assets/img/capa (3).png",
      titulo: "A culpa é das estrelas",
      autor: "John Green",
      alt: "Capa do livro A culpa é das estrelas",
    },
    {
      imagem: "assets/img/capa (1).png",
      titulo: "Nascer da estrela",
      autor: "Carla Abrantes",
      alt: "Capa do livro Nascer da estrela",
    },
    {
      imagem: "assets/img/capa (2).png",
      titulo: "Caçadora de estrelas",
      autor: "Raiza Varella",
      alt: "Capa do livro Caçadora de estrelas",
    },
    {
      imagem: "assets/img/capa (4).png",
      titulo: "O Planeta Sombrio",
      autor: "Paulo Borges",
      alt: "Capa do livro O planeta sombrio",
    },
        {
      imagem: "assets/img/capa (1).png",
      titulo: "Nascer da estrela",
      autor: "Carla Abrantes",
      alt: "Capa do livro Nascer da estrela",
    },
    {
      imagem: "assets/img/capa (2).png",
      titulo: "Caçadora de estrelas",
      autor: "Raiza Varella",
      alt: "Capa do livro Caçadora de estrelas",
    },
    {
      imagem: "assets/img/capa (4).png",
      titulo: "O Planeta Sombrio",
      autor: "Paulo Borges",
      alt: "Capa do livro O planeta sombrio",
    },
  ];
  //carrossel dos livros recentes

  const livrosRecentes = [
    {
      imagem: "assets/img/recentes (1).png",
      titulo: "O livro da capa verde",
      autor: "Bruno Eduardo",
      alt: "Capa do livro O livro da capa verde",
    },
    {
      imagem: "assets/img/recentes (2).png",
      titulo: "É assim que acaba",
      autor: "Collen Hoover",
      alt: "Capa do livro É assim que acaba",
    },

    {
      imagem: "assets/img/recentes (3).png",
      titulo: "A culpa é das estrelas",
      autor: "John Green",
      alt: "Capa do livro A culpa é das estrelas",
    },
    {
      imagem: "assets/img/recentes (4).png",
      titulo: "Mestres do tempo",
      autor: "R.V Campbell",
      alt: "Capa do livro Mestres do tempo",
    },
    {
      imagem: "assets/img/recentes (5).png",
      titulo: "Vovó virou semente",
      autor: "Rodrigo Ciríaco",
      alt: "Capa do livro Vovó virou semente",
    },
    {
      imagem: "assets/img/recentes (6).png",
      titulo: "Torto arado",
      autor: "Itamar Vieira",
      alt: "Capa do livro Torto arado",
    },
    {
      imagem: "assets/img/recentes (4).png",
      titulo: "Mestres do tempo",
      autor: "R.V Campbell",
      alt: "Capa do livro Mestres do tempo",
    },
    {
      imagem: "assets/img/recentes (5).png",
      titulo: "Vovó virou semente",
      autor: "Rodrigo Ciríaco",
      alt: "Capa do livro Vovó virou semente",
    },
    {
      imagem: "assets/img/recentes (6).png",
      titulo: "Torto arado",
      autor: "Itamar Vieira",
      alt: "Capa do livro Torto arado",
    },
  ];

  //carrossel dos livros de terror
  const livrosTerror = [
    {
      imagem: "assets/img/terror (1).png",
      titulo: "O vilarejo",
      autor: "Raphael Montes",
      alt: "Capa do livro O vilarejo",
    },
    {
      imagem: "assets/img/terror (2).png",
      titulo: "IT a coisa",
      autor: "Stephen King",
      alt: "IT a coisa",
    },
    {
      imagem: "assets/img/terror (3).png",
      titulo: "Terra de sonhos e acasos",
      autor: "Filipe Ribeiro",
      alt: "Capa do livro Terra de sonhos e acasos",
    },
    {
      imagem: "assets/img/terror (4).png",
      titulo: "Knock Knock",
      autor: "Beverly Mariel",
      alt: "Capa do livro MKnock Knock",
    },
    {
      imagem: "assets/img/terror (5).png",
      titulo: "Os mortos-vivos",
      autor: "Peter Straub",
      alt: "Capa do livro Os mortos-vivos",
    },
    {
      imagem: "assets/img/terror (6).png",
      titulo: "Saco de ossos",
      autor: "Stephen King",
      alt: "Capa do livro Saco de ossos",
    },
    {
      imagem: "assets/img/terror (4).png",
      titulo: "Knock Knock",
      autor: "Beverly Mariel",
      alt: "Capa do livro MKnock Knock",
    },
    {
      imagem: "assets/img/terror (5).png",
      titulo: "Os mortos-vivos",
      autor: "Peter Straub",
      alt: "Capa do livro Os mortos-vivos",
    },
    {
      imagem: "assets/img/terror (6).png",
      titulo: "Saco de ossos",
      autor: "Stephen King",
      alt: "Capa do livro Saco de ossos",
    },
  ];

//carrossel dos livros de romance
    const livrosRomance = [
    {
      imagem: "assets/img/romance (1).png",
      titulo: "Encontrei você",
      autor: "Ana Morais",
      alt: "Capa do livro Encontrei você",
    },
    {
      imagem: "assets/img/recentes (2).png",
      titulo: "É assim que acaba",
      autor: "Collen Hoover",
      alt: "Capa do livro É assim que acaba",
    },
    {
      imagem: "assets/img/romance (3).png",
      titulo: "Até breve",
      autor: "Helena Figueiredo",
      alt: "Capa do livro Até breve",
    },
    {
      imagem: "assets/img/recentes (4).png",
      titulo: "Vovó virou semente",
      autor: "Rodrigo Ciríaco",
      alt: "Capa do livro Vovó virou semente",
    },
    {
      imagem: "assets/img/recentes (5).png",
      titulo: "Torto arado",
      autor: "Itamar Vieira",
      alt: "Capa do livro Torto arado",
    },
    {
      imagem: "assets/img/terror (4).png",
      titulo: "O destino das terras altas",
      autor: "Hannah Howell",
      alt: "Capa do livro O destino das terras altas",
    },
    {
      imagem: "assets/img/recentes (4).png",
      titulo: "Vovó virou semente",
      autor: "Rodrigo Ciríaco",
      alt: "Capa do livro Vovó virou semente",
    },
    {
      imagem: "assets/img/recentes (5).png",
      titulo: "Torto arado",
      autor: "Itamar Vieira",
      alt: "Capa do livro Torto arado",
    },
    {
      imagem: "assets/img/terror (4).png",
      titulo: "O destino das terras altas",
      autor: "Hannah Howell",
      alt: "Capa do livro O destino das terras altas",
    },
  ];
  //FUNÇÃO QUE CRIA O HTML DE CADA CARD
  function gerarCardHTML(livro) {
    return `
            <div class="item">
                <div class="card">
                    <div class="card-image-container">
                        <img src="${livro.imagem}" alt="${livro.alt}" />
                        <button class="like-btn" aria-label="Curtir">
                            <svg width="24" height="24" viewBox="0 0 24 24">
                                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                            </svg>
                        </button>
                    </div>
                    <div class="card-content">
                        <p class="card-title">${livro.titulo}</p>
                        <p class="card-author">${livro.autor}</p>
                        <button class="details-btn">Detalhes</button>
                    </div>
                </div>
            </div>
        `;
  }

  //FUNÇÃO QUE PREENCHE O CARROSSEL E INICIALIZA TUDO
  function popularCarrossel(seletorCarrossel, listaDeLivros) {
    const carrosselList = document.querySelector(`${seletorCarrossel} .list`);
    if (carrosselList) {
      // Gera o HTML para todos os livros e junta tudo em uma única string
      carrosselList.innerHTML = listaDeLivros.map(gerarCardHTML).join("");
    } else {
      console.error(
        `Elemento .list não encontrado para o seletor ${seletorCarrossel}`
      );
    }
  }

  // Preenche cada carrossel com seus respectivos livros
  popularCarrossel("#emalta", livrosEmAlta);
  popularCarrossel("#recentes", livrosRecentes);
  popularCarrossel("#terror", livrosTerror);
    popularCarrossel("#romance", livrosRomance);

  // AGORA, inicializamos a funcionalidade de todos os carrosséis
  // (O código a seguir é o mesmo da solução anterior, que já está funcionando)

  function initializeCarousel(slider) {
    const list = slider.querySelector(".list");
    const items = slider.querySelectorAll(".list .item");
    const nextBtn = slider.querySelector(".next-btn");
    const prevBtn = slider.querySelector(".prev-btn");

    if (!list || items.length === 0 || !nextBtn || !prevBtn) {
      console.error(
        "ERRO: Elementos essenciais do carrossel não foram encontrados dentro de:",
        slider
      );
      return;
    }

    let active = 0;
    let autoPlayInterval;

    function reloadSlider() {
      if (items.length === 0) return;
      const cardWidth = items[0].offsetWidth;
      const gap =
        parseInt(window.getComputedStyle(list).getPropertyValue("gap")) || 0;
      const step = cardWidth + gap;
      const itemsPerScreen = Math.floor(slider.offsetWidth / step);
      const scrollLimit = Math.max(0, items.length - itemsPerScreen);

      if (active > scrollLimit) active = scrollLimit;
      if (active < 0) active = 0;

      let scrollDistance = active * step;
      list.style.transform = `translateX(-${scrollDistance}px)`;

      clearInterval(autoPlayInterval);
      autoPlayInterval = setInterval(() => nextBtn.click(), 5000);
    }

    nextBtn.onclick = function () {
      const cardWidth = items[0].offsetWidth;
      const gap =
        parseInt(window.getComputedStyle(list).getPropertyValue("gap")) || 0;
      const step = cardWidth + gap;
      const itemsPerScreen = Math.floor(slider.offsetWidth / step);
      const scrollLimit = Math.max(0, items.length - itemsPerScreen);

      if (active < scrollLimit) {
        active++;
      } else {
        active = 0;
      }
      reloadSlider();
    };

    prevBtn.onclick = function () {
      const cardWidth = items[0].offsetWidth;
      const gap =
        parseInt(window.getComputedStyle(list).getPropertyValue("gap")) || 0;
      const step = cardWidth + gap;
      const itemsPerScreen = Math.floor(slider.offsetWidth / step);
      const scrollLimit = Math.max(0, items.length - itemsPerScreen);

      if (active > 0) {
        active--;
      } else {
        active = scrollLimit;
      }
      reloadSlider();
    };

    reloadSlider();
    window.addEventListener("resize", reloadSlider);
  }

  const allSliders = document.querySelectorAll(".slider");
  allSliders.forEach((slider) => {
    initializeCarousel(slider);
  });

  const likeButtons = document.querySelectorAll(".like-btn");
  likeButtons.forEach((button) => {
    button.addEventListener("click", function () {
      this.classList.toggle("active");
      this.classList.add("animating");
      setTimeout(() => {
        this.classList.remove("animating");
      }, 300);
    });
  });
});


