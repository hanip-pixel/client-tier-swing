package worker.karyawan;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import api.KaryawanApiClient;
import model.Karyawan;
import view.KaryawanFrame;

public class SaveKaryawanWorker extends SwingWorker<Void, Void> {
    private final KaryawanFrame frame;
    private final KaryawanApiClient karyawanApiClient;
    private final Karyawan karyawan;

    public SaveKaryawanWorker(KaryawanFrame frame, KaryawanApiClient karyawanApiClient, Karyawan karyawan) {
        this.frame = frame;
        this.karyawanApiClient = karyawanApiClient;
        this.karyawan = karyawan;
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Saving new employee...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        karyawanApiClient.create(karyawan);
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            get();
            frame.getProgressBar().setString("Employee saved successfully");
            JOptionPane.showMessageDialog(frame,
                    "New employee record has been saved.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to save employee");
            JOptionPane.showMessageDialog(frame,
                    "Error saving data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}