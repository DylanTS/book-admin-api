package org.library.domain;

import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotBlank;

/**
 * A JPA {@link Entity} representation of a Book, which extends {@link AbstractPersistable} to
 * provide generic methods.
 * 
 * @author dylants
 * 
 */
@Entity
public class Book extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 9126262074862025864L;

    @NotBlank
    private String title;
    @NotBlank
    private String author;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author
     *            the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return String.format("Book with id: %s, title: %s, author: %s", getId(), title, author);
    }

}