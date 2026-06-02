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
            "ID", "Nama Kategori", "Aksi"
        };
        dataTable1.setColumns(kolomKategori);
        dataTable1.getTable().getColumnModel().getColumn(0).setMinWidth(0);
        dataTable1.getTable().getColumnModel().getColumn(0).setMaxWidth(0);
        dataTable1.getTable().getColumnModel().getColumn(0).setWidth(0);
        dataTable1.addActionColumn(2);

        dataTable1.getSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadDataKategori();
            }
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

        refreshData();
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
        statCard1.setData("TOTAL KATEGORI", getTotalKategori(), new java.awt.Color(160, 250, 200), "C:\\Users\\HP\\Documents\\NetBeansProjects\\InventoriKu\\src\\main\\java\\assets\\category-icon.png");
        statCard2.setData("BARANG TERDAFTAR", getTotalBarang(), new java.awt.Color(220, 225, 255), "C:\\Users\\HP\\Documents\\NetBeansProjects\\InventoriKu\\src\\main\\java\\assets\\barang-icon.png");
    }

    private void loadDataKategori() {
        String keyword = dataTable1.getSearchField().getText().trim();
        if (keyword.equals("Cari Kategori...")) {
            keyword = "";
        }

        DefaultTableModel model = dataTable1.getModel();
        model.setRowCount(0);

        try {
            Connection conn = db.koneksi.getConnection();
            String sql = "SELECT id_kategori, nama_kategori FROM kategori WHERE nama_kategori LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_kategori"),
                    rs.getString("nama_kategori"),
                    ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getTotalKategori() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM kategori");
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getTotalBarang() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM barang");
            if (rs.next()) return String.valueOf(rs.getInt(1));
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

        statCard1 = new Components.StatCard();
        statCard2 = new Components.StatCard();
        dataTable1 = new Components.DataTable();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 1054, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(statCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(dataTable1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Components.DataTable dataTable1;
    private Components.StatCard statCard1;
    private Components.StatCard statCard2;
    // End of variables declaration//GEN-END:variables
}
