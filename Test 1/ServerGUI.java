import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class ServerGUI {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton disconnectButton;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread clientHandlerThread;

    public ServerGUI() {
        frame = new JFrame("Server");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        disconnectButton = new JButton("Disconnect");
        sendButton.setEnabled(false); // Disable until connected
        disconnectButton.setEnabled(false);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(disconnectButton, BorderLayout.SOUTH);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        startServer();

        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                sendMessageToClient(message);
                messageArea.append("Server: " + message + "\n");
                inputField.setText("");
            }
        });

        disconnectButton.addActionListener(e -> disconnect());
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(12345);
            messageArea.append("Server started. Waiting for connection...\n");

            clientHandlerThread = new Thread(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    messageArea.append("Client connected.\n");
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    sendButton.setEnabled(true);
                    disconnectButton.setEnabled(true);

                    String message;
                    while ((message = in.readLine()) != null) {
                        messageArea.append("Client: " + message + "\n");
                    }
                } catch (IOException e) {
                    messageArea.append("Client disconnected.\n");
                } finally {
                    disconnect();
                }
            });
            clientHandlerThread.start();

        } catch (IOException e) {
            messageArea.append("Could not start the server: " + e.getMessage() + "\n");
        }
    }

    private void sendMessageToClient(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void disconnect() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            sendButton.setEnabled(false);
            disconnectButton.setEnabled(false);
            messageArea.append("Disconnected.\n");
        } catch (IOException e) {
            messageArea.append("Error while disconnecting: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}