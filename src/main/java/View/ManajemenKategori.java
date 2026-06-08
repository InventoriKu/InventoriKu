/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package View;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
/**
 *
 * @author LENOVO
 */
public class ManajemenKategori extends javax.swing.JPanel {

    public ManajemenKategori() {
        initComponents();
        db.koneksi.getConnection();              

        dataTable1.setSearchPlaceholder("Cari Kategori...");
        dataTable1.setButtonText("Tambah Kategori");
        dataTable1.setComboBoxVisible(false);

        String[] kolomKategori = {
            "ID", "Nama Kategori", "Total Barang", "Aksi"
        };
        dataTable1.setColumns(kolomKategori);
        dataTable1.getTable().getColumnModel().getColumn(0).setMinWidth(0);
        dataTable1.getTable().getColumnModel().getColumn(0).setMaxWidth(0);
        dataTable1.getTable().getColumnModel().getColumn(0).setWidth(0);
        dataTable1.addActionColumn(3);
        
        dataTable1.setPaginationActionListener((targetPage, limit) -> {
            loadDataKategori(targetPage, limit);
        });

        dataTable1.getSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadDataKategori();
            }
        });
        
        dataTable1.getBtnCetak().addActionListener(e -> {
            cetakLaporan();
        });

        dataTable1.getBtnTambah().addActionListener(e -> {
            showKategoriDialog(null);
        });
        
        dataTable1.setTableActionListener(new Components.DataTable.TableActionListener() {
            @Override
            public void onEdit(int row) {
                int idKategori = (int) dataTable1.getModel().getValueAt(row, 0);
                showKategoriDialog(idKategori);
            }

            @Override
            public void onDelete(int row) {
                int idKategori = (int) dataTable1.getModel().getValueAt(row, 0);
                String namaKategori = dataTable1.getModel().getValueAt(row, 1).toString();
                hapusKategori(idKategori, namaKategori);
            }
        });

        refreshStatCard();
        loadDataKategori(dataTable1.getCurrentPage(), dataTable1.getLimitPerPage());    
    }
    
    private void showKategoriDialog(Integer idKategori) {
        JTextField txtNamaKategori = new JTextField();

        if (idKategori != null) {
            try {
                Connection conn = db.koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT nama_kategori FROM kategori WHERE id_kategori = ?");
                ps.setInt(1, idKategori);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtNamaKategori.setText(rs.getString("nama_kategori"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Nama Kategori:"));
        panel.add(txtNamaKategori);

        String title = (idKategori == null) ? "Tambah Kategori Baru" : "Edit Kategori";
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String namaBaru = txtNamaKategori.getText().trim();
            
            if (namaBaru.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Connection conn = db.koneksi.getConnection();
                if (idKategori == null) {
                    String sql = "INSERT INTO kategori (nama_kategori) VALUES (?)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, namaBaru);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Kategori Berhasil Ditambahkan!");
                } else {
                    String sql = "UPDATE kategori SET nama_kategori=? WHERE id_kategori=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, namaBaru);
                    ps.setInt(2, idKategori);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Kategori Berhasil Diperbarui!");
                }
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusKategori(int idKategori, String namaKategori) {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus kategori '" + namaKategori + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = db.koneksi.getConnection();
                String sql = "DELETE FROM kategori WHERE id_kategori = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idKategori);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Kategori Berhasil Dihapus!");
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal Menghapus! Pastikan tidak ada barang yang menggunakan kategori ini.\n\nError: " + e.getMessage(), "Kategori Terpakai", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshData() {
        loadDataKategori();
        refreshStatCard();
    }
    
    private void refreshStatCard() {
        statCard1.setData("TOTAL KATEGORI", getTotalKategori(), new java.awt.Color(160, 250, 200), "assets/category-icon.png");
        statCard2.setData("BARANG TERDAFTAR", getTotalBarang(), new java.awt.Color(220, 225, 255), "assets/barang-icon.png");
    }

    private void loadDataKategori() {
        dataTable1.resetPage();
        loadDataKategori(dataTable1.getCurrentPage(), dataTable1.getLimitPerPage());
    }

    private void loadDataKategori(int page, int limit) {
        String keyword = dataTable1.getSearchField().getText().trim();
        if (keyword.equals("Cari Kategori...")) {
            keyword = "";
        }

        DefaultTableModel model = dataTable1.getModel();
        model.setRowCount(0);

        int totalDataFilter = 0;
        int offset = (page - 1) * limit; 

        try {
            Connection conn = db.koneksi.getConnection();

            String countSql = "SELECT COUNT(*) FROM kategori WHERE nama_kategori LIKE ?";
            PreparedStatement psCount = conn.prepareStatement(countSql);
            psCount.setString(1, "%" + keyword + "%");
            ResultSet rsCount = psCount.executeQuery();
            if (rsCount.next()) {
                totalDataFilter = rsCount.getInt(1);
            }

            String dataSql = """
                SELECT k.id_kategori, k.nama_kategori, COUNT(b.id_barang) AS jumlah_barang 
                FROM kategori k 
                LEFT JOIN barang b ON k.id_kategori = b.id_kategori 
                WHERE k.nama_kategori LIKE ? 
                GROUP BY k.id_kategori, k.nama_kategori 
                LIMIT ? OFFSET ?
            """;
            
            PreparedStatement psData = conn.prepareStatement(dataSql);
            psData.setString(1, "%" + keyword + "%");
            psData.setInt(2, limit);
            psData.setInt(3, offset);

            ResultSet rs = psData.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_kategori"),
                    rs.getString("nama_kategori"),
                    rs.getInt("jumlah_barang") + " Unit",
                    "" 
                });
            }

            dataTable1.updatePaginationStatus(totalDataFilter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getTotalKategori() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM kategori");
            if (rs.next()) return String.valueOf(rs.getInt(1))+ " Grup";
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getTotalBarang() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM barang");
            if (rs.next()) return String.valueOf(rs.getInt(1))+ " Unit";
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }
    
    private void cetakLaporan() {
        try {
            String keyword = "";
            if (dataTable1.getSearchField() != null && dataTable1.getSearchField().getText() != null) {
                keyword = dataTable1.getSearchField().getText().trim();
                if (keyword.equalsIgnoreCase("Cari Kategori...")) {
                    keyword = "";
                }
            }

            String[] printKolom = {"Nama Kategori", "Jumlah Barang"};
            DefaultTableModel printModel = new DefaultTableModel(printKolom, 0);

            Connection conn = db.koneksi.getConnection();
            StringBuilder dataSql = new StringBuilder("""
                SELECT k.nama_kategori, COUNT(b.id_barang) AS jumlah_barang 
                FROM kategori k 
                LEFT JOIN barang b ON k.id_kategori = b.id_kategori 
                WHERE 1=1 
            """);

            if (!keyword.isEmpty()) {
                dataSql.append(" AND k.nama_kategori LIKE ? ");
            }

            dataSql.append(" GROUP BY k.id_kategori, k.nama_kategori ");

            PreparedStatement psData = conn.prepareStatement(dataSql.toString());

            if (!keyword.isEmpty()) {
                psData.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = psData.executeQuery();
            while (rs.next()) {
                printModel.addRow(new Object[]{
                    rs.getString("nama_kategori") != null ? rs.getString("nama_kategori") : "-",
                    rs.getInt("jumlah_barang") + " Barang"
                });
            }

            if (printModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data kategori yang sesuai untuk dicetak!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JTable printTable = new JTable(printModel);
            printTable.setRowHeight(25);
            printTable.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
            printTable.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));

            javax.swing.JFrame tempFrame = new javax.swing.JFrame();
            tempFrame.setUndecorated(true);
            tempFrame.add(new javax.swing.JScrollPane(printTable));
            tempFrame.pack(); 

            String tanggal = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            MessageFormat header = new MessageFormat("Laporan Grup Kategori InventoriKu | Dicetak: " + tanggal);
            MessageFormat footer = new MessageFormat("Halaman {0}");

            JOptionPane.showMessageDialog(this, "Menyiapkan " + printModel.getRowCount() + " baris data kategori. Silakan tunggu...", "Informasi", JOptionPane.INFORMATION_MESSAGE);

            boolean isPrinted = printTable.print(
                JTable.PrintMode.FIT_WIDTH, 
                header, 
                footer, 
                true,   
                null, 
                true,   
                null
            );

            tempFrame.dispose();

            if (isPrinted) {
                JOptionPane.showMessageDialog(this, "Laporan kategori berhasil dicetak!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan oleh pengguna.", "Dibatalkan", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan kategori!\nError: " + e.getMessage(), "Error Print", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statCard1 = new Components.StatCard();
        statCard2 = new Components.StatCard();
        dataTable1 = new Components.DataTable();
        label2 = new java.awt.Label();

        label2.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        label2.setForeground(new java.awt.Color(30, 58, 138));
        label2.setText("Manajemen Kategori");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 1054, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(statCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(dataTable1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Components.DataTable dataTable1;
    private java.awt.Label label2;
    private Components.StatCard statCard1;
    private Components.StatCard statCard2;
    // End of variables declaration//GEN-END:variables
}
