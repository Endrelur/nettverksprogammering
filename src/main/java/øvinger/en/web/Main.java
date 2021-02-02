package øvinger.en.web;

import øvinger.en.web.http.HttpHeaderServer;

public class Main {

    public static void main(String[] args) {
        int port = 8080;

        HttpHeaderServer server = new HttpHeaderServer();
        server.start(port);
    }
}
