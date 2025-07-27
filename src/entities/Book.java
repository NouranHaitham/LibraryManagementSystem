package entities;

import services.Searchable;

import java.util.Objects;

public class Book implements Searchable {
    private final String id;
    private String title, author, genre;
    private int availableCopies = 0;

   public Book(String id,String title,String author,String genre,int availableCopies) throws Exception {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;

        if(availableCopies < 0) throw new Exception("Invalid availableCopies");
        else this.availableCopies = availableCopies;

    }

   public void increaseCopies()
    {
       this.availableCopies++;
    }

   public void decreaseCopies() throws Exception {
        if(availableCopies == 0) throw new Exception("No Copies to remove from.");
        else this.availableCopies--;
    }

    @Override
    public String getID() {
        return getId();
    }

    @Override
    public String getName() {
        return getTitle();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", availableCopies=" + availableCopies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(getId(), book.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) throws Exception {
        if(availableCopies < 0) throw new Exception("Invalid availableCopies");
        else this.availableCopies = availableCopies;
    }
}
