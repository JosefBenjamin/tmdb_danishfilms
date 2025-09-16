package app.services;

import app.DAO.ActorDAO;
import app.entities.Actor;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class ActorService {

    private final ActorDAO actorDAO;

    public ActorService() {
        // Initialize EntityManagerFactory (you should configure this properly)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("your-persistence-unit");
        this.actorDAO = new ActorDAO(emf);
    }

    /**
     * Finds all actors in the database using JPA
     * @return List of all Actor entities
     */
    public List<Actor> getAllActors() {
        return actorDAO.findAll();
    }

    // Example of how to use the service
    public static void main(String[] args) {
        ActorService service = new ActorService();

        // This will execute the JPA query: SELECT a FROM Actor a
        List<Actor> allActors = service.getAllActors();

        System.out.println("Found " + allActors.size() + " actors:");
        for (Actor actor : allActors) {
            System.out.println("- " + actor.getName() + " (Age: " + actor.getAge() + ")");
        }
    }
}
