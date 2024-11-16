import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class CommandClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JTextArea commentArea;
    private JTextField commentField;

    public CommandClient(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        setupGUI();
        receiveFeedback();
    }

    private void setupGUI() {
        JFrame frame = new JFrame("Command System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        commentArea = new JTextArea();
        commentArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        commentField = new JTextField();
        frame.add(commentField, BorderLayout.SOUTH);

        JButton sendButton = new JButton("Send Command");
        sendButton.addActionListener(e -> sendCommand());
        frame.add(sendButton, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private void sendCommand() {
        String command = commentField.getText();
        if (!command.isEmpty()) {
            out.println(command);  // Send the command to the server
            commentField.setText("");  // Clear the text field
        }
    }

    private void receiveFeedback() {
        new Thread(() -> {
            try {
                String feedback;
                while ((feedback = in.readLine()) != null) {
                    commentArea.append(feedback + "\n");  // Display feedback (optional)
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        String serverIp = JOptionPane.showInputDialog("Enter the server IP address for Command System:");
        if (serverIp != null && !serverIp.isEmpty()) {
            try {
                new CommandClient(serverIp, 2222);  
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage());
            }
        }
    }
}
