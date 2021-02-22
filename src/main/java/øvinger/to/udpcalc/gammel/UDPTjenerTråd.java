package øvinger.to.udpcalc.gammel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPTjenerTråd extends Thread {
    private DatagramSocket socket;
    private InetAddress ip;
    private int klientPort;
    private String respons;
    private boolean kjører;

    public UDPTjenerTråd(int port, InetAddress ip, int klientPort) throws Exception {
        this.klientPort = klientPort;
        this.socket = new DatagramSocket(port);
        this.ip = ip;
    }

    public void run() {
        try {
            this.kjører = true;
            printLog("Starter opp");
            meny();
        } catch (Exception e) {
            printLog(e.toString());
        }

    }

    private void meny() throws Exception {
        while (this.kjører) {
            printLog("Viser meny");
            leggTilRespons("");
            leggTilRespons("Tjenerkalkulator V0.1");
            leggTilRespons("Meny:");
            leggTilRespons("Pluss");
            leggTilRespons("Minus");
            sendRespons();
            String respons = mottaRespons().toLowerCase().trim();
            switch (respons) {
                case "pluss":
                    pluss();
                    break;
                case "minus":
                    minus();
                    break;
                case "avslutt":
                    this.kjører = false;
                    break;
                default:

            }
        }
        printLog("Avslutter.");

    }

    private void minus() {
        printLog("Starter minus.");

    }

    private void pluss() {
        this.printLog("Starter pluss.");

    }

    private void leggTilRespons(String tekst) {
        this.respons += "\n" + tekst;
    }

    private String mottaRespons() throws Exception {
        boolean motatt = false;
        String motattString = "";
        while (!motatt) {
            byte[] mottaksBuffer = new byte[1024];
            DatagramPacket motattPakke = new DatagramPacket(
                    mottaksBuffer, mottaksBuffer.length);
            printLog("Venter på respons");
            this.socket.receive(motattPakke);
            motattString = new String(motattPakke.getData()).trim();
            motatt = true;
            printLog("Mottok: " + motattString);

        }
        return motattString;
    }

    private int mottaIntRespons() {
        return 0;
    }

    private void sendRespons() {
        try {
            byte[] responsArray = this.respons.getBytes();
            DatagramPacket pakke = new DatagramPacket(
                    responsArray, responsArray.length, this.ip, this.klientPort);
            printLog("Sender respons.");
            this.socket.send(pakke);
            this.respons = "";
        } catch (Exception e) {
            printLog(e.toString());
        }
    }

    private void printLog(String melding) {
        System.out.println("TRÅD " + this.getId() + ": " + melding);
    }

}
