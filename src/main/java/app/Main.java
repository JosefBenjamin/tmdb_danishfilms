package app;
import app.DAO.*;
import app.config.*;
import app.entities.*;
import app.DTO.*;
import app.services.*;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Get EntityManagerFactory
        var emf = HibernateConfig.getEntityManagerFactory();

        try {
            // Create service instances
            MovieService movieService = new MovieService(emf);
            ActorService actorService = new ActorService(emf);
            GenreService genreService = new GenreService(emf);

            // Fetch initial data
            System.out.println("\n=== Fetching Initial Data ===");
            System.out.println("Fetching genres...");
            genreService.fetchAllGenres();
            
            System.out.println("Fetching Danish movies...");
            movieService.fetchDanishMovies();
            
            System.out.println("Fetching cast information...");
            movieService.fetchMovieCast();
            
            System.out.println("Data fetch completed!");

            // Test Movie Service Operations
            System.out.println("\n=== Testing MovieService Operations ===");
            movieService.getAll().forEach(System.out::println);
            System.out.println("\nSearching for 'Crocodile Tears':");
            System.out.println(movieService.searchByTitle("Crocodile Tears"));
            
            System.out.println("\nMovies rated between 8.5 and 9.9:");
            movieService.getMoviesByRating(8.5, 9.9).forEach(System.out::println);

            // Test Actor Service Operations
            System.out.println("\n=== Testing ActorService CRUD Operations ===");
            
            // Create new actor via DTO
            ActorDTO newActorDTO = new ActorDTO(
                null,                   // id
                "Jane Smith",           // name
                "Supporting Actor",     // character/job
                "/profile/path.jpg",    // profilePath
                1,                      // castId
                2                       // order
            );
            
            ActorDTO savedDTO = actorService.save(newActorDTO);
            System.out.println("Saved Actor via Service: " + savedDTO.name() + " with ID: " + savedDTO.getId());

            // Test retrieval
            var retrievedActor = actorService.getById(savedDTO.getId());
            if (retrievedActor.isPresent()) {
                System.out.println("Retrieved Actor: " + retrievedActor.get().name());
            }

            // Get all actors
            var allActors = actorService.getAll();
            System.out.println("Total actors in database: " + allActors.size());

            // Test direct DAO usage
            ActorDAO actorDAO = new ActorDAO(emf);
            Actor actor = Actor.builder()
                .name("John Doe")
                .age(30)
                .build();

            Actor savedActor = actorDAO.persist(actor);
            System.out.println("Saved Actor ID: " + savedActor.getId());

            // Test conversion
            ActorDTO test = new ActorDTO(
                1,                      // id
                "John Doe",            // name
                "Lead Actor",          // character/job
                "/profile/doe.jpg",    // profilePath
                2,                     // castId
                1                      // order
            );

            Actor convertedActor = actorService.convertToEntity(test);
            System.out.println("Converted Actor: " + convertedActor.getName());

        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        } finally {
            emf.close();
        }
    }
}
