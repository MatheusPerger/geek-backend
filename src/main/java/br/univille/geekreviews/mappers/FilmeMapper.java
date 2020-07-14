package br.univille.geekreviews.mappers;

import br.univille.geekreviews.domain.Filme;
import br.univille.geekreviews.domain.enums.Categoria;
import br.univille.geekreviews.dtos.filme.FilmeDTO;
import br.univille.geekreviews.dtos.filme.FilmePesquisaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel="spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {Categoria.class})
public interface FilmeMapper {

    FilmeDTO toDto(Filme entity);

    @Mappings({
            // @Mapping(target = "categorias", expression = "java(Categoria.getByDescricaoList(dto.getCategorias()))")
    })
    Filme toEntity(FilmeDTO dto);

    List<FilmeDTO> toDtos(List<Filme> filmes);

    FilmePesquisaDTO toPesquisaDto(Filme entity);

    List<FilmePesquisaDTO> toPesquisaDtos(List<Filme> filmes);
}