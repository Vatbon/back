package ftc.shift.secretsanta.repositories.database.extractors;

import ftc.shift.secretsanta.models.Book;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookExtractor implements ResultSetExtractor<List<Book>> {
    @Override
    public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Book> books = new HashMap<>();

        while (rs.next()) {
            String bookId = rs.getString("BOOK_ID");

            Book book;
            if (books.containsKey(bookId)) {
                book = books.get(bookId);
            } else {
                book = new Book();

                book.setId(rs.getString("BOOK_ID"));
                book.setName(rs.getString("NAME"));
                book.setAuthor(rs.getString("AUTHOR"));
                book.setPages(rs.getInt("PAGES"));
                book.setGenre(new ArrayList<>());

                books.put(bookId, book);
            }

            String genre = rs.getString("GENRE");
            book.getGenre().add(genre);
        }

        return new ArrayList<>(books.values());
    }
}
