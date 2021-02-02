package øvinger.en.klient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class SocketKlient {
    private Socket forbindelse;
    private LinkedList<String> tekstBuffer;
    private BufferedReader tjenerLeser;
    private PrintWriter tjenerSkriver;

    public SocketKlient(int port, String tjenernavn) throws Exception {
        this.tekstBuffer = new LinkedList<>();
        this.opprettForbindelse(port, tjenernavn);
    }

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

    public void sendIntOgPrint(int i) throws Exception {
        this.tjenerSkriver.println(i);
        printResponsBuffer();
    }

    public void sendInt(int i) {
        this.tjenerSkriver.println(i);
    }

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

    private void printResponsBuffer() throws Exception {
        lesFraTjener();
        while (!this.tekstBuffer.isEmpty()) {
            System.out.println(tekstBuffer.poll());
        }
    }

}
