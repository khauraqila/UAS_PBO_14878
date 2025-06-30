package passwordmanager;

import javafx.application.Application;
import javafx.stage.Stage;
import passwordmanager.dao.UserDAO;
import passwordmanager.dao.FolderDAO;
import passwordmanager.dao.PasswordDAO;
import passwordmanager.view.LoginView;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        // Inisialisasi semua tabel yang dibutuhkan
        UserDAO.createTableIfNotExists();
        FolderDAO.createTableIfNotExists();
        PasswordDAO.createTableIfNotExists(); // ← tambahan penting

        // Tampilkan halaman login
        LoginView loginView = new LoginView();
        stage.setScene(loginView.getLoginScene(stage));
        stage.setTitle("StorePassword");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}