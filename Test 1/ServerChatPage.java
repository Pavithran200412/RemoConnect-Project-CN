import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ServerChatPage {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerChatPage() {
        setupGUI();
        startServer();
    }

    private void setupGUI() {
        frame = new JFrame("Server Chat");
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

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(12345);
                chatArea.append("Server started. Waiting for client...\n");
                clientSocket = serverSocket.accept();
                chatArea.append("Client connected.\n");

                // Redirect to Options Page
                frame.dispose();
                new OptionsPage(true);

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append("Client: " + message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Server error: " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            chatArea.append("Server: " + message + "\n");
            messageField.setText("");
        }
    }
}
