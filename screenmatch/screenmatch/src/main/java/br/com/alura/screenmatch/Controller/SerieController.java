package br.com.alura.screenmatch.Controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SeriesDto;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService service;


@GetMapping
    public List<SeriesDto> obterSerie(){

   return service.ObterTodasAsSeries();
}

@GetMapping("/top5")

    public  List<SeriesDto> obterTop5(){

    return service.obterTop5();
}

@GetMapping("/lancamentos")

    public  List<SeriesDto> obterlancamentos(){
    return service.obterlancamentos();
}
@GetMapping("/{id}")

    public SeriesDto obterPorid (@PathVariable Long id){
    return  service.obterid(id);
}

@GetMapping ("/{id}/temporadas/todas")

    public List<EpisodioDTO> obterTodasAsTemporadas (@PathVariable Long id){
    return  service.obterTodasAsTemporadas(id);

}
@GetMapping ("/{id}/temporadas/{numero}")

    public  List<EpisodioDTO> obterTemporadaPorNumero (@PathVariable Long id , @PathVariable Long numero){

    return  service.obterTemporadasPorNumero(id,numero);
}
@GetMapping ("/categoria/{genero}")

    public  List<SeriesDto> obterSeriePorGenero(@PathVariable String genero){
    return  service.obterSeriePorGenero(genero);
}

}
