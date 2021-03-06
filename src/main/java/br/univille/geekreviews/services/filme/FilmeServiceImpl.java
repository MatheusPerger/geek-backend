package br.univille.geekreviews.services.filme;

import br.univille.geekreviews.domain.Filme;
import br.univille.geekreviews.dtos.filme.FilmeDTO;
import br.univille.geekreviews.dtos.filme.FilmePesquisaDTO;
import br.univille.geekreviews.mappers.FilmeMapper;
import br.univille.geekreviews.repositories.FilmeRepository;
import br.univille.geekreviews.services.S3Service;
import br.univille.geekreviews.services.exception.BusinessException;
import br.univille.geekreviews.services.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Service
@Transactional
public class FilmeServiceImpl implements FilmeService {

    @Autowired
    private FilmeRepository repo;

    @Autowired
    private FilmeMapper mapper;

    @Autowired
    private S3Service s3Service;

    @Override
    public FilmeDTO obterPorId(Long id) {

        Filme entity = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Nenhum registro encontrado"));
        return mapper.toDto(entity);
    }

    @Override
    public Page<FilmePesquisaDTO> filtrar(String search, Pageable p) {

        Page<Filme> filmes = repo.filtrar(search, p);
        Page<FilmePesquisaDTO> dtos = filmes.map(v -> mapper.toPesquisaDto(v));

        return dtos;
    }

    @Override
    public Long salvar(FilmeDTO dto) {

        Filme entity = mapper.toEntity(dto);
        this.validarInsercaoAtualizacao(dto);

        Filme filme = repo.save(entity);
        return filme.getId();
    }

    @Override
    public Long atualizar(FilmeDTO dto) {

        Filme entity = mapper.toEntity(dto);
        this.validarInsercaoAtualizacao(dto);

        Filme filme = repo.save(entity);
        return filme.getId();
    }

    @Override
    public void excluir(Long id) {

        Filme entity = repo.findById(id).orElseThrow(null);
        repo.delete(entity);
    }

    private void validarInsercaoAtualizacao(FilmeDTO dto) throws BusinessException {

        Filme filme = repo.findByTitulo(dto.getTitulo());

        if (filme == null)
            return;

        if (dto.isEdicao() && (filme.getId() == dto.getId()))
            return;

        throw new BusinessException("O filme " + dto.getTitulo() + " já está cadastrado.");
    }

    @Override
    public void uploadImagem(MultipartFile multipartFile, Long idMidia) {

        Filme entity = repo.findById(idMidia).orElseThrow(() -> new ObjectNotFoundException("Nenhum registro encontrado"));

        if (entity.getUrlCapa() != null)
            s3Service.deleteFile(entity.getUrlCapa());

        URI urlImagem = s3Service.uploadFile(multipartFile);
        entity.setUrlCapa(urlImagem.toString());
        repo.save(entity);
    }

}
