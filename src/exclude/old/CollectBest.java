package exclude.old;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class CollectBest {

    static String srcA = "result\\dsgenerators.RunMultCritExp\\1498841833828\\";
    static String srcB = "result\\dsgenerators.RunMultCritExp\\1499696328585\\";
    static String dst = "result\\dsgenerators.RunMultCritExp\\best\\";

    public static void main(String[] args) throws IOException {

        for (File file : new File(srcB).listFiles()) {
            String name = file.getName();

            if (name.contains(".arff.txt")) {
                continue;
            }

            String srcC = null;
            double best = Double.POSITIVE_INFINITY;

            try (BufferedReader reader = new BufferedReader(new FileReader(srcA + name))) {
                String line = reader.readLine();
                double val = Double.parseDouble(line.substring(2));

                if (val < best) {
                    best = val;
                    srcC = srcA;
                }
            } catch (Exception e) {
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(srcB + name))) {
                String line = reader.readLine();
                double val = Double.parseDouble(line.substring(2));

                if (val < best) {
                    best = val;
                    srcC = srcB;
                }
            } catch (Exception e) {
            }

            if (srcC == null) {
                continue;
            }

            Files.copy(new File(srcC + name).toPath(), new File(dst + name).toPath());

            System.out.println(srcC + name);
        }

    }

}
