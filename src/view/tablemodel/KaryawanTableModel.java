package view.tablemodel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;
import model.Karyawan;

public class KaryawanTableModel extends AbstractTableModel {
    private List<Karyawan> karyawanList = new ArrayList<>();
    private final String[] columnNames = { 
        "ID", "Employee ID", "Name", "Department", "Position", 
        "Salary", "Hire Date", "Email", "Phone" 
    };
    
    // Format untuk Indonesia
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public KaryawanTableModel() {
        currencyFormat.setMaximumFractionDigits(0); // Tanpa desimal
    }

    public void setKaryawanList(List<Karyawan> karyawanList) {
        this.karyawanList = karyawanList;
        fireTableDataChanged();
    }

    public Karyawan getKaryawanAt(int rowIndex) {
        return karyawanList.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return karyawanList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Karyawan karyawan = karyawanList.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> karyawan.getId();
            case 1 -> karyawan.getEmployeeId();
            case 2 -> karyawan.getName();
            case 3 -> karyawan.getDepartment();
            case 4 -> karyawan.getPosition();
            case 5 -> currencyFormat.format(karyawan.getSalary()); // Format: Rp 40.000.000
            case 6 -> karyawan.getHireDate() != null ? 
                     karyawan.getHireDate().format(dateFormatter) : "";
            case 7 -> karyawan.getEmail();
            case 8 -> karyawan.getPhone();
            default -> null;
        };
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;
            case 5 -> String.class; // Salary formatted as currency string
            default -> String.class;
        };
    }
}