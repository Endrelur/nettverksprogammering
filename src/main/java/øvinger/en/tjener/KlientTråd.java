package øvinger.en.tjener;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class KlientTråd extends Thread {
    private Socket socket;
    private BufferedReader klientLeser;
    private PrintWriter klientSkriver;
    private boolean running;

    public KlientTråd(Socket socket, BufferedReader klientLeser, PrintWriter klientSkriver) {
        this.socket = socket;
        this.klientLeser = klientLeser;
        this.klientSkriver = klientSkriver;
    }

    public void run() {
        this.running = true;
        while (this.running) {
            try {

                final int PLUSS = 1;
                final int MINUS = 2;
                final int AVSLUTT = 9;
                System.out.println("Thread" + this.getId() + "viser hovedmeny");
                this.klientSkriver.println(" ");
                this.klientSkriver.println("Pluss og Minus tjener Meny:");
                this.klientSkriver.println(" ");
                this.klientSkriver.println("1. Pluss");
                this.klientSkriver.println("2. Minus");
                this.klientSkriver.println("9. Koble fra");
                this.klientSkriver.println("Skriv inn korresponderende tall for å velge handling...");
                int menyvalg = this.mottaInt();
                switch (menyvalg) {
                    case PLUSS:
                        pluss();
                        break;
                    case MINUS:
                        minus();
                        break;
                    case AVSLUTT:
                        avslutt();
                        break;
                    default:
                        this.klientSkriver.println(menyvalg + ", er ikke et gyldig menyvalg.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("THREAD:" + this.getId() + ":" + e.toString());
            }
        }
    }

    private void avslutt() throws Exception {
        this.running = false;
        this.klientSkriver.close();
        this.klientLeser.close();
        this.socket.close();
        System.out.println("THREAD:" + this.getId() + "Avsluttet.");
    }

    private void minus() throws Exception {
        System.out.println("THREAD:" + this.getId() + ": Klienten har valgt minus");
        this.klientSkriver.println(" ");
        this.klientSkriver.println("Minus:");
        this.klientSkriver.println("Skriv inn ett tall...");
        int tall1 = mottaInt();
        this.klientSkriver.println("- et tall til...");
        int tall2 = mottaInt();
        this.klientSkriver.println(tall1 + "-" + tall2 + "=");
        this.klientSkriver.println(tall1 - tall2);
    }

    private void pluss() throws Exception {
        System.out.println("THREAD:" + this.getId() + ": Klienten har valgt pluss");
        this.klientSkriver.println(" ");
        this.klientSkriver.println("Pluss:");
        this.klientSkriver.println("Skriv inn ett tall...");
        int tall1 = mottaInt();
        this.klientSkriver.println("+ et tall til...");
        int tall2 = mottaInt();
        this.klientSkriver.println(tall1 + "+" + tall2 + "=");
        this.klientSkriver.println(tall1 + tall2);

    }

    private int mottaInt() throws Exception {
        System.out.println("THREAD:" + this.getId() + "THREAD:" + this.getId() + ": Avventer int-input");
        this.klientSkriver.println("");
        String linje = this.klientLeser.readLine();
        while (!Pattern.compile("[0-9]").matcher(linje).find()) {
            linje = this.klientLeser.readLine();
            this.klientSkriver.println("Vennligst skriv inn ett tall.");
        }
        System.out.println("THREAD:" + this.getId() + ": Fikk: " + linje + " som input.");
        return Integer.parseInt(linje);
    }
}
