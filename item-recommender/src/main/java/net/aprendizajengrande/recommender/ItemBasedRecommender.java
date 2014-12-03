package net.aprendizajengrande.recommender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.aprendizajengrande.recommender.ItemSimilarityFactory.SimilarityType;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ItemBasedRecommender {

    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            System.err
                    .println("Usage: <db dir> <preference file> <output file> <Similarity = (File, Euclidean).");
            System.exit(1);
        }

        File dbDir = new File(args[0]);
        File input = new File(args[1]);
        File output = new File(args[2]);
        SimilarityType type = SimilarityType.fromString(args[3]);

        // Compute recommendations with Mahout
        DataModel model = new FileDataModel(input);
        List<String> files = files(dbDir);
        List<String> authors = authors(dbDir);
        ItemSimilarityFactory similarityFactory = new ItemSimilarityFactory(
                model, files);

        ItemSimilarity similarity = similarityFactory.getItemSimilarity(type);
        Recommender recommender = new GenericItemBasedRecommender(model,
                similarity);

        // Generate recommendations
        System.out.println("Generate recommendations");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            for (int i = 1; i <= authors.size(); i++) {
                List<RecommendedItem> userRecommendation = recommender
                        .recommend(i, 5);
                String userName = authors.get(i - 1);
                System.out.println("\t Recommendation for user: [" + i + "] - "
                        + userName);
                for (RecommendedItem item : userRecommendation) {
                    String fileName = files.get(Long.valueOf(item.getItemID())
                            .intValue() - 1);
                    writer.append(userName).append(" ").append(fileName)
                            .append(" ")
                            .append(String.valueOf(item.getValue()));
                    writer.newLine();
                }
            }
        }
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
