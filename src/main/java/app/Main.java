package app;
import app.Function.DAO.*;
import app.Function.services.ActorService;
import app.Function.services.DirectorService;
import app.Function.services.MovieService;
import app.Function.services.GenreService;
import app.Instance.DTO.ActorDTO;
import app.Instance.entities.ActorEntity;
import app.config.*;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {
        // Get EntityManagerFactory
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        System.out.println("\n=== Testing Complete Architecture ===");

        // Test all services
        ActorService actorService = new ActorService(emf);
        DirectorService directorService = new DirectorService(emf);
        MovieService movieService = new MovieService(emf);
        GenreService genreService = new GenreService(emf);

        // Test Actor operations
        System.out.println("\n--- Actor Service Tests ---");
        var allActors = actorService.getAll();
        System.out.println("Total actors in database: " + allActors.size());

        // Test Director operations
        System.out.println("\n--- Director Service Tests ---");
        var allDirectors = directorService.getAll();
        System.out.println("Total directors in database: " + allDirectors.size());

        // Test Movie operations
        System.out.println("\n--- Movie Service Tests ---");
        var allMovies = movieService.getAll();
        System.out.println("Total movies in database: " + allMovies.size());

        // Test Genre operations
        System.out.println("\n--- Genre Service Tests ---");
        var allGenres = genreService.getAll();
        System.out.println("Total genres in database: " + allGenres.size());

        // Test direct DAO usage (still works)
        System.out.println("\n--- Direct DAO Test ---");
        ActorDAO actorDAO = new ActorDAO(emf);
        ActorEntity actorEntity = ActorEntity.builder()
            .name("Test Actor")
            .age(30)
            .build();

        ActorEntity savedActorEntity = actorDAO.persist(actorEntity);
        System.out.println("Saved Actor ID: " + savedActorEntity.getId());

        // Test service save method
        System.out.println("\n--- Service Save Test ---");
        ActorDTO testActorDTO = new ActorDTO(null, "Service Test Actor", "Acting");
        ActorDTO savedActorDTO = actorService.save(testActorDTO);
        System.out.println("Saved Actor via Service - ID: " + savedActorDTO.getId() + ", Name: " + savedActorDTO.name());

        // Test business methods
        if (savedActorDTO.getId() != null) {
            System.out.println("\n--- Business Logic Tests ---");
            var moviesForActor = actorService.getMoviesByActor(savedActorDTO.getId());
            System.out.println("Movies for Actor " + savedActorDTO.name() + ": " + moviesForActor.size());
        }

        System.out.println("\n=== Architecture Test Complete ===");

        // Close EntityManagerFactory at the end
        emf.close();
    }
}