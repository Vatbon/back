package ftc.shift.sample.api;


import ftc.shift.secretsanta.models.Book;
import ftc.shift.secretsanta.services.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Все аннотации, начинающиеся с @Api нужны только для построения <a href="http://localhost:8081/swagger-ui.html#/">swagger-документации</a>
 * Их удаление ничего не сломает.
 */
@RestController
@Api(description = "Запросы для работы с книгами")
public class BooksController {

    private static final String BOOKS_PATH = "/api/v001/books";

    @Autowired
    private BookService service;

    @PostMapping(BOOKS_PATH)
    @ApiOperation(value = "Добавление новой книги")
    public ResponseEntity<Book> createBook(
            @ApiParam(value = "Идентификатор пользователя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Данные для новой книги (Название, автор, количество страниц, жанры)")
            @RequestBody Book book) {
        Book result = service.createBook(userId, book);
        return ResponseEntity.ok(result);
    }

    @GetMapping(BOOKS_PATH + "/{bookId}")
    @ApiOperation(value = "Получение книги с указанным идентификатором")
    public ResponseEntity<Book> readBook(
            @ApiParam(value = "Идентификатор пользователя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Идентификатор книги")
            @PathVariable String bookId) {
        Book book = service.provideBook(userId, bookId);
        return ResponseEntity.ok(book);
    }

    @PatchMapping(BOOKS_PATH + "/{bookId}")
    @ApiOperation(value = "Обновление существующей книги")
    public ResponseEntity<Book> updateBook(
            @ApiParam(value = "Идентификатор пользователя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Идентификатор книги, которую необходимо обновить")
            @PathVariable String bookId,
            @ApiParam(value = "Новые данные для книги (Название, автор, количество страниц, жанры)")
            @RequestBody Book book) {
        Book updatedBook = service.updateBook(userId, bookId, book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping(BOOKS_PATH + "/{bookId}")
    @ApiOperation(value = "Удаление существующей книги")
    public ResponseEntity<?> deleteBook(
            @ApiParam(value = "Идентификатор пользователя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Идентификатор книги, которую необходимо удалить")
            @PathVariable String bookId) {
        service.deleteBook(userId, bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(BOOKS_PATH)
    @ApiOperation(value = "Получение всех книг пользователя")
    public ResponseEntity<Collection<Book>> listBooks(
            @ApiParam(value = "Идентификатор пользователя")
            @RequestHeader("userId") String userId) {
        Collection<Book> books = service.provideBooks(userId);
        return ResponseEntity.ok(books);
    }
}