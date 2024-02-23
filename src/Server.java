import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    // the port the server is going to listen on
    private static int port = 8888;

    // list of connected users' names
    Set<String> userNames = new HashSet<>();

    // list of connected users' thread handling their connection to the server
    private Set<UserThread> userThreads = new HashSet<>();

    // main method to start up the program
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();

    }

    // method to attempt to fire up the chat server and then accept connections from
    // users
    // and create a new thread to handle each connected user
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            // keep trying to accept connections to the socket from users
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("User Connected.");

                // create new thread for user, passing in the socket just created (socket) and
                // the server (this)
                UserThread newUser = new UserThread(socket, this, userNames);
                userThreads.add(newUser);

                // System.out.println(userThreads);

                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Chat Server Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // method to broadcast any incoming messages to all connected users (other than
    // user that sent message)
    public void broadcast(String message, UserThread excludeUser) {
        
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
            // aUser.sendMessage(message);
        }
    }

    // method to add users' names to our list of user names
    public void addUserName(String userName) {
        userNames.add(userName);
    }

    // method to remove a user from the list of connected users
    public void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println(userName + " has left the chat.");
        }
    }

    // method to return if we have any connected users
    public boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    // getter method to return list of usernames
    Set<String> getUserNames() {
        System.out.println(userNames);
        return this.userNames;
    }

}