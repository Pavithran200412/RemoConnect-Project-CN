import java.io.*;
import java.net.*;

public class FtpServer {
    private static final int PORT = 9002;
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());

                // Handle client requests continuously
                while (true) {
                    String command = dataIn.readUTF();
                    if ("SEND".equals(command)) {
                        receiveFile(dataIn, dataOut);
                    } else if ("RECEIVE".equals(command)) {
                        sendFile(dataIn, dataOut);
                    } else {
                        break; // Disconnect if command is unknown
                    }
                }

                // Close resources after finishing with the client
                dataIn.close();
                dataOut.close();
                clientSocket.close();
                System.out.println("Client disconnected.");
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void receiveFile(DataInputStream dataIn, DataOutputStream dataOut) {
        try {
            String fileName = dataIn.readUTF();
            long fileSize = dataIn.readLong();
            File file = new File("received_" + fileName);

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                long totalRead = 0;
                int bytesRead;
                while (totalRead < fileSize && (bytesRead = dataIn.read(buffer, 0, Math.min(buffer.length, (int)(fileSize - totalRead)))) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
            }

            System.out.println("File received: " + fileName);
            dataOut.writeUTF("File received: " + fileName);
        } catch (IOException e) {
            try {
                dataOut.writeUTF("ERROR");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error receiving file: " + e.getMessage());
        }
    }

    private static void sendFile(DataInputStream dataIn, DataOutputStream dataOut) {
        try {
            String fileName = dataIn.readUTF();
            File file = new File(fileName);

            if (!file.exists()) {
                dataOut.writeUTF("ERROR");
                System.err.println("File not found on server: " + fileName);
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

            System.out.println("File sent: " + file.getName());
            dataOut.writeUTF("File sent: " + file.getName());
        } catch (IOException e) {
            System.err.println("Error sending file: " + e.getMessage());
        }
    }
}
