package temp;

import java.util.ArrayList;
import java.util.List;

public interface MainClass {
    static List<MainClass> list = new ArrayList<>();

    public static void add(MainClass mainClass) {
        synchronized (list) {
            list.add(mainClass);
        }
    }

    public static void print() {
        synchronized (list) {
            for (MainClass mainClass : list) {
                System.err.println(mainClass.name());
            }
        }
    }

    String name();

}
