package net.aprendizajengrande.recommender.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Commit {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "HASH")
    private String hash;

    @ManyToOne
    @JoinColumn(name = "ID")
    private User user;

    @ManyToMany
    @JoinTable(name = "COMMIT_FILES",
               joinColumns = { @JoinColumn(name = "COMMIT_ID", referencedColumnName = "ID") },
               inverseJoinColumns = { @JoinColumn(name = "FILE_ID", referencedColumnName = "ID") })
    private List<File> files;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

}
