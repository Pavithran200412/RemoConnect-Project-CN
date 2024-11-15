import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientChatPage {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientChatPage(String serverIp) {
        setupGUI();
        connectToServer(serverIp);
    }

    private void setupGUI() {
        frame = new JFrame("Client Chat");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void connectToServer(String serverIp) {
        new Thread(() -> {
            try {
                socket = new Socket(serverIp, 12345);
                chatArea.append("Connected to server.\n");

                // Redirect to Options Page
                frame.dispose();
                new OptionsPage(false);

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append("Server: " + message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Error: Unable to connect to server. " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            chatArea.append("Client: " + message + "\n");
            messageField.setText("");
        }
    }
}
