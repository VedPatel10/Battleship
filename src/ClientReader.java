import java.awt.*;
import java.io.*;
import java.net.Socket;

// Thread that reads data from the server 
public class ClientReader implements Runnable {

    Socket socket;
    GameGui gameGui;
    PrintWriter writer;

    String oppName;

    // same socket and chatgui are passed in to get server data and update the gui
    public ClientReader(Socket s, GameGui gGui) {
        socket = s;
        gameGui = gGui;
    }

    public void run() {

        try {
            OutputStream userOutput;
            userOutput = socket.getOutputStream();
            writer = new PrintWriter(userOutput, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Inputstream is used to read input from server
        InputStream input;
        try {
            while (true) {
                input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                // each line from the server is read and added to the gui
                String serverData = reader.readLine();

                // System.out.println(serverData);
                // gameGui.chatArea.append(serverData + "\n");

                gameGui.chatArea.setText(gameGui.chatArea.getText() + serverData + "\n");

                // if user leaves the server, remove username from list of users
                if (serverData.endsWith(" has left.")) {
                    String name = serverData.substring(serverData.indexOf("[") + 1,
                    serverData.indexOf("]"));
                    
                    // if the person who left was the opponent, reset everything and wait for new opponent
                    if (oppName.equals(name)) {
                        gameGui.oppTitle.setText("Waiting for Opponent...");

                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                gameGui.oppButton[r][c].setEnabled(false);
                                gameGui.oppButton[r][c].setBackground(gameGui.defualt);
                                gameGui.oppButton[r][c].setText("");
                                gameGui.userButton[r][c].setBackground(gameGui.defualt);
                                gameGui.userBoard[r][c] = 0;
                                gameGui.userButton[r][c].setText("");
                            }
                        }
                        // re-add all ship length options
                        gameGui.lengthModel.removeAllElements();
                        gameGui.lengthModel.addElement("2");
                        gameGui.lengthModel.addElement("3");
                        gameGui.lengthModel.addElement("4");
                        gameGui.lengthModel.addElement("5");
                        
                        gameGui.placeShipButton.setEnabled(true);
                        gameGui.messageLabel.setText("");
                        gameGui.readyPlayers = 0;
                    }
                }

                // if new user connects, add the username to the list of users
                if (serverData.startsWith("New User Connected: ") && gameGui.oppTitle.getText().equals("Waiting for Opponent...")) {
                    String nameString = serverData.substring(serverData.indexOf(":") + 1);
                    oppName = serverData.substring(serverData.indexOf(":") + 1);
                    gameGui.oppTitle.setText(nameString.trim() + "'s Board");

                }

                // if user connects to a server, loop through the list of all users connected,
                // add each to the list of users
                if (serverData.startsWith("Connected Users:") && gameGui.oppTitle.getText().equals("Waiting for Opponent...")) {
                    String nameString = serverData.substring(serverData.indexOf("[") + 1,
                    serverData.indexOf("]"));
                    oppName = serverData.substring(serverData.indexOf("[") + 1,
                    serverData.indexOf("]"));
                    gameGui.oppTitle.setText(nameString.trim() + "'s Board");
                }

                //System.out.println(serverData);
                if (serverData.startsWith("ATTACK!")) {

                    // remove all everything but numbers from string, manipulate numbers to get row # and column #
                    String nums = String.valueOf(Integer.parseInt(serverData.replaceAll("[\\D]", "")));
                    int num = Integer.parseInt(nums);

                    int column = (num % 10) - 1;
                    int row = ((num / 10) % 10) - 1;

                    // System.out.println(row + " and " + column);
                    // if the guess did not hit a ship
                    if (gameGui.userBoard[row][column] == 0) {
                        gameGui.userButton[row][column].setBackground(Color.black);
                        gameGui.userBoard[row][column] = 0;
                        writer.println("MISS! Row: " + (row+1) + " Column: " + (column+1) + " is empty");
                    }

                    // if the guess hit a 2 block ship
                    if (gameGui.userBoard[row][column] == 2) {
                        gameGui.userButton[row][column].setBackground(Color.pink);
                        gameGui.userBoard[row][column] = -1;
                        writer.println("HIT! Row: " + (row+1) + " Column: " + (column+1) + " is contains a ship");
                    }

                    // if the guess hit a 3 block ship
                    if (gameGui.userBoard[row][column] == 3) {
                        gameGui.userButton[row][column].setBackground(Color.yellow);
                        gameGui.userBoard[row][column] = -1;
                        writer.println("HIT! Row: " + (row+1) + " Column: " + (column+1) + " is contains a ship");
                    }

                    // if the guess hit a 4 block ship
                    if (gameGui.userBoard[row][column] == 4) {
                        gameGui.userButton[row][column].setBackground(Color.green);
                        gameGui.userBoard[row][column] = -1;
                        writer.println("HIT! Row: " + (row+1) + " Column: " + (column+1) + " is contains a ship");
                    }

                    // if the guess hit a five block ship
                    if (gameGui.userBoard[row][column] == 5) {
                        gameGui.userButton[row][column].setBackground(Color.blue);
                        gameGui.userBoard[row][column] = -1;
                        writer.println("HIT! Row: " + (row+1) + " Column: " + (column+1) + " is contains a ship");
                    }
                    //gameGui.userButton[row][column].setFont(new Font(null, 1, 20));
                    gameGui.userButton[row][column].setText("X");

                    // enable board buttons for other player to make next move
                    for (int r = 0; r < 8; r++) {
                        for (int c = 0; c < 8; c++) {
                            gameGui.oppButton[r][c].setEnabled(true);
                        }
                    }

                }

                // when user who attacks receives a MISS message
                if (serverData.startsWith("MISS!")) {
                    // manipulate string to get row and column #
                    String numsOnly = String.valueOf(Integer.parseInt(serverData.replaceAll("[\\D]", "")));
                    int numsInt = Integer.parseInt(numsOnly);

                    int column = (numsInt % 10) - 1;
                    int row = ((numsInt / 10) % 10) - 1;
                    //System.out.println((row+1) + " is row, column is " + (column+1));
                    gameGui.oppButton[row][column].setBackground(Color.black);
                }

                // when user who attacks receives a HIT message
                if (serverData.startsWith("HIT!")) {
                    // manipulate string to get row and column #
                    String numsOnly = String.valueOf(Integer.parseInt(serverData.replaceAll("[\\D]", "")));
                    int numsInt = Integer.parseInt(numsOnly);

                    int column = (numsInt % 10) - 1;
                    int row = ((numsInt / 10) % 10) - 1;

                    gameGui.oppButton[row][column].setBackground(Color.black);
                    // add "X" to show a hit
                    gameGui.oppButton[row][column].setText("X");
                }

                // when user receives READY message (all ships of opponent are placed)
                if (serverData.startsWith("READY!")) {
                    gameGui.readyPlayers++;
                    gameGui.messageLabel.setText("");

                    if (gameGui.readyPlayers == 2) {
                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                gameGui.oppButton[r][c].setEnabled(true);
                            }
                        }
                    }
                }

                // when user revieves GAMEOVER message
                if (serverData.startsWith("GAMEOVER!")) {
                    gameGui.playAgainButton.setVisible(true);
                    if (gameGui.readyPlayers == 2) {
                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                gameGui.oppButton[r][c].setEnabled(false);
                            }
                        }
                    }
                }

                // after each move, check if someone has won (if there are 14 hits)
                if (gameGui.checkGame() == 14) {
                    System.out.println("Gameover");
                    writer.println("GAMEOVER! All of " + oppName.trim() + "'s ships are down. " + gameGui.username + " is the winner!");
                    gameGui.chatArea.setText(gameGui.chatArea.getText() + "GAMEOVER! All of " + oppName.trim() + "'s ships are down. " + gameGui.username + " is the winner!\n");
                    gameGui.playAgainButton.setVisible(true);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println("Client reader error: " + e.getMessage());
        }
    }
}