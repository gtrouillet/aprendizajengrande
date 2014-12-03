package net.aprendizajengrande.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.cf.taste.hadoop.item.RecommenderJob;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Recommend {

    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            System.err
                    .println("Usage: <db dir> <hdfs input file> <hdfs folder for output> <output file>"
                            + ". " + args.length);
            System.exit(1);
        }

        Configuration conf = new Configuration();

        File dbDir = new File(args[0]);
        String input = args[1];
        String outputName = args[2] + "/recos";
        File outputFile = new File(args[3]);
        Path output = new Path(outputName);
        Path actualOutput = new Path(outputName + "/part-r-00000");

        // compute recommendation in Hadoop
        ToolRunner.run(new Configuration(), new RecommenderJob(), new String[] {
                "--input", input, "--output", outputName,
                "--similarityClassname", "SIMILARITY_COSINE" });

        // read recommendations
        FSDataInputStream fsdis = output.getFileSystem(conf).open(actualOutput);
        BufferedReader br = new BufferedReader(new InputStreamReader(fsdis));
        String line = br.readLine();

        PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
        List<String> files = files(dbDir);
        List<String> authors = authors(dbDir);
        while (line != null) {
            String[] parts = line.split("\\s+");
            String author = authors.get(Integer.parseInt(parts[0])-1);
            parts = parts[1].substring(1, parts[1].length() - 1).split(",");
            for (String pair : parts) {
                String[] pairsPart = pair.split(":");
                pw.println(author + "\t"
                        + files.get(Integer.parseInt(pairsPart[0])-1) + "\t"
                        + pairsPart[1]);
            }
            line = br.readLine();
        }
        pw.close();
    }

    private static List<String> authors(File dbDir) throws IOException {
        List<String> authors = Files.readLines(new File(dbDir.getAbsolutePath()
                + "/" + "authors.txt"), Charsets.UTF_8);
        return authors;
    }

    private static List<String> files(File dbDir) throws IOException {
        List<String> files = Files.readLines(new File(dbDir.getAbsolutePath()
                + "/" + "files.txt"), Charsets.UTF_8);
        return files;
    }

}
