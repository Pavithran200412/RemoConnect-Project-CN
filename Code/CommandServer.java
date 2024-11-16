import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class CommandServer {
    private ServerSocket serverSocket;
    private JTextArea logArea;

    public CommandServer
(int port) throws IOException {
        // Setup the GUI
        JFrame frame = new JFrame("Command Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);

        // Start the server
        serverSocket = new ServerSocket(port);
        logArea.append("Server started on port " + port + "\n");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start(); // Handle each client in a new thread
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String command;
                while ((command = in.readLine()) != null) {
                    logArea.append("Received command: " + command + "\n");
                    executeCommand(command); // Execute the received command
                }
            } catch (IOException e) {
                logArea.append("Error: " + e.getMessage() + "\n");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logArea.append("Error closing socket: " + e.getMessage() + "\n");
                }
            }
        }

        private void executeCommand(String command) {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                String finalCommand;

                if (os.contains("win")) {
                    String cmdPath = "C:\\Windows\\System32\\cmd.exe";

                    if (command.startsWith("cd")) {
                        // For "cd", chain it with "&& dir" to validate and list the new directory
                        finalCommand = cmdPath + " /c " + command + " && dir";
                    } else {
                        // Other commands
                        finalCommand = cmdPath + " /c " + command;
                    }
                } else {
                    // For Unix-based systems
                    finalCommand = command;
                }

                // Execute the command
                Process process = Runtime.getRuntime().exec(finalCommand);

                // Capture output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                // Capture errors
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }

                // Log output and errors to the GUI
                if (output.length() > 0) {
                    logArea.append("Command output:\n" + output.toString() + "\n");
                }
                if (errorOutput.length() > 0) {
                    logArea.append("Command error:\n" + errorOutput.toString() + "\n");
                }
            } catch (IOException e) {
                logArea.append("Error executing command: " + e.getMessage() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        try {
            new CommandServer(2222); // Start server on port 12345
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
