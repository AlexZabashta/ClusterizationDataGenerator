package optimization;

import java.util.List;
import java.util.Random;

public interface Mutation<T> {

    public abstract List<T> mutate(T source, Random random);

}
