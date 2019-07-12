package ftc.shift.secretsanta.repositories;

import ftc.shift.secretsanta.exception.NotFoundException;
import ftc.shift.secretsanta.models.Book;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализиция, хранящая все данные в памяти приложения
 */
@Repository
@ConditionalOnProperty(name = "use.database", havingValue = "false")
public class InMemoryBookRepository implements BookRepository {
    /**
     * Ключ - имя пользователя, значение - все книги, которые есть у пользователя
     */
    private Map<String, Map<String, Book>> bookCache = new HashMap<>();

    public InMemoryBookRepository() {
        // Заполним репозиторий тестовыми данными
        // В тестовых данных существует всего 3 пользователя: UserA / UserB / UserC

        bookCache.put("UserA", new HashMap<>());
        bookCache.get("UserA").put("1", new Book("1", "Название 1", "Автор Авторович", 12,
                Collections.singletonList("Фантастика")));
        bookCache.get("UserA").put("2", new Book("2", "Название 2", "Автор Писателевич", 48,
                Collections.singletonList("Детектив")));

        bookCache.put("UserB", new HashMap<>());
        bookCache.get("UserB").put("3", new Book("3", "Название 3", "Писатель Авторович", 24,
                Collections.singletonList("Киберпанк")));

        bookCache.put("UserC", new HashMap<>());
    }

    @Override
    public Book fetchBook(String userId, String bookId) {
        if (!bookCache.containsKey(userId)) {
            // Пользователь не найден
            throw new NotFoundException();
        }

        Map<String, Book> userBooks = bookCache.get(userId);

        if (!userBooks.containsKey(bookId)) {
            // У пользователя не найдена книга
            throw new NotFoundException();
        }

        return userBooks.get(bookId);
    }

    @Override
    public Book updateBook(String userId, String bookId, Book book) {
        if (!bookCache.containsKey(userId)) {
            // Пользователь не найден
            throw new NotFoundException();
        }

        Map<String, Book> userBooks = bookCache.get(userId);

        if (!userBooks.containsKey(bookId)) {
            // У пользователя не найдена книга
            throw new NotFoundException();
        }

        book.setId(bookId);
        userBooks.put(bookId, book);
        return book;
    }

    @Override
    public void deleteBook(String userId, String bookId) {
        if (!bookCache.containsKey(userId)) {
            // Пользователь не найден
            throw new NotFoundException();
        }

        Map<String, Book> userBooks = bookCache.get(userId);

        if (!userBooks.containsKey(bookId)) {
            // У пользователя не найдена книга
            throw new NotFoundException();
        }

        bookCache.remove(bookId);
    }

    @Override
    public Book createBook(String userId, Book book) {
        if (!bookCache.containsKey(userId)) {
            // Пользователь не найден
            throw new NotFoundException();
        }

        Map<String, Book> userBooks = bookCache.get(userId);

        // Плохой способ генерирования случайных идентификаторов, использовать только для примеров
        book.setId(String.valueOf(System.currentTimeMillis()));
        userBooks.put(book.getId(), book);
        return book;
    }

    @Override
    public Collection<Book> getAllBooks(String userId) {
        if (!bookCache.containsKey(userId)) {
            // Пользователь не найден
            throw new NotFoundException();
        }

        return bookCache.get(userId).values();
    }
}
