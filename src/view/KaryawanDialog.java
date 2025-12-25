package view;

import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import model.Karyawan;
import net.miginfocom.swing.MigLayout;

public class KaryawanDialog extends JDialog {
    private final JTextField employeeIdField = new JTextField(25);
    private final JTextField nameField = new JTextField(25);
    private final JTextField departmentField = new JTextField(25);
    private final JTextField positionField = new JTextField(25);
    private final JTextField salaryField = new JTextField(25);
    private final JTextField hireDateField = new JTextField(25);
    private final JTextField emailField = new JTextField(25);
    private final JTextField phoneField = new JTextField(25);
    
    private final JButton saveButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Cancel");
    
    private Karyawan karyawan;
    private final NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));

    public KaryawanDialog(JFrame owner) {
        super(owner, "Add New Employee", true);
        this.karyawan = new Karyawan();
        setupComponents();
    }

    public KaryawanDialog(JFrame owner, Karyawan karyawanToEdit) {
        super(owner, "Edit Employee", true);
        this.karyawan = karyawanToEdit;
        setupComponents();
        
        employeeIdField.setText(karyawanToEdit.getEmployeeId());
        nameField.setText(karyawanToEdit.getName());
        departmentField.setText(karyawanToEdit.getDepartment());
        positionField.setText(karyawanToEdit.getPosition());
        
        // Format salary tanpa mata uang, hanya angka dengan pemisah ribuan
        salaryField.setText(String.format("%,.0f", karyawanToEdit.getSalary()));
        
        if (karyawanToEdit.getHireDate() != null) {
            hireDateField.setText(karyawanToEdit.getHireDate().toString());
        }
        
        emailField.setText(karyawanToEdit.getEmail());
        phoneField.setText(karyawanToEdit.getPhone());
    }

    private void setupComponents() {
        setLayout(new MigLayout("fill, insets 20", "[right]20[grow, fill]"));
        
        add(new JLabel("Employee ID*"), "");
        add(employeeIdField, "wrap");
        add(new JLabel("Name*"), "");
        add(nameField, "wrap");
        add(new JLabel("Department*"), "");
        add(departmentField, "wrap");
        add(new JLabel("Position*"), "");
        add(positionField, "wrap");
        add(new JLabel("Salary*"), "");
        add(salaryField, "wrap");
        add(new JLabel("Hire Date (YYYY-MM-DD)"), "");
        add(hireDateField, "wrap");
        add(new JLabel("Email"), "");
        add(emailField, "wrap");
        add(new JLabel("Phone"), "");
        add(phoneField, "wrap");
        
        saveButton.setBackground(UIManager.getColor("Button.default.background"));
        saveButton.setForeground(UIManager.getColor("Button.default.foreground"));
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
        
        JPanel buttonPanel = new JPanel(new MigLayout("", "[]10[]"));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, "span, right");
        
        pack();
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(getOwner());
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public Karyawan getKaryawan() {
        karyawan.setEmployeeId(employeeIdField.getText().trim());
        karyawan.setName(nameField.getText().trim());
        karyawan.setDepartment(departmentField.getText().trim());
        karyawan.setPosition(positionField.getText().trim());
        
        // Parse salary dengan benar (hilangkan pemisah ribuan)
        String salaryText = salaryField.getText().trim();
        try {
            // Hapus titik sebagai pemisah ribuan jika ada
            salaryText = salaryText.replace(".", "");
            // Jika ada koma sebagai pemisah desimal, ambil hanya bagian integer
            if (salaryText.contains(",")) {
                salaryText = salaryText.split(",")[0];
            }
            karyawan.setSalary(Double.parseDouble(salaryText));
        } catch (NumberFormatException e) {
            karyawan.setSalary(0);
        }
        
        try {
            String hireDateText = hireDateField.getText().trim();
            if (!hireDateText.isEmpty()) {
                karyawan.setHireDate(LocalDate.parse(hireDateText));
            }
        } catch (DateTimeParseException e) {
            // Tanggal tidak valid, biarkan null
        }
        
        karyawan.setEmail(emailField.getText().trim());
        karyawan.setPhone(phoneField.getText().trim());
        
        return karyawan;
    }
}