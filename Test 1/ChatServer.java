import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatServer {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatServer() throws IOException {
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

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(9000);
        chatArea.append("Server started. Waiting for client...\n");
        clientSocket = serverSocket.accept();
        chatArea.append("Client connected.\n");

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append("Client: " + message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Error: " + e.getMessage() + "\n");
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
