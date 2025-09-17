package app;
import app.Function.DAO.ActorDAO;
import app.Function.services.ActorService;
import app.Object.DTO.ActorDTO;
import app.Object.entities.ActorEntity;
import app.config.*;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {
        // Get EntityManagerFactory
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

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
        ActorEntity actorEntity = new ActorEntity();
        actorEntity.setName("John Doe");
        actorEntity.setAge(30);

        ActorEntity savedActorEntity = actorDAO.persist(actorEntity);
        System.out.println("Saved Actor ID: " + savedActorEntity.getId());

        // Test service usage with proper ActorService
        ActorDTO test = new ActorDTO(1, "John Doe", "Director");


        // Test conversion methods (these are now public through the service)
        ActorEntity convertedActorEntity = actorService.convertToEntity(test);
        System.out.println("Converted Actor ID (DTO): " + test.name());
        System.out.println("Converted Actor Name (Entity): " + convertedActorEntity.getName());

        // Close EntityManagerFactory at the end
        emf.close();
    }
}