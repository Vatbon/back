package ftc.shift.secretsanta.repositories;

import ftc.shift.secretsanta.models.Book;

import java.util.Collection;

/**
 * Интерфейс для получения данных по книгам
 */

public interface BookRepository {

    Book fetchBook(String userId, String bookId);

    Book updateBook(String userId, String bookId, Book book);

    void deleteBook(String userId, String bookId);

    Book createBook(String userId, Book book);

    Collection<Book> getAllBooks(String userId);
}
