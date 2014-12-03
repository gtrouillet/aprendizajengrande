package net.aprendizajengrande.recommender;

import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.google.common.collect.Maps;

public class ItemSimilarityFactory {

    public enum SimilarityType {
        EUCLIDEAN("EUCLIDEAN"), FILE("FILE");

        private String value;

        SimilarityType(String value) {
            this.value = value;
        }

        private static final Map<String, SimilarityType> INSTANCES = Maps
                .newHashMap();
        static {
            for (SimilarityType type : values()) {
                INSTANCES.put(type.value, type);
            }
        }

        public static SimilarityType fromString(String name) {
            if (INSTANCES.containsKey(name.toUpperCase())) {
                return INSTANCES.get(name.toUpperCase());
            } else {
                throw new IllegalArgumentException("Invalid name: " + name);
            }
        }
    }

    Map<SimilarityType, ItemSimilarity> distances;

    public ItemSimilarityFactory(DataModel model, List<String> files)
            throws TasteException {
        distances = Maps.newHashMap();
        distances.put(SimilarityType.EUCLIDEAN,
                new EuclideanDistanceSimilarity(model));
        distances
                .put(SimilarityType.FILE, new FileItemSimilarity(model, files));
    }

    public ItemSimilarity getItemSimilarity(SimilarityType type) {
        if (distances.containsKey(type)) {
            return distances.get(type);
        } else {
            throw new IllegalStateException("Can not find distance: " + type);
        }
    }
}
