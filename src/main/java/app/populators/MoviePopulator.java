
package app.populators;

import app.DAO.MovieDAO;
import app.entities.Movie;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data helper that inserts a predictable set of 10 Movie rows
 * and returns the managed entities. Intended for integration tests.
 */
public class MoviePopulator {

    /**
     * Clears nothing by itself – caller should truncate/reset tables if needed.
     * Persists 10 movies with different ratings/dates/titles and returns them
     * in a stable order (index 0..9).
     */
    public List<Movie> populateMovies(MovieDAO movieDAO) {
        List<Movie> saved = new ArrayList<>();

        // Language kept as Danish ("da") since your dataset is DK-focused
        saved.add(persist(movieDAO, Movie.builder()
                .title("Crocodile Tears")
                .releaseDate(LocalDate.of(2021, 11, 24))
                .rating(7.8)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("Silent Fjord")
                .releaseDate(LocalDate.of(2020, 5, 12))
                .rating(6.3)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("Northern Lights Club")
                .releaseDate(LocalDate.of(2023, 1, 13))
                .rating(9.1)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("Hygge Heist")
                .releaseDate(LocalDate.of(2022, 3, 4))
                .rating(5.0)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("Midnight Smørrebrød")
                .releaseDate(LocalDate.of(2024, 6, 21))
                .rating(8.6)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("København Noir")
                .releaseDate(LocalDate.of(2019, 9, 7))
                .rating(3.2)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("Viking Wifi")
                .releaseDate(LocalDate.of(2025, 2, 20))
                .rating(10.0)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("Brick by Brick")
                .releaseDate(LocalDate.of(2018, 10, 2))
                .rating(1.4)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("The Jutland Drift")
                .releaseDate(LocalDate.of(2023, 8, 31))
                .rating(4.7)
                .originalLanguage("da")
                .build()));

        saved.add(persist(movieDAO, Movie.builder()
                .title("Seagulls of Samsø")
                .releaseDate(LocalDate.of(2022, 12, 5))
                .rating(0.0)
                .originalLanguage("da")
                .build()));

        return saved;
    }

    private Movie persist(MovieDAO dao, Movie m) {
        return dao.persist(m);
    }
}
