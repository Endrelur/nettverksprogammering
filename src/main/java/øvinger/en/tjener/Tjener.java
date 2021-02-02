package øvinger.en.tjener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Tjener {
    private ServerSocket tjener;

    public Tjener(int port) throws Exception {
        try {
            this.tjener = new ServerSocket(port);
            System.out.println("Tjenerlogg: ");
            while (true) {
                ventPåForbindelse();
            }
        } catch (Exception e) {
            System.out.println("MAIN: Noe gikk feil under oppstart: " + e.toString());
        }

    }

    private void ventPåForbindelse() {
        try {
            Socket forbindelse = this.tjener.accept();
            BufferedReader klientLeser = new BufferedReader
                    (new InputStreamReader(forbindelse.getInputStream())
                    );
            PrintWriter klientSkriver = new PrintWriter(forbindelse.getOutputStream(), true);
            klientSkriver.println("Du har oppnådd forbindelse med: \n" + this.tjener.toString());
            System.out.println("MAIN: Opnådde forbidelse med" + forbindelse.toString());
            Thread tråd = new KlientTråd(forbindelse, klientLeser, klientSkriver);
            System.out.println("MAIN: delte ut thread: " + tråd.getId() + "til klient: " + forbindelse.toString());
            tråd.start();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}


