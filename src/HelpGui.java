import java.awt.*;
import javax.swing.*;

// a gui that opens up with some info on how to play the game
public class HelpGui {

    public HelpGui() {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(410, 400));
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        frame.setLayout(null);
        frame.setTitle("Battleship Help");
        frame.setResizable(false);

        // Get center of screen and set the gui location to it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - 410) / 2;
        int y = (screenSize.height - 350) / 2;
        frame.setLocation(x, y);

        Font myFont = new Font("Impact", Font.TRUETYPE_FONT, 20);
        Font calibriFont = new Font("Calibri", Font.BOLD, 13);

        JLabel title = new JLabel("Welcome to Battleship!", SwingConstants.CENTER);
        title.setBounds(30,30,350, 25);
        title.setFont(myFont);
        frame.add(title);

        JLabel intro = new JLabel("<html>The objective of Battleship is to destroy your opponents ships before they destroy yours</html>", SwingConstants.CENTER);
        intro.setBounds(30, 60, 350, 45);
        intro.setFont(calibriFont);
        frame.add(intro);        

        JLabel intro2 = new JLabel("<html>Get started by placing your 4 ships using the options at the bottom left</html>", SwingConstants.CENTER);
        intro2.setBounds(30, 100, 350, 45);
        intro2.setFont(calibriFont);
        frame.add(intro2);

        JLabel placingLabel = new JLabel("<html>When placing ships, select the location of the front of the ship and use the orientation and direction settings to build the ship out from its head. Try to be quick to get the first attack!</html>", SwingConstants.CENTER);
        placingLabel.setBounds(30, 150, 350, 45);
        placingLabel.setFont(calibriFont);
        frame.add(placingLabel);

        JLabel attackLabel = new JLabel("<html>Once both players' ships have been placed, you will be able to take turns attacking the other player on the right side board. An \'X\' on the right board indicates a hit while an \'X\' on the left board shows where your opponent has attacked</html>", SwingConstants.CENTER);
        attackLabel.setBounds(30, 205, 350, 65);
        attackLabel.setFont(calibriFont);
        frame.add(attackLabel);

        JLabel chatLabel = new JLabel("<html>The chat box in the bottom right will show infomation about the game as you play. You can also message your opponent here</html>", SwingConstants.CENTER);
        chatLabel.setBounds(30, 280, 350, 45);
        chatLabel.setFont(calibriFont);
        frame.add(chatLabel);


        frame.pack();
        frame.setVisible(true);
    }
    
}