package øvinger.en.klient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * En klasse som brukes for å kommunisere via socket med en tjener.
 */
public class SocketKlient {
    private Socket forbindelse;
    private LinkedList<String> tekstBuffer;
    private BufferedReader tjenerLeser;
    private PrintWriter tjenerSkriver;

    /**
     * Brukes for å opprette en Socketklient som kan koble seg opp mot en sockettjener.
     *
     * @param port       portnummeret maskinen ligger på.
     * @param tjenernavn navn på tjenermaskinen.
     * @throws Exception
     */
    public SocketKlient(int port, String tjenernavn) throws Exception {
        this.tekstBuffer = new LinkedList<>();
        this.opprettForbindelse(port, tjenernavn);
    }

    /**
     * Oppretter en forbindelse for denne tjeneren opp mot en tjener.
     *
     * @param port       porten tjeneren ligger på.
     * @param tjenernavn navnet på tjenermaskinen.
     * @throws Exception
     */
    private void opprettForbindelse(int port, String tjenernavn) throws Exception {
        try {
            this.forbindelse = new Socket(tjenernavn, port);
            this.tjenerSkriver = new PrintWriter(this.forbindelse.getOutputStream(), true);
            this.tjenerLeser = new BufferedReader(
                    new InputStreamReader(this.forbindelse.getInputStream())
            );


        } catch (Exception e) {
            throw new Exception("Fikk ikke forbindelse: " + e.getMessage());
        }
        printResponsBuffer();
    }

    /**
     * Stenger socket forbindelsen.
     *
     * @throws Exception
     */
    public void stengForbindelse() throws Exception {
        if (this.forbindelse.isConnected()) {
            try {
                this.forbindelse.close();
                this.tekstBuffer.clear();
                this.tjenerLeser.close();
            } catch (Exception e) {
                throw new Exception("Stenging av forbindelser feilet: " + e.getMessage());
            }
        } else {
            throw new Exception("Har ingen forbindelse og stenge");
        }
        System.out.println("SUKSESS i frakobling.");
    }

    /**
     * Sender en integer til tjeneren og printer ut responsen.
     *
     * @param i heltallet som skal sendes til tjeneren.
     * @throws Exception
     */
    public void sendIntOgPrint(int i) throws Exception {
        this.tjenerSkriver.println(i);
        printResponsBuffer();
    }

    /**
     * Sender en integer til tjeneren.
     *
     * @param i Heltallet som skal sendes til tjeneren.
     */
    public void sendInt(int i) {
        this.tjenerSkriver.println(i);
    }

    /**
     * Leser inn data fra tjeneren og fyller opp det lokale bufferet.
     *
     * @throws Exception
     */
    private void lesFraTjener() throws Exception {
        try {
            String s = this.tjenerLeser.readLine();
            while (!s.equals("")) {
                this.tekstBuffer.addLast(s);
                s = this.tjenerLeser.readLine();
            }
        } catch (Exception e) {
            throw new Exception("Problem med lesing fra tjener: " + e.getMessage());
        }
    }

    /**
     * Printer ut alt den motatte dataen lagrede fra tjeneren ut i terminalen.
     *
     * @throws Exception
     */
    private void printResponsBuffer() throws Exception {
        lesFraTjener();
        while (!this.tekstBuffer.isEmpty()) {
            System.out.println(tekstBuffer.poll());
        }
    }

}
