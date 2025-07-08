package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.net.Socket;

public class ChatController {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField inputField;

    private PrintWriter out;
    private String username;

    public void initialize() {
        // Ask user for username on launch
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog("User" + (int)(Math.random() * 1000));
            dialog.setTitle("Enter Username");
            dialog.setHeaderText("Choose your chat name");
            dialog.setContentText("Username:");
            dialog.showAndWait().ifPresent(name -> {
                username = name.trim();
                if (username.isEmpty()) username = "User" + (int)(Math.random() * 1000);
                connectToServer();
            });
        });
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 1234);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    String finalLine = line;
                    Platform.runLater(() -> messageArea.appendText(finalLine + "\n"));
                }
            } catch (IOException e) {
                Platform.runLater(() -> messageArea.appendText("❌ Could not connect to server at localhost:1234\n"));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void sendMessage() {
        String message = inputField.getText();

        if (out == null) {
            messageArea.appendText("❗ Cannot send: Not connected to server.\n");
            return;
        }

        if (!message.isEmpty()) {
            out.println(username + ": " + message);
            inputField.clear();
        }
    }
}
