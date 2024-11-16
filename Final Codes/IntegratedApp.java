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
        frame.setSize(800, 600);

        MyPanel panel = new MyPanel();  // Create instance of MyPanel
        frame.getContentPane().add(panel);

        // Action listeners for MyPanel buttons using getter methods
        panel.getChatButton().addActionListener(e -> openChat());
        panel.getFileTransferButton().addActionListener(e -> openFileTransfer());
        panel.getScreenShareButton().addActionListener(e -> openScreenSharing());
        panel.getCommentButton().addActionListener(e -> openComment());

        frame.setVisible(true);
    }

    private void openChat() {
        frame.dispose();  // Close the main menu
        if (isServer) {
            try {
                new ChatServer(); // Starts the chat server
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error starting Chat Server: " + e.getMessage());
            }
        } else {
            String serverIp = JOptionPane.showInputDialog("Enter the server IP address for Chat:");
            if (serverIp != null && !serverIp.isEmpty()) {
                try {
                    new ChatClient(serverIp); // Connects to the chat server
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error connecting to Chat Server: " + e.getMessage());
                }
            }
        }
    }

    private void openFileTransfer() {
        frame.dispose();  // Close the main menu
        if (isServer) {
            new Thread(() -> {
                new FtpServer(); // Start the server
            }).start();
            JOptionPane.showMessageDialog(null, "File Transfer Server started.");
        } else {
            new Thread(() -> {
                new FtpClient(); // Start the client
            }).start();
        }
    }

    private void openScreenSharing() {
        frame.dispose();  // Close the main menu
        if (isServer) {
            new Thread(() -> new ScreenSharingServer()).start();
            JOptionPane.showMessageDialog(null, "Screen Sharing Server started.");
        } else {
            String serverIp = JOptionPane.showInputDialog("Enter the server IP address for Screen Sharing:");
            if (serverIp != null && !serverIp.isEmpty()) {
                new Thread(() -> new ScreenSharingClient(serverIp)).start();
            }
        }
    }

    private void openComment() {
        frame.dispose();
        if (isServer) {
            new Thread(() -> {
                try {
                    new CommandServer(4444);  // Start CommandServer on a separate thread
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(null, "Error starting Command Server: " + e.getMessage()));
                }
            }).start();
            JOptionPane.showMessageDialog(null, "Command Server started on port 4444.");
        } else {
            String serverIp = JOptionPane.showInputDialog("Enter the server IP address for Command System:");
            if (serverIp != null && !serverIp.isEmpty()) {
                new Thread(() -> {
                    try {
                        new CommandClient(serverIp, 4444);  // Connect to CommandServer
                    } catch (IOException e) {
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(null, "Error connecting to Command Server: " + e.getMessage()));
                    }
                }).start();
            }
        }
    }
    

    public static void main(String[] args) {
        boolean isServer = JOptionPane.showConfirmDialog(null, "Start as server?") == JOptionPane.YES_OPTION;
        SwingUtilities.invokeLater(() -> new IntegratedApp(isServer));
    }
}























































































/*import java.awt.*;
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
*/