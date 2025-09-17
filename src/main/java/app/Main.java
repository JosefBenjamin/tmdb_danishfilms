package app;
import app.DAO.*;
import app.config.*;
import app.entities.*;

public class Main {

    public static void main(String[] args) {
        ActorDAO actorDAO = new ActorDAO(HibernateConfig.getEntityManagerFactory());

        Actor actor = new Actor();
        actor.setName("John Doe");
        actor.setAge(30);

        Actor savedActor = actorDAO.persist(actor);
        System.out.println("Saved Actor ID: " + savedActor.getId());

        HibernateConfig.getEntityManagerFactory().close();

    }
}