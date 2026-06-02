/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package View;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HP
 */
public class ManajemenBarang extends javax.swing.JPanel {

    /**
     * Creates new form ManajemenBarang
     */
    // Helper class untuk menyimpan ID dan Nama Kategori pada ComboBox
    class KategoriItem {
        int id;
        String nama;
        public KategoriItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }
        @Override
        public String toString() {
            return nama;
        }
    }

    public ManajemenBarang() {
        initComponents();
        
        // Memanggil koneksi database
        db.koneksi.getConnection();

        // 1. Konfigurasi UI DataTable
        tblBarang.setSearchPlaceholder("Cari Barang...");
        tblBarang.setButtonText("Tambah Barang");
        tblBarang.setComboBoxModel(new String[]{"Semua Kategori"});

        // 2. Event Listener Tombol 'Tambah Barang' di Panel Atas
        tblBarang.getBtnTambah().addActionListener(e -> {
            showBarangDialog(null); // null menandakan operasi CREATE
        });

        // 3. Setup Aksi Tombol Edit & Delete dalam Tabel
        tblBarang.setTableActionListener(new Components.DataTable.TableActionListener() {
            @Override
            public void onEdit(int row) {
                // Ambil ID Barang dari Kolom ke-0 (Kolom Tersembunyi)
                int idBarang = (int) tblBarang.getModel().getValueAt(row, 0);
                showBarangDialog(idBarang); // ID diberikan untuk operasi UPDATE
            }

            @Override
            public void onDelete(int row) {
                int idBarang = (int) tblBarang.getModel().getValueAt(row, 0);
                String namaBarang = tblBarang.getModel().getValueAt(row, 1).toString();
                hapusBarang(idBarang, namaBarang); // Operasi DELETE
            }
        });

        // 4. Muat Data Pertama Kali
        refreshData();
        
        // Data Dummy untuk Card ke-4
        statCard4.setData("MUTASI HARI INI", "42 Items", new java.awt.Color(255, 220, 220), "C:\\Users\\HP\\Documents\\NetBeansProjects\\InventoriKu\\src\\main\\java\\assets\\stats-up-icon.png");
    }
    
    // ==============================================
    // BAGIAN LOGIKA CRUD DATABASE
    // ==============================================
    
    // Method Pop-up Form untuk Tambah (Create) dan Edit (Update)
    private void showBarangDialog(Integer idBarang) {
        JTextField txtNama = new JTextField();
        JComboBox<KategoriItem> cbKategori = new JComboBox<>();
        JSpinner spinStok = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        JSpinner spinMinStok = new JSpinner(new SpinnerNumberModel(5, 0, 999999, 1));
        JTextField txtSupplier = new JTextField();

        // Ambil Data Kategori dari Database untuk dimasukkan ke ComboBox
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT id_kategori, nama_kategori FROM kategori");
            while (rs.next()) {
                cbKategori.addItem(new KategoriItem(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        // Jika idBarang tidak NULL (Berarti Mode EDIT), Muat Data Lama dari DB
        if (idBarang != null) {
            try {
                Connection conn = db.koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM barang WHERE id_barang = ?");
                ps.setInt(1, idBarang);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtNama.setText(rs.getString("nama_barang"));
                    spinStok.setValue(rs.getInt("stok"));
                    spinMinStok.setValue(rs.getInt("stok_minimum"));
                    txtSupplier.setText(rs.getString("supplier"));
                    
                    int idKat = rs.getInt("id_kategori");
                    for (int i = 0; i < cbKategori.getItemCount(); i++) {
                        if (cbKategori.getItemAt(i).id == idKat) {
                            cbKategori.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }

        // Susun Form UI
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Nama Barang:")); panel.add(txtNama);
        panel.add(new JLabel("Kategori:")); panel.add(cbKategori);
        panel.add(new JLabel("Stok:")); panel.add(spinStok);
        panel.add(new JLabel("Stok Minimum (Batas Kritis):")); panel.add(spinMinStok);
        panel.add(new JLabel("Supplier:")); panel.add(txtSupplier);

        String title = (idBarang == null) ? "Tambah Barang Baru" : "Edit Data Barang";
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Jika user mengklik tombol OK (Save)
        if (result == JOptionPane.OK_OPTION) {
            if (txtNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama barang tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            KategoriItem selectedKat = (KategoriItem) cbKategori.getSelectedItem();
            Integer katId = (selectedKat != null) ? selectedKat.id : null;

            try {
                Connection conn = db.koneksi.getConnection();
                
                if (idBarang == null) {
                    // C R E A T E
                    String sql = "INSERT INTO barang (nama_barang, id_kategori, stok, stok_minimum, supplier) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, txtNama.getText().trim());
                    if (katId != null) ps.setInt(2, katId); else ps.setNull(2, java.sql.Types.INTEGER);
                    ps.setInt(3, (int) spinStok.getValue());
                    ps.setInt(4, (int) spinMinStok.getValue());
                    ps.setString(5, txtSupplier.getText().trim());
                    ps.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Barang Berhasil Ditambahkan!");
                } else {
                    // U P D A T E
                    String sql = "UPDATE barang SET nama_barang=?, id_kategori=?, stok=?, stok_minimum=?, supplier=? WHERE id_barang=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, txtNama.getText().trim());
                    if (katId != null) ps.setInt(2, katId); else ps.setNull(2, java.sql.Types.INTEGER);
                    ps.setInt(3, (int) spinStok.getValue());
                    ps.setInt(4, (int) spinMinStok.getValue());
                    ps.setString(5, txtSupplier.getText().trim());
                    ps.setInt(6, idBarang);
                    ps.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Barang Berhasil Diperbarui!");
                }
                
                // Segarkan Tabel dan Stat Cards
                refreshData();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method Hapus Barang (Delete)
    private void hapusBarang(int idBarang, String namaBarang) {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus barang '" + namaBarang + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = db.koneksi.getConnection();
                String sql = "DELETE FROM barang WHERE id_barang = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idBarang);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Barang Berhasil Dihapus!");
                refreshData();
                
            } catch (Exception e) {
                e.printStackTrace();
                // Antisipasi error Foreign Key constraint (Barang masih digunakan di stok_masuk / stok_keluar)
                JOptionPane.showMessageDialog(this, "Gagal Menghapus! Pastikan barang ini tidak memiliki riwayat stok masuk/keluar.\nError: " + e.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Method untuk Segarkan ulang seluruh komponen UI
    private void refreshData() {
        loadDataBarang(); // Muat ulang tabel
        
        // Muat ulang statistik card
        statCard1.setData("TOTAL BARANG", getTotalBarang(), new java.awt.Color(220, 225, 255), "C:\\Users\\HP\\Documents\\NetBeansProjects\\InventoriKu\\src\\main\\java\\assets\\barang-icon.png");
        statCard2.setData("KATEGORI", getTotalKategori(), new java.awt.Color(160, 250, 200), "C:\\Users\\HP\\Documents\\NetBeansProjects\\InventoriKu\\src\\main\\java\\assets\\category-icon.png");
        statCard3.setData("STOK KRITIS", getStokKritis(), new java.awt.Color(255, 210, 210), "C:\\Users\\HP\\Documents\\NetBeansProjects\\InventoriKu\\src\\main\\java\\assets\\danger-icon.png");
    }

    // ==============================================
    // MENGAMBIL DATA (READ & STATS)
    // ==============================================

    private void loadDataBarang() {
        String[] kolomBarang = {
            "ID",            // Index 0 (Akan disembunyikan)
            "Nama Barang",   // Index 1
            "Kategori",      // Index 2
            "Stok Saat Ini", // Index 3 (Diwarnai Hijau/Merah)
            "Supplier",      // Index 4
            "Aksi"           // Index 5 (Tombol Edit/Hapus)
        };

        tblBarang.setColumns(kolomBarang);
        
        // Sembunyikan kolom ID secara visual, tapi tetap dapat diambil datanya
        tblBarang.getTable().getColumnModel().getColumn(0).setMinWidth(0);
        tblBarang.getTable().getColumnModel().getColumn(0).setMaxWidth(0);
        tblBarang.getTable().getColumnModel().getColumn(0).setWidth(0);

        // Update Indeks kolom karena ketambahan kolom "ID"
        tblBarang.addStatusColumn(3); 
        tblBarang.addActionColumn(5); 

        DefaultTableModel model = tblBarang.getModel();
        model.setRowCount(0);

        try {
            Connection conn = db.koneksi.getConnection();
            String sql = """
                SELECT
                    b.id_barang,
                    b.nama_barang,
                    k.nama_kategori,
                    b.stok,
                    b.stok_minimum,
                    b.supplier
                FROM barang b
                LEFT JOIN kategori k
                ON b.id_kategori = k.id_kategori
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idBarang = rs.getInt("id_barang");
                int stok = rs.getInt("stok");
                int stokMin = rs.getInt("stok_minimum");
                
                String statusTeks = (stok <= stokMin) ? stok + " (Kritis)" : stok + " (Aman)";

                model.addRow(new Object[]{
                    idBarang,                           // 0
                    rs.getString("nama_barang"),        // 1
                    rs.getString("nama_kategori"),      // 2
                    statusTeks,                         // 3
                    rs.getString("supplier"),           // 4
                    ""                                  // 5 Slot Action
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getStokKritis() {
        try {
            Connection conn = db.koneksi.getConnection();
            String sql = "SELECT COUNT(*) FROM barang WHERE stok <= stok_minimum";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) + " Barang";
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "0 Barang";
    }
    
    private String getTotalKategori() {
        try {
            Connection conn = db.koneksi.getConnection();
            String sql = "SELECT COUNT(*) FROM kategori";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }
    
    private String getTotalBarang() {
        try {
            Connection conn = db.koneksi.getConnection();
            String sql = "SELECT COUNT(*) FROM barang";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        statCard1 = new Components.StatCard();
        statCard2 = new Components.StatCard();
        statCard3 = new Components.StatCard();
        statCard4 = new Components.StatCard();
        tblBarang = new Components.DataTable();

        setMaximumSize(new java.awt.Dimension(820, 2147483647));

        jPanel1.setMaximumSize(new java.awt.Dimension(800, 90));
        jPanel1.setMinimumSize(new java.awt.Dimension(800, 90));
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 90));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 18));
        jPanel1.add(statCard1);
        jPanel1.add(statCard2);
        jPanel1.add(statCard3);
        jPanel1.add(statCard4);

        tblBarang.setEnabled(false);
        tblBarang.setPreferredSize(new java.awt.Dimension(820, 377));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1080, Short.MAX_VALUE)
                    .addComponent(tblBarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private Components.StatCard statCard1;
    private Components.StatCard statCard2;
    private Components.StatCard statCard3;
    private Components.StatCard statCard4;
    private Components.DataTable tblBarang;
    // End of variables declaration//GEN-END:variables
}
