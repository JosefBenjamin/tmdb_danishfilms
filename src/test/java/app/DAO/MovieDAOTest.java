package app.DAO;

import app.config.HibernateConfig;
import app.entities.Movie;
import app.exceptions.ApiException;
import app.populators.MoviePopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class MovieDAOTest {

    private static EntityManagerFactory emf;
    private static MovieDAO movieDAO;
    private Movie movie1;
    private Movie movie2;
    private Movie movie3;
    private Movie movie4;
    private Movie movie5;
    private Movie movie6;
    private Movie movie7;
    private Movie movie8;
    private Movie movie9;
    private Movie movie10;
    private List<Movie> movies;



    @BeforeAll
    static void initOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        movieDAO = new MovieDAO(emf);

    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE movies RESTART IDENTITY CASCADE")
                    .executeUpdate();
            em.getTransaction().commit();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to reset tables", e);
        }

        MoviePopulator moviePopulator = new MoviePopulator();

        movies = moviePopulator.populateMovies(movieDAO);

        if (movies.size() == 10) {
            movie1 = movies.get(0);
            movie2 = movies.get(1);
            movie3 = movies.get(2);
            movie4 = movies.get(3);
            movie5 = movies.get(4);
            movie6 = movies.get(5);
            movie7 = movies.get(6);
            movie8 = movies.get(7);
            movie9 = movies.get(8);
            movie10 = movies.get(9);

        } else {
            throw new ApiException(500, "Populator doesnt work");
        }
    }

    @AfterAll
    static void tearDownAll() {
        if(emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    @Test
    void create() {
        // Create a new movie object (10 movies already inserted in @BeforeEach)
        Movie movie = Movie.builder()
                .title("Unit Test Movie")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .rating(7.5)
                .originalLanguage("da")
                .build();

        // Persist the entity via DAO (managed and returned)
        Movie created = movieDAO.persist(movie);

        // The returned entity should not be null and should have same business data
        assertNotNull(created);
        assertEquals(movie.getTitle(), created.getTitle());
        assertNotNull(created.getId());

        // Total count should now be 11 (10 from populator + 1 just created)
        List<Movie> all = movieDAO.findAll();
        assertEquals(11, all.size());
    }


    @Test
    void averageRatingOfAllMovies() {

        //TODO: Arrange
        Double totalAvg = movies.stream().map(movie -> movie.getRating())
                .filter(rating -> Objects.nonNull(rating))
                .mapToDouble(d -> d.doubleValue()).average().getAsDouble();

        //TODO: Act
        Double actual = movieDAO.averageRatingOfAllMovies();

        //TODO: Assert
        assertNotNull(actual);
        assertNotNull(totalAvg);
        assertEquals(totalAvg, actual);
    }

    @Test
    void highestRatedMovies() {
        //TODO: Arrange
        List<Movie> highestRated = movies.stream()
                .filter(film ->  film.getRating() != null)
                .sorted(Comparator.comparing((Movie m) -> m.getRating()).reversed())
                .limit(5)
                .toList();


        //TODO: Act
        List<Movie> moviesHR = movieDAO.highestRatedMovies(5);




        //TODO: Assert
        assertNotNull(highestRated);
        assertNotNull(moviesHR);
        assertEquals(5, moviesHR.size());
        assertEquals(5, highestRated.size());
        assertEquals(highestRated.stream().map(m -> {
            return m.getRating();
                }).toList(),
                moviesHR.stream().map(m -> {
                    return  m.getRating();
                }).toList());
    }

    @Test
    void lowestRatedMovies() {
        //TODO: Arrange
        List<Movie> lowestRated = movies.stream()
                .filter(film ->  film.getRating() != null)
                .sorted(Comparator.comparing((Movie m) -> m.getRating()))
                .limit(5)
                .toList();


        //TODO: Act
        List<Movie> moviesLR = movieDAO.lowestRatedMovies(5);




        //TODO: Assert
        assertNotNull(lowestRated);
        assertNotNull(moviesLR);
        assertEquals(5, lowestRated.size());
        assertEquals(5, moviesLR.size());
        assertEquals(lowestRated.stream().map(m -> {
                    return m.getRating();
                }).toList(),
                moviesLR.stream().map(m -> {
                    return  m.getRating();
                }).toList());
    }


}