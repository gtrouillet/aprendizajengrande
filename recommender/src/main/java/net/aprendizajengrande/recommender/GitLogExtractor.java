package net.aprendizajengrande.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

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

    @Inject
    private ProcessFactory processFactory;

    public GitLogExtractor(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

    public void extractLogInformation(File gitRepoPath) throws Exception {
        List<CommitInfo> commits = extractLogInfo(gitRepoPath);

        System.out.println(commits.size());
    }

    private List<CommitInfo> extractLogInfo(File gitRepoPath) {
        try {
            final Process p = processFactory.createProccess(gitRepoPath, "git",
                    "log", "--format=format:commit: \"%H\", author: \"%an\"",
                    "--name-status", "--no-merges");
            BufferedReader processOutput = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            List<CommitInfo> commits = new LinkedList<CommitInfo>();

            String line = null;
            CommitInfo commitInfo = null;
            Pattern commitLine = Pattern.compile(COMMIT_LINE_PATTERN);
            Pattern fileLine = Pattern.compile(FILE_LINE_PATTERN);

            while ((line = processOutput.readLine()) != null) {
                if (line.isEmpty()) {
                    commits.add(commitInfo);
                } else if (line.startsWith(COMMIT_LINE_PREFIX)) {
                    Matcher m = commitLine.matcher(line);
                    if (m.matches()) {
                        String id = m.group(1);
                        String author = m.group(2);
                        commitInfo = new CommitInfo(id, author);
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
            }
            // If the last commit has files we add it to the list
            if (commitInfo.getFiles() != null
                    && !commitInfo.getFiles().isEmpty()) {
                commits.add(commitInfo);
            }
            p.waitFor();
            System.out.println("Done.");
            return commits;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public void setProcessFactory(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

    /**
     * Class to store information about a commit
     * 
     * @author Germán E. Trouillet
     */
    private static class CommitInfo {
        private String id;
        private String author;
        private List<String> files;

        public CommitInfo(String id, String name) {
            this.id = id;
            this.author = name;
        }

        public void addFile(String file) {
            getFiles().add(file);
        }

        public List<String> getFiles() {
            if (files == null) {
                files = new LinkedList<String>();
            }
            return files;
        }

        public String toString() {
            return String
                    .format("CommitInfo[id = %s, author = %s]", id, author);
        }
    }
}
