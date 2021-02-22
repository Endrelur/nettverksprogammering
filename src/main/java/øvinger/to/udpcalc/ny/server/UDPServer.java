package øvinger.to.udpcalc.ny.server;

import java.net.*;
import java.util.HashMap;
import java.util.Iterator;

public class UDPServer implements Runnable {
    private static final int SERVER_PORT = 8080;
    private static final int TIMEOUT_TIME = 2000;
    private volatile boolean exit = false;
    private HashMap<Calculator, Thread> activeThreads;
    private DatagramSocket serverSocket;
    private InetAddress adress;
    byte[] receiveBytes;

    public UDPServer() throws Exception {
        this.activeThreads = new HashMap<>();
        this.serverSocket = new DatagramSocket(SERVER_PORT);
        this.adress = InetAddress.getLocalHost();
        this.serverSocket.setSoTimeout(TIMEOUT_TIME);
    }

    public void run() {

        printLog("has started");
        while (!exit) {
            try {
                checkThreads();

                this.receiveBytes = new byte[1024];
                DatagramPacket incoming = new DatagramPacket(
                        this.receiveBytes, this.receiveBytes.length, this.adress, SERVER_PORT);
                this.serverSocket.receive(incoming);
                if (!isPortActive(incoming.getPort())) {
                    printLog("aquired a new connection from port" + incoming.getPort() + ", assigning thread");
                    assignThread(incoming.getPort());
                }
            } catch (SocketTimeoutException | SocketException se) {
                //må time out for å sjekke om trådene er aktive enda.
            } catch (Exception e) {
                printLog(e.toString());
            }
        }
        for (Calculator calc : this.activeThreads.keySet()) {
            calc.stop();
        }
        printLog("has stopped");

    }

    private boolean isPortActive(int port) {
        boolean containsPort = false;
        for (Calculator calc : this.activeThreads.keySet()) {
            if (calc.clientPort == port) {
                containsPort = true;
            }
        }
        return containsPort;
    }

    private void assignThread(int clientPort) throws Exception {
        Calculator calc = new Calculator(this.serverSocket, clientPort);
        Thread t = new Thread(calc);
        this.activeThreads.put(calc, t);
        printLog("assigned thread" + t.getId() + ", to port: " + clientPort);
        t.start();
    }

    private void checkThreads() throws Exception {
        Iterator<Thread> it = this.activeThreads.values().iterator();
        while (it.hasNext()) {
            Thread t = it.next();
            if (t.getState().equals(Thread.State.TERMINATED)) {
                printLog("removed:" + t.getId() + ", due to inactivity");
                it.remove();
            }
        }
    }

    private void printLog(String entry) {
        String s = ("SERVER THREAD: " + entry);
        System.out.println(s);
    }

    public void stop() {
        exit = true;
        this.serverSocket.close();
    }

    private class Calculator implements Runnable {

        private volatile boolean exit = false;
        private int clientPort;
        private DatagramSocket socket;
        private String reply;

        private Calculator(DatagramSocket socket, int clientPort) throws Exception {
            this.clientPort = clientPort;
            this.socket = socket;
        }

        public void run() {
            printLog("has started");
            buildReply("Calculator:");
            while (!exit) {
                try {
                    menu();
                } catch (Exception e) {

                }
            }
            printLog("has stopped");
        }

        private void menu() throws Exception {
            buildReply("Menu:");
            buildReply("Plus");
            buildReply("Minus");
            buildReply("Disconnect");
            sendReply();
            String choice = getReply().trim().toLowerCase();
            switch (choice) {
                case "plus":
                    plus();
                    break;
                case "minus":
                    minus();
                    break;
                case "disconnect":
                    this.stop();
                    break;
                default:
                    buildReply(choice + " is not a valid option");
                    buildReply("");
                    sendReply();
                    break;
            }
        }

        private void minus() throws Exception {
            String firstReply;
            String secondReply;
            buildReply("PLUS:");
            buildReply("please type in the first number:");
            sendReply();

            try {
                firstReply = getReply();
                int first = Integer.parseInt(firstReply);
                buildReply("please type in second number:");
                sendReply();
                secondReply = getReply();
                int second = Integer.parseInt(secondReply);
                buildReply(first + "+" + second);
                buildReply("=" + (first + second));
            } catch (NumberFormatException nfe) {
                buildReply(" a invalid number was received");
                buildReply("");
            }

        }

        private void plus() {
        }

        private void printLog(String entry) {
            String s = ("CALCULATOR THREAD: " + entry);
            System.out.println(s);
        }

        private void buildReply(String s) {
            this.reply += ("\n" + s);
        }

        private void sendReply() {
            try {
                byte[] responsArray = this.reply.getBytes();
                DatagramPacket p = new DatagramPacket(
                        responsArray, responsArray.length, adress, this.clientPort);
                printLog("Sending reply.");
                this.socket.send(p);
                this.reply = "";
            } catch (Exception e) {
                printLog(e.toString());
            }
        }

        private String getReply() throws Exception {
            boolean receivedReply = false;
            String receiveString = "";
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivedP = new DatagramPacket(
                    receiveBuffer, receiveBuffer.length);
            printLog("waiting for reply");
            while (!receivedReply) {
                try {
                    this.socket.receive(receivedP);
                    receivedReply = true;
                } catch (SocketTimeoutException ste) {
                }
            }
            receiveString = new String(receivedP.getData()).trim();
            printLog("Mottok: " + receiveString);

            return receiveString;
        }

        private void stop() {
            exit = true;
        }

    }
}
