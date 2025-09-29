package br.ifba.edu.BibliotecaOnline.mapper;

import br.ifba.edu.BibliotecaOnline.DTO.AvaliacaoDTO;
import br.ifba.edu.BibliotecaOnline.entities.AvaliacaoEntity;
import br.ifba.edu.BibliotecaOnline.entities.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-29T14:26:56-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class AvaliacaoMapperImpl implements AvaliacaoMapper {

    @Override
    public AvaliacaoDTO toDTO(AvaliacaoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();

        avaliacaoDTO.setNomeUsuario( entityUsuarioNome( entity ) );
        avaliacaoDTO.setFotoUsuario( entityUsuarioFotoPerfil( entity ) );
        avaliacaoDTO.setEmailUsuario( entityUsuarioEmail( entity ) );
        avaliacaoDTO.setId( entity.getId() );
        avaliacaoDTO.setComentario( entity.getComentario() );
        avaliacaoDTO.setNota( entity.getNota() );
        avaliacaoDTO.setDataAvaliacao( entity.getDataAvaliacao() );

        return avaliacaoDTO;
    }

    private String entityUsuarioNome(AvaliacaoEntity avaliacaoEntity) {
        Usuario usuario = avaliacaoEntity.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNome();
    }

    private String entityUsuarioFotoPerfil(AvaliacaoEntity avaliacaoEntity) {
        Usuario usuario = avaliacaoEntity.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getFotoPerfil();
    }

    private String entityUsuarioEmail(AvaliacaoEntity avaliacaoEntity) {
        Usuario usuario = avaliacaoEntity.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getEmail();
    }
}
