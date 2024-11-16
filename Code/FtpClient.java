import java.io.*;
import java.net.*;
import javax.swing.*;

public class FtpClient {
    private static final int PORT = 3107;

    public FtpClient() {
        try {
            String serverIp = JOptionPane.showInputDialog("Enter the server IP address:");
            if (serverIp == null || serverIp.isEmpty()) {
                return;
            }

            try (Socket socket = new Socket(serverIp, PORT);
                 DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream())) {

                String[] options = {"Send File", "Receive File"};
                int choice = JOptionPane.showOptionDialog(null, "Choose an option:", "File Transfer Client",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    // Send File
                    String filePath = JOptionPane.showInputDialog("Enter the file path to send:");
                    sendFile(filePath, dataOut);
                } else if (choice == 1) {
                    // Receive File
                    String fileName = JOptionPane.showInputDialog("Enter the file name to receive:");
                    dataOut.writeUTF("RECEIVE");
                    dataOut.writeUTF(fileName);
                    receiveFile(fileName, dataIn);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String filePath, DataOutputStream dataOut) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "File not found: " + filePath);
            return;
        }

        dataOut.writeUTF("SEND");
        dataOut.writeUTF(file.getName());
        dataOut.writeLong(file.length());

        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;

            while ((read = fileIn.read(buffer)) != -1) {
                dataOut.write(buffer, 0, read);
            }
        }

        JOptionPane.showMessageDialog(null, "File sent: " + file.getName());
    }

    private void receiveFile(String fileName, DataInputStream dataIn) throws IOException {
        String response = dataIn.readUTF();

        if ("ERROR".equals(response)) {
            JOptionPane.showMessageDialog(null, "File not found on server.");
            return;
        }

        String receivedFileName = dataIn.readUTF();
        long fileSize = dataIn.readLong();
        File file = new File("Client_" + receivedFileName);

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long bytesRead = 0;

            while (bytesRead < fileSize) {
                int read = dataIn.read(buffer);
                fileOut.write(buffer, 0, read);
                bytesRead += read;
            }
        }

        JOptionPane.showMessageDialog(null, "File received: " + file.getName());
    }
}
