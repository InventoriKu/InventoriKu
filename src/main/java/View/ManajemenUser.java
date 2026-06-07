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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author LENOVO
 */
public class ManajemenUser extends javax.swing.JPanel {

    /**
     * Creates new form ManajemenUser
     */
    public ManajemenUser() {
         initComponents();
        db.koneksi.getConnection();              

        dataTable1.setSearchPlaceholder("Cari User (Username/Nama)...");
        dataTable1.setButtonText("Tambah User");
        
        dataTable1.setComboBoxModel(new String[]{"Semua Role", "ADMIN", "STAFF"});

        String[] kolomUser = {
            "ID", "Username", "Nama Lengkap", "Role", "Aksi"
        };
        dataTable1.setColumns(kolomUser);
        dataTable1.getTable().getColumnModel().getColumn(0).setMinWidth(0);
        dataTable1.getTable().getColumnModel().getColumn(0).setMaxWidth(0);
        dataTable1.getTable().getColumnModel().getColumn(0).setWidth(0);
        dataTable1.addActionColumn(4);
        
        dataTable1.setPaginationActionListener((targetPage, limit) -> {
            loadDataUser(targetPage, limit);
        });

        dataTable1.getSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadDataUser();
            }
        });

        dataTable1.getComboBox().addActionListener(e -> {
            loadDataUser();
        });

        dataTable1.getBtnTambah().addActionListener(e -> {
            showUserDialog(null);
        });
        
        dataTable1.setTableActionListener(new Components.DataTable.TableActionListener() {
            @Override
            public void onEdit(int row) {
                int idUser = (int) dataTable1.getModel().getValueAt(row, 0);
                showUserDialog(idUser);
            }

            @Override
            public void onDelete(int row) {
                int idUser = (int) dataTable1.getModel().getValueAt(row, 0);
                String username = dataTable1.getModel().getValueAt(row, 1).toString();
                hapusUser(idUser, username);
            }
        });

        refreshStatCard();
        loadDataUser(dataTable1.getCurrentPage(), dataTable1.getLimitPerPage());
    }
    
    private void showUserDialog(Integer idUser) {
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtNamaLengkap = new JTextField();
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"STAFF", "ADMIN"});

        if (idUser != null) {
            try {
                Connection conn = db.koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT username, nama_lengkap, role FROM users WHERE id_user = ?");
                ps.setInt(1, idUser);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtUsername.setText(rs.getString("username"));
                    txtNamaLengkap.setText(rs.getString("nama_lengkap"));
                    cmbRole.setSelectedItem(rs.getString("role"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(txtUsername);
        
        String passLabel = (idUser == null) ? "Password:" : "Password (opsional):";
        panel.add(new JLabel(passLabel));
        panel.add(txtPassword);
        
        panel.add(new JLabel("Nama Lengkap:"));
        panel.add(txtNamaLengkap);
        panel.add(new JLabel("Role:"));
        panel.add(cmbRole);

        String title = (idUser == null) ? "Tambah User Baru" : "Edit User";
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String namaLengkap = txtNamaLengkap.getText().trim();
            String role = cmbRole.getSelectedItem().toString();
            
            if (username.isEmpty() || namaLengkap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan Nama Lengkap tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Connection conn = db.koneksi.getConnection();
                if (idUser == null) {
                    if (password.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Password tidak boleh kosong untuk user baru!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    String sql = "INSERT INTO users (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, namaLengkap);
                    ps.setString(4, role);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "User Berhasil Ditambahkan!");
                    
                } else {
                    // Update User
                    String sql;
                    PreparedStatement ps;
                    
                    if (password.isEmpty()) {
                        sql = "UPDATE users SET username=?, nama_lengkap=?, role=? WHERE id_user=?";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, username);
                        ps.setString(2, namaLengkap);
                        ps.setString(3, role);
                        ps.setInt(4, idUser);
                    } else {
                        sql = "UPDATE users SET username=?, password=?, nama_lengkap=?, role=? WHERE id_user=?";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, username);
                        ps.setString(2, password);
                        ps.setString(3, namaLengkap);
                        ps.setString(4, role);
                        ps.setInt(5, idUser);
                    }
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "User Berhasil Diperbarui!");
                }
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                if(e.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(this, "Username sudah digunakan, silakan pilih yang lain!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void hapusUser(int idUser, String username) {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus user '" + username + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = db.koneksi.getConnection();
                String sql = "DELETE FROM users WHERE id_user = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idUser);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User Berhasil Dihapus!");
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal Menghapus! Pastikan user ini tidak terikat dengan data transaksi masuk/keluar.\n\nError: " + e.getMessage(), "User Terpakai", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshData() {
        loadDataUser();
        refreshStatCard();
    }

    private void refreshStatCard() {
        statCard1.setData("TOTAL USER", getTotalUser(), new java.awt.Color(160, 250, 200), "assets/user-icon.png");
        statCard2.setData("TOTAL ADMIN", getTotalAdmin(), new java.awt.Color(220, 225, 255), "assets/admin-icon.png");
        statCard3.setData("TOTAL STAFF", getTotalStaff(), new java.awt.Color(255, 220, 180), "assets/staff-icon.png");
    }

    private void loadDataUser() {
        dataTable1.resetPage();
        loadDataUser(dataTable1.getCurrentPage(), dataTable1.getLimitPerPage());
    }

    private void loadDataUser(int page, int limit) {
        String keyword = dataTable1.getSearchField().getText().trim();
        if (keyword.equals("Cari User (Username/Nama)...")) {
            keyword = "";
        }

        String selectedRole = dataTable1.getComboBox().getSelectedItem() != null 
            ? dataTable1.getComboBox().getSelectedItem().toString() 
            : "Semua Role";

        DefaultTableModel model = dataTable1.getModel();
        model.setRowCount(0);

        int totalDataFilter = 0;
        int offset = (page - 1) * limit; 

        try {
            Connection conn = db.koneksi.getConnection();

            StringBuilder countSql = new StringBuilder("""
                SELECT COUNT(*) FROM users 
                WHERE (username LIKE ? OR nama_lengkap LIKE ?)
            """);

            if (!selectedRole.equals("Semua Role")) {
                countSql.append(" AND role = ? ");
            }

            PreparedStatement psCount = conn.prepareStatement(countSql.toString());
            psCount.setString(1, "%" + keyword + "%");
            psCount.setString(2, "%" + keyword + "%");
            
            if (!selectedRole.equals("Semua Role")) {
                psCount.setString(3, selectedRole);
            }

            ResultSet rsCount = psCount.executeQuery();
            if (rsCount.next()) {
                totalDataFilter = rsCount.getInt(1);
            }
            
            StringBuilder dataSql = new StringBuilder("""
                SELECT id_user, username, nama_lengkap, role FROM users 
                WHERE (username LIKE ? OR nama_lengkap LIKE ?)
            """);

            if (!selectedRole.equals("Semua Role")) {
                dataSql.append(" AND role = ? ");
            }
            
            dataSql.append(" LIMIT ? OFFSET ? ");

            PreparedStatement psData = conn.prepareStatement(dataSql.toString());
            
            int paramIndex = 1;
            psData.setString(paramIndex++, "%" + keyword + "%");
            psData.setString(paramIndex++, "%" + keyword + "%");
            
            if (!selectedRole.equals("Semua Role")) {
                psData.setString(paramIndex++, selectedRole);
            }
            
            psData.setInt(paramIndex++, limit);
            psData.setInt(paramIndex++, offset);

            ResultSet rs = psData.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("nama_lengkap"),
                    rs.getString("role"),
                    ""
                });
            }

            dataTable1.updatePaginationStatus(totalDataFilter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getTotalUser() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) return String.valueOf(rs.getInt(1)) + " Akun";
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getTotalAdmin() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'");
            if (rs.next()) return String.valueOf(rs.getInt(1)) + " Admin";
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getTotalStaff() {
        try {
            Connection conn = db.koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users WHERE role = 'STAFF'");
            if (rs.next()) return String.valueOf(rs.getInt(1)) + " Staff";
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
        statCard3 = new Components.StatCard();
        dataTable1 = new Components.DataTable();
        jLabel1 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(30, 58, 138));
        jLabel1.setText("Manajemen User");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 1029, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(statCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(statCard3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statCard3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(dataTable1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Components.DataTable dataTable1;
    private javax.swing.JLabel jLabel1;
    private Components.StatCard statCard1;
    private Components.StatCard statCard2;
    private Components.StatCard statCard3;
    // End of variables declaration//GEN-END:variables
}
