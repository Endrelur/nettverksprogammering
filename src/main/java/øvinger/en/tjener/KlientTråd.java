package øvinger.en.tjener;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Denne klassen representerer en tråd som kan benyttes av en klient.
 */
public class KlientTråd extends Thread {
    private Socket socket;
    private BufferedReader klientLeser;
    private PrintWriter klientSkriver;
    private boolean running;

    /**
     * Oppretter en tråd som en klient kan benytte til simple aritmetiske oppgaver.
     *
     * @param socket        socketen klienten er koblet til.
     * @param klientLeser   datastreamen klienten skriver til.
     * @param klientSkriver datastreamen klienten leser fra.
     */
    public KlientTråd(Socket socket, BufferedReader klientLeser, PrintWriter klientSkriver) {
        this.socket = socket;
        this.klientLeser = klientLeser;
        this.klientSkriver = klientSkriver;
    }

    /**
     * Starter tråden og det aritmetiske programmet klienten benytter.
     */
    public void run() {
        this.running = true;
        while (this.running) {
            try {

                final int PLUSS = 1;
                final int MINUS = 2;
                final int AVSLUTT = 9;
                System.out.println("THREAD" + this.getId() + ": Viser hovedmeny");
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
                System.out.println("THREAD" + this.getId() + ":" + e.toString());
            }
        }
    }

    /**
     * Kobler fra socket og stenger datastreamer.
     *
     * @throws Exception
     */
    private void avslutt() throws Exception {
        this.running = false;
        this.klientSkriver.close();
        this.klientLeser.close();
        this.socket.close();
        System.out.println("THREAD" + this.getId() + ": Avsluttet.");
    }

    /**
     * Utfører en enkel a-b oppgave for en klient.
     *
     * @throws Exception
     */
    private void minus() throws Exception {
        System.out.println("THREAD" + this.getId() + ": Klienten har valgt minus");
        this.klientSkriver.println(" ");
        this.klientSkriver.println("Minus:");
        this.klientSkriver.println("Skriv inn ett tall...");
        int tall1 = mottaInt();
        this.klientSkriver.println("- et tall til...");
        int tall2 = mottaInt();
        this.klientSkriver.println(tall1 + "-" + tall2 + "=");
        this.klientSkriver.println(tall1 - tall2);
    }

    /**
     * Utfører en enkel a+b oppgave for en klient.
     *
     * @throws Exception
     */
    private void pluss() throws Exception {
        System.out.println("THREAD" + this.getId() + ": Klienten har valgt pluss");
        this.klientSkriver.println(" ");
        this.klientSkriver.println("Pluss:");
        this.klientSkriver.println("Skriv inn ett tall...");
        int tall1 = mottaInt();
        this.klientSkriver.println("+ et tall til...");
        int tall2 = mottaInt();
        this.klientSkriver.println(tall1 + "+" + tall2 + "=");
        this.klientSkriver.println(tall1 + tall2);

    }

    /**
     * Holder på klienten til et heltall blir motatt av tjeneren.
     *
     * @return tallet klienten sendte.
     * @throws Exception
     */
    private int mottaInt() throws Exception {
        System.out.println("THREAD" + this.getId() + ": Avventer int-input");
        this.klientSkriver.println("");
        String linje = this.klientLeser.readLine();
        while (!Pattern.compile("[0-9]").matcher(linje).find()) {
            linje = this.klientLeser.readLine();
            this.klientSkriver.println("Vennligst skriv inn ett tall.");
        }
        System.out.println("THREAD" + this.getId() + ": Fikk: " + linje + " som input.");
        return Integer.parseInt(linje);
    }
}
