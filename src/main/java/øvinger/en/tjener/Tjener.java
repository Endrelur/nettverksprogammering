package øvinger.en.tjener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Tjener {
    private ServerSocket tjener;
    private Socket forbindelse;
    private BufferedReader klientLeser;
    private PrintWriter klientSkriver;
    private boolean kjører;

    public Tjener(int port) throws Exception {
        try {
            this.tjener = new ServerSocket(port);
            System.out.println("Tjenerlogg: ");
            while (true) {
                ventPåForbindelse();
            }
        } catch (Exception e) {
            System.out.println("Noe gikk feil under oppstart: " + e.toString());
        }

    }

    private void ventPåForbindelse() throws Exception {
        Socket forbindelse = this.tjener.accept();
        BufferedReader klientLeser = new BufferedReader
                (new InputStreamReader(this.forbindelse.getInputStream())
                );
        PrintWriter klientSkriver = new PrintWriter(this.forbindelse.getOutputStream(), true);
        this.kjører = true;
        this.klientSkriver.println("Du har oppnådd forbindelse med: \n" + this.tjener.toString());
        System.out.println("opnådde forbidelse med" + this.forbindelse.toString());
        Thread tråd = new KlientTråd(forbindelse, klientLeser, klientSkriver);
        System.out.println("MAIN: delte ut thread: " + tråd.getId() + "til klient: " + forbindelse.toString());
        tråd.start();
        //menu();
    }
/*
    private void meny() throws Exception {

        final int PLUSS = 1;
        final int MINUS = 2;
        final int AVSLUTT = 9;

        while (this.kjører) {
            System.out.println("viser hovedmeny");
            this.klientSkriver.println(" ");
            this.klientSkriver.println("Pluss og Minus tjener Meny:");
            this.klientSkriver.println(" ");
            this.klientSkriver.println("1. Pluss");
            this.klientSkriver.println("2. Minus");
            this.klientSkriver.println("9. Koble fra");
            this.klientSkriver.println("Skriv inn korresponderende tall for å velge handling...");
            int menyvalg = mottaInt();
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
        }
    }

    private void avslutt() throws Exception {
        try {
            this.klientSkriver.close();
            this.klientLeser.close();
            this.tjener.close();
            this.kjører = false;
        } catch (Exception e) {
            throw e;
        }
        System.out.println("avsluttet.");
    }

    private void minus() throws Exception {
        System.out.println("klienten har valgt minus");
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
        System.out.println("klienten har valgt pluss");
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
        System.out.println("avventer int-input");
        this.klientSkriver.println("");
        String linje = this.klientLeser.readLine();
        while (!Pattern.compile("[0-9]").matcher(linje).find()) {
            linje = this.klientLeser.readLine();
            this.klientSkriver.println("Vennligst skriv inn ett tall.");
        }
        System.out.println("fikk: " + linje + " som input.");
        return Integer.parseInt(linje);
    }*/
}


