package app;
import app.DAO.*;
import app.DTO.*;
import app.services.*;
import app.utils.*;
import app.config.*;
import app.entities.*;

public class Main {

    public static void main(String[] args) {
        ActorDAO actorDAO = new ActorDAO(HibernateConfig.getEntityManagerFactory());

        Actor actor = new Actor();
        actor.setName("John Doe");
        actor.setAge(30);

        Actor savedActor = actorDAO.save(actor);
        System.out.println("Saved Actor ID: " + savedActor.getId());

        HibernateConfig.getEntityManagerFactory().close();

    }
}