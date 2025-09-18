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

        var http = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .proxy(java.net.ProxySelector.of(null))
                .build();
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");

        MovieService movieService = new MovieService(emf);



        // Debug: verify API key is visible *before* making TMDB calls
        String apiKey = System.getenv("API_KEY");
        System.out.println("API key seen by JVM: len=" +
                (apiKey == null ? 0 : apiKey.length()) +
                ", head=" + (apiKey == null ? "null" : apiKey.substring(0, 3)) +
                ", tail=" + (apiKey == null ? "" : apiKey.substring(Math.max(0, apiKey.length() - 3))));


        System.out.println("API key seen by JVM: len=" + System.getenv("API_KEY").length()
                + ", head=" + System.getenv("API_KEY").substring(0,3)
                + ", tail=" + System.getenv("API_KEY").substring(System.getenv("API_KEY").length()-3));

        try {
            var addrs = java.net.InetAddress.getAllByName("api.themoviedb.org");
            System.out.println("[DNS] api.themoviedb.org -> " + java.util.Arrays.toString(addrs));
        } catch (Exception e) {
            System.out.println("[DNS] failed: " + e);
        }

        System.out.println("[Proxy] http.proxyHost=" + System.getProperty("http.proxyHost")
                + " https.proxyHost=" + System.getProperty("https.proxyHost"));


        movieService.fetchDanishMovies();



        // Close EntityManagerFactory at the end
        emf.close();



    }
}