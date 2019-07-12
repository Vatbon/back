package ftc.shift.secretsanta.models;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class Book {
  @ApiModelProperty(value = "Уникальный идентификатор книги", required = true)
  private String id;

  @ApiModelProperty(value = "Название книги", required = true)
  private String name;

  @ApiModelProperty(value = "Автор", required = true)
  private String author;

  @ApiModelProperty(value = "Количество страниц", required = true)
  private Integer pages;

  @ApiModelProperty(value = "Список жанров", required = true)
  private List<String> genre;

  public Book() {
  }

  public Book(String id, String name, String author, Integer pages, List<String> genre) {
    this.id = id;
    this.name = name;
    this.author = author;
    this.pages = pages;
    this.genre = genre;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Integer getPages() {
    return pages;
  }

  public void setPages(Integer pages) {
    this.pages = pages;
  }

  public List<String> getGenre() {
    return genre;
  }

  public void setGenre(List<String> genre) {
    this.genre = genre;
  }
}
