import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;

public class ScreenSharingServer {
    private static final int PORT = 2212;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream dataOut;
    private Robot robot;

    public ScreenSharingServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for client...");
            socket = serverSocket.accept();
            dataOut = new DataOutputStream(socket.getOutputStream());

            // Create Robot instance to capture screen
            robot = new Robot();

            // Start screen sharing
            startScreenSharing();

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    private void startScreenSharing() {
        try {
            while (true) {
                // Capture the screen
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage screenCapture = robot.createScreenCapture(screenRect);

                // Convert the image to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenCapture, "jpg", baos);
                baos.flush();
                byte[] imageData = baos.toByteArray();
                baos.close();

                // Send the image size first
                dataOut.writeInt(imageData.length);
                dataOut.write(imageData);

                Thread.sleep(100); // Send updates at regular intervals (e.g., 100ms)
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ScreenSharingServer();
    }
}
