package passwordmanager.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import passwordmanager.dao.UserDAO;
import passwordmanager.model.User;
import passwordmanager.util.HashUtil;
import passwordmanager.util.Session;
import passwordmanager.view.MainView;

public class LoginView {

    private boolean isRegisterMode = false;

    public Scene getLoginScene(Stage stage) {
        Label title = new Label("StorePassword - Login");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField fullnameField = new TextField();
        fullnameField.setPromptText("Nama Lengkap");
        fullnameField.setVisible(false); // hanya untuk register

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Konfirmasi Password");
        confirmField.setVisible(false); // hanya untuk register

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button toggleButton = new Button("Belum punya akun? Register");
        Button actionButton = new Button("Login");

        toggleButton.setOnAction(e -> {
            isRegisterMode = !isRegisterMode;
            title.setText(isRegisterMode ? "StorePassword - Register" : "StorePassword - Login");
            fullnameField.setVisible(isRegisterMode);
            confirmField.setVisible(isRegisterMode);
            actionButton.setText(isRegisterMode ? "Register" : "Login");
            toggleButton.setText(isRegisterMode ? "Sudah punya akun? Login" : "Belum punya akun? Register");
            errorLabel.setText("");
        });

        actionButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username dan password wajib diisi.");
                return;
            }

            if (isRegisterMode) {
                String confirm = confirmField.getText().trim();
                String fullname = fullnameField.getText().trim();

                if (fullname.isEmpty() || confirm.isEmpty()) {
                    errorLabel.setText("Semua field wajib diisi.");
                    return;
                }

                if (!password.equals(confirm)) {
                    errorLabel.setText("Password dan konfirmasi tidak sama.");
                    return;
                }

                String passwordHash = HashUtil.sha256(password);
                User newUser = new User(username, passwordHash, fullname);

                boolean success = UserDAO.registerUser(newUser);
                if (success) {
                    Session.setCurrentUser(newUser); // ✅ diganti dari 'user' ke 'newUser'
                    MainView mainView = new MainView();
                    stage.setScene(mainView.getMainScene(stage));
                } else {
                    errorLabel.setStyle("-fx-text-fill: red;");
                    errorLabel.setText("Username sudah digunakan.");
                }

            } else {
                User user = UserDAO.findByUsername(username);
                if (user == null) {
                    errorLabel.setText("User tidak ditemukan.");
                    return;
                }

                String inputHash = HashUtil.sha256(password);
                if (!inputHash.equals(user.getPasswordHash())) {
                    errorLabel.setText("Password salah.");
                } else {
                    Session.setCurrentUser(user); // ✅ login berhasil → set session
                    MainView mainView = new MainView();
                    stage.setScene(mainView.getMainScene(stage));
                }
            }
        });

        VBox form = new VBox(10, title, usernameField, passwordField, fullnameField, confirmField, errorLabel, actionButton, toggleButton);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER);

        return new Scene(form, 400, 400);
    }
}
