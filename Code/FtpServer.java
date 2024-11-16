import java.io.*;
import java.net.*;

public class FtpServer {
    private static final int PORT = 3107;

    public FtpServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("File Transfer Server started. Waiting for connections...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream())) {

                    String command = dataIn.readUTF();

                    if ("SEND".equals(command)) {
                        receiveFile(dataIn);
                    } else if ("RECEIVE".equals(command)) {
                        String fileName = dataIn.readUTF();
                        sendFile(fileName, dataOut);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(DataInputStream dataIn) throws IOException {
        String fileName = dataIn.readUTF();
        long fileSize = dataIn.readLong();
        File file = new File("Server_" + fileName);

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long bytesRead = 0;

            while (bytesRead < fileSize) {
                int read = dataIn.read(buffer);
                fileOut.write(buffer, 0, read);
                bytesRead += read;
            }
        }

        System.out.println("File received: " + file.getName());
    }

    private void sendFile(String fileName, DataOutputStream dataOut) throws IOException {
        File file = new File(fileName);

        if (!file.exists()) {
            dataOut.writeUTF("ERROR");
            return;
        }

        dataOut.writeUTF("OK");
        dataOut.writeUTF(file.getName());
        dataOut.writeLong(file.length());

        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;

            while ((read = fileIn.read(buffer)) != -1) {
                dataOut.write(buffer, 0, read);
            }
        }

        System.out.println("File sent: " + file.getName());
    }
}
