package worker.karyawan;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import api.KaryawanApiClient;
import model.Karyawan;
import view.KaryawanFrame;

public class UpdateKaryawanWorker extends SwingWorker<Void, Void> {
    private final KaryawanFrame frame;
    private final KaryawanApiClient karyawanApiClient;
    private final Karyawan karyawan;

    public UpdateKaryawanWorker(KaryawanFrame frame, KaryawanApiClient karyawanApiClient, Karyawan karyawan) {
        this.frame = frame;
        this.karyawanApiClient = karyawanApiClient;
        this.karyawan = karyawan;
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Updating employee data...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        karyawanApiClient.update(karyawan);
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            get();
            frame.getProgressBar().setString("Employee updated successfully");
            JOptionPane.showMessageDialog(frame,
                    "Employee record has been updated.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to update employee");
            JOptionPane.showMessageDialog(frame,
                    "Error updating data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}