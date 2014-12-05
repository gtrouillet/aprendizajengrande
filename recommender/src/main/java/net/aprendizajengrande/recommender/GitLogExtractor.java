package net.aprendizajengrande.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import net.aprendizajengrande.recommender.db.LogDB;
import net.aprendizajengrande.recommender.model.Commit;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

/**
 * Extracts the necessary information from the Git Log
 * 
 * @author Germán E. Trouillet
 */
@Component("gitLogExtractor")
public class GitLogExtractor {

    private static final String COMMIT_LINE_PREFIX = "commit:";
    private static final String COMMIT_LINE_PATTERN = "commit: \"(.+)\", author: \"(.*)\"";
    private static final String FILE_LINE_PATTERN = "\\w\\s+(.+)";
    private static final String UNKNOWN_COMMIT_PATTER_MSG_TEMPLATE = "Unkown commit line pattern: %s";
    private static final String UNKNOWN_FILE_PATTER_MSG_TEMPLATE = "Unkown file line pattern: %s";
    private static final String NEW_COMMIT_MSG = "New Commit added: %s [%s]";
    private static final String UNKNOWN_USR_NAME = "Unknown";
    private static final String USR_FILE_NAME = "authors.txt";
    private static final String FILE_FILE_NAME = "files.txt";
    private static final String CC_FILE_NAME = "commit-counts.tsv";
    private static final String COUNTS_FILE_NAME = "counts.tsv";

    private ProcessFactory processFactory;

    private LogDB logDB;

    @Inject
    public GitLogExtractor(ProcessFactory processFactory, LogDB logDB) {
        this.processFactory = processFactory;
        this.logDB = logDB;
    }

    public void extractLogInformation(File gitRepoPath, String outPutDirectory)
            throws Exception {
        // Process Git log info and populate logDB
        extractLogInfo(gitRepoPath);

        System.out.println("Number of users: " + logDB.getUserCount());
        System.out.println("Number of files: " + logDB.getFileCount());
        System.out.println("Number of commits: " + logDB.getCommitCount());

        // Create output files with data of logDB
        exportData(outPutDirectory);
    }

    private void extractLogInfo(File gitRepoPath) {
        try {
            final Process p = processFactory.createProccess(gitRepoPath, "git",
                    "log", "--format=format:commit: \"%H\", author: \"%an\"",
                    "--name-status");
            BufferedReader processOutput = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            String line = null;
            Commit commitInfo = null;
            Pattern commitLine = Pattern.compile(COMMIT_LINE_PATTERN);
            Pattern fileLine = Pattern.compile(FILE_LINE_PATTERN);
            Integer count = 1;

            line = processOutput.readLine();
            while (line != null) {
                if (line.isEmpty()) {
                    logDB.addCommit(commitInfo);
                    System.out.println(String.format(NEW_COMMIT_MSG,
                            commitInfo.getHash(), count));
                    count++;
                } else if (line.startsWith(COMMIT_LINE_PREFIX)) {
                    Matcher m = commitLine.matcher(line);
                    if (m.matches()) {
                        String id = m.group(1);
                        String author = m.group(2);
                        commitInfo = new Commit(
                                id,
                                Strings.isNullOrEmpty(author) ? UNKNOWN_USR_NAME
                                        : author);
                    } else {
                        throw new IllegalStateException(String.format(
                                UNKNOWN_COMMIT_PATTER_MSG_TEMPLATE, line));
                    }
                } else {
                    Matcher m = fileLine.matcher(line);
                    if (m.matches()) {
                        String file = m.group(1);
                        commitInfo.addFile(file);
                    } else {
                        throw new IllegalStateException(String.format(
                                UNKNOWN_FILE_PATTER_MSG_TEMPLATE, line));
                    }
                }

                line = processOutput.readLine();
            }
            // If the last commit has files we add it to the list
            if (commitInfo.getFiles() != null
                    && !commitInfo.getFiles().isEmpty()) {
                logDB.addCommit(commitInfo);
                System.out.println(String.format(NEW_COMMIT_MSG,
                        commitInfo.getHash(), count));
            }
            p.waitFor();
            System.out.println("Done.");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected void exportData(String outPutDirectory) {
        // Creates the files, remove them if exist
        Path authors = Paths.get(outPutDirectory, USR_FILE_NAME);
        Path commitCounts = Paths.get(outPutDirectory, CC_FILE_NAME);
        Path files = Paths.get(outPutDirectory, FILE_FILE_NAME);
        Path counts = Paths.get(outPutDirectory, COUNTS_FILE_NAME);

        try {
            Files.deleteIfExists(authors);
            authors = Files.createFile(authors);

            Files.deleteIfExists(commitCounts);
            commitCounts = Files.createFile(commitCounts);

            Files.deleteIfExists(files);
            files = Files.createFile(files);

            Files.deleteIfExists(counts);
            counts = Files.createFile(counts);
        } catch (IOException ex) {
            System.out.println("Error creating file");
        }

        System.out.println("Exporting authors and commit-count ...");
        logDB.exportUserData(authors, commitCounts);
        System.out.println("Exporting files ...");
        logDB.exportFiles(files);
        System.out.println("Exporting counts ...");
        logDB.exportFileCommitCountPerUser(counts);
    }

    public void setProcessFactory(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }
}
