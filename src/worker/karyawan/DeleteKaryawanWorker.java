package worker.karyawan;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import api.KaryawanApiClient;
import model.Karyawan;
import view.KaryawanFrame;

public class DeleteKaryawanWorker extends SwingWorker<Void, Void> {
    private final KaryawanFrame frame;
    private final KaryawanApiClient karyawanApiClient;
    private final Karyawan karyawan;

    public DeleteKaryawanWorker(KaryawanFrame frame, KaryawanApiClient karyawanApiClient, Karyawan karyawan) {
        this.frame = frame;
        this.karyawanApiClient = karyawanApiClient;
        this.karyawan = karyawan;
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Deleting employee record...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        karyawanApiClient.delete(karyawan.getId());
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            get();
            frame.getProgressBar().setString("Employee deleted successfully");
            JOptionPane.showMessageDialog(frame,
                    "Employee record has been deleted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to delete employee");
            JOptionPane.showMessageDialog(frame,
                    "Error deleting data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}