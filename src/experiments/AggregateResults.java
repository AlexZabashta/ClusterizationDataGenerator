package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.MoreObjects;

public class AggregateResults {

    public Integer notNull(Integer value) {
        return MoreObjects.firstNonNull(value, Integer.valueOf(0));
    }

    public static void main(String[] args) throws IOException {
        class Result implements Comparable<Result> {
            public final String opt, prob, data, opt_prob;
            public final double value;

            public Result(String opt, String prob, String data, double value) {
                this.opt = opt;
                this.prob = prob;
                this.data = data;
                this.value = value;
                this.opt_prob = opt + "_" + prob;
            }

            @Override
            public int compareTo(Result r) {
                return opt_prob.compareTo(r.opt_prob);
            }
        }

        String folder = "result\\experiments.RunExp\\1545332320227";

        List<Result> results = new ArrayList<>();
        Set<String> opts = new TreeSet<>();
        Set<String> probs = new TreeSet<>();
        Set<String> datas = new TreeSet<>();

        for (File file : new File(folder).listFiles()) {
            String[] name = file.getName().split("_");
            if (name.length == 3) {
                double value;
                String opt = name[0];
                String prob = name[1];
                String data = name[2];

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    value = Double.parseDouble(reader.readLine().substring(2));
                }

                Result result = new Result(opt, prob, data, value);

                results.add(result);
                opts.add(opt);
                probs.add(prob);
                datas.add(data);
            }
        }

        Collections.sort(results);

        int n = results.size();

        Map<String, String> table = new HashMap<>();

        int l = 0;
        while (l < n) {
            Result result = results.get(l);

            int r = l;
            while (r < n && result.compareTo(results.get(r)) == 0) {
                ++r;
            }

            double avg = 0;

            for (int i = l; i < r; i++) {
                avg += results.get(i).value;
            }

            table.put(result.opt_prob, String.format(Locale.ENGLISH, "%.4f", avg / (r - l)));

            l = r;
        }

        try (PrintWriter writer = new PrintWriter("results.tex")) {

            for (String prob : probs) {
                writer.print("  &  ");
                writer.printf("%10s", prob);
            }
            for (String opt : opts) {
                writer.println();
                writer.printf("%10s", opt);
                for (String prob : probs) {
                    String value = table.get(opt + "_" + prob);
                    writer.print("  &  ");
                    writer.printf("%10s", value);
                }
                writer.println();
            }
        }

    }
}
