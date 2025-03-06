package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
   Optional<Serie>findByTituloContainingIgnoreCase(String nomeSerie);

   List<Serie> findByAtoresContainingIgnoreCaseAndAndAvaliacaoGreaterThanEqual(String nomeAtor,Double Avaliacao);

   List<Serie> findTop5ByOrderByAvaliacaoDesc();

   List<Serie> findByGenero(Categoria categoria);


 List<Serie> findTop5ByOrderByEpisodiosDataLancamentoDesc();

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numero")
    List<Episodio> obterEpisodiosPorTemporada(Long id, Long numero);


        @Query("SELECT s FROM Serie s " +
            "JOIN s.episodios e " +
            "GROUP BY s " +
            "ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> encontrarEpisodiosMaisRecentes();

   @Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
   List<Serie> seriesPorTemporadaEAValiacao(int totalTemporadas, double avaliacao);

@Query("select  e from Serie s JOIN  s.episodios e where e.titulo ILIKE %:trechoEpisodio%")
   List<Episodio> episodiosPorTrecho(String trechoEpisodio);

@Query("select  e from Serie s JOIN  s.episodios e where s = :serie order by e.avaliacao desc limit 5")
   List<Episodio> topEpisodiosPorSerie(Serie serie);

@Query ("select  e from Serie s JOIN  s.episodios e where s = :serie and YEAR (e.dataLancamento) >=:anoLancamento ")
   List<Episodio> episodiosPorAno(Serie serie, int anoLancamento);
}
