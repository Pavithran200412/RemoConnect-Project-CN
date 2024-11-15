import java.awt.*;
import javax.swing.*;

public class OptionsPage {
    private JFrame frame;
    private JButton chatButton;
    private JButton fileSharingButton;
    private JButton screenSharingButton;

    public OptionsPage(boolean isServer) {
        frame = new JFrame(isServer ? "Server Options" : "Client Options");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1)); // Arrange buttons vertically

        // Chat Button
        chatButton = new JButton("Chat");
        chatButton.addActionListener(e -> openChat(isServer));
        panel.add(chatButton);

        // File Sharing Button
        fileSharingButton = new JButton("File Sharing");
        fileSharingButton.addActionListener(e -> openFileTransfer(isServer));
        panel.add(fileSharingButton);

        // Screen Sharing Button
        screenSharingButton = new JButton("Screen Sharing");
        screenSharingButton.addActionListener(e -> startScreenSharing(isServer));
        panel.add(screenSharingButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void openChat(boolean isServer) {
        frame.dispose();
        if (isServer) {
            new ServerGUI(); // Launch Server Chat Page
        } else {
            String ip = JOptionPane.showInputDialog("Enter Server IP for Chat:");
            if (ip != null && !ip.isEmpty()) {
                new ClientChatPage(ip); // Launch Client Chat Page
            } else {
                JOptionPane.showMessageDialog(null, "Invalid IP Address!");
            }
        }
    }

    private void openFileTransfer(boolean isServer) {
        frame.dispose();
        if (isServer) {
            new Thread(() -> FtpServer.main(null)).start(); // Start FTP Server
            JOptionPane.showMessageDialog(null, "File Transfer Server started!");
        } else {
            new FtpClient(); // Open FTP Client GUI
        }
    }

    private void startScreenSharing(boolean isServer) {
        frame.dispose();
        if (isServer) {
            new Thread(() -> ScreenSharingServer.main(null)).start(); // Start Screen Sharing Server
            JOptionPane.showMessageDialog(null, "Screen Sharing Server started!");
        } else {
            String ip = JOptionPane.showInputDialog("Enter Server IP for Screen Sharing:");
            if (ip != null && !ip.isEmpty()) {
                new ScreenSharingClient(ip); // Launch Screen Sharing Client
            } else {
                JOptionPane.showMessageDialog(null, "Invalid IP Address!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OptionsPage(true)); // Start with server mode by default
    }
}
