package ftc.shift.secretsanta.repositories;

import ftc.shift.secretsanta.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Реализиция, хранящая все данные в БД
 */
@Repository
@ConditionalOnProperty(name = "use.database", havingValue = "true")
public class DatabaseBookRepository implements BookRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private BookExtractor bookExtractor;

    @PostConstruct
    public void initialize() {
        // Подразумевается, что H2 работает в in-memory режиме и таблицы необходимо создавать при каждом старте приложения
        // SQL запросы для создания таблиц
        String createGenerateBookIdSequenceSql = "create sequence BOOK_ID_GENERATOR";

        String createBookTableSql = "create table BOOKS (" +
                "BOOK_ID  VARCHAR(64) default BOOK_ID_GENERATOR.nextval," +
                "USER_ID  VARCHAR(64)," +
                "NAME     VARCHAR(64)," +
                "AUTHOR   VARCHAR(64)," +
                "PAGES    INTEGER" +
                ");";

        String createGenresTableSql = "create table GENRES (" +
                "BOOK_ID  VARCHAR(64)," +
                "GENRE    VARCHAR(64)" +
                ");";

        jdbcTemplate.update(createGenerateBookIdSequenceSql, new MapSqlParameterSource());
        jdbcTemplate.update(createBookTableSql, new MapSqlParameterSource());
        jdbcTemplate.update(createGenresTableSql, new MapSqlParameterSource());

        // Заполним таблицы тестовыми данными
        createBook("UserA", new Book("1", "Название 1", "Автор Авторович", 12,
                Arrays.asList("Фантастика", "Драма", "Нуар")));

        createBook("UserA", new Book("2", "Название 2", "Автор Писателевич", 48,
                Collections.singletonList("Детектив")));

        createBook("UserB", new Book("3", "Название 3", "Писатель Авторович", 24,
                Collections.singletonList("Киберпанк")));
    }

    @Override
    public Collection<Book> getAllBooks(String userId) {
        String sql = "select USER_ID, BOOKS.BOOK_ID, NAME, AUTHOR, PAGES, GENRE " +
                "from BOOKS, GENRES " +
                "where BOOKS.BOOK_ID = GENRES.BOOK_ID and BOOKS.USER_ID=:userId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        return jdbcTemplate.query(sql, params, bookExtractor);
    }

    @Override
    public Book fetchBook(String userId, String bookId) {
        String sql = "select USER_ID, BOOKS.BOOK_ID, NAME, AUTHOR, PAGES, GENRE " +
                "from BOOKS, GENRES " +
                "where BOOKS.BOOK_ID = GENRES.BOOK_ID and BOOKS.BOOK_ID=:bookId and BOOKS.USER_ID=:userId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookId", bookId);

        List<Book> books = jdbcTemplate.query(sql, params, bookExtractor);

        if (books.isEmpty()) {
            return null;
        }

        return books.get(0);
    }

    @Override
    public void deleteBook(String userId, String bookId) {
        String deleteGenresSql = "delete from GENRES where BOOK_ID=:bookId";
        String deleteBookSql = "delete from BOOKS where BOOK_ID=:bookId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookId", bookId);

        jdbcTemplate.update(deleteGenresSql, params);
        jdbcTemplate.update(deleteBookSql, params);
    }

    @Override
    public Book createBook(String userId, Book book) {
        // Добавляем книгу
        String insertBookSql = "insert into BOOKS (USER_ID, NAME, AUTHOR, PAGES) values (:userId, :name, :author, :pages)";

        // (!) При этом мы не указываем значения для столбца BOOK_ID.
        // Он будет сгенерирован автоматически на стороне БД
        MapSqlParameterSource bookParams = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("name", book.getName())
                .addValue("author", book.getAuthor())
                .addValue("pages", book.getPages());

        // Класс, который позволит получить сгенерированный bookId
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(insertBookSql, bookParams, generatedKeyHolder);

        String bookId = generatedKeyHolder.getKeys().get("BOOK_ID").toString();
        book.setId(bookId);

        for (String genre : book.getGenre()) {
            String insertGenreSql = "insert into GENRES (BOOK_ID, GENRE) values (:bookId, :genre)";

            // Он будет сгенерирован автоматически на стороне БД
            MapSqlParameterSource genreParams = new MapSqlParameterSource()
                    .addValue("bookId", bookId)
                    .addValue("genre", genre);

            jdbcTemplate.update(insertGenreSql, genreParams);
        }

        return book;
    }

    @Override
    public Book updateBook(String userId, String bookId, Book book) {
        // 1) Обновляем информацию о книге
        String updateBookSql = "update BOOKS " +
                "set USER_ID=:userId, " +
                "NAME=:name, " +
                "AUTHOR=:author, " +
                "PAGES=:pages " +
                "where BOOK_ID=:bookId";

        MapSqlParameterSource bookParams = new MapSqlParameterSource()
                .addValue("bookId", bookId)
                .addValue("userId", userId)
                .addValue("name", book.getName())
                .addValue("author", book.getAuthor())
                .addValue("pages", book.getPages());

        jdbcTemplate.update(updateBookSql, bookParams);

        // 2) Удаляем старые жанры
        String deleteGenresSql = "delete from GENRES where BOOK_ID=:bookId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookId", bookId);

        jdbcTemplate.update(deleteGenresSql, params);

        // 3) Добавляем новые жанры
        for (String genre : book.getGenre()) {
            String insertGenreSql = "insert into GENRES (BOOK_ID, GENRE) values (:bookId, :genre)";

            // Он будет сгенерирован автоматически на стороне БД
            MapSqlParameterSource genreParams = new MapSqlParameterSource()
                    .addValue("bookId", bookId)
                    .addValue("genre", genre);

            jdbcTemplate.update(insertGenreSql, genreParams);
        }

        return book;
    }
}
