import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class FtpServer {
    private static final int PORT = 9002;
    private JTextArea logArea;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FtpServer server = new FtpServer();
            server.createAndShowGUI();
            new Thread(server::startServer).start();
        });
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("FTP Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logArea = new JTextArea(20, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JButton stopButton = new JButton("Stop Server");
        stopButton.addActionListener(e -> stopServer());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(stopButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            logMessage("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logMessage("Client connected: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            logMessage("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                String command = dataIn.readUTF();
                if ("SEND".equals(command)) {
                    receiveFile(dataIn, dataOut);
                } else if ("RECEIVE".equals(command)) {
                    sendFile(dataIn, dataOut);
                } else {
                    break;
                }
            }

            logMessage("Client disconnected: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            logMessage("Error handling client: " + e.getMessage());
        }
    }

    private void receiveFile(DataInputStream dataIn, DataOutputStream dataOut) {
        try {
            String fileName = dataIn.readUTF();
            long fileSize = dataIn.readLong();
            File file = new File("received_" + fileName);

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                long totalRead = 0;
                int bytesRead;
                while (totalRead < fileSize &&
                        (bytesRead = dataIn.read(buffer, 0, Math.min(buffer.length, (int) (fileSize - totalRead)))) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
            }

            logMessage("File received: " + fileName);
            dataOut.writeUTF("File received: " + fileName);
        } catch (IOException e) {
            try {
                dataOut.writeUTF("ERROR");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            logMessage("Error receiving file: " + e.getMessage());
        }
    }

    private void sendFile(DataInputStream dataIn, DataOutputStream dataOut) {
        try {
            String fileName = dataIn.readUTF();
            File file = new File(fileName);

            if (!file.exists()) {
                dataOut.writeUTF("ERROR");
                logMessage("File not found on server: " + fileName);
                return;
            }

            dataOut.writeUTF(file.getName());
            dataOut.writeLong(file.length());

            try (FileInputStream fileIn = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
            }

            logMessage("File sent: " + file.getName());
            dataOut.writeUTF("File sent: " + file.getName());
        } catch (IOException e) {
            logMessage("Error sending file: " + e.getMessage());
        }
    }

    private void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                logMessage("Server stopped.");
            }
        } catch (IOException e) {
            logMessage("Error stopping server: " + e.getMessage());
        }
    }
}
