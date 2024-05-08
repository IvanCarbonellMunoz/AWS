package Sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

public class ClientExample extends JFrame {

    private final static int PORT = 5005;
    private final static String SERVER = "10.2.1.155";

    public static void main(String[] args) {
        new ClientExample();
    }

    public ClientExample() {
        try {
            System.out.println("Client -> Start");
            Socket socket = new Socket(SERVER, PORT);

            Thread senderThread = new Thread(new Sender(socket));
            Thread receiverThread = new Thread(new Receiver(socket));

            senderThread.start();
            receiverThread.start();

            senderThread.join();
            receiverThread.join();

            System.out.println("Client -> End of the program");
            socket.close();
        } catch (IOException | InterruptedException ex) {
            System.err.println("Client -> " + ex.getMessage());
        }
    }

    private static class Sender implements Runnable {
        private Socket socket;

        public Sender(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                PrintStream output = new PrintStream(socket.getOutputStream());
                Scanner scanner = new Scanner(System.in);

                String line;
                while ((line = scanner.nextLine()) != null) {
                    output.println(line);
                    if (line.equals("***")) {
                        break;
                    }
                }

                scanner.close();
            } catch (IOException ex) {
                System.err.println("Client -> " + ex.getMessage());
            }
        }
    }

    private static class Receiver implements Runnable {
        private Socket socket;

        public Receiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    System.out.println("Server -> " + line);
                    if (line.equals("***")) {
                        System.out.println("Client -> Received exit signal, closing connection.");
                        socket.close();
                        System.exit(0); // Exit the program
                    }
                }
            } catch (IOException ex) {
                System.err.println("Client -> " + ex.getMessage());
            }
        }
    }
}

