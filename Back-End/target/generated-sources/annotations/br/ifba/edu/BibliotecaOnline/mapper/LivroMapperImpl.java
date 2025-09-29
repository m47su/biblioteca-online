package br.ifba.edu.BibliotecaOnline.mapper;

import br.ifba.edu.BibliotecaOnline.DTO.LivroDTO;
import br.ifba.edu.BibliotecaOnline.entities.Autor;
import br.ifba.edu.BibliotecaOnline.entities.LivroEntity;
import br.ifba.edu.BibliotecaOnline.entities.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-29T14:26:56-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class LivroMapperImpl implements LivroMapper {

    @Override
    public LivroEntity toEntity(LivroDTO dto) {
        if ( dto == null ) {
            return null;
        }

        LivroEntity livroEntity = new LivroEntity();

        livroEntity.setId( dto.getId() );
        livroEntity.setNome( dto.getNome() );
        livroEntity.setAnoPublicacao( dto.getAnoPublicacao() );
        livroEntity.setCapaUrl( dto.getCapaUrl() );
        livroEntity.setPdfUrl( dto.getPdfUrl() );
        livroEntity.setSinopse( dto.getSinopse() );
        livroEntity.setGenero( dto.getGenero() );

        return livroEntity;
    }

    @Override
    public LivroDTO toDTO(LivroEntity entity) {
        if ( entity == null ) {
            return null;
        }

        LivroDTO livroDTO = new LivroDTO();

        livroDTO.setPublicadoPorNome( entityPublicadoPorNome( entity ) );
        livroDTO.setAutorNome( entityAutorNomeAutor( entity ) );
        livroDTO.setAutorId( entityAutorId( entity ) );
        livroDTO.setId( entity.getId() );
        livroDTO.setNome( entity.getNome() );
        livroDTO.setAnoPublicacao( entity.getAnoPublicacao() );
        livroDTO.setCapaUrl( entity.getCapaUrl() );
        livroDTO.setPdfUrl( entity.getPdfUrl() );
        livroDTO.setSinopse( entity.getSinopse() );
        livroDTO.setGenero( entity.getGenero() );

        return livroDTO;
    }

    private String entityPublicadoPorNome(LivroEntity livroEntity) {
        Usuario publicadoPor = livroEntity.getPublicadoPor();
        if ( publicadoPor == null ) {
            return null;
        }
        return publicadoPor.getNome();
    }

    private String entityAutorNomeAutor(LivroEntity livroEntity) {
        Autor autor = livroEntity.getAutor();
        if ( autor == null ) {
            return null;
        }
        return autor.getNomeAutor();
    }

    private Long entityAutorId(LivroEntity livroEntity) {
        Autor autor = livroEntity.getAutor();
        if ( autor == null ) {
            return null;
        }
        return autor.getId();
    }
}
