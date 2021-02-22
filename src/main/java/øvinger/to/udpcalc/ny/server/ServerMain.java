package øvinger.to.udpcalc.ny.server;

import java.util.Scanner;

public class ServerMain {

    private UDPServer udp;
    private boolean running;
    private boolean online;

    public static void main(String args[]) throws InterruptedException {
        ServerMain server = new ServerMain();
        server.run();
    }

    private void run() {
        try {
            this.udp = new UDPServer();
            this.running = true;
            this.online = false;
            while (this.running) {
                Scanner sc = new Scanner(System.in);

                if (this.online) {
                    printLog("status: ONLINE");
                } else {
                    printLog("status: OFFLINE");
                }
                printLog("Server Controls:");
                printLog("Start, Stop");
                String userchoice = sc.nextLine().trim().toLowerCase();
                switch (userchoice) {
                    case "start":
                        start();
                        break;
                    case "stop":
                        stop();
                        break;
                    default:
                        break;
                }
            }
            printLog("Thanks for using this server-application.");
        } catch (Exception e) {
            printLog(e.toString());
        }

    }

    private void stop() throws Exception {
        if (this.online) {
            printLog("stopping the server");
            this.udp.stop();
            this.online = false;
            this.udp = new UDPServer();
            Thread.sleep(1000);
        } else {
            this.running = false;
        }
    }

    private void start() throws Exception {
        if (this.online) {
            printLog("The server is already running");
        } else {
            Thread t = new Thread(this.udp);
            t.start();
            printLog("started the server on thread: " + t.getId());
            this.online = true;
            Thread.sleep(1000);
        }
    }

    private void printLog(String entry) {
        String s = ("MAIN: " + entry);
        System.out.println(s);
    }


}

