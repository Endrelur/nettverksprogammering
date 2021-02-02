package testing.threads;

public class Navnprinter extends Thread {
    private String navn;

    Navnprinter(String navn){
        this.navn = navn;
    }

    public void run(){
        int antallGanger = 50;
        for (int i = 1;i<=antallGanger;i++){
            System.out.print(i+": " + navn + ". ");
        }
    }

}
