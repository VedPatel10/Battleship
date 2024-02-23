// Ved Patel
// ICS4U CCT Project - Online Battleship
// Jan 31, 2023

import java.awt.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;

// main gui where Battleship is played on
// this is meant to be played against one other player connected to the server
public class GameGui implements ActionListener {

    JFrame gameFrame;

    // Jbutton array representing the 64 possible guesses
    JButton userButton[][] = new JButton[8][8];
    JButton oppButton[][] = new JButton[8][8];

    JLabel oppTitle;

    // chat area components
    JTextArea chatArea = new JTextArea();
    JTextArea userTextArea = new JTextArea();
    JButton sendButton = new JButton();
    JButton exitButton = new JButton();

    // networking components
    PrintWriter writer;
    Socket socket;
    String username;

    // place ships components
    JComboBox<String> orientationBool;
    JComboBox<String> rowNum;
    JComboBox<String> colNum;
    DefaultComboBoxModel<String> lengthModel;
    JComboBox<String> lengthNum;
    JComboBox<String> directionBool;
    JLabel orientationLabel;
    JLabel rowNumLabel;
    JLabel colNumLabel;
    JLabel lengthNumLabel;
    JLabel directionLabel;
    JButton placeShipButton;
    JButton helpButton;
    JLabel messageLabel;
    JButton playAgainButton;
    Color defualt;

    // 3 different types of fonts used 
    Font myFont = new Font("Impact", Font.TRUETYPE_FONT, 15);
    Font comboBoxFont = new Font("Impact", Font.TRUETYPE_FONT, 13);
    Font calibriFont = new Font("Calibri", Font.BOLD, 14);

    // Array of Integers used for storing the locations of the ships
    int userBoard[][] = new int[8][8];

    int readyPlayers = 0;

    public GameGui(String username, Socket socket) {

        this.socket = socket;
        this.username = username;

        // create writer, used to send text to server using socket
        try {
            OutputStream userOutput;
            userOutput = socket.getOutputStream();
            writer = new PrintWriter(userOutput, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // first thing userThread looks for is a username
        writer.println(username);

        // main JFrame setup
        gameFrame = new JFrame();
        gameFrame.setPreferredSize(new Dimension(820, 700));
        gameFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gameFrame.setLayout(null);
        gameFrame.setTitle("Battleship");
        gameFrame.setResizable(false);

        // Get center of screen and set the gui location to it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - 850) / 2;
        int y = (screenSize.height - 700) / 2;
        gameFrame.setLocation(x, y);

        // -------- USER battleship board, 8x8 grid ----------

        JPanel userBoardPanel = new JPanel();
        userBoardPanel.setBounds(30, 60, 350, 350);
        // 8x8 grid layout
        userBoardPanel.setLayout(new GridLayout(8, 8, 1, 1));
        userBoardPanel.setBackground(Color.black);
        gameFrame.add(userBoardPanel);

        JLabel userTitle = new JLabel(username + "'s Board", SwingConstants.LEFT);
        userTitle.setFont(myFont);
        userTitle.setBounds(30, 30, 350, 20);
        gameFrame.add(userTitle);

        // populate user's board with JButtons on them 
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                userButton[row][column] = new JButton();                
                // (text colour)
                userButton[row][column].setForeground(Color.red);
                userBoardPanel.add(userButton[row][column]);
            }
        }

        // -------- OPPONENT battleship board, 8x8 grid ----------

        JPanel oppBoardPanel = new JPanel();
        oppBoardPanel.setBounds(440, 60, 350, 350);
        oppBoardPanel.setLayout(new GridLayout(8, 8, 1, 1));
        oppBoardPanel.setBackground(Color.red);
        gameFrame.add(oppBoardPanel);

        oppTitle = new JLabel("Waiting for Opponent...", SwingConstants.RIGHT);
        oppTitle.setBounds(440, 30, 350, 20);
        oppTitle.setFont(myFont);
        gameFrame.add(oppTitle);

        // populates the board with panels with JButtons on them
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                oppButton[row][column] = new JButton();
                oppButton[row][column].setForeground(Color.red);
                oppButton[row][column].addActionListener(new ButtonPressed(row, column));
                oppBoardPanel.add(oppButton[row][column]);
                oppButton[row][column].setEnabled(false);
            }
        }

        // ------------ CHAT AREA WHERE MESSAGES ARE SENT ------------

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createLineBorder(Color.black));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(calibriFont);

        // adding a vertical scroll bar to the area where text is displayed
        // only shows up when there is enough text
        JScrollPane chatScrollPane = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // this makes chatArea automatically scoll to bottom when it is updated 
        DefaultCaret caret = (DefaultCaret)chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        chatScrollPane.setBounds(440, 450, 350, 150);
        gameFrame.add(chatScrollPane);

        // Create the area where user will type to enter text
        userTextArea = new JTextArea();
        userTextArea.setBorder(BorderFactory.createLineBorder(Color.black));
        // userTextArea.setBounds(80, 300, 480, 40);
        userTextArea.setLineWrap(true);
        userTextArea.setWrapStyleWord(true);
        userTextArea.setFont(calibriFont);
        JScrollPane textScrollPane = new JScrollPane(userTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textScrollPane.setBounds(440, 610, 270, 30);
        gameFrame.add(textScrollPane);

        // Create the send button to send whatever the user has typed in userTextArea
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setBounds(720, 610, 70, 30);
        sendButton.setFont(myFont);
        gameFrame.add(sendButton);

        defualt = sendButton.getBackground();

        // exit button to leave
        exitButton = new JButton("Quit");
        exitButton.addActionListener(this);
        exitButton.setBounds(30, 610, 60, 30);
        exitButton.setFont(myFont);
        gameFrame.add(exitButton);

        // ------------ PLACE SHIP COMPONENTS (drop down menus to select ship info, labels, and buttons) ----------

        // dropdown menu with all options for different info needed to place ship
        // ORIENTAIION
        String[] orientations = {"Horizontal", "Vertical"};
        orientationBool = new JComboBox<>(orientations);
        orientationBool.setBounds(140, 450, 90, 25);
        orientationBool.setFont(calibriFont);
        gameFrame.add(orientationBool);

        orientationLabel = new JLabel("Orientation");
        orientationLabel.setBounds(30, 450, 100, 25);
        orientationLabel.setFont(myFont);
        gameFrame.add(orientationLabel);

        // ROW
        String[] rowValues = {"1", "2", "3", "4", "5", "6", "7", "8"};
        rowNum = new JComboBox<>(rowValues);
        rowNum.setBounds(140, 480, 40, 25);
        rowNum.setFont(calibriFont);
        gameFrame.add(rowNum);

        rowNumLabel = new JLabel("Head Row #");
        rowNumLabel.setBounds(30, 480, 100, 25);
        rowNumLabel.setFont(myFont);
        gameFrame.add(rowNumLabel);

        // COLUMN
        String[] colValues = {"1", "2", "3", "4", "5", "6", "7", "8"};
        colNum = new JComboBox<>(colValues);
        colNum.setBounds(140, 510, 40, 25);
        colNum.setFont(calibriFont);
        gameFrame.add(colNum);

        colNumLabel = new JLabel("Head Column #");
        colNumLabel.setBounds(30, 510, 100, 25);
        colNumLabel.setFont(myFont);
        gameFrame.add(colNumLabel);

        // length comboBox requires model because it changes as ships are placed and when game is reset
        lengthNum = new JComboBox<>();
        lengthModel = new DefaultComboBoxModel<>();
        lengthNum.setModel(lengthModel);
        lengthNum.setBounds(140, 540, 40, 25);
        lengthNum.setFont(calibriFont);
        lengthModel.addElement("2");
        lengthModel.addElement("3");
        lengthModel.addElement("4");
        lengthModel.addElement("5");
        gameFrame.add(lengthNum);

        lengthNumLabel = new JLabel("Ship Length");
        lengthNumLabel.setBounds(30, 540, 100, 25);
        lengthNumLabel.setFont(myFont);
        gameFrame.add(lengthNumLabel);

        // DIRECTION
        String[] directions = {"Right/Down", "Left/Up"};
        directionBool = new JComboBox<>(directions);
        directionBool.setBounds(140, 570, 100, 25);
        directionBool.setFont(calibriFont);
        gameFrame.add(directionBool);

        directionLabel = new JLabel("Direction");
        directionLabel.setBounds(30, 570, 70, 25);
        directionLabel.setFont(myFont);
        gameFrame.add(directionLabel);

        // PLACE SHIP BUTTON
        placeShipButton = new JButton("Place Ship");
        placeShipButton.addActionListener(this);
        placeShipButton.setBounds(250, 450, 125, 50);
        placeShipButton.setFont(myFont);
        gameFrame.add(placeShipButton);

        // ---------------- OTHER COMPONENTS ------------------
        // HELP BUTTON
        helpButton = new JButton("Help");
        helpButton.addActionListener(this);
        helpButton.setBounds(250, 520, 125, 50);
        helpButton.setFont(myFont);
        gameFrame.add(helpButton);

        // PLAY AGAIN BUTTON
        playAgainButton = new JButton("Play Again");
        playAgainButton.addActionListener(this);
        playAgainButton.setBounds(250, 590, 125, 50);
        playAgainButton.setFont(myFont);
        gameFrame.add(playAgainButton);
        playAgainButton.setVisible(false);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setBounds(100, 610, 150, 30);
        messageLabel.setFont(myFont);
        gameFrame.add(messageLabel);

        // some lines to section off different parts made with thin panels with borders
        JLabel vertLine = new JLabel();
        vertLine.setBounds(406, 60, 8, 580);
        vertLine.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(4.0f)));
        gameFrame.add(vertLine);

        JLabel horzLine = new JLabel();
        horzLine.setBounds(30, 426, 760, 8);
        horzLine.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(4.0f)));
        gameFrame.add(horzLine);

        JLabel mainTitle = new JLabel("Battleship", SwingConstants.CENTER);
        mainTitle.setBounds(30, 17, 760, 38);
        mainTitle.setFont(new Font("Impact", Font.TRUETYPE_FONT, 38));
        gameFrame.add(mainTitle);


        gameFrame.pack();
        gameFrame.setVisible(true);
    }

    // class for when user clicks on opponent buttons 
    public class ButtonPressed implements ActionListener {

        int r;// row of button pressed
        int c;// column of button pressed

        public ButtonPressed(int row, int column) {
            // add 1 because JButton[][] is 0 to 7, 1 to 8 makes more sense when reading in chat
            r = row + 1;
            c = column + 1;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

            // send the attack message to the output stream for opponent to receive on clientReader thread
            writer.println("ATTACK! [" + username + "] strikes Row: " + r + ", Column: " + c);

            chatArea.setText(chatArea.getText() + "ATTACK! [" + username + "] strikes Row: " + r + ", Column: " + c + "\n");

            // disable opponent buttons on user's screen as it is now the opponent's turn
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    oppButton[r][c].setEnabled(false);
                }
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        // SEND MESSAGE BUTTON
        if (e.getSource() == sendButton) {

            // make sure user has typed something to send
            if (userTextArea.getText().isEmpty() == false) {

                // if user types 'bye' capitalize it so user doesnt get disconnected
                if (userTextArea.getText().equals("[" + username + "]: bye")) {
                    writer.println("Bye");
                } else {
                    // put user's text in writer (outputStream), this sends text to server
                    writer.println("[" + username + "]: " + userTextArea.getText().trim());
                }

                // manually add the user's text to the chat area
                chatArea.setText(chatArea.getText() + "[You]: " +
                        userTextArea.getText().trim() + "\n");

                userTextArea.setText("");

            }
        }

        // EXIT BUTTON
        if (e.getSource() == exitButton) {
            // send 'bye' to server so the server can remove user
            writer.println("bye");

            try {
                socket.close();
            } catch (IOException er) {
                // TODO Auto-generated catch block
                er.printStackTrace();
            }
            System.exit(0);
        }

        // PLACE SHIP BUTTON
        if (e.getSource() == placeShipButton) {

            // get all values chosen by user to place ship, try to place ship using values
            String lengthStr = (String) lengthNum.getSelectedItem();
            int shipLength = Integer.parseInt(lengthStr);
            int row = rowNum.getSelectedIndex();
            int col = colNum.getSelectedIndex();
            boolean orientation;
            if (orientationBool.getSelectedIndex() == 0) {
                orientation = true;
            } else orientation = false;
            boolean direction;
            if (directionBool.getSelectedIndex() == 0) {
                direction= true;
            } else direction = false;

            // System.out.println("length: " + shipLength + " , row: " + row + " col:" + col + " or: " + orientation + " dir: " + direction);

            populateBoard(shipLength, row, col, orientation, direction);

            // loop through board and change colours depending on ship length
            // remove the ship length from length options (only 1 of each ship can be placed)
            for (int r = 0; r < 8; r ++) {
                for (int c = 0; c < 8; c++) {
                    // blue == 5 block ship
                    if (userBoard[r][c] == 5) {
                        userButton[r][c].setBackground(Color.blue);
                        lengthModel.removeElement("5");         
                    } 
                    // green == 4 block ship
                    if (userBoard[r][c] == 4) {
                        userButton[r][c].setBackground(Color.green);   
                        lengthModel.removeElement("4");               
                    } 
                    // yellow == 3 block ship
                    if (userBoard[r][c] == 3) {
                        userButton[r][c].setBackground(Color.yellow);            
                        lengthModel.removeElement("3");      
                    } 
                    // pink == 2 block ship
                    if (userBoard[r][c] == 2) {
                        userButton[r][c].setBackground(Color.pink);
                        lengthModel.removeElement("2");      
                    } 
                }
            }

            // if the user has placed all 4 of their ships, send READY message
            if (lengthModel.getSize() == 0) {

                writer.println("READY! [" + username + "] has placed all of their ships");
                chatArea.setText(chatArea.getText() + "READY! [" + username + "] has placed all of their ships\n");
                readyPlayers++;

                placeShipButton.setEnabled(false);

                // if you are the second player to ready up, you will not attack first!
                if (readyPlayers == 2) {
                    messageLabel.setText("");
                    // for (int r = 0; r < 8; r++) {
                    //     for (int c = 0; c < 8; c++) {
                    //         oppButton[r][c].setEnabled(true);
                    //     }
                    // }
                }
            }

        }

        // PLAY AGAIN BUTTON
        if (e.getSource() == playAgainButton) {
            playAgainButton.setVisible(false);
           
            // loop through boards and reset them 
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    oppButton[r][c].setEnabled(false);
                    oppButton[r][c].setBackground(defualt);
                    oppButton[r][c].setText("");
                    userButton[r][c].setBackground(defualt);
                    userBoard[r][c] = 0;
                    userButton[r][c].setText("");
                }
            }
            // re-add all ship length options 
            lengthModel.addElement("2");
            lengthModel.addElement("3");
            lengthModel.addElement("4");
            lengthModel.addElement("5");
            
            placeShipButton.setEnabled(true);
            messageLabel.setText("");
            readyPlayers = 0;
        }

        // open up the help menu gui
        if (e.getSource() == helpButton) {
            new HelpGui();
        }
        
    }

    // add ship to the board with parameters choesen by user
    public void populateBoard(int len, int row, int col, boolean orient, boolean dir) {

        // true == horizontal ship 
        // false == vertical ship
        boolean orientation = orient;

        // true == ship goes right / down from head (depending on orientation)
        // false == ship goes left / up from head (depending on orientation)
        boolean direction = dir;

        // the position of the HEAD of the ship on the board
        int x = row;
        int y = col;
        
        int length = len;

        // integer representing whethere a specific square is empty or not 
        boolean emptySquare = true;

        //System.out.println("placing ships");

        // horizontal
        if (orientation) {
            // ship goes right from head
            if (direction) {

                // both points are one the board
                if (y + length <= 8) {
                    for (int i = y; i < y + length; i++) {
                        // square is already occupied
                        if (userBoard[x][i] != 0) {
                            emptySquare = false;
                            messageLabel.setText("Could not place ship");
                        }
                    }

                    // ship can be placed here
                    if (emptySquare) {
                        for (int i = y; i < y + length; i++) {
                            userBoard[x][i] = length;

                            messageLabel.setText("Ship placed");

                            //userButton[x][i].setBackground(Color.ORANGE);
                        }
                        return;
                    }
                }
            }

            // ship goes left from head
            else {
                if (y - length >= -1) {
                    for (int i = y; i > y - length; i--) {
                        // square is already occupied
                        if (userBoard[x][i] != 0) {
                            emptySquare = false;
                            messageLabel.setText("Could not place ship");

                        }
                    }

                    // ship can be placed here
                    if (emptySquare) {
                        for (int i = y; i > y - length; i--) {

                            userBoard[x][i] = length;
                            messageLabel.setText("Ship placed");

                            //userButton[x][i].setBackground(Color.ORANGE);
                        }
                        return;
                    }
                }
            }

        }

        // vertical
        if (!orientation) {
            // ship goes up from head
            if (!direction) {
                // both points are one the board
                if (x - length >= -1) {
                    for (int i = x; i > x - length; i--) {
                        // square is already occupied
                        if (userBoard[i][y] != 0) {
                            emptySquare = false;
                            messageLabel.setText("Could not place ship");

                        }

                    }

                    // ship can be placed here
                    if (emptySquare) {
                        for (int i = x; i > x - length; i--) {
                            userBoard[i][y] = length;
                            messageLabel.setText("Ship placed");

                            // userButton[i][y].setBackground(Color.GREEN);

                        }
                        return;
                    }
                }
            }

            // ship goes down from head
            else {
                if (x + length <= 8) {
                    for (int i = x; i < x + length; i++) {
                        // square is already occupied
                        if (userBoard[i][y] != 0) {
                            emptySquare = false;
                            messageLabel.setText("Could not place ship");

                        }

                    }

                    // ship can be placed here
                    if (emptySquare) {
                        for (int i = x; i < x + length; i++) {

                            userBoard[i][y] = length;
                            messageLabel.setText("Ship placed");

                            //userButton[i][y].setBackground(Color.BLUE);
                        }
                        return;
                    }
                }
            }
        }
    }

    // check if there are 14 "X" on the board, (14 because there are 4 ships that add up to 14 blocks) 
    public int checkGame() {
        int counter = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (oppButton[r][c].getText().equals("X")) {
                    counter++;
                }
            }
        }
        return counter;
    }
}