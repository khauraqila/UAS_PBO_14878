package passwordmanager.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import passwordmanager.dao.FolderDAO;
import passwordmanager.dao.PasswordDAO;
import passwordmanager.dao.UserDAO;
import passwordmanager.model.Folder;
import passwordmanager.model.PasswordEntry;
import passwordmanager.model.User;
import passwordmanager.util.EncryptionUtil;
import passwordmanager.util.HashUtil;
import passwordmanager.util.Session;

public class MainView {

    private TreeItem<String> rootItem;
    private TreeView<String> treeView;
    private VBox rightPanel;
    private final Map<String, PasswordEntry> passwordMap = new HashMap<>();
    private TextField searchField;

    public Scene getMainScene(Stage stage) {
        User user = Session.getCurrentUser();

        Label greeting = new Label("Halo, " + user.getFullname());
        greeting.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button addFolderBtn = new Button("Tambah Folder");
        Button addPasswordBtn = new Button("Tambah Password");
        Button editProfileBtn = new Button("Edit Profil");
        Button deletePasswordBtn = new Button("Hapus Password");
        Button deleteFolderBtn = new Button("Hapus Folder");
        Button logoutBtn = new Button("Logout");

        searchField = new TextField();
        searchField.setPromptText("Cari password...");
        Button searchBtn = new Button("Cari");

        searchBtn.setOnAction(e -> searchPassword());
        editProfileBtn.setOnAction(e -> showEditProfileDialog(stage));
        addFolderBtn.setOnAction(e -> showAddFolderDialog());
        addPasswordBtn.setOnAction(e -> showAddPasswordDialog());
        deletePasswordBtn.setOnAction(e -> deleteSelectedPassword());
        deleteFolderBtn.setOnAction(e -> deleteSelectedFolder());

        HBox toolbar = new HBox(10, addFolderBtn, addPasswordBtn, editProfileBtn, deletePasswordBtn, deleteFolderBtn, logoutBtn, searchField, searchBtn);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #f0f0f0;");

        rootItem = new TreeItem<>("Password Saya");
        rootItem.setExpanded(true);
        treeView = new TreeView<>(rootItem);
        treeView.setPrefWidth(200);
        refreshFolderTree();

        VBox leftPanel = new VBox(treeView);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(250);

        rightPanel = new VBox();
        rightPanel.setPadding(new Insets(20));
        rightPanel.getChildren().add(new Label("Silakan pilih akun untuk melihat detail."));

        BorderPane root = new BorderPane();
        root.setTop(new VBox(greeting, toolbar));
        root.setLeft(leftPanel);
        root.setCenter(rightPanel);

        logoutBtn.setOnAction(e -> {
            Session.clear();
            stage.setScene(new LoginView().getLoginScene(stage));
        });

        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.getValue().equals("Password Saya")) return;
            String value = newVal.getValue();
            if (value.startsWith("\uD83D\uDD10 ")) {
                String idStr = value.substring(2).split(" - ")[0].trim();
                PasswordEntry entry = passwordMap.get(idStr);
                if (entry != null) {
                    showPasswordDetail(entry);
                } else {
                    showError("Gagal menampilkan detail: data tidak ditemukan.");
                }
            }
        });

        return new Scene(root, 800, 500);
    }

    private void refreshFolderTree() {
        rootItem.getChildren().clear();
        passwordMap.clear();

        int userId = Session.getCurrentUser().getId();
        for (Folder folder : FolderDAO.getFoldersByUserId(userId)) {
            TreeItem<String> folderItem = new TreeItem<>(folder.getName());
            List<PasswordEntry> entries = PasswordDAO.getPasswordsByFolderId(folder.getId());
            for (PasswordEntry entry : entries) {
                TreeItem<String> passItem = new TreeItem<>("\uD83D\uDD10 " + entry.getId() + " - " + entry.getName());
                passwordMap.put(String.valueOf(entry.getId()), entry);
                folderItem.getChildren().add(passItem);
            }
            rootItem.getChildren().add(folderItem);
        }
    }

    private void searchPassword() {
        String keyword = searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            refreshFolderTree();
            return;
        }

        rootItem.getChildren().clear();
        passwordMap.clear();

        int userId = Session.getCurrentUser().getId();
        List<PasswordEntry> results = PasswordDAO.searchByKeyword(keyword, userId);

        Map<Integer, TreeItem<String>> folderItems = new HashMap<>();
        for (PasswordEntry entry : results) {
            Folder folder = FolderDAO.getFolderById(entry.getFolderId());
            if (folder == null) continue;
            TreeItem<String> folderItem = folderItems.computeIfAbsent(folder.getId(), id -> {
                TreeItem<String> fi = new TreeItem<>(folder.getName());
                rootItem.getChildren().add(fi);
                return fi;
            });
            TreeItem<String> passItem = new TreeItem<>("\uD83D\uDD10 " + entry.getId() + " - " + entry.getName());
            passwordMap.put(String.valueOf(entry.getId()), entry);
            folderItem.getChildren().add(passItem);
        }
    }

    private void showPasswordDetail(PasswordEntry entry) {
        rightPanel.getChildren().clear();
        Label nameLabel = new Label("Nama Akun: " + entry.getName());
        Label userLabel = new Label("Username: " + entry.getUsername());
        String decrypted = EncryptionUtil.decrypt(entry.getEncryptedPassword(), entry.getHashKey());
        Label passLabel = new Label("Password: " + decrypted);
        Button copyBtn = new Button("Salin Password");
        copyBtn.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(decrypted);
            Clipboard.getSystemClipboard().setContent(content);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password disalin ke clipboard.");
            alert.setHeaderText(null);
            alert.show();
        });
        rightPanel.getChildren().addAll(nameLabel, userLabel, passLabel, copyBtn);
    }

    private void showAddFolderDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tambah Folder");
        dialog.setHeaderText(null);
        dialog.setContentText("Nama folder:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showError("Nama folder tidak boleh kosong.");
                return;
            }
            Folder folder = new Folder(name.trim(), Session.getCurrentUser().getId());
            if (FolderDAO.addFolder(folder)) {
                refreshFolderTree();
                showInfo("Folder berhasil ditambahkan.");
            } else {
                showError("Gagal menambahkan folder.");
            }
        });
    }

    private void showAddPasswordDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tambah Password");

        Label nameLabel = new Label("Nama Akun:");
        TextField nameField = new TextField();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label folderLabel = new Label("Folder:");
        ComboBox<String> folderCombo = new ComboBox<>();
        for (Folder folder : FolderDAO.getFoldersByUserId(Session.getCurrentUser().getId())) {
            folderCombo.getItems().add(folder.getName());
        }
        Label categoryLabel = new Label("Kategori:");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Web App", "Mobile App", "Desktop App", "Game", "Lainnya");

        VBox content = new VBox(10, nameLabel, nameField, usernameLabel, usernameField, passwordLabel, passwordField,
                folderLabel, folderCombo, categoryLabel, categoryCombo);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String name = nameField.getText().trim();
                String username = usernameField.getText().trim();
                String pass = passwordField.getText();
                String folderName = folderCombo.getValue();
                String category = categoryCombo.getValue();
                if (name.isEmpty() || username.isEmpty() || pass.isEmpty() || folderName == null || category == null) {
                    showError("Semua field harus diisi.");
                    return null;
                }
                Folder folder = FolderDAO.getFolderByName(folderName, Session.getCurrentUser().getId());
                if (folder == null) {
                    showError("Folder tidak ditemukan.");
                    return null;
                }
                String hashKey = HashUtil.generateRandomKey();
                String encrypted = EncryptionUtil.encrypt(pass, hashKey);
                PasswordEntry entry = new PasswordEntry(name, username, encrypted, hashKey,
                        folder.getId(), Session.getCurrentUser().getId(), category);
                if (PasswordDAO.addPassword(entry)) {
                    refreshFolderTree();
                    showInfo("Password berhasil ditambahkan.");
                } else {
                    showError("Gagal menambahkan password.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteSelectedPassword() {
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue().equals("Password Saya")) return;
        String value = selected.getValue();
        if (value.startsWith("\uD83D\uDD10 ")) {
            String idStr = value.substring(2).split(" - ")[0].trim();
            PasswordEntry entry = passwordMap.get(idStr);
            if (entry == null) {
                showError("Password tidak ditemukan.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus password ini?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    boolean success = PasswordDAO.deletePasswordById(entry.getId());
                    if (success) {
                        refreshFolderTree();
                        rightPanel.getChildren().clear();
                        rightPanel.getChildren().add(new Label("Password berhasil dihapus."));
                    } else {
                        showError("Gagal menghapus password.");
                    }
                }
            });
        }
    }

    private void deleteSelectedFolder() {
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue().equals("Password Saya")) return;
        String folderName = selected.getValue();
        if (folderName.startsWith("\uD83D\uDD10 ")) {
            showError("Pilih folder, bukan password, untuk menghapus folder.");
            return;
        }
        List<Folder> folders = FolderDAO.getFoldersByUserId(Session.getCurrentUser().getId());
        Folder folderToDelete = folders.stream().filter(f -> f.getName().equals(folderName)).findFirst().orElse(null);
        if (folderToDelete == null) {
            showError("Folder tidak ditemukan.");
            return;
        }
        List<PasswordEntry> passwordsInFolder = PasswordDAO.getPasswordsByFolderId(folderToDelete.getId());
        if (!passwordsInFolder.isEmpty()) {
            showError("Folder masih berisi password. Hapus semua password terlebih dahulu.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus folder ini?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = FolderDAO.deleteFolder(folderToDelete.getId());
                if (success) {
                    refreshFolderTree();
                    rightPanel.getChildren().clear();
                    rightPanel.getChildren().add(new Label("Folder berhasil dihapus."));
                } else {
                    showError("Gagal menghapus folder.");
                }
            }
        });
    }

    private void showEditProfileDialog(Stage stage) {
        User currentUser = Session.getCurrentUser();
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Profil");
        Label fullnameLabel = new Label("Nama Lengkap:");
        TextField fullnameField = new TextField(currentUser.getFullname());
        Label passwordLabel = new Label("Password Baru:");
        PasswordField passwordField = new PasswordField();
        Label confirmLabel = new Label("Konfirmasi Password:");
        PasswordField confirmField = new PasswordField();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        VBox content = new VBox(10, fullnameLabel, fullnameField, passwordLabel, passwordField, confirmLabel, confirmField, errorLabel);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String fullname = fullnameField.getText().trim();
                String newPassword = passwordField.getText();
                String confirmPassword = confirmField.getText();
                if (!newPassword.equals(confirmPassword)) {
                    errorLabel.setText("Konfirmasi password tidak cocok!");
                    return null;
                }
                String hashed = newPassword.isBlank() ? currentUser.getPasswordHash() : HashUtil.sha256(newPassword);
                currentUser.setFullname(fullname);
                boolean success = UserDAO.updateUser(currentUser, hashed);
                if (success) {
                    Session.setCurrentUser(currentUser);
                    stage.setScene(getMainScene(stage));
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Terjadi kesalahan");
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}
