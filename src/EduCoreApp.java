import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;
import controller.KaryawanController;
import view.KaryawanFrame;

public class EduCoreApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
        } catch (Exception ex) {
            System.err.println("Failed to set FlatLaf theme");
        }

        SwingUtilities.invokeLater(() -> {
            KaryawanFrame frame = new KaryawanFrame();
            new KaryawanController(frame);
            frame.setVisible(true);
        });
    }
}