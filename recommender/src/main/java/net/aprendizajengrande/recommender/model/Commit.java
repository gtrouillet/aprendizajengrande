package net.aprendizajengrande.recommender.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.google.common.collect.Lists;

@Table(indexes = { @Index(columnList = "HASH") })
@Entity
@NamedQueries({ @NamedQuery(name = "commits.count", query = "SELECT COUNT(c) FROM Commit c") })
public class Commit {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "HASH")
    private String hash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER")
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "COMMIT_FILES", joinColumns = { @JoinColumn(name = "COMMIT_ID", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "FILE_ID", referencedColumnName = "ID") })
    private List<File> files;

    public Commit() {
        super();
    }

    public Commit(String hash, String userName) {
        super();
        this.hash = hash;
        this.user = new User();
        this.user.setName(userName);
        this.files = Lists.newArrayList();
    }

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

    public void addFile(String path) {
        File f = new File();
        f.setPath(path);
        getFiles().add(f);
    }
}
