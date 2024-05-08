package Sockets;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.PrintStream;

import java.net.ServerSocket;

import java.net.Socket;


public class ServerExample {


    private final static int PORT = 5005;


    public static void main(String[] args) {

        try {

            // Crear un ServerSocket para esperar solicitudes de red en el puerto especificado

            ServerSocket server = new ServerSocket(PORT);

            System.out.println("Server started");


            while (true) {

                // Esperar a que un cliente se conecte

                Socket client = server.accept();

                System.out.println("Connected to client: " + client.getInetAddress());


                // Iniciar un nuevo hilo para manejar la comunicación con el cliente

                ClientHandler handler = new ClientHandler(client);

                handler.start();


                // Iniciar un nuevo hilo para enviar mensajes al cliente

                ClientSender sender = new ClientSender(client);

                sender.start();

            }

        } catch (IOException ex) {

            System.err.println(ex.getMessage());

        }

    }


    // Clase interna para manejar la comunicación con el cliente

    static class ClientHandler extends Thread {

        private final Socket client;


        public ClientHandler(Socket client) {

            this.client = client;

        }


        @Override

        public void run() {

            try {

                // Configurar un lector de entrada para recibir mensajes del cliente

                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));


                String line;

                // Leer continuamente mensajes del cliente

                while ((line = input.readLine()) != null) {

                    // Mostrar en la consola el mensaje recibido del cliente

                    System.out.println("Client (" + client.getInetAddress() + "): " + line);

                    // Si el mensaje es "***", salir del bucle

                    if (line.equals("***")) {

                        break;

                    }

                }


                // Cerrar la conexión con el cliente y terminar el programa

                System.out.println("Connection closed with client: " + client.getInetAddress());

                client.close();

                System.exit(0);

            } catch (IOException ex) {

                System.err.println(ex.getMessage());

            }

        }

    }


    // Clase interna para enviar mensajes al cliente

    static class ClientSender extends Thread {

        private final Socket client;

        private final BufferedReader keyboardInput;


        public ClientSender(Socket client) {

            this.client = client;

            // Configurar un lector de entrada para recibir mensajes desde el teclado

            this.keyboardInput = new BufferedReader(new InputStreamReader(System.in));

        }


        @Override

        public void run() {

            try {

                // Configurar un flujo de salida para enviar mensajes al cliente

                PrintStream output = new PrintStream(client.getOutputStream());


                String line;

                // Leer continuamente mensajes desde el teclado

                while ((line = keyboardInput.readLine()) != null) {

                    // Enviar el mensaje al cliente

                    output.println(line);

                    // Si el mensaje es "***", salir del bucle

                    if (line.equals("***")) {

                        break;

                    }

                }


                // Cerrar la conexión con el cliente y terminar el programa

                System.out.println("Connection closed with client: " + client.getInetAddress());

                client.close();

                System.exit(0);

            } catch (IOException ex) {

                System.err.println(ex.getMessage());

            }

        }

    }

}