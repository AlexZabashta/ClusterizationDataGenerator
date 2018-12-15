package ru.ifmo.ctddev.FSSARecSys.db.internal;

import ru.ifmo.ctddev.FSSARecSys.calculators.FSSAlgorithm;
import ru.ifmo.ctddev.FSSARecSys.calculators.internal.FSSAlgorithmImpl;
import ru.ifmo.ctddev.FSSARecSys.db.FSSAlgorithmManager;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by warrior on 04.03.15.
 */
public class SimpleFSSAlgorithmManager implements FSSAlgorithmManager {

    public static final String CFS_SBS = "CFS_SBS";
    public static final String CFS_SFS = "CFS_SFS";
    public static final String CFS_BIS = "CFS_BiS";
    public static final String CFS_GS = "CFS_GS";
    public static final String CFS_LS = "CFS_LS";
    public static final String CFS_RS = "CFS_RS";
    public static final String CFS_SS = "CFS_SS";
    public static final String CFS_SWS = "CFS_SWS";
    public static final String CONS_SBS = "Cons_SBS";
    public static final String CONS_SFS = "Cons_SFS";
    public static final String CONS_BIS = "Cons_BiS";
    public static final String CONS_GS = "Cons_GS";
    public static final String CONS_LS = "Cons_LS";
    public static final String CONS_RS = "Cons_RS";
    public static final String CONS_SS = "Cons_SS";
    public static final String CONS_SWS = "Cons_SWS";
    public static final String RELIEF_F = "Relief_F";


    private static final String CFS_SBS_SEARCH_CLASS = "weka.attributeSelection.BestFirst";
    private static final String[] CFS_SBS_SEARCH_OPTIONS = {"-D", "0"};
    private static final String CFS_SBS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_SBS_EVALUATION_OPTIONS = {};

    private static final String CFS_SFS_SEARCH_CLASS = "weka.attributeSelection.BestFirst";
    private static final String[] CFS_SFS_SEARCH_OPTIONS = {"-D", "1"};
    private static final String CFS_SFS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_SFS_EVALUATION_OPTIONS = {};

    private static final String CFS_BIS_SEARCH_CLASS = "weka.attributeSelection.BestFirst";
    private static final String[] CFS_BIS_SEARCH_OPTIONS = {"-D", "2"};
    private static final String CFS_BIS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_BIS_EVALUATION_OPTIONS = {};

    private static final String CFS_GS_SEARCH_CLASS = "weka.attributeSelection.GeneticSearch";
    private static final String[] CFS_GS_SEARCH_OPTIONS = {};
    private static final String CFS_GS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_GS_EVALUATION_OPTIONS = {};

    private static final String CFS_LS_SEARCH_CLASS = "weka.attributeSelection.LinearForwardSelection";
    private static final String[] CFS_LS_SEARCH_OPTIONS = {};
    private static final String CFS_LS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_LS_EVALUATION_OPTIONS = {};

    private static final String CFS_RS_SEARCH_CLASS = "weka.attributeSelection.RankSearch";
    private static final String[] CFS_RS_SEARCH_OPTIONS = {};
    private static final String CFS_RS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_RS_EVALUATION_OPTIONS = {};

    private static final String CFS_SS_SEARCH_CLASS = "weka.attributeSelection.ScatterSearchV1";
    private static final String[] CFS_SS_SEARCH_OPTIONS = {};
    private static final String CFS_SS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_SS_EVALUATION_OPTIONS = {};

    private static final String CFS_SWS_SEARCH_CLASS = "weka.attributeSelection.GreedyStepwise";
    private static final String[] CFS_SWS_SEARCH_OPTIONS = {};
    private static final String CFS_SWS_EVALUATION_CLASS = "weka.attributeSelection.CfsSubsetEval";
    private static final String[] CFS_SWS_EVALUATION_OPTIONS = {};

    private static final String CONS_SBS_SEARCH_CLASS = "weka.attributeSelection.BestFirst";
    private static final String[] CONS_SBS_SEARCH_OPTIONS = {"-D", "0"};
    private static final String CONS_SBS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_SBS_EVALUATION_OPTIONS = {};

    private static final String CONS_SFS_SEARCH_CLASS = "weka.attributeSelection.BestFirst";
    private static final String[] CONS_SFS_SEARCH_OPTIONS = {"-D", "1"};
    private static final String CONS_SFS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_SFS_EVALUATION_OPTIONS = {};

    private static final String CONS_BIS_SEARCH_CLASS = "weka.attributeSelection.BestFirst";
    private static final String[] CONS_BIS_SEARCH_OPTIONS = {"-D", "2"};
    private static final String CONS_BIS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_BIS_EVALUATION_OPTIONS = {};

    private static final String CONS_GS_SEARCH_CLASS = "weka.attributeSelection.GeneticSearch";
    private static final String[] CONS_GS_SEARCH_OPTIONS = {};
    private static final String CONS_GS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_GS_EVALUATION_OPTIONS = {};

    private static final String CONS_LS_SEARCH_CLASS = "weka.attributeSelection.LinearForwardSelection";
    private static final String[] CONS_LS_SEARCH_OPTIONS = {};
    private static final String CONS_LS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_LS_EVALUATION_OPTIONS = {};

    private static final String CONS_RS_SEARCH_CLASS = "weka.attributeSelection.RankSearch";
    private static final String[] CONS_RS_SEARCH_OPTIONS = {};
    private static final String CONS_RS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_RS_EVALUATION_OPTIONS = {};

    private static final String CONS_SS_SEARCH_CLASS = "weka.attributeSelection.ScatterSearchV1";
    private static final String[] CONS_SS_SEARCH_OPTIONS = {};
    private static final String CONS_SS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_SS_EVALUATION_OPTIONS = {};

    private static final String CONS_SWS_SEARCH_CLASS = "weka.attributeSelection.GreedyStepwise";
    private static final String[] CONS_SWS_SEARCH_OPTIONS = {};
    private static final String CONS_SWS_EVALUATION_CLASS = "weka.attributeSelection.ConsistencySubsetEval";
    private static final String[] CONS_SWS_EVALUATION_OPTIONS = {};

    private static final String RELIEF_F_SEARCH_CLASS = "weka.attributeSelection.Ranker";
    private static final String[] RELIEF_F_SEARCH_OPTIONS = {"-T", "0.01"};
    private static final String RELIEF_F_EVALUATION_CLASS = "weka.attributeSelection.ReliefFAttributeEval";
    private static final String[] RELIEF_F_EVALUATION_OPTIONS = {};

    private static SimpleFSSAlgorithmManager simpleFSSAlgorithmManager;

    private final List<String> algorithmNames = new ArrayList<>();
    private final Map<String, FSSAlgorithm> nameToAlgorithm = new HashMap<>();

    private SimpleFSSAlgorithmManager() {
        register(CFS_SBS, CFS_SBS_SEARCH_CLASS, CFS_SBS_SEARCH_OPTIONS, CFS_SBS_EVALUATION_CLASS, CFS_SBS_EVALUATION_OPTIONS);
        register(CFS_SFS, CFS_SFS_SEARCH_CLASS, CFS_SFS_SEARCH_OPTIONS, CFS_SFS_EVALUATION_CLASS, CFS_SFS_EVALUATION_OPTIONS);
        register(CFS_BIS, CFS_BIS_SEARCH_CLASS, CFS_BIS_SEARCH_OPTIONS, CFS_BIS_EVALUATION_CLASS, CFS_BIS_EVALUATION_OPTIONS);
        register(CFS_GS, CFS_GS_SEARCH_CLASS, CFS_GS_SEARCH_OPTIONS, CFS_GS_EVALUATION_CLASS, CFS_GS_EVALUATION_OPTIONS);
        register(CFS_LS, CFS_LS_SEARCH_CLASS, CFS_LS_SEARCH_OPTIONS, CFS_LS_EVALUATION_CLASS, CFS_LS_EVALUATION_OPTIONS);
        register(CFS_RS, CFS_RS_SEARCH_CLASS, CFS_RS_SEARCH_OPTIONS, CFS_RS_EVALUATION_CLASS, CFS_RS_EVALUATION_OPTIONS);
        register(CFS_SS, CFS_SS_SEARCH_CLASS, CFS_SS_SEARCH_OPTIONS, CFS_SS_EVALUATION_CLASS, CFS_SS_EVALUATION_OPTIONS);
        register(CFS_SWS, CFS_SWS_SEARCH_CLASS, CFS_SWS_SEARCH_OPTIONS, CFS_SWS_EVALUATION_CLASS, CFS_SWS_EVALUATION_OPTIONS);
        register(CONS_SBS, CONS_SBS_SEARCH_CLASS, CONS_SBS_SEARCH_OPTIONS, CONS_SBS_EVALUATION_CLASS, CONS_SBS_EVALUATION_OPTIONS);
        register(CONS_SFS, CONS_SFS_SEARCH_CLASS, CONS_SFS_SEARCH_OPTIONS, CONS_SFS_EVALUATION_CLASS, CONS_SFS_EVALUATION_OPTIONS);
        register(CONS_BIS, CONS_BIS_SEARCH_CLASS, CONS_BIS_SEARCH_OPTIONS, CONS_BIS_EVALUATION_CLASS, CONS_BIS_EVALUATION_OPTIONS);
        register(CONS_GS, CONS_GS_SEARCH_CLASS, CONS_GS_SEARCH_OPTIONS, CONS_GS_EVALUATION_CLASS, CONS_GS_EVALUATION_OPTIONS);
        register(CONS_LS, CONS_LS_SEARCH_CLASS, CONS_LS_SEARCH_OPTIONS, CONS_LS_EVALUATION_CLASS, CONS_LS_EVALUATION_OPTIONS);
        register(CONS_RS, CONS_RS_SEARCH_CLASS, CONS_RS_SEARCH_OPTIONS, CONS_RS_EVALUATION_CLASS, CONS_RS_EVALUATION_OPTIONS);
        register(CONS_SS, CONS_SS_SEARCH_CLASS, CONS_SS_SEARCH_OPTIONS, CONS_SS_EVALUATION_CLASS, CONS_SS_EVALUATION_OPTIONS);
        register(CONS_SWS, CONS_SWS_SEARCH_CLASS, CONS_SWS_SEARCH_OPTIONS, CONS_SWS_EVALUATION_CLASS, CONS_SWS_EVALUATION_OPTIONS);
        register(RELIEF_F, RELIEF_F_SEARCH_CLASS, RELIEF_F_SEARCH_OPTIONS, RELIEF_F_EVALUATION_CLASS, RELIEF_F_EVALUATION_OPTIONS);    }

    @Override
    public List<String> getAvailableFSSAlgorithms() {
        return algorithmNames;
    }

    @Override
    public FSSAlgorithm get(String name) {
        return nameToAlgorithm.get(name);
    }

    @Override
    public boolean register(String name, String searchClassName, String[] searchOptions, String evaluationClassName, String[] evaluationOptions) {
        if (!algorithmNames.contains(name)) {
            try {
                ASSearch search = ASSearch.forName(searchClassName, searchOptions);
                ASEvaluation evaluation = ASEvaluation.forName(evaluationClassName, evaluationOptions);
                FSSAlgorithm fssAlgorithm = new FSSAlgorithmImpl(name, search, evaluation);
                algorithmNames.add(name);
                nameToAlgorithm.put(name, fssAlgorithm);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean register(String name, String searchClassName, String[] searchOptions, String evaluationClassName, String[] evaluationOptions, File jarFile) {
        return false;
    }

    public static SimpleFSSAlgorithmManager getInstance() {
        if (simpleFSSAlgorithmManager == null) {
            simpleFSSAlgorithmManager = new SimpleFSSAlgorithmManager();
        }
        return simpleFSSAlgorithmManager;
    }
}
