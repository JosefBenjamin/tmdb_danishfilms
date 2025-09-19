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

        // Use ActorService instead of anonymous AbstractService
        ActorService actorService = new ActorService(emf);

        // Test full CRUD operations
        System.out.println("\n=== Testing ActorService CRUD Operations ===");


        // Save using service
        ActorDTO newActorDTO = new ActorDTO(null, "Jane Smith", "Acting");
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

        // Test service usage with proper ActorService
        ActorDTO test = new ActorDTO(1, "John Doe", "Director");


        // Test conversion methods (these are now public through the service)
        Actor convertedActor = actorService.convertToEntity(test);
        System.out.println("Converted Actor ID (DTO): " + test.name());
        System.out.println("Converted Actor Name (Entity): " + convertedActor.getName());


        
        MovieService movieService = new MovieService(emf);

        // step 1
        movieService.fetchDanishMovies();
        // step 2 - Hvilken vil vi helst bruge?
        //movieService.printAllMovies();
        movieService.getAll().forEach(System.out::println);

        // step 6
        System.out.println(movieService.searchByTitle("Crocodile Tears"));

        // step 7
        System.out.println(movieService.getTotalAverageRating());
        System.out.println("Total average rating");
        movieService.getTop10ByRating().forEach(System.out::println);
        System.out.println("Top 10 movies");
        movieService.getBottom10ByRating().forEach(System.out::println);
        System.out.println("Bottom 10 movies");
        movieService.getMoviesByRating(8.5, 9.9).forEach(System.out::println);
        System.out.println("Movies rated between 8.5 and 9.9");
        System.out.println("yo");


        // Close EntityManagerFactory at the end
        emf.close();
    }
}