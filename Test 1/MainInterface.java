import java.awt.*;
import javax.swing.*;

public class MainInterface {
    private JFrame frame;

    public MainInterface() {
        frame = new JFrame("Main Interface");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1)); // Two buttons stacked vertically

        // Server button
        JButton serverButton = new JButton("Server");
        serverButton.addActionListener(e -> startServer());
        panel.add(serverButton);

        // Client button
        JButton clientButton = new JButton("Client");
        clientButton.addActionListener(e -> startClient());
        panel.add(clientButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void startServer() {
        frame.dispose();
        new ServerGUI(); // Open the Server chat interface
    }

    private void startClient() {
        String ip = JOptionPane.showInputDialog(frame, "Enter Server IP to connect:");
        if (ip != null && !ip.isEmpty()) {
            frame.dispose();
            new ClientChatPage(ip); // Open the Client chat interface
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid IP address. Please try again.");
        }
    }

    public static void main(String[] args) {
        new MainInterface();
    }
}
