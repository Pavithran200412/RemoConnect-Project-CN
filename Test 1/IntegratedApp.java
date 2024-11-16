import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class IntegratedApp {
    private JFrame frame;
    private boolean isServer;

    public IntegratedApp(boolean isServer) {
        this.isServer = isServer;
        setupGUI();
    }

    private void setupGUI() {
        frame = new JFrame("Integrated Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton chatButton = new JButton("Chat");
        chatButton.addActionListener(e -> openChat());

        JButton fileTransferButton = new JButton("File Transfer");
        fileTransferButton.addActionListener(e -> openFileTransfer());

        JButton screenSharingButton = new JButton("Screen Sharing");
        screenSharingButton.addActionListener(e -> openScreenSharing());

        panel.add(chatButton);
        panel.add(fileTransferButton);
        panel.add(screenSharingButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void openChat() {
    frame.dispose();
    if (isServer) {
        try {
            new ChatServer(); // Starts the chat server
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error starting Chat Server: " + e.getMessage());
        }
    } else {
        String serverIp = JOptionPane.showInputDialog("Enter the server IP address for Chat:");
        if (serverIp != null && !serverIp.isEmpty()) {
            try {
                new ChatClient(serverIp); // Connects to the chat server
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to Chat Server: " + e.getMessage());
            }
        }
    }
}

private void openFileTransfer() {
    frame.dispose();

    if (isServer) {
        new Thread(() -> {
            new FtpServer(); // Start the server
        }).start();
        JOptionPane.showMessageDialog(null, "File Transfer Server started.");
        new IntegratedApp(true); // Return to the main GUI
    } else {
        new Thread(() -> {
            new FtpClient(); // Start the client
        }).start();
    }
}


    private void openScreenSharing() {
        frame.dispose();
        if (isServer) {
            new Thread(() -> new ScreenSharingServer()).start();
            JOptionPane.showMessageDialog(null, "Screen Sharing Server started.");
            new IntegratedApp(true); // Return to the main GUI
        } else {
            String serverIp = JOptionPane.showInputDialog("Enter the server IP address for Screen Sharing:");
            if (serverIp != null && !serverIp.isEmpty()) {
                new Thread(() -> new ScreenSharingClient(serverIp)).start();
            }
        }
    }

    public static void main(String[] args) {
        boolean isServer = JOptionPane.showConfirmDialog(null, "Start as server?") == JOptionPane.YES_OPTION;
        SwingUtilities.invokeLater(() -> new IntegratedApp(isServer));
    }
}
