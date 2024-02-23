import java.net.Socket;

public class Client {

    public void startClient(String username, String host, Socket socket) {

        // Create gameGui, where the user can play Battleship against another player on the server, socket is passed to communicate with server
        GameGui gameGui = new GameGui(username, socket);

        // create and start clientReader thread
        // clientreader keeps track of data from server to output to user
        ClientReader clientReader = new ClientReader(socket, gameGui);
        Thread clientReaderThread = new Thread(clientReader);
        clientReaderThread.start();
    }

    public static void main(String[] args) {
        new ModalGui();
    }
}