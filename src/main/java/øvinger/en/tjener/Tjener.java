package øvinger.en.tjener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * En tjener klasse som benytter flere trådklienter for å kommunisere aritmetiske oppgaver med klienter.
 * ADVARSEL må hard-stoppes.
 */
public class Tjener {
    private ServerSocket tjener;

    /**
     * Oppretter en tjener på den spesifiserte porten på denne datamaskinen.
     *
     * @param port porten for å opprette tjeneren på.
     * @throws Exception
     */
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

    /**
     * Venter på koblinger fra forskjellige klienter og oppretter en tråd de kan kjøre på.
     */
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


