package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SeriesDto;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repository;

    public List<SeriesDto> ObterTodasAsSeries(){
        return coverteDados(repository.findAll());

    }

    public List<SeriesDto> obterTop5() {
        return coverteDados(repository.findTop5ByOrderByAvaliacaoDesc());


    }

    public List<SeriesDto> coverteDados(List<Serie> series){
        return series .stream().map(s-> new SeriesDto(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse()))
                .collect(Collectors.toList());

    }
    public List<SeriesDto> obterlancamentos(){
        return  coverteDados(repository.encontrarEpisodiosMaisRecentes());
    }

    public SeriesDto obterid(Long id) {
        Optional<Serie> serie = repository.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return new SeriesDto(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse());

        }
        return null;
    }

    public List<EpisodioDTO> obterTodasAsTemporadas(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroDoEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
        return repository.obterEpisodiosPorTemporada(id, numero)
                .stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroDoEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SeriesDto> obterSeriePorGenero(String genero) {

        Categoria categoria = Categoria.fromportugues(genero);
        return coverteDados(repository.findByGenero(categoria));

    }
}