package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class principal {

    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados converteDados = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";


    private final String API_KEY = "&apikey=e570b683";


    Scanner leitura = new Scanner(System.in);

    public void exibimenu() {


        System.out.println("digite um serie para  buscar");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);


        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dados);


        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+")+"&Season=" + i + API_KEY);

            var dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);


        temporadas.forEach(t -> t.episodios() .forEach(e -> System.out.println(e.titulo()))); ;


        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());


        System.out.println("\n top 10 epsodios");

        dadosEpisodios.stream()
                .filter(e ->!e.avaliacao().equalsIgnoreCase("n/a") )
                .peek(e-> System.out.println("primeiro filtro "+e))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .peek(e-> System.out.println("ordenção "+e))
                .limit(10)
                .peek(e-> System.out.println("limit "+e))
                .map(e-> e.titulo().toUpperCase())
                .peek(e-> System.out.println("mapeamento "+e))
                .forEach(System.out::println);


        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d-> new Episodio(t.numero(),d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("digite um trecho do titulo para buscar");
        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodiobuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();

        if (episodiobuscado.isPresent()) {
            System.out.println("episodio encontrado: ");
            System.out.println("temporada: " + episodiobuscado.get().getTemporada());
        } else {
            System.out.println("nao foi encontrado");
        }

        System.out.println("a partir de que ano voce deseja ver os episodios");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate datadeBusca = LocalDate.of(ano,1,1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e-> e.getDataLancamento()!= null && e.getDataLancamento().isAfter(datadeBusca))
                .forEach (e-> System.out.println(
                        "temporada: "+e.getTemporada()+
                                " episodio: " + e.getTitulo()+
                                " data de lancamento: "+e.getDataLancamento().format(formatador)
                ));


                   Map<Integer,Double> AvalicaoPorTemporada = episodios.stream()
                           .filter(e-> e.getAvaliacao()>0.0)
                           .collect(Collectors.groupingBy(Episodio::getTemporada,
                                   Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(AvalicaoPorTemporada);










    }
}
