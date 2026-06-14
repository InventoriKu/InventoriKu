/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Components;

import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.JButton;
/**
 *
 * @author farre
 */

public class StockForm extends javax.swing.JPanel {

    /**
     * Creates new form StockForm
     */
    private java.util.Date currentDate;
    private java.text.SimpleDateFormat sdfTampilan = new java.text.SimpleDateFormat("dd/MM/yyyy");
    private java.text.SimpleDateFormat sdfDatabase = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public StockForm() {
        initComponents();  
        setTanggalOtomatis();
    }
    
    public void loadBarangData() {
        javax.swing.JComboBox cb = (javax.swing.JComboBox) inputBarang;
        cb.removeAllItems();
        try {
            java.sql.Connection conn = db.koneksi.getConnection();
            java.sql.ResultSet rs = conn.createStatement().executeQuery(
                "SELECT id_barang, nama_barang FROM barang ORDER BY nama_barang"
            );
            while (rs.next()) {
                cb.addItem(new BarangItem(rs.getInt("id_barang"), rs.getString("nama_barang")));
            }
            
            Object pertama = cb.getItemAt(0);
            if (pertama instanceof BarangItem) {
                loadSupplierUntukBarang(((BarangItem) pertama).id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load supplier/departemen (untuk stok masuk: supplier unik dari barang)
    public void loadSupplierData() {
        inputTarget.removeAllItems();
        try {
            java.sql.Connection conn = db.koneksi.getConnection();
            java.sql.ResultSet rs = conn.createStatement().executeQuery(
                "SELECT DISTINCT supplier FROM barang WHERE supplier IS NOT NULL AND supplier != '' ORDER BY supplier"
            );
            while (rs.next()) {
                inputTarget.addItem(rs.getString("supplier"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load departemen (untuk stok keluar)
    public void loadDepartemenData() {
        inputTarget.removeAllItems();
        inputTarget.addItem("Departemen IT");
        inputTarget.addItem("Departemen HR");
        inputTarget.addItem("Departemen Finance");
        inputTarget.addItem("Departemen Marketing");
        inputTarget.addItem("Departemen Produksi");
    }

    // Ambil id_barang yang dipilih
    public int getIdBarang() {
        Object item = inputBarang.getSelectedItem();
        if (item instanceof BarangItem) {
            return ((BarangItem) item).id;
        }
        return -1;
    }

    // Inner class untuk combo box barang
    public static class BarangItem {
        public int id;
        public String nama;

        public BarangItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        @Override
        public String toString() {
            return nama;
        }
    }

    
    public void setJudul(String judul) {
        label1.setText(judul);
    }
   
    public void setLabelTarget(String teks) {
        jLabel7.setText(teks); // "SUPPLIER" atau "DEPARTEMEN"
    }     

    public void setItemsBarang(String[] items) {
        inputBarang.removeAllItems();
        for (String item : items) inputBarang.addItem(item);
    }

    public void setItemsTarget(String[] items) {
        inputTarget.removeAllItems();
        for (String item : items) inputTarget.addItem(item);
    }

    // Ambil nilai dari form
    public String getBarang() {
        Object item = inputBarang.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    public String getTarget() {
        Object item = inputTarget.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    public String getJumlah() {
        return inputJumlah.getText().trim();
    }

    public java.util.Date getTanggal() {        
        try {
            
            return sdfDatabase.parse(inputTanggal.getText());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void setTanggalOtomatis() {
        currentDate = new java.util.Date(); 
        inputTanggal.setText(sdfTampilan.format(currentDate));
    }

    public String getCatatan() {
        return inoutCatatan.getText().trim();
    }

    public void resetForm() {
        inputJumlah.setText("");
        inoutCatatan.setText("");        
    }

    // Pasang listener tombol dari luar
    public void setConfirmation(java.awt.event.ActionListener listener) {                
        for (java.awt.event.ActionListener al : confirmButton.getActionListeners()) {
            confirmButton.removeActionListener(al);
        }
        confirmButton.addActionListener(listener);
    }
    
    private void loadSupplierUntukBarang(int idBarang) {
        inputTarget.removeAllItems();
        try {
            java.sql.Connection conn = db.koneksi.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT supplier FROM barang WHERE id_barang = ? AND supplier IS NOT NULL AND supplier != ''"
            );
            ps.setInt(1, idBarang);
            java.sql.ResultSet rs = ps.executeQuery();

            boolean adaSupplier = false;
            while (rs.next()) {
                inputTarget.addItem(rs.getString("supplier"));
                adaSupplier = true;
            }

            if (!adaSupplier) {
                inputTarget.addItem("(Tidak ada supplier terdaftar)");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        label1 = new java.awt.Label();
        inputTarget = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        inputJumlah = new javax.swing.JTextField();
        inputBarang = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        inoutCatatan = new javax.swing.JTextArea();
        inputTanggal = new javax.swing.JTextField();
        confirmButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(280, 410));

        label1.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        label1.setForeground(new java.awt.Color(30, 58, 138));
        label1.setText("Stok Masuk");

        inputTarget.setEditable(true);
        inputTarget.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        inputTarget.addActionListener(this::inputTargetActionPerformed);

        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("TANGGAL");

        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("JUMLAH UNIT");

        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("PILIH BARANG");

        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("CATATAN (OPSIONAL)");

        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("SUPPLIER");

        inputJumlah.addActionListener(this::inputJumlahActionPerformed);

        inputBarang.setEditable(true);
        inputBarang.addActionListener(this::inputBarangActionPerformed);

        inoutCatatan.setColumns(20);
        inoutCatatan.setRows(5);
        jScrollPane1.setViewportView(inoutCatatan);

        inputTanggal.setEditable(false);
        inputTanggal.setBackground(new java.awt.Color(255, 255, 255));
        inputTanggal.addActionListener(this::inputTanggalActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
            .addComponent(inputTarget, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputBarang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(inputJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(inputTanggal)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(inputJumlah, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(inputTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(9, 9, 9)
                .addComponent(inputTarget, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        confirmButton.setBackground(new java.awt.Color(30, 58, 138));
        confirmButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        confirmButton.setForeground(new java.awt.Color(255, 255, 255));
        confirmButton.setText("KONFIRMASI PERUBAHAN STOK");
        confirmButton.addActionListener(this::confirmButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                    .addComponent(confirmButton, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void inputTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputTargetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputTargetActionPerformed

    private void inputBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputBarangActionPerformed
        // TODO add your handling code here:
        Object selected = inputBarang.getSelectedItem();
        if (selected instanceof BarangItem) {
            int idBarang = ((BarangItem) selected).id;
            loadSupplierUntukBarang(idBarang);
        }
    }//GEN-LAST:event_inputBarangActionPerformed

    private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_confirmButtonActionPerformed

    private void inputJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputJumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputJumlahActionPerformed

    private void inputTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputTanggalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton confirmButton;
    private javax.swing.JTextArea inoutCatatan;
    private javax.swing.JComboBox<String> inputBarang;
    private javax.swing.JTextField inputJumlah;
    private javax.swing.JTextField inputTanggal;
    private javax.swing.JComboBox<String> inputTarget;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Label label1;
    // End of variables declaration//GEN-END:variables
}
