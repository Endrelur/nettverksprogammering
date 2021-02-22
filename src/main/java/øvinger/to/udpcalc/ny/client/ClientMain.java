package øvinger.to.udpcalc.ny.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {

        DatagramSocket udpSocket;
        int port = 8080;
        final int TIMEOUT_TIME = 3000;
        System.out.println("Starter klient");
        try {
            udpSocket = new DatagramSocket(); //UDP-Socketen som benyttes.
            Scanner sc = new Scanner(System.in);//Scanner for user input.
            DatagramPacket pakke; //Pakken som benyttes til å sende/motta.

            //Input & Output byteArrays.
            byte[] klientValgBytes;
            byte[] tjenerResponsBytes;


            String tjenerRespons; //Responsen fra tjeneren.
            String valg = "start"; //Initialiserer ved å starte tjeneren.

            boolean kjører = true;
            //Hovedloop:
            while (kjører) {
                //Gjøre om Stringen til en byte-array.
                klientValgBytes = valg.getBytes();

                //Opprette pakken som skal sendes til tjeneren.
                pakke = new DatagramPacket(
                        klientValgBytes, klientValgBytes.length, InetAddress.getLocalHost(), port);
                udpSocket.send(pakke); //Sender pakken.

                //Motta respons fra tjeneren.
                tjenerResponsBytes = new byte[1024];
                pakke = new DatagramPacket(
                        tjenerResponsBytes, tjenerResponsBytes.length);
                udpSocket.receive(pakke); //Mottar pakken.
                tjenerRespons = new String(pakke.getData()); //Konverterer byte-array til String.
                System.out.println(tjenerRespons);
                if (valg.equals("avslutt")) {
                    kjører = false;
                }
            }
            System.out.println("Takk for at du brukte denne klienten.");
            udpSocket.close();
        } catch (SocketTimeoutException ste) {
            System.out.println("Connection timed out");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
