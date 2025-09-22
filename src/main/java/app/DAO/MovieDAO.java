package app.DAO;

import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MovieDAO implements BaseDAO<Movie, Integer> {

    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<Movie> findById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = em.find(Movie.class, id);
            return Optional.ofNullable(movie);
        }
    }

    @Override
    public List<Movie> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            return query.getResultList();
        }
    }

    public Movie persist(Movie entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(entity);
                em.getTransaction().commit();
                return entity;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    public Movie update(Movie entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Movie updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    public void delete(Movie entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Movie managedMovie = em.find(Movie.class, entity.getId());
                if (managedMovie != null) {
                    em.remove(managedMovie);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    // Additional query methods
    public List<Movie> findByTitle(String title) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery(
                "SELECT m FROM Movie m WHERE m.title LIKE :title", Movie.class);
            query.setParameter("title", "%" + title + "%");
            return query.getResultList();
        }
    }

    public List<Movie> findByDirectorId(Integer directorId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery(
                "SELECT m FROM Movie m WHERE m.director.id = :directorId", Movie.class);
            query.setParameter("directorId", directorId);
            return query.getResultList();
        }
    }


    public Double averageRatingOfAllMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            Double avg = em.createQuery("SELECT avg(m.rating) from Movie m", Double.class)
                    .getSingleResult();
            return avg;
         } catch (RuntimeException e) {
            throw ApiException.notFound("No results found");
        }
    }


    public List<Movie> highestRatedMovies(int numberOfMovies) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = em.createQuery("SELECT m " + "FROM Movie m " +
                    "WHERE m.rating IS NOT NULL " +
                    "ORDER BY m.rating DESC nulls last ", Movie.class)
                    .setMaxResults(numberOfMovies)
                    .getResultList();
            return  movies;
        } catch (RuntimeException e) {
            throw ApiException.badRequest("Use an int to pick movies amount");
        }
    }


    public List<Movie> lowestRatedMovies(int numberOfMovies) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = em.createQuery("SELECT m " + "FROM Movie m " +
                            "WHERE m.rating IS NOT NULL " +
                            "ORDER BY m.rating ASC NULLS LAST ", Movie.class)
                    .setMaxResults(numberOfMovies)
                    .getResultList();
            return movies;
        } catch (RuntimeException e) {
            throw ApiException.notFound("No results found");
        }
    }

}
