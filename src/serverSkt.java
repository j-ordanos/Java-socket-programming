import java.io.*;
import java.net.*;
import java.util.Scanner;

public class serverSkt {

    private static final int PORT = 4444;  // Specify the port number for the server to listen on

    public static void main(String[] args) {
        try {
            // Initialize the server and begin listening for client connections
            System.out.println("Server is started");
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Waiting for client request...");
            Socket socket = serverSocket.accept();  // Accept a client connection
            System.out.println("Client connected from " + socket.getInetAddress().getHostAddress());

            // Thread for sending messages to the client
            Thread sendThread = new Thread(() -> {
                try (Scanner scanner = new Scanner(System.in);  // Scanner for reading server-side input
                     DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {  // Output stream to send data to client
                    String messageToSend;
                    while (!(messageToSend = scanner.nextLine()).equalsIgnoreCase("<exit>")) {
                        outputStream.writeUTF(messageToSend);  // Send text input to client
                        outputStream.flush(); // Ensure data is sent immediately
                    }
                    System.out.println("Server shutdown command received. Closing connection.");
                } catch (IOException e) {
                    System.out.println("Error in sending messages: " + e.getMessage());
                }
            });

            // Thread for receiving messages from the client
            Thread receiveThread = new Thread(() -> {
                try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {  // Input stream to receive data from client
                    String messageReceived;
                    while (!(messageReceived = inputStream.readUTF()).equalsIgnoreCase("<exit>")) {
                        System.out.println("Client: " + messageReceived);  // Display client messages
                    }
                    System.out.println("Client has left the chat.");
                } catch (IOException e) {
                    System.out.println("Error in receiving messages: " + e.getMessage());
                }
            });

            // Start communication threads
            sendThread.start();
            receiveThread.start();

            // Ensure server stays operational until communication ends
            sendThread.join();
            receiveThread.join();
            socket.close();  // Close client socket
            serverSocket.close();  // Terminate server socket
            System.out.println("Server closed successfully.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
