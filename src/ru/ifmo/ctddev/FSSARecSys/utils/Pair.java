package ru.ifmo.ctddev.FSSARecSys.utils;

import java.util.Comparator;

public class Pair<F, S> implements Comparable<Pair>, Comparator<Pair> {

    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }


    @Override
    public int compare(Pair o1, Pair o2) {
        if ((Double)o1.first < (Double)o2.first)
            return 1;
        if ((Double)o1.first == (Double)o2.first){
            if ((Integer)o1.second < (Integer)o2.second)
                return 1;
            if ((Integer)o1.second == (Integer)o2.second)
                return 0;
            else return -1;
        } else {
            return -1;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair pair = (Pair) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    @Override
    public int compareTo(Pair o) {
        return 0;
    }
}
