package ru.ifmo.ctddev.FSSARecSys.db.internal;

/**
 * Created by sergey on 28.03.15.
 */
public class FSSAlgorithm {
    String fssAlgoName;
    String searchClass;
    String evalClass;
    String searchOptions;
    String evalOptions;

    public FSSAlgorithm (String fssAlgoName, String searchClass, String evalClass,
                         String searchOptions, String evalOptions) {
        this.fssAlgoName = fssAlgoName;
        this.searchClass = searchClass;
        this.evalClass = evalClass;
        this.searchOptions = searchOptions;
        this.evalOptions = evalOptions;
    }

    public String getFssAlgoName() {
        return fssAlgoName;
    }

    public String getSearchClass() {
        return searchClass;
    }

    public String getEvalClass() {
        return evalClass;
    }

    public String getEvalOptions() {
        return evalOptions;
    }

    public String getSearchOptions() {
        return searchOptions;
    }
}
