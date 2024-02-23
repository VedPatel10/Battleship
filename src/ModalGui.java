import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.swing.*;

// The modal gui is the interface for the user to log into a chat server
// The user must enter a host name and a username to join
public class ModalGui implements ActionListener {

    // components on gui
    JTextField hostText;
    JTextField userText;
    JLabel loginMessage;
    JFrame modalFrame;

    public ModalGui() {

        // setup frame and panel of modal box gui
        modalFrame = new JFrame();
        JPanel modalPanel = new JPanel();
        int guiWidth = 350;
        int guiHeight = 220;// dont make var
        modalFrame.setSize(guiWidth, guiHeight);
        modalFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        modalFrame.add(modalPanel);
        modalPanel.setLayout(null);
        modalFrame.setTitle("Battleship Login");
        modalFrame.setResizable(false);

        // Get center of screen and set the gui location to it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - modalFrame.getWidth()) / 2;
        int y = (screenSize.height - modalFrame.getHeight()) / 2;
        modalFrame.setLocation(x, y);

        Font impactFont = new Font("Impact", Font.TRUETYPE_FONT, 16);
        Font calibriFont = new Font("Calibri", Font.BOLD, 14);

        JLabel title = new JLabel("Battleship", SwingConstants.CENTER);
        title.setBounds(10, 15, 330, 25);
        title.setFont(new Font("Impact", Font.TRUETYPE_FONT, 24));
        modalPanel.add(title);

        // Create label and textfield asking for host name
        JLabel hostLabel = new JLabel("Host");
        hostLabel.setBounds(10, 50, 80, 25);
        hostLabel.setFont(impactFont);
        modalPanel.add(hostLabel);

        hostText = new JTextField(20);
        hostText.setBounds(92, 50, 165, 25);
        hostText.setFont(calibriFont);
        modalPanel.add(hostText);

        // Create label and textfield asking for username
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(10, 80, 80, 25);
        userLabel.setFont(impactFont);
        modalPanel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(92, 80, 165, 25);
        userText.setFont(calibriFont);
        modalPanel.add(userText);

        // Create button to join server
        JButton joinButton = new JButton("Join");
        joinButton.setBounds((guiWidth / 2) - 50, 110, 100, 30);
        joinButton.setFont(impactFont);
        modalPanel.add(joinButton);
        joinButton.addActionListener(this);

        // Create message when user tries to login
        loginMessage = new JLabel("", SwingConstants.CENTER);
        loginMessage.setBounds(10, 145, 330, 25);
        loginMessage.setFont(impactFont);
        modalPanel.add(loginMessage);

        // Set frame to visible after all items are created they all appear properly
        modalFrame.setVisible(true);

    }

    @Override
    // WHEN JOIN BUTTON PRESSED
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        // Make sure user has entered host name and username
        if ((userText.getText()).isEmpty() == true) {
            loginMessage.setText("Enter a username");
        } else if ((hostText.getText()).isEmpty() == true) {
            loginMessage.setText("Enter a host to join");
        } else if (hostText.getText().contains("]") || hostText.getText().contains("[")) {
            // checks to see if username has brackets, this helps make sure the list of
            // users works properly
            loginMessage.setText("Username cannot contain brackets");
        } else {
            // if the user has entered a username and host
            try {
                Client client = new Client();
                // try to create a socket with the host that the user has provided and port 8888
                // hard coded
                Socket socket = new Socket(hostText.getText(), 8888);
                client.startClient(userText.getText().trim(), hostText.getText(), socket);
                // close modal gui as it is no longer needed
                modalFrame.dispose();
            } catch (Exception er) {
                // TODO: handle exception
                // socket could not be created as host provided could not be found
                loginMessage.setText("Could not find server");
            }

        }

    }

}