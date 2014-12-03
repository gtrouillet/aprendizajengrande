package net.aprendizajengrande.recommender;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Creates the Preference file from the db information, and copy the result to a
 * HDFS folder
 * 
 * @author Germán E. Trouillet
 */
public class PreferenceBuilder {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: <db dir> <hdfs folder for input>" + ". "
                    + args.length);
            System.exit(1);
        }

        Configuration conf = new Configuration();

        File dbDir = new File(args[0]);

        String inputName = args[1] + "/ratings";

        Path input = new Path(inputName);

        // populate ratings file
        FSDataOutputStream fsdos = input.getFileSystem(conf).create(input);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(fsdos));

        // compute affinity for files as % of commits that touch that file
        int[] authorCommitCounts = commitsPerAuthor(dbDir);
        Map<Integer, Integer> counts[] = counts(authorCommitCounts.length,
                dbDir);

        for (int author = 0; author < authorCommitCounts.length; author++) {
            for (Map.Entry<Integer, Integer> c : counts[author].entrySet()) {
                pw.println(author + 1
                        + ","
                        + c.getKey()
                        + ","
                        + ((c.getValue() / (authorCommitCounts[author] * 1.0)) * 10000.0));
            }
        }
        pw.close();
    }

    private static Map<Integer, Integer>[] counts(int authors, File dbDir)
            throws IOException {
        List<String> countsFile = Files.readLines(
                new File(dbDir.getAbsolutePath() + "/" + "counts.tsv"),
                Charsets.UTF_8);

        @SuppressWarnings("unchecked")
        Map<Integer, Integer>[] counts = new Map[authors];
        Map<Integer, Integer> current = null;
        for (String line : countsFile) {
            String[] parts = line.split("\t");
            if (parts.length == 3)
                current.put(Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            else {
                int currentId = Integer.parseInt(parts[0]) - 1;
                current = new HashMap<>();
                counts[currentId] = current;
            }
        }
        return counts;
    }

    private static int[] commitsPerAuthor(File dbDir) throws IOException {
        List<String> counts = Files.readLines(new File(dbDir.getAbsolutePath()
                + "/" + "commit-counts.tsv"), Charsets.UTF_8);
        int[] result = new int[counts.size()];
        int i = 0;
        for (String line : counts) {
            result[i] = Integer.valueOf(line.split("\t")[1]);
            i++;
        }
        return result;
    }

}
