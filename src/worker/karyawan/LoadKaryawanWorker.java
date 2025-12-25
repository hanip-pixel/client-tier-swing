package worker.karyawan;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import api.KaryawanApiClient;
import model.Karyawan;
import view.KaryawanFrame;

public class LoadKaryawanWorker extends SwingWorker<List<Karyawan>, Void> {
    private final KaryawanFrame frame;
    private final KaryawanApiClient karyawanApiClient;

    public LoadKaryawanWorker(KaryawanFrame frame, KaryawanApiClient karyawanApiClient) {
        this.frame = frame;
        this.karyawanApiClient = karyawanApiClient;
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Loading employee data...");
    }

    @Override
    protected List<Karyawan> doInBackground() throws Exception {
        return karyawanApiClient.findAll();
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            List<Karyawan> result = get();
            frame.getProgressBar().setString(result.size() + " records loaded");
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to load data");
            JOptionPane.showMessageDialog(frame,
                    "Error loading data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}