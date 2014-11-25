package net.aprendizajengrande.recommender.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(indexes = { @Index(columnList = "PATH") })
@Entity
@NamedQueries({ @NamedQuery(name = "files.count", query = "SELECT COUNT(f) FROM File f") })
public class File {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "PATH")
    private String path;

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

}
