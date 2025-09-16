// assets/js/gerenciar-livros.js
      document.addEventListener("DOMContentLoaded", function () {
        
        // --- FUNCIONALIDADE DE DELETAR ---
        const bookList = document.getElementById("book-list");
        if (bookList) {
          bookList.addEventListener("click", function (event) {
            const deleteButton = event.target.closest(".delete-btn");
            if (deleteButton) {
              const rowToDelete = deleteButton.closest("tr");
              Swal.fire({
                title: "Você tem certeza?",
                text: "Esta ação não poderá ser revertida!",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#2f3559", // Cor do botão "Sim, deletar!"
                confirmButtonText: "Sim, deletar!",
                cancelButtonText: "Cancelar",
                customClass: {
                    cancelButton: 'swal2-cancel-button-custom' // Adiciona classe para o botão de cancelar
                },
                buttonsStyling: false // Desabilita o estilo padrão do SweetAlert
              }).then((result) => {
                if (result.isConfirmed) {
                  rowToDelete.remove();
                  Swal.fire({
                    title: "Deletado!",
                    text: "O livro foi removido com sucesso.",
                    icon: "success",
                    confirmButtonColor: "#2f3559",
                    confirmButtonText: "OK", // Texto do botão de confirmação
                    customClass: {
                       confirmButton: 'swal2-confirm' // Garante que o botão de OK siga o estilo customizado se houver
                    }
                  });
                }
              });
            }
          });
        }
        // --- NOVA FUNCIONALIDADE DE PESQUISA NA TABELA ---
        const searchInput = document.getElementById('table-search-input');
        const tableRows = document.querySelectorAll('#book-list tr');

        if(searchInput) {
            searchInput.addEventListener('keyup', function(event) {
                const searchTerm = event.target.value.toLowerCase();

                tableRows.forEach(row => {
                    // Pega o texto da segunda e terceira coluna (Título e Autor)
                    const title = row.cells[1].textContent.toLowerCase();
                    const author = row.cells[2].textContent.toLowerCase();
                    
                    // Se o termo de pesquisa estiver no título ou no autor, mostra a linha. Senão, esconde.
                    if (title.includes(searchTerm) || author.includes(searchTerm)) {
                        row.style.display = ''; // '' redefine para o valor padrão (table-row)
                    } else {
                        row.style.display = 'none';
                    }
                });
            });
        }

      });