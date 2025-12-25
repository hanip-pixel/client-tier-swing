package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import net.miginfocom.swing.MigLayout;
import view.tablemodel.KaryawanTableModel;

public class KaryawanFrame extends JFrame {
    private final JTextField searchField = new JTextField();
    private final JButton addButton = new JButton("Add New");
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton deleteButton = new JButton("Delete");
    private final JLabel totalRecordsLabel = new JLabel("0 Records");
    
    private final JTable karyawanTable = new JTable();
    private final KaryawanTableModel karyawanTableModel = new KaryawanTableModel();
    private final JProgressBar progressBar = new JProgressBar();
    private final JScrollPane tableScrollPane = new JScrollPane(karyawanTable);

    public KaryawanFrame() {
        initializeUI();        
    }

    private void initializeUI() {
        setTitle("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Layout utama: fill seluruh frame
        setLayout(new MigLayout("fill, insets 20", 
            "[grow]", 
            "[][][grow][][]"));
        
        karyawanTable.setModel(karyawanTableModel);
        karyawanTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        karyawanTable.setFillsViewportHeight(true);
        
        // === TAMBAHKAN INI: Atur alignment untuk semua kolom ===
        setupTableAlignment();
        
        // Atur agar scroll pane mengisi seluruh area
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        progressBar.setStringPainted(true);
        
        // 1. Judul
        JLabel titleLabel = new JLabel("Employee List");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(titleLabel, "wrap, span, gapbottom 10, center");
        
        // 2. Panel atas (search + buttons)
        JPanel topPanel = new JPanel(new MigLayout("fillx, insets 0", 
            "[grow][shrink]", 
            "[]"));
        
        // Search panel
        JPanel searchPanel = new JPanel(new MigLayout("fillx, insets 0", "[][grow]", "[]"));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField, "growx, w 200:300:400");
        topPanel.add(searchPanel, "growx, pushx");
        
        // Button panel
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0, gapx 5", "", "[]"));
        addButton.setFont(addButton.getFont().deriveFont(Font.BOLD));
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        topPanel.add(buttonPanel, "wrap");
        
        add(topPanel, "growx, wrap, gapbottom 10");
        
        // 3. Tabel - mengambil semua ruang yang tersisa
        add(tableScrollPane, "grow, push, wrap, gapbottom 10");
        
        // 4. Progress bar
        add(progressBar, "growx, h 25!, wrap, gapbottom 5");
        
        // 5. Total records
        add(totalRecordsLabel, "right, wrap");
        
        // Atur ukuran minimum dan tampilkan
        setMinimumSize(new Dimension(1000, 600));
        pack();
        setLocationRelativeTo(null);
        
        // Resize listener untuk menjaga tabel tetap responsive
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Force table to update column widths
                karyawanTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                karyawanTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        });
    }
    
    // === METODE BARU UNTUK SETUP ALIGNMENT ===
    private void setupTableAlignment() {
        // Buat renderer untuk rata kiri
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        
        // Buat renderer untuk rata kiri dengan font standar
        DefaultTableCellRenderer leftRendererRegular = new DefaultTableCellRenderer();
        leftRendererRegular.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        
        // Buat renderer untuk header juga rata kiri
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        
        // Atur renderer untuk semua kolom
        for (int i = 0; i < karyawanTable.getColumnCount(); i++) {
            karyawanTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }
        
        // Atur header juga rata kiri
        karyawanTable.getTableHeader().setDefaultRenderer(headerRenderer);
        
        // Atau bisa juga atur per kolom jika ingin berbeda alignment
        // Misalnya, kolom ID dan Salary bisa rata tengah:
        /*
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        
        // Kolom ID (index 0) rata tengah
        karyawanTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        // Kolom Salary (index 5) rata tengah
        karyawanTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        */
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JTable getKaryawanTable() {
        return karyawanTable;
    }

    public KaryawanTableModel getKaryawanTableModel() {
        return karyawanTableModel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getTotalRecordsLabel() {
        return totalRecordsLabel;
    }
}