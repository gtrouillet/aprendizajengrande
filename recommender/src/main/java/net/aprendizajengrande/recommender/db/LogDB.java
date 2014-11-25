package net.aprendizajengrande.recommender.db;

import java.nio.file.Path;

import net.aprendizajengrande.recommender.model.Commit;

/**
 * Interface for the interaction method with the commit Log DB
 * 
 * @author Germán E. Trouillet
 */
public interface LogDB {

    /**
     * Add a new commit information
     * 
     * @param commit
     *            the {@link Commit} information
     */
    void addCommit(Commit commit);

    /**
     * Exports all the users
     * 
     * @param authors
     *            {@link Path} to the destination file for user list
     * @param commitCounts
     *            {@link Path} to the destination file for authors commit count
     */
    void exportUserData(Path authors, Path commitCounts);

    /**
     * Exports all the files
     * 
     * @param files
     *            {@link Path} to the destination file
     */
    void exportFiles(Path files);

    /**
     * Exports all the commits per file grouped by user
     * 
     * @param counts
     *            {@link Path} to the destination file
     */
    void exportFileCommitCountPerUser(Path counts);

    /**
     * Returns the amount of Users stored in the DB
     * 
     * @return the count of users
     */
    Long getUserCount();

    /**
     * Returns the amount of Files stored in the DB
     * 
     * @return the count of files
     */
    Long getFileCount();

    /**
     * Returns the amount of Commits stored in the DB
     * 
     * @return the count of commits
     */
    Long getCommitCount();

}
