package net.aprendizajengrande.recommender.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class File {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "PATH")
    private String path;

    @Column(name = "NAME")
    private long name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getName() {
        return name;
    }

    public void setName(long name) {
        this.name = name;
    }

}
