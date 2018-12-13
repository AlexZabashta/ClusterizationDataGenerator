package optimization;

import java.util.Random;

public interface Generator<T> {

    public abstract T generate(Random random);

}
