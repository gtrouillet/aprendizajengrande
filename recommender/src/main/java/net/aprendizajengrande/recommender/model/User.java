package net.aprendizajengrande.recommender.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Class that represents the User of the commit
 * 
 * @author Germán E. Trouillet
 */
@Table(indexes = { @Index(columnList = "NAME") })
@Entity
@NamedQueries({ @NamedQuery(name = "users.count", query = "SELECT COUNT(u) FROM User u") })
public class User {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Commit> commits;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

}
