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
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
/**
 *
 * @author HP
 */
public class ManajemenBarang extends javax.swing.JPanel {

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
        db.koneksi.getConnection();

        tblBarang.setSearchPlaceholder("Cari Barang...");
        tblBarang.setButtonText("Tambah Barang");
        
        loadKategoriFilter();        
        
        String[] kolomBarang = {
            "ID", "Nama Barang", "Kategori", "Stok Saat Ini", "Supplier", "Aksi"
        };
        tblBarang.setColumns(kolomBarang);
        tblBarang.getTable().getColumnModel().getColumn(0).setMinWidth(0);
        tblBarang.getTable().getColumnModel().getColumn(0).setMaxWidth(0);
        tblBarang.getTable().getColumnModel().getColumn(0).setWidth(0);
        tblBarang.addStatusColumn(3); 
        tblBarang.addActionColumn(5); 
        
        tblBarang.setPaginationActionListener((targetPage, limit) -> {
            loadDataBarang(targetPage, limit);
        });

        tblBarang.getSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadDataBarang();
            }
        });
        
        tblBarang.getBtnCetak().addActionListener(e -> {
            cetakLaporan();
        });

        tblBarang.getComboBox().addActionListener(e -> {
            loadDataBarang();
        });

        tblBarang.getBtnTambah().addActionListener(e -> {
            showBarangDialog(null);
        });
        
        tblBarang.setTableActionListener(new Components.DataTable.TableActionListener() {
            @Override
            public void onEdit(int row) {
                int idBarang = (int) tblBarang.getModel().getValueAt(row, 0);
                showBarangDialog(idBarang);
            }

            @Override
            public void onDelete(int row) {
                int idBarang = (int) tblBarang.getModel().getValueAt(row, 0);
                String namaBarang = tblBarang.getModel().getValueAt(row, 1).toString();
                hapusBarang(idBarang, namaBarang);
            }
        });

        refreshStatCard();
        loadDataBarang(tblBarang.getCurrentPage(), tblBarang.getLimitPerPage());                
    }
    
    private void loadKategoriFilter() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT nama_kategori FROM kategori");
            java.util.ArrayList<String> list = new java.util.ArrayList<>();
            list.add("Semua Kategori");
            while (rs.next()) {
                list.add(rs.getString("nama_kategori"));
            }
            tblBarang.setComboBoxModel(list.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBarangDialog(Integer idBarang) {
        JTextField txtNama = new JTextField();
        JComboBox<KategoriItem> cbKategori = new JComboBox<>();
        JSpinner spinStok = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        JSpinner spinMinStok = new JSpinner(new SpinnerNumberModel(5, 0, 999999, 1));
        JTextField txtSupplier = new JTextField();

        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT id_kategori, nama_kategori FROM kategori");
            while (rs.next()) {
                cbKategori.addItem(new KategoriItem(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }

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

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Nama Barang:")); panel.add(txtNama);
        panel.add(new JLabel("Kategori:")); panel.add(cbKategori);
        panel.add(new JLabel("Stok:")); panel.add(spinStok);
        panel.add(new JLabel("Stok Minimum (Batas Kritis):")); panel.add(spinMinStok);
        panel.add(new JLabel("Supplier:")); panel.add(txtSupplier);

        String title = (idBarang == null) ? "Tambah Barang Baru" : "Edit Data Barang";
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (txtNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama barang tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtSupplier.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Supplier barang tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            KategoriItem selectedKat = (KategoriItem) cbKategori.getSelectedItem();
            if (selectedKat == null) {
                JOptionPane.showMessageDialog(this, 
                    "Kategori belum dipilih!\nSilakan tambahkan kategori terlebih dahulu melalui menu Kelola Kategori.", 
                    "Kategori Tidak Tersedia", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Integer katId =  selectedKat.id;

            try {
                Connection conn = db.koneksi.getConnection();
                
                if (idBarang == null) {
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
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusBarang(int idBarang, String namaBarang) {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus barang '" + namaBarang + "'? Perubahan akan memengaruhi seluruh pergerekan stok " + namaBarang, "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Gagal Menghapus! Barang mungkin memiliki riwayat transaksi.\nError: " + e.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshData() {
        loadDataBarang();
        refreshStatCard();
    }
    
    private void refreshStatCard() {
        String totalBarang  = getTotalBarang();
        String totalKategori = getTotalKategori();
        String stokKritis   = getStokKritis();

        // Tampilkan ke StatCard
        statCard1.setData("TOTAL BARANG",  totalBarang,   new java.awt.Color(220, 225, 255), "assets/barang-icon.png");
        statCard2.setData("KATEGORI",      totalKategori, new java.awt.Color(160, 250, 200), "assets/category-icon.png");
        statCard3.setData("STOK KRITIS",   stokKritis,    new java.awt.Color(255, 210, 210), "assets/danger-icon.png");
    }

    private void loadDataBarang() {
        tblBarang.resetPage(); // Reset halaman ke 1 saat user ngetik pencarian / ganti kategori
        loadDataBarang(tblBarang.getCurrentPage(), tblBarang.getLimitPerPage());
    }

    private void loadDataBarang(int page, int limit) {
        String keyword = tblBarang.getSearchField().getText().trim();
        if (keyword.equals("Cari Barang...")) {
            keyword = "";
        }

        String selectedKategori = tblBarang.getComboBox().getSelectedItem() != null 
            ? tblBarang.getComboBox().getSelectedItem().toString() 
            : "Semua Kategori";

        DefaultTableModel model = tblBarang.getModel();
        model.setRowCount(0);

        int totalDataFilter = 0;
        int offset = (page - 1) * limit; 

        try {
            Connection conn = db.koneksi.getConnection();
            
            StringBuilder countSql = new StringBuilder("""
                SELECT COUNT(*) 
                FROM barang b
                LEFT JOIN kategori k ON b.id_kategori = k.id_kategori
                WHERE (b.nama_barang LIKE ? OR b.supplier LIKE ?)
            """);
            
            if (!selectedKategori.equals("Semua Kategori")) {
                countSql.append(" AND k.nama_kategori = ? ");
            }
            
            PreparedStatement psCount = conn.prepareStatement(countSql.toString());
            psCount.setString(1, "%" + keyword + "%");
            psCount.setString(2, "%" + keyword + "%");
            if (!selectedKategori.equals("Semua Kategori")) {
                psCount.setString(3, selectedKategori);
            }
            
            ResultSet rsCount = psCount.executeQuery();
            if (rsCount.next()) {
                totalDataFilter = rsCount.getInt(1);
            }

            StringBuilder dataSql = new StringBuilder("""
                SELECT
                    b.id_barang,
                    b.nama_barang,
                    k.nama_kategori,
                    b.stok,
                    b.stok_minimum,
                    b.supplier
                FROM barang b
                LEFT JOIN kategori k ON b.id_kategori = k.id_kategori
                WHERE (b.nama_barang LIKE ? OR b.supplier LIKE ?)
            """);

            if (!selectedKategori.equals("Semua Kategori")) {
                dataSql.append(" AND k.nama_kategori = ? ");
            }
            
            dataSql.append(" LIMIT ? OFFSET ? ");

            PreparedStatement psData = conn.prepareStatement(dataSql.toString());
            
            int paramIndex = 1;
            psData.setString(paramIndex++, "%" + keyword + "%");
            psData.setString(paramIndex++, "%" + keyword + "%");
            
            if (!selectedKategori.equals("Semua Kategori")) {
                psData.setString(paramIndex++, selectedKategori);
            }
            
            psData.setInt(paramIndex++, limit);
            psData.setInt(paramIndex++, offset);

            ResultSet rs = psData.executeQuery();
            while (rs.next()) {
                int idBarang = rs.getInt("id_barang");
                int stok = rs.getInt("stok");
                int stokMin = rs.getInt("stok_minimum");
                String statusTeks = (stok <= stokMin) ? stok + " (Kritis)" : stok + " (Aman)";

                model.addRow(new Object[]{
                    idBarang,
                    rs.getString("nama_barang"),
                    rs.getString("nama_kategori"),
                    statusTeks,
                    rs.getString("supplier"),
                    ""
                });
            }
            
            tblBarang.updatePaginationStatus(totalDataFilter);

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
            if (rs.next()) return rs.getInt(1) + " Unit";
            System.out.println(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "0 Barang";
    }
    
    private String getTotalKategori() {
        try {
            Connection conn = db.koneksi.getConnection();
            String sql = "SELECT COUNT(*) FROM kategori";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return String.valueOf(rs.getInt(1))+ " Grup";
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }
    
    private String getTotalBarang() {
        try {
            Connection conn = db.koneksi.getConnection();
            String sql = "SELECT COUNT(*) FROM barang";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return String.valueOf(rs.getInt(1))+ " Unit";
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }
    
    private void cetakLaporan() {
        try {
            String keyword = "";
            if (tblBarang.getSearchField() != null && tblBarang.getSearchField().getText() != null) {
                keyword = tblBarang.getSearchField().getText().trim();
                if (keyword.equalsIgnoreCase("Cari Barang...")) {
                    keyword = "";
                }
            }

            String selectedKategori = "Semua Kategori";
            if (tblBarang.getComboBox() != null && tblBarang.getComboBox().getSelectedItem() != null) {
                selectedKategori = tblBarang.getComboBox().getSelectedItem().toString();
            }

            String[] printKolom = {"Nama Barang", "Kategori", "Stok Saat Ini", "Supplier"};
            DefaultTableModel printModel = new DefaultTableModel(printKolom, 0);

            Connection conn = db.koneksi.getConnection();
            StringBuilder dataSql = new StringBuilder("""
                SELECT
                    b.nama_barang,
                    k.nama_kategori,
                    b.stok,
                    b.stok_minimum,
                    b.supplier
                FROM barang b
                LEFT JOIN kategori k ON b.id_kategori = k.id_kategori
                WHERE 1=1 
            """);

            if (!keyword.isEmpty()) {
                dataSql.append(" AND (b.nama_barang LIKE ? OR b.supplier LIKE ?) ");
            }
            if (!selectedKategori.equals("Semua Kategori")) {
                dataSql.append(" AND k.nama_kategori = ? ");
            }

            PreparedStatement psData = conn.prepareStatement(dataSql.toString());
            int paramIndex = 1;
            
            if (!keyword.isEmpty()) {
                psData.setString(paramIndex++, "%" + keyword + "%");
                psData.setString(paramIndex++, "%" + keyword + "%");
            }
            if (!selectedKategori.equals("Semua Kategori")) {
                psData.setString(paramIndex++, selectedKategori);
            }

            ResultSet rs = psData.executeQuery();
            while (rs.next()) {
                int stok = rs.getInt("stok");
                int stokMin = rs.getInt("stok_minimum");
                String statusTeks = (stok <= stokMin) ? stok + " (Kritis)" : stok + " (Aman)";

                printModel.addRow(new Object[]{
                    rs.getString("nama_barang"),
                    rs.getString("nama_kategori") != null ? rs.getString("nama_kategori") : "-",
                    statusTeks,
                    rs.getString("supplier") != null ? rs.getString("supplier") : "-"
                });
            }

            if (printModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data yang sesuai untuk dicetak!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
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
            MessageFormat header = new MessageFormat("Laporan Data Barang InventoriKu | Dicetak: " + tanggal);
            MessageFormat footer = new MessageFormat("Halaman {0}");

            JOptionPane.showMessageDialog(this, "Menyiapkan " + printModel.getRowCount() + " baris data. Silakan tunggu dialog Print...", "Informasi", JOptionPane.INFORMATION_MESSAGE);

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
                JOptionPane.showMessageDialog(this, "Laporan berhasil dicetak!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan oleh pengguna.", "Dibatalkan", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan!\nError: " + e.getMessage(), "Error Print", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        statCard1 = new Components.StatCard();
        statCard2 = new Components.StatCard();
        statCard3 = new Components.StatCard();
        tblBarang = new Components.DataTable();
        label2 = new java.awt.Label();

        setMaximumSize(new java.awt.Dimension(820, 2147483647));

        jPanel1.setMaximumSize(new java.awt.Dimension(800, 90));
        jPanel1.setMinimumSize(new java.awt.Dimension(800, 90));
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 90));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 18));
        jPanel1.add(statCard1);
        jPanel1.add(statCard2);
        jPanel1.add(statCard3);

        label2.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        label2.setForeground(new java.awt.Color(30, 58, 138));
        label2.setText("Manajemen Barang");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tblBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 1064, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private java.awt.Label label2;
    private Components.StatCard statCard1;
    private Components.StatCard statCard2;
    private Components.StatCard statCard3;
    private Components.DataTable tblBarang;
    // End of variables declaration//GEN-END:variables
}
