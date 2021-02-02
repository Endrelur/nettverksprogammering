package testing.threads;

public class Main {
    public static void main(String[] args) {
        final int ANTALL = 100;
        Navnprinter printer1 = new Navnprinter("Endré");
        Navnprinter printer2 = new Navnprinter("André");
        printer1.start();
        printer2.start();
    }
}
