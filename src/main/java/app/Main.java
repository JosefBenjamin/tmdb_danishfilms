package app;
import app.DAO.*;
import app.config.*;
import app.entities.*;
import app.DTO.*;
import app.services.*;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        // Get EntityManagerFactory
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Use ActorService instead of anonymous AbstractService
        ActorService actorService = new ActorService(emf);

        // Test full CRUD operations
        System.out.println("\n=== Testing ActorService CRUD Operations ===");

        // Get all actors
        List<ActorDTO> allActors = actorService.getAll();
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

        // Close EntityManagerFactory at the end
        emf.close();
    }
}