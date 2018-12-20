package exclude.temp;

public class Impl1 implements MainClass {

    static int a = 3;

    static {
        MainClass.add(new Impl1());
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

}
