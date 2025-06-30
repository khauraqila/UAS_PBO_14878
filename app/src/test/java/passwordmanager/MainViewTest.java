package passwordmanager;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passwordmanager.model.User;
import passwordmanager.util.Session;
import passwordmanager.view.MainView;

import static org.junit.jupiter.api.Assertions.*;

public class MainViewTest {

    @BeforeEach
    public void setUp() {
        // Simulasikan login user
        User dummyUser = new User(1, "testuser", "hashed_password", "Test User");
        Session.setCurrentUser(dummyUser);
    }

    @Test
    public void testSessionUserNotNull() {
        assertNotNull(Session.getCurrentUser(), "User di session harus tidak null setelah login.");
    }

    @Test
    public void testSessionUserDetails() {
        User user = Session.getCurrentUser();
        assertEquals("testuser", user.getUsername());
        assertEquals("Test User", user.getFullname());
    }

    @Test
    public void testMainViewSceneCreation() {
        MainView mainView = new MainView();
        assertDoesNotThrow(() -> {
            javafx.scene.Scene scene = mainView.getMainScene(new Stage());
            assertNotNull(scene);
        });
    }

    @Test
    public void testLogoutClearsSession() {
        Session.clear();
        assertNull(Session.getCurrentUser(), "Session harus null setelah logout.");
    }
}
