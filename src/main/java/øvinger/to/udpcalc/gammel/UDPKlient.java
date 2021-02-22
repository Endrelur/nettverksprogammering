package øvinger.to.udpcalc.gammel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPKlient {
    public static void main(String[] args) {
        DatagramSocket udpSocket;
        int port = UDPTjener.TJENER_PORT;
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
                if (tjenerRespons.contains("!portbytte:")) {
                    Scanner in = new Scanner(tjenerRespons).useDelimiter("[^0-9]+");
                    port = in.nextInt();
                    System.out.println("Skiftet port til " + port);
                    tjenerResponsBytes = new byte[1024];
                    pakke = new DatagramPacket(
                            tjenerResponsBytes, tjenerResponsBytes.length);
                    udpSocket.receive(pakke); //Mottar pakken.
                    tjenerRespons = new String(pakke.getData()); //Konverterer byte-array til String.
                }

                System.out.println(tjenerRespons);


                if (valg.trim().equals("avslutt")) {
                    kjører = false;
                    System.out.println("Avslutter..");
                } else {
                    valg = sc.nextLine();
                }
            }
            System.out.println("Takk for at du brukte denne klienten.");
            udpSocket.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
