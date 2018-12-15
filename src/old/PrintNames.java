package old;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import misc.FolderUtils;

public class PrintNames {

    final static String q = File.separator;

    public static void main(String[] args) throws IOException {

        int[] fid = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 23, 24, 25 };

        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all();
        for (int id : fid) {
            System.out.println(id + " " + all.get(id).getName());
        }
    }
}
