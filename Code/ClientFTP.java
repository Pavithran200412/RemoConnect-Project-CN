/*import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class ClientFTP {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private JTextField ipField;
    private JButton connectButton;
    private JButton sendButton;
    private JButton disconnectButton;
    private JButton transferFileButton;
    private FTPClient ftpClient;

    public ClientFTP() {
        frame = new JFrame("Client FTP");
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

        JPanel inputPanel = new JPanel(new GridLayout(1, 4));
        inputField = new JTextField();
        sendButton = new JButton("Send");
        disconnectButton = new JButton("Disconnect");
        transferFileButton = new JButton("Transfer File");

        sendButton.setEnabled(false);
        disconnectButton.setEnabled(false);
        transferFileButton.setEnabled(false);

        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        inputPanel.add(disconnectButton);
        inputPanel.add(transferFileButton);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        connectButton.addActionListener(e -> {
            String ipAddress = ipField.getText().trim();
            if (!ipAddress.isEmpty()) {
                connectToFtpServer(ipAddress);
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
        transferFileButton.addActionListener(e -> selectAndSendFile());
    }

    private void connectToFtpServer(String ipAddress) {
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ipAddress, 21); // Replace with actual server IP and port
            ftpClient.login("user", "password"); // Replace with actual credentials
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            messageArea.append("Connected to FTP server at " + ipAddress + "\n");

            sendButton.setEnabled(true);
            disconnectButton.setEnabled(true);
            transferFileButton.setEnabled(true);
        } catch (IOException e) {
            messageArea.append("Error connecting to FTP server: " + e.getMessage() + "\n");
        }
    }

    private void sendMessageToServer(String message) {
        // This can be used to send messages over the TCP connection if needed
        messageArea.append("Sending message: " + message + "\n");
    }

    private void selectAndSendFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            sendFileToServer(file);
        }
    }

    private void sendFileToServer(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            boolean success = ftpClient.storeFile(file.getName(), fileInputStream);
            if (success) {
                messageArea.append("File sent to FTP server: " + file.getName() + "\n");
            } else {
                messageArea.append("Error sending file.\n");
            }
        } catch (IOException e) {
            messageArea.append("Error sending file: " + e.getMessage() + "\n");
        }
    }

    private void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                messageArea.append("Disconnected from FTP server.\n");
            }
            sendButton.setEnabled(false);
            disconnectButton.setEnabled(false);
            transferFileButton.setEnabled(false);
        } catch (IOException e) {
            messageArea.append("Error while disconnecting: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientFTP::new);
    }
}*/