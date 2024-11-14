import java.awt.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class ConnectPage {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private JTextField ipField;
    private JButton connectButton;
    private JButton sendButton;
    private JButton disconnectButton;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread serverHandlerThread;

    public ConnectPage() {
        frame = new JFrame("Client");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel connectPanel = new JPanel(new BorderLayout());
        ipField = new JTextField();
        ipField.setBorder(BorderFactory.createTitledBorder("Enter Server IP Address"));
        connectButton = new JButton("Connect");
        connectPanel.add(ipField, BorderLayout.CENTER);
        connectPanel.add(connectButton, BorderLayout.EAST);
        frame.add(connectPanel, BorderLayout.NORTH);

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

        connectButton.addActionListener(e -> {
            String ipAddress = ipField.getText().trim();
            if (!ipAddress.isEmpty()) {
                connectToServer(ipAddress);
            } else {
                messageArea.append("Please enter a valid IP address.\n");
            }
        });

        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                sendMessageToServer(message);
                messageArea.append("Client: " + message + "\n");
                inputField.setText("");
            }
        });

        disconnectButton.addActionListener(e -> disconnect());
    }

    private void connectToServer(String ipAddress) {
        try {
            socket = new Socket(ipAddress, 12345);
            messageArea.append("Connected to the server at " + ipAddress + "\n");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            sendButton.setEnabled(true);
            disconnectButton.setEnabled(true);

            serverHandlerThread = new Thread(new ServerHandler());
            serverHandlerThread.start();
        } catch (IOException e) {
            messageArea.append("Connection failed: " + e.getMessage() + "\n");
        }
    }

    private void sendMessageToServer(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            sendButton.setEnabled(false);
            disconnectButton.setEnabled(false);
            messageArea.append("Disconnected from server.\n");
        } catch (IOException e) {
            messageArea.append("Error while disconnecting: " + e.getMessage() + "\n");
        }
    }

    private class ServerHandler implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    messageArea.append("Server: " + message + "\n");
                }
            } catch (IOException e) {
                messageArea.append("Server disconnected.\n");
                disconnect();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConnectPage::new);
    }
}