package net.aprendizajengrande.recommender.db;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import net.aprendizajengrande.recommender.model.Commit;
import net.aprendizajengrande.recommender.model.File;
import net.aprendizajengrande.recommender.model.User;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Repository("logDB")
public class LogDBJPA implements LogDB {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void addCommit(Commit commit) {
        checkNotNull(commit, "Commit object cannot be null");
        checkNotNull(commit.getUser(), "Commit's user canot be null");
        checkArgument(!Strings.isNullOrEmpty(commit.getUser().getName()),
                "User name canot be null");
        checkArgument(!Strings.isNullOrEmpty(commit.getHash()),
                "Commit Hash cannot be null");

        createCommit(commit);
    }

    @Transactional
    public void exportUserData(Path authors, Path commitCounts) {
        TypedQuery<User> q = entityManager.createQuery(
                "SELECT u FROM User u ORDER BY u.id", User.class);
        List<User> users = q.getResultList();

        // Write content in files
        try (BufferedWriter userWriter = Files.newBufferedWriter(authors,
                Charset.defaultCharset());
                BufferedWriter ccWriter = Files.newBufferedWriter(commitCounts,
                        Charset.defaultCharset())) {

            for (User u : users) {
                userWriter.append(u.getName());
                userWriter.newLine();
                ccWriter.append(new StringBuilder()
                        .append(String.valueOf(u.getId()))
                        .append("\t")
                        .append(String.valueOf(u.getCommits().size())
                                .toString()));
                ccWriter.newLine();
            }
            userWriter.flush();
            ccWriter.flush();
        } catch (IOException exception) {
            System.out.println("Error writing to files");
            throw new IllegalStateException("Error creating files");
        }
    }

    @Override
    @Transactional
    public void exportFiles(Path files) {
        TypedQuery<File> q = entityManager.createQuery(
                "SELECT f FROM File f ORDER BY f.id", File.class);
        List<File> fileList = q.getResultList();

        // Write content in file
        try (BufferedWriter fileWriter = Files.newBufferedWriter(files,
                Charset.defaultCharset())) {

            for (File f : fileList) {
                fileWriter.append(f.getPath());
                fileWriter.newLine();
            }
            fileWriter.flush();
        } catch (IOException exception) {
            System.out.println("Error writing to files");
            throw new IllegalStateException("Error creating file");
        }
    }

    @Override
    @Transactional
    public void exportFileCommitCountPerUser(Path counts) {
        TypedQuery<User> q = entityManager.createQuery(
                "SELECT u FROM User u ORDER BY u.id", User.class);
        List<User> users = q.getResultList();
        Map<Long, Map<Long, Counter>> fileCount = new HashMap<Long, Map<Long, Counter>>();
        for (User u : users) {
            long userId = u.getId();
            fileCount.put(userId, new HashMap<Long, Counter>());
            for (Commit c : u.getCommits()) {
                for (File f : c.getFiles()) {
                    long fileId = f.getId();
                    if (fileCount.get(userId).containsKey(fileId)) {
                        fileCount.get(userId).get(fileId).inc();
                    } else {
                        fileCount.get(userId).put(fileId, new Counter());
                    }
                }
            }
        }

        // Write content in file
        try (BufferedWriter countsWriter = Files.newBufferedWriter(counts,
                Charset.defaultCharset())) {
            for (User u : users) {
                countsWriter.append(String.valueOf(u.getId()));
                countsWriter.newLine();
                for (Long f : fileCount.get(u.getId()).keySet()) {
                    countsWriter
                            .append(new StringBuilder().append("\t").append(
                                    String.valueOf(f)))
                            .append("\t")
                            .append(String.valueOf(fileCount.get(u.getId())
                                    .get(f).getValue()));
                    countsWriter.newLine();
                }
            }
            countsWriter.flush();
        } catch (IOException exception) {
            System.out.println("Error writing to file");
            throw new IllegalStateException("Error creating file");
        }
    }

    private void createCommit(Commit commit) {
        User user = getUserByName(commit.getUser());
        List<File> files = getFiles(commit.getFiles());

        commit.setUser(user);
        commit.setFiles(files);

        entityManager.persist(commit);
    }

    private User getUserByName(User user) {
        User result = user;

        TypedQuery<User> q = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.name = :name", User.class);
        q.setParameter("name", user.getName());
        List<User> results = q.getResultList();

        if (results != null && !results.isEmpty()) {
            result = results.get(0);
        }

        return result;
    }

    private List<File> getFiles(List<File> files) {
        List<File> results = Lists.newArrayList();
        for (File f : files) {
            results.add(getFile(f));
        }
        return results;
    }

    private File getFile(File file) {
        File result = file;

        TypedQuery<File> q = entityManager.createQuery(
                "SELECT f FROM File f WHERE f.path = :path", File.class);
        q.setParameter("path", file.getPath());
        List<File> results = q.getResultList();

        if (results != null && !results.isEmpty()) {
            result = results.get(0);
        }

        return result;
    }

    /**
     * Sets the {@link EntityManager}
     * 
     * @param entityManager
     *            the {@link EntityManager} to set
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Long getUserCount() {
        return (Long) entityManager.createNamedQuery("users.count")
                .getSingleResult();
    }

    public Long getFileCount() {
        return (Long) entityManager.createNamedQuery("files.count")
                .getSingleResult();
    }

    public Long getCommitCount() {
        return (Long) entityManager.createNamedQuery("commits.count")
                .getSingleResult();
    }

    public static class Counter {
        private int value;

        public Counter() {
            value = 1;
        }

        public void inc() {
            value++;
        }

        public int getValue() {
            return value;
        }
    }
}
