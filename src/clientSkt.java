import java.io.*;
import java.net.*;
import java.util.Scanner;

public class clientSkt {

    private static final int PORT = 4444;  // Constant for the server's listening port
    private static final String HOST = "127.0.0.1";  // Server IP address (localhost)

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(HOST, PORT);  // Establish a connection to the server
            System.out.println("Client is connected to server at " + HOST + ":" + PORT);

            // Thread for sending messages to the server
            Thread sendThread = new Thread(() -> {
                try (Scanner scanner = new Scanner(System.in);  // Read user input from the console
                     DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {  // Stream to send data to the server
                    while (true) {
                        String messageToSend = scanner.nextLine();
                        outputStream.writeUTF(messageToSend);  // Send message to the server
                        outputStream.flush();  // Ensure data is sent immediately
                        if (messageToSend.equalsIgnoreCase("<exit>")) {  // Check if the exit command is entered
                            System.out.println("Client shutdown command sent. Exiting.");
                            break;  // Break loop on exit command
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error in sending messages: " + e.getMessage());
                }
            });

            // Thread for receiving messages from the server
            Thread receiveThread = new Thread(() -> {
                try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {  // Stream to receive data from the server
                    String messageReceived;
                    while (true) {
                        messageReceived = inputStream.readUTF();  // Read messages from the server
                        System.out.println("Server: " + messageReceived);  // Print server's message
                        if (messageReceived.equalsIgnoreCase("<exit>")) {  // Check if server sent exit command
                            System.out.println("Server has closed the connection.");
                            break;  // Break loop on server exit command
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error in receiving messages: " + e.getMessage());
                }
            });

            sendThread.start();  // Start the sending thread
            receiveThread.start();  // Start the receiving thread

            // Wait for both threads to finish
            sendThread.join();
            receiveThread.join();
            socket.close();  // Close the connection to the server
            System.out.println("Client disconnected from server.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Unable to connect with the server: " + e.getMessage());
        }
    }
}
