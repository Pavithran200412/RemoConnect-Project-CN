import java.awt.*;
import javax.swing.*;

public class MyPanel extends JPanel {
    private JButton jcomp1;  // Chat
    private JButton jcomp2;  // Share File
    private JButton jcomp3;  // Screen Share
    private JButton jcomp4;  // Comment
    private JLabel jcomp5;

    public MyPanel() {
        // Construct components
        jcomp1 = new JButton("Chat");
        jcomp2 = new JButton("Share File");
        jcomp3 = new JButton("Screen Share");
        jcomp4 = new JButton("Command");
        jcomp5 = new JLabel("Option Menu");

        // Adjust size and set layout
        setPreferredSize(new Dimension(754, 427));
        setLayout(null);

        // Add components
        add(jcomp1);
        add(jcomp2);
        add(jcomp3);
        add(jcomp4);
        add(jcomp5);

        // Set component bounds (only needed by Absolute Positioning)
        jcomp1.setBounds(230, 15, 285, 65);
        jcomp2.setBounds(230, 110, 285, 65);
        jcomp3.setBounds(230, 205, 285, 65);
        jcomp4.setBounds(230, 305, 285, 65);
        jcomp5.setBounds(20, 15, 170, 50);
    }

    // Getter methods for buttons
    public JButton getChatButton() {
        return jcomp1;
    }

    public JButton getFileTransferButton() {
        return jcomp2;
    }

    public JButton getScreenShareButton() {
        return jcomp3;
    }

    public JButton getCommentButton() {
        return jcomp4;
    }
}
