package net.aprendizajengrande.recommender;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.AbstractItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;

public class FileItemSimilarity extends AbstractItemSimilarity {

    private List<String> files;

    public FileItemSimilarity(DataModel model, List<String> files) {
        super(model);
        this.files = files;
    }

    @Override
    public double itemSimilarity(long itemID1, long itemID2)
            throws TasteException {
        return doItemSimilarity(itemID1, itemID2);
    }

    @Override
    public double[] itemSimilarities(long itemID1, long[] itemID2s)
            throws TasteException {
        int length = itemID2s.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = doItemSimilarity(itemID1, itemID2s[i]);
        }
        return result;
    }

    private double doItemSimilarity(long itemID1, long itemID2)
            throws TasteException {
        Path file1 = Paths.get(files.get(Long.valueOf(itemID1).intValue() - 1));
        String parent1[] = null;
        if (file1.getParent() != null) {
            parent1 = file1.getParent().toString().split("/");
        }
        String name1 = file1.getFileName().toString();

        Path file2 = Paths.get(files.get(Long.valueOf(itemID2).intValue() - 1));
        String parent2[] = null;
        if (file2.getParent() != null) {
            parent2 = file2.getParent().toString().split("/");
        }
        String name2 = file2.getFileName().toString();

        double parentDiff = 0.0;
        if (parent1 != null && parent2 != null) {
            double min = Math.min(parent1.length, parent2.length);
            double max = Math.max(parent1.length, parent2.length);

            int i = 0;
            while (i < min && parent1[i].equals(parent2[i])) {
                i++;
            }

            parentDiff = 1.0 - ((max - i) / max);
        }

        double nameDiff = 0.0;
        if (name1 != null && name2 != null) {
            int max = Math.max(name1.length(), name2.length());
            double levDistance = StringUtils.getLevenshteinDistance(name1,
                    name2);
            nameDiff = 1.0 - (levDistance / max);
        }

        return -1.0 + parentDiff + nameDiff;
    }
}
