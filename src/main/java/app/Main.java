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
            // Create MovieService instance
            MovieService movieService = new MovieService(emf);

            // First fetch Danish movies
            System.out.println("Fetching Danish movies...");
            movieService.fetchDanishMovies();

            // Then fetch cast information for those movies
            System.out.println("Fetching cast information...");
            movieService.fetchMovieCast();

            System.out.println("Data fetch completed!");

            // Use ActorService instead of anonymous AbstractService
            ActorService actorService = new ActorService(emf);

            // Test full CRUD operations
            System.out.println("\n=== Testing ActorService CRUD Operations ===");

            // Save using service (now with all required parameters)
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

            // Get by ID
            var retrievedActor = actorService.getById(savedDTO.getId());
            if (retrievedActor.isPresent()) {
                System.out.println("Retrieved Actor: " + retrievedActor.get().name());
            }

            // Get all actors
            var allActors = actorService.getAll();
            System.out.println("Total actors in database: " + allActors.size());

            // Test direct DAO usage
            ActorDAO actorDAO = new ActorDAO(emf);
            Actor actor = new Actor();
            actor.setName("John Doe");
            actor.setAge(30);

            Actor savedActor = actorDAO.persist(actor);
            System.out.println("Saved Actor ID: " + savedActor.getId());

            // Test service usage with proper ActorService (now with all required parameters)
            ActorDTO test = new ActorDTO(
                    1,                      // id
                    "John Doe",            // name
                    "Lead Actor",          // character/job
                    "/profile/doe.jpg",    // profilePath
                    2,                     // castId
                    1                      // order
            );

            // Test conversion methods
            Actor convertedActor = actorService.convertToEntity(test);
            System.out.println("Converted Actor ID (DTO): " + test.name());
            System.out.println("Converted Actor Name (Entity): " + convertedActor.getName());

        } catch (Exception e) {
            System.err.println("Error during data fetch: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close EntityManagerFactory at the end
            emf.close();
        }
    }
}
