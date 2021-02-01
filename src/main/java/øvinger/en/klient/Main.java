package øvinger.en.klient;

import java.util.Scanner;

public class Main {
    private boolean running;
    private SocketKlient socket;
    public static void main(String[] args) {
        Main main = new Main();
        try {
            main.startKlient();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    void Main() {
        this.running = false;
    }

    private void startKlient() {
        this.running = true;
    }

    private void meny() {
        while (this.running) {
            final int PLUSS = 1;
            final int MINUS = 2;
            final int AVSLUTT = 9;
            System.out.println("Pluss og minus klient");
            System.out.println("v0.1");
            System.out.println();
            System.out.println("MENY");
            System.out.println("1. Pluss");
            System.out.println("2. Minus");
            System.out.println("9. Avslutt");

            Scanner sc = new Scanner(System.in);
            int menuChoice = sc.nextInt();
            switch (menuChoice) {
                case PLUSS:
                    pluss();
                    break;
                case MINUS:
                    minus();
                    break;
                case AVSLUTT:
                    this.running = false;
                    break;
                default:
                    break;
            }
        }
    }
    private void startSocket() {
        try {
            this.socket = new SocketKlient();
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private void pluss() {
    }

    private void minus() {

    }
}
