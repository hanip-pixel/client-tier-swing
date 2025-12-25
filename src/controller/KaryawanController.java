package controller;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import api.KaryawanApiClient;
import model.Karyawan;
import view.KaryawanDialog;
import view.KaryawanFrame;
import worker.karyawan.DeleteKaryawanWorker;
import worker.karyawan.LoadKaryawanWorker;
import worker.karyawan.SaveKaryawanWorker;
import worker.karyawan.UpdateKaryawanWorker;

public class KaryawanController {
    private final KaryawanFrame frame;
    private final KaryawanApiClient karyawanApiClient = new KaryawanApiClient();
    
    private List<Karyawan> allKaryawan = new ArrayList<>();
    private List<Karyawan> displayedKaryawan = new ArrayList<>();

    public KaryawanController(KaryawanFrame frame) {
        this.frame = frame;
        setupEventListeners();
        loadAllKaryawan();
    }

    private void setupEventListeners() {
        frame.getAddButton().addActionListener(e -> openKaryawanDialog(null));
        frame.getRefreshButton().addActionListener(e -> loadAllKaryawan());
        frame.getDeleteButton().addActionListener(e -> deleteSelectedKaryawan());
        
        frame.getKaryawanTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = frame.getKaryawanTable().getSelectedRow();
                    if (selectedRow >= 0) {
                        openKaryawanDialog(displayedKaryawan.get(selectedRow));
                    }
                }
            }
        });
        
        frame.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearchFilter();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearchFilter();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearchFilter();
            }
            
            private void applySearchFilter() {
                String keyword = frame.getSearchField().getText().toLowerCase().trim();
                displayedKaryawan = new ArrayList<>();
                for (Karyawan karyawan : allKaryawan) {
                    if (karyawan.getEmployeeId().toLowerCase().contains(keyword) ||
                        karyawan.getName().toLowerCase().contains(keyword) ||
                        karyawan.getDepartment().toLowerCase().contains(keyword) ||
                        karyawan.getPosition().toLowerCase().contains(keyword) ||
                        (karyawan.getEmail() != null && karyawan.getEmail().toLowerCase().contains(keyword))) {
                        displayedKaryawan.add(karyawan);
                    }
                }
                frame.getKaryawanTableModel().setKaryawanList(displayedKaryawan);
                updateTotalRecordsLabel();
            }
        });
    }

    private void openKaryawanDialog(Karyawan karyawanToEdit) {
        KaryawanDialog dialog;
        
        if (karyawanToEdit == null) {
            dialog = new KaryawanDialog(frame);
        } else {
            dialog = new KaryawanDialog(frame, karyawanToEdit);
        }
        
        dialog.getSaveButton().addActionListener(e -> {
            Karyawan karyawan = dialog.getKaryawan();
            
            // Validasi input
            if (karyawan.getEmployeeId().isEmpty() || karyawan.getName().isEmpty() || 
                karyawan.getDepartment().isEmpty() || karyawan.getPosition().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Employee ID, Name, Department, and Position are required!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (karyawan.getSalary() <= 0) {
                JOptionPane.showMessageDialog(dialog, 
                    "Salary must be greater than 0!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            SwingWorker<Void, Void> worker;
            
            if (karyawanToEdit == null) {
                worker = new SaveKaryawanWorker(frame, karyawanApiClient, karyawan);
            } else {
                worker = new UpdateKaryawanWorker(frame, karyawanApiClient, karyawan);
            }
            
            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    dialog.dispose();
                    loadAllKaryawan();
                }
            });
            worker.execute();
        });
        dialog.setVisible(true);
    }

    private void deleteSelectedKaryawan() {
        int selectedRow = frame.getKaryawanTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(frame, "Please select a record to delete.");
            return;
        }
        Karyawan karyawan = displayedKaryawan.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Delete employee: " + karyawan.getEmployeeId() + " - " + karyawan.getName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            DeleteKaryawanWorker worker = new DeleteKaryawanWorker(frame, karyawanApiClient, karyawan);
            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    loadAllKaryawan();
                }
            });
            worker.execute();
        }
    }

    private void loadAllKaryawan() {
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Loading data...");
        
        LoadKaryawanWorker worker = new LoadKaryawanWorker(frame, karyawanApiClient);
        worker.addPropertyChangeListener(evt -> {
            if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                try {
                    allKaryawan = worker.get();
                    displayedKaryawan = new ArrayList<>(allKaryawan);
                    frame.getKaryawanTableModel().setKaryawanList(displayedKaryawan);
                    updateTotalRecordsLabel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to load data: " + ex.getMessage());
                } finally {
                    frame.getProgressBar().setIndeterminate(false);
                    frame.getProgressBar().setString("Ready");
                }
            }
        });
        worker.execute();
    }

    private void updateTotalRecordsLabel() {
        frame.getTotalRecordsLabel().setText(displayedKaryawan.size() + " Records");
    }
}