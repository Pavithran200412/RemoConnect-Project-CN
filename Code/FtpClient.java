import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class FtpClient {
    private static final int PORT = 9002;
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    private JFrame frame;
    private JTextArea textArea;
    private JButton sendButton, receiveButton, disconnectButton;

    private String serverIpAddress;

    public FtpClient() {
        try {
            frame = new JFrame("FTP Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 1));

            sendButton = new JButton("SEND");
            sendButton.addActionListener(e -> sendFileAction());
            panel.add(sendButton);

            receiveButton = new JButton("RECEIVE");
            receiveButton.addActionListener(e -> receiveFileAction());
            panel.add(receiveButton);

            disconnectButton = new JButton("Disconnect");
            disconnectButton.addActionListener(e -> disconnectAction());
            panel.add(disconnectButton);

            frame.add(panel, BorderLayout.SOUTH);
            frame.setVisible(true);

            serverIpAddress = JOptionPane.showInputDialog(frame, "Enter the server IP address:");
            if (serverIpAddress == null || serverIpAddress.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Invalid IP address", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            socket = new Socket(serverIpAddress, PORT);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendFileAction() {
        try {
            String filePath = JOptionPane.showInputDialog(frame, "Enter file path to send:");
            if (filePath != null && !filePath.isEmpty()) {
                sendFile(filePath);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error sending file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void receiveFileAction() {
        try {
            String fileName = JOptionPane.showInputDialog(frame, "Enter file name to receive:");
            if (fileName != null && !fileName.isEmpty()) {
                dataOut.writeUTF("RECEIVE");
                dataOut.writeUTF(fileName);
                receiveFile(fileName);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error receiving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(frame, "File not found: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dataOut.writeUTF("SEND");
        dataOut.writeUTF(file.getName());
        dataOut.writeLong(file.length());

        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                dataOut.write(buffer, 0, bytesRead);
            }
        }

        textArea.append("File sent: " + file.getName() + "\n");
    }

    private void receiveFile(String fileName) throws IOException {
        String serverResponse = dataIn.readUTF();
        if (serverResponse.equals("ERROR")) {
            JOptionPane.showMessageDialog(frame, "File not found on server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String receivedFileName = dataIn.readUTF();
        long fileSize = dataIn.readLong();

        File file = new File("received_" + receivedFileName);
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long totalRead = 0;
            int bytesRead;

            while (totalRead < fileSize && (bytesRead = dataIn.read(buffer, 0, Math.min(buffer.length, (int)(fileSize - totalRead)))) != -1) {
                fileOut.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }
        }

        textArea.append("File received: " + file.getName() + "\n");
    }

    private void disconnectAction() {
        try {
            dataIn.close();
            dataOut.close();
            socket.close();
            textArea.append("Disconnected from server\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error disconnecting: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FtpClient());
    }
}
