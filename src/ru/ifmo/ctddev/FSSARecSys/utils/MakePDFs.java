package ru.ifmo.ctddev.FSSARecSys.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by sergey on 07.11.15.
 */
public class MakePDFs {

    public static String datasetName;

    public MakePDFs() {

    }

    public void setDataset(String datasetName) {
        this.datasetName = datasetName;
    }



    public void generateText(int val) throws IOException {
        String first = "%% LyX 2.1.4 created this file.  For more info, see http://www.lyx.org/.\n" +
                "%% Do not edit unless you really know what you are doing.\n" +
                "\\documentclass[20pt,english]{extarticle}\n" +
                "\\usepackage[T1]{fontenc}\n" +
                "\\usepackage[latin9]{inputenc}\n" +
                "\\usepackage[landscape,a0paper]{geometry}\n" +
                "\\geometry{verbose,tmargin=2cm,bmargin=2cm,lmargin=2cm,rmargin=2cm}\n" +
                "\\usepackage{graphicx}\n" +
                "\n" +
                "\\makeatletter\n" +
                "\n" +
                "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% LyX specific LaTeX commands.\n" +
                "%% Because html converters don't know tabularnewline\n" +
                "\\providecommand{\\tabularnewline}{\\\\}\n" +
                "%% A simple dot to overcome graphicx limitations\n" +
                "\\newcommand{\\lyxdot}{.}\n" +
                "\n" +
                "\n" +
                "\\makeatother\n" +
                "\n" +
                "\\usepackage{babel}\n" +
                "\\begin{document}\n\n";


        CharSequence c1 = "_";
        CharSequence c2 = "\\_";

        String dcopy = datasetName;
        dcopy.replace(c1, c2);

        String section = "\\section*{" + dcopy + ".arff}\n\n";

        String table = "\\begin{tabular}{|c|c|c|c|c|}\n" +
                "\\hline \n" +
                "\\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName + "}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/" + datasetName + "_K-means-1}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/" + datasetName + "_K-means-2}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/" + datasetName + "_K-means-3}" +
                " & \\includegraphics[bb=0bp 0bp 500bp 500bp]{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName + "_EM-1}\\tabularnewline\n" +
                "\\hline \n" +
                "Original & K-Means-1 & K-Means-2 & K-Means-3 & EM-1\\tabularnewline\n" +
                "\\hline \n" +
                "\\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName +"_EM-2}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName + "_EM-3}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName + "_FarthestFirst-1}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName + "_FarthestFirst-2}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName + "_FarthestFirst-3}\\tabularnewline\n" +
                "\\hline \n" +
                "EM-2 & EM-3 & FarthestFirst-1 & FarthestFirst-2 & FarthestFirst-3\\tabularnewline\n" +
                "\\hline \n" +
                "\\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/"+ datasetName +"_Hieracical}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/" + datasetName + "_DBSCAN}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/" + datasetName + "_X-Means-1}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/" + datasetName + "_X-Means-2}" +
                " & \\includegraphics{/home/sergey/anew_pictures/" + datasetName + "/" + datasetName + "_X-Means-3} \\tabularnewline\n" +
                "\\hline \n" +
                "Hieracical & DBSCAN & X-Means-1 & X-Means-2 & X-Means-3\\tabularnewline\n" +
                "\\hline \n" +
                "\\end{tabular}\n" +
                "\\end{document}";

        File f = new File("/home/sergey/masters/FSSARecSys/pdfs_real/" + String.format("%03d", val) + " - " + datasetName + ".tex");

        FileOutputStream fos = new FileOutputStream(f);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write(first);
        bw.write(section);
        bw.write(table);
        bw.close();
        fos.close();

    }



    public static void main(String [] args) throws Exception {
        // list_of_datasets

        List<String> listOfFiles = new ArrayList<>();


        HashMap<String, ArrayList<Integer>> clusterNums = new HashMap<>();
        Scanner input = new Scanner(new File("clusterDistr"));
        while (input.hasNext()){
            String nextLine = input.nextLine();
            String[] tokenize = nextLine.split(" ");
            ArrayList<Integer> tmp = new ArrayList<>();
            listOfFiles.add(tokenize[1]);
//            for(int i = 2; i < 12; i++){
//                tmp.add(Integer.parseInt(tokenize[i]));
//            }
//            //System.out.println(tokenize[0]);
//            clusterNums.put(tokenize[1], tmp);

        }
        input.close();

//        File fl = new File("list_of_datasets.txt");
//        FileInputStream fis = new FileInputStream(fl);
//
//        //Construct BufferedReader from InputStreamReader
//        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//
//        String line = null;
//        while ((line = br.readLine()) != null) {
//            listOfFiles.add(line);
//        }
//
//        br.close();
//        fis.close();

        MakePDFs mp = new MakePDFs();

        int i = 1;
        for (String s: listOfFiles) {
            mp.setDataset(s);
            mp.generateText(i);
            i++;
        }

    }
}
