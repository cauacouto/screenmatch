package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class principal {

    private Scanner leitura  = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=e570b683";
    private List<DadosSerie> dadosSerie = new ArrayList<>();
    private SerieRepository repository;

    public principal(SerieRepository repository) {
        this.repository = repository;
    }


    public void exibimenu() {
        var opcao=-1;
        while(opcao!=0) {



            var menu =        """
                
                
                1-buscar series
                2- buscar episodios
                3- listar series buscadas
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
        DadosSerie dados=getDadosSerie();
        Serie serie= new Serie(dados);
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
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();


        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);


    }

    private void listarSeriesBuscadas() {

        List<Serie> series = new ArrayList<>();
        series=  dadosSerie.stream()
                .map(d_-> new Serie(d_))
                .collect(Collectors.toList());

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);



    }
}
