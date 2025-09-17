package app;
import app.DAO.*;
import app.config.*;
import app.entities.*;
import app.DTO.*;
import app.services.*;

public class Main {

    public static void main(String[] args) {
        ActorDAO actorDAO = new ActorDAO(HibernateConfig.getEntityManagerFactory());
        Actor actor = new Actor();
        actor.setName("John Doe");
        actor.setAge(30);

        Actor savedActor = actorDAO.persist(actor);
        System.out.println("Saved Actor ID: " + savedActor.getId());

        HibernateConfig.getEntityManagerFactory().close();

        ActorDTO test = new ActorDTO(1, "John Doe", "Director");
        AbstractService abstractService = new AbstractService() {
        };
        Actor convertedActor = (Actor) abstractService.convertToEntity(test, ActorDTO.class);
        System.out.println("Converted Actor ID (DTO): " + test.name());
        System.out.println("Converted Actor Name (Entity): " + convertedActor.getName());

    }
}