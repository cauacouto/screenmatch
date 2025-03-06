package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=e570b683";
    private List<DadosSerie> dadosSerie = new ArrayList<>();
    private  Optional<Serie> serieBusca;
    private SerieRepository repository;

    private List<Serie> series  = new ArrayList<>();

    public principal(SerieRepository repository) {
        this.repository = repository;
    }


    public void exibimenu() {
        var opcao = -1;
        while (opcao != 0) {


            var menu = """
                                    
                                    
                    1-buscar series
                    2- buscar episodios
                    3- listar séries buscadas
                    4- buscar serie por titulo
                    5- buscar serie por ator
                    6- buscar top 5 series
                    7- buscar séries por genero
                    8- filtra séries
                    9 - series por trecho 
                    10- top 5 episodios por serie
                    11- episodios por data de lançamento
                    0 -sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();


            switch (opcao) {

                case 1:
                    buscarSerieWeb();
                    break;

                case 2:
                    buscarEpisodiosPorSerie();
                    break;

                case 3:
                    listarSeriesBuscadas();
                    break;

                case 4:
                    buscarSeriePorTitulo();
                    break;

                case 5:
                    buscarSeriePorAtor();
                    break;

                case 6:
                    buscarTop5Series();
                    break;

                case 7:
                    buscarSeriesporCategoria();
                    break;

                case 8:
                    filtraSeriesPorTemporadaEAvaliacao();
                    break;

                case 9 :
                    buscarSeriesPorTrecho();
                    break;

                case 10:
                    buscarTop5EpisodiosPorSerie();
                    break;
                case 11:
                    buscarPorEpisodioPordata();

                case 0:
                    System.out.println("saindo..");
                    break;

                default:
                    System.out.println("opção invalida");
                    break;

            }
        }
    }



    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
//        dadosSerie.add(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {

        System.out.println("digite um serie para  buscar");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);

        return dados;
    }


    private void buscarEpisodiosPorSerie() {
        listarSeriesBuscadas();
        System.out.println("escolha uma serie pelo nome:");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> {
                    if (s.getTitulo() != null) {
                        return s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase());
                    } else {
                        return false;
                    }
                })
                .findFirst();
        if (serie.isPresent()) {

            var seriesEncotradas = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();


            for (int i = 1; i <= seriesEncotradas.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + seriesEncotradas.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            seriesEncotradas.setEpisodios(episodios);
            repository.save(seriesEncotradas);

        }else{
            System.out.println("serie não encontrada");
        }

    }

    private void listarSeriesBuscadas() {
        series = repository.findAll();
        series.stream()
                .forEach(System.out::println);
    }
    private void buscarSeriePorTitulo() {
        System.out.println("escolha a serie pelo nome:");
        var nomeSerie = leitura.nextLine();
        serieBusca = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("dados da serie" + serieBusca.get());
        } else {
            System.out.println("serie não encontrada");
        }
    }
    private void buscarSeriePorAtor(){
        System.out.println(" qual nome para busca ");
        var nomeAtor=leitura.nextLine();
        System.out.println(" avalição a partir de que valor ");
        var Avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas =  repository.findByAtoresContainingIgnoreCaseAndAndAvaliacaoGreaterThanEqual(nomeAtor,Avaliacao);
        System.out.println(" series em que o " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s->
                System.out.println(s.getTitulo() + " avalição:" + s.getAvaliacao()));


    }
    private void buscarTop5Series(){
        List<Serie> serieTop=repository.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s->
                System.out.println(s.getTitulo() + " avalição:" + s.getAvaliacao()));
    }

    private void buscarSeriesporCategoria(){
        System.out.println("digite a categoria/genero que deseja");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromportugues(nomeGenero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("series da categoria" + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);


    }
    private void filtraSeriesPorTemporadaEAvaliacao(){
        System.out.println("filtre series até quantas temporadas ");
        var totalTemporadas = leitura.nextInt();
        System.out.println("com avalição a partir de que valor ");
        var avaliacao = leitura.nextDouble();
        List<Serie> filtroSeries = repository.seriesPorTemporadaEAValiacao(totalTemporadas,avaliacao);
        System.out.println("*** series filtradas**");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "- avaliação:" + s.getAvaliacao()));

    }

    private void buscarSeriesPorTrecho(){
        System.out.println("qual nome do episodio deseja buscar ?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroDoEpisodio(), e.getTitulo()));
    }

    private void buscarTop5EpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios=   repository.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroDoEpisodio(), e.getTitulo(), e.getAvaliacao() ));

        }
    }
    private  void buscarPorEpisodioPordata(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("digite uma data limite ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repository.episodiosPorAno(serie,anoLancamento);
            episodiosAno.forEach(System.out::println);

        }

    }

}
