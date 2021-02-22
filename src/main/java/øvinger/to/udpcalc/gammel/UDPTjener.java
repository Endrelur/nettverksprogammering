package øvinger.to.udpcalc.gammel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 */
public class UDPTjener {
    //UDP porten til denne tjeneren:
    public static final int TJENER_PORT = 5000;
    public boolean kjører = true;

    private byte[] mottaksBuffer;
    private DatagramSocket socket;
    private HashMap<Thread, Integer> trådPorter;

    private UDPTjener() {
        try {
            this.trådPorter = new HashMap<>();
            this.socket = new DatagramSocket(TJENER_PORT);
            ventPåKlient();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void ventPåKlient() throws Exception {

        while (this.kjører) {
            printLog("Venter på en ny klient");
            String motatt = "";
            this.mottaksBuffer = new byte[1024];
            DatagramPacket motattPakke = new DatagramPacket(
                    this.mottaksBuffer, mottaksBuffer.length);
            this.socket.receive(motattPakke);
            motatt = new String(motattPakke.getData()).trim();
            sjekkTråder();
            printLog("Mottok: " + motatt);
            if (motatt.trim().equals("start")) {
                int ledigPort = fåLedigPort();
                UDPTjenerTråd traad = new UDPTjenerTråd(
                        ledigPort, InetAddress.getLocalHost(),motattPakke.getPort());
                byte[] portBytteArray = ("!portbytte:" + ledigPort).getBytes();
                DatagramPacket portByttePacket = new DatagramPacket(
                        portBytteArray, portBytteArray.length, InetAddress.getLocalHost(), motattPakke.getPort());
                this.socket.send(portByttePacket);
                new Thread(traad).start();
                this.trådPorter.put(traad, ledigPort);
            } else if (!this.trådPorter.containsValue(motattPakke.getPort())) {
                String respons = "Gjenkjenner ikke: " + motatt + ", som intern kommando.";
                byte[] responsArray = respons.getBytes();
                DatagramPacket responsPakke = new DatagramPacket(
                        responsArray, responsArray.length, InetAddress.getLocalHost(), motattPakke.getPort());
                this.socket.send(responsPakke);
            }
        }
    }

    private int fåLedigPort() {
        int i = TJENER_PORT + 1;
        while (this.trådPorter.containsValue(i)) {
            i++;
        }
        return i;
    }

    private void sjekkTråder() {
        Iterator it = this.trådPorter.keySet().iterator();
        while (it.hasNext()) {
            Thread current = (Thread) it.next();
            if (!current.isAlive()) {
                it.remove();
            }
        }
    }

    private void printLog(String melding) {
        System.out.println("HOVED : " + melding);
    }

    public static void main(String[] args) {
        UDPTjener tjener = new UDPTjener();
    }
}


