import java.io.*;
import java.net.*;
import java.util.*;

public class UserThread extends Thread {

    // socket for network communication
    private Socket socket;

    // instance of the chat server itself
    private Server server;

    // PrintWriter object to stream output
    private PrintWriter writer;

    Set<String> userNames;

    // constructor
    public UserThread(Socket socket, Server server, Set<String> users) {
        this.socket = socket;
        this.server = server;
        this.userNames = users;
    }

    // override Thread's run() method
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();

            writer.println(userName);

            server.addUserName(userName);

            writer.println(userNames);

            String serverMessage = "New User Connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = "user [" + userName + "] has left.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("UserThread Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // method for printing list of connected users
    public void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected Users: " + server.getUserNames());
            // System.out.println("Connected Users: " + server.getUserNames());
        } else {
            writer.println("No Users Connected");
            // System.out.println("No Users Connected");
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}