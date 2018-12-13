package temp;

public class Impl2 implements MainClass {

    static int b = 4;

    static {
        MainClass.add(new Impl2());
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

}
