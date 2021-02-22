package øvinger.en.klient;

import java.util.Scanner;

public class KlientMain {

    public static void main(String[] args) {
        final String TJENERNAVN = "MSI";
        try {
            SocketKlient klient = new SocketKlient(1250, TJENERNAVN);
            boolean kjører = true;
            Scanner sc = new Scanner(System.in);
            while (kjører) {
                int valg = sc.nextInt();
                if (valg == 9) {
                    klient.sendInt(valg);
                    kjører = false;
                    klient.stengForbindelse();
                } else {
                    klient.sendIntOgPrint(valg);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
