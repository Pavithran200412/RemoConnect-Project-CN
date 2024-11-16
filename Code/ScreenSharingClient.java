import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;

public class ScreenSharingClient {
    private static final int PORT = 2212;
    private Socket socket;
    private DataInputStream dataIn;
    private JFrame frame;
    private JLabel screenLabel;

    public ScreenSharingClient(String serverIp) {
        try {
            socket = new Socket(serverIp, PORT);
            dataIn = new DataInputStream(socket.getInputStream());

            frame = new JFrame("Screen Sharing Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);

            screenLabel = new JLabel();
            frame.add(screenLabel, BorderLayout.CENTER);

            // Start receiving screen data
            receiveScreenData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveScreenData() {
        try {
            while (true) {
                // Read the image data size
                int imageLength = dataIn.readInt();
                byte[] imageData = new byte[imageLength];

                // Read the image bytes
                dataIn.readFully(imageData);

                // Convert byte array to image
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

                // Display the image
                ImageIcon imageIcon = new ImageIcon(image);
                screenLabel.setIcon(imageIcon);

                // Refresh the client window
                frame.repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverIp = JOptionPane.showInputDialog("Enter the server IP address:");
        new ScreenSharingClient(serverIp);
    }
}