/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Components;

/**
 *
 * @author HP
 */
public class DataTable extends javax.swing.JPanel {

    /**
     * Creates new form DataTable
     */
    public DataTable() {
        initComponents();
        
        // Design table header
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel headerLabel = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 15, 12, 15)); 
                headerLabel.setBackground(new java.awt.Color(240, 244, 248));
                headerLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                return headerLabel;
            }
        });

        // design kolom tabel
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                javax.swing.JLabel cell = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)); 

                if (!isSelected) {
                    if (row % 2 == 0) {
                        cell.setBackground(java.awt.Color.WHITE);
                    } else {
                        cell.setBackground(new java.awt.Color(252, 253, 254)); 
                    }
                    cell.setForeground(java.awt.Color.BLACK);
                    cell.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
                }

                if (column == 2 && value != null) {
                    String textValue = value.toString().toUpperCase();

                    if (textValue.contains("THRESHOLD") || textValue.contains("KRITIS") || textValue.contains("RENDAH")) {
                        cell.setForeground(new java.awt.Color(204, 0, 0)); 
                        cell.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                    } else if (textValue.contains("AMAN") || textValue.contains("CUKUP")) {
                        cell.setForeground(new java.awt.Color(0, 153, 76)); 
                        cell.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                    }
                }

                return cell;
            }
        });

        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0)); 
        table.setBackground(java.awt.Color.WHITE); 
        table.setSelectionBackground(new java.awt.Color(242, 244, 248));
        table.setSelectionForeground(java.awt.Color.BLACK);        
    }
    
    public void setupTable(String[] columnNames) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(columnNames);
        model.setRowCount(0);
        
        int kolomTerakhir = table.getColumnCount() - 1;
        if (kolomTerakhir >= 0) {
            table.getColumnModel().getColumn(kolomTerakhir).setCellRenderer(new ActionButtonRenderer());
        }
        
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12)); 
        header.setBackground(new java.awt.Color(240, 242, 245)); 
        header.setForeground(new java.awt.Color(100, 110, 120));
        header.setReorderingAllowed(false); 

        if (table.getParent() != null && table.getParent().getParent() instanceof javax.swing.JScrollPane) {
            javax.swing.JScrollPane scrollPane = (javax.swing.JScrollPane) table.getParent().getParent();
            scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(java.awt.Color.WHITE);
        }
    }
    
    // Button Action Renderer
    class ActionButtonRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 8));

            panel.setBackground(isSelected ? table.getSelectionBackground() : java.awt.Color.WHITE);

            javax.swing.JButton btnEdit = new javax.swing.JButton("Edit");
            javax.swing.JButton btnHapus = new javax.swing.JButton("Hapus");

            styleMinibutton(btnEdit, new java.awt.Color(0, 102, 204));
            styleMinibutton(btnHapus, new java.awt.Color(204, 0, 0));

            panel.add(btnEdit);
            panel.add(btnHapus);

            return panel;
        }

        private void styleMinibutton(javax.swing.JButton btn, java.awt.Color color) {
            btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            btn.setForeground(color);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false); 
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn.setMargin(new java.awt.Insets(0, 0, 0, 0)); 
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

        textFieldSearch = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        textFieldSearch.setBackground(new java.awt.Color(242, 244, 246));
        textFieldSearch.setForeground(new java.awt.Color(197, 197, 211));
        textFieldSearch.setText("Cari Barang");
        textFieldSearch.setToolTipText("");
        textFieldSearch.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(107, 114, 128), 1, true));
        textFieldSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldSearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldSearchFocusLost(evt);
            }
        });
        textFieldSearch.addActionListener(this::textFieldSearchActionPerformed);

        btnTambah.setBackground(new java.awt.Color(0, 35, 111));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 35, 111), 1, true));
        btnTambah.setIconTextGap(12);
        btnTambah.setLabel("Tambah Barang");

        jComboBox1.setBackground(new java.awt.Color(242, 244, 246));
        jComboBox1.setForeground(new java.awt.Color(25, 28, 30));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua Kategori" }));
        jComboBox1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(107, 114, 128), 1, true));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.setEnabled(false);
        table.setFocusable(false);
        jScrollPane1.setViewportView(table);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Menampilkan 5 dari 15 barang");

        btnPrevious.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPrevious.setText("<");
        btnPrevious.addActionListener(this::btnPreviousActionPerformed);

        btnNext.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNext.setText(">");
        btnNext.addActionListener(this::btnNextActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap())
                    .addComponent(btnPrevious, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(btnNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnNextActionPerformed

    private void textFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldSearchActionPerformed

    private void textFieldSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldSearchFocusGained
        // TODO add your handling code here:
        if (textFieldSearch.getText().equals("Cari Barang...")) {
           textFieldSearch.setText("");
           textFieldSearch.setForeground(java.awt.Color.BLACK); 
        }
    }//GEN-LAST:event_textFieldSearchFocusGained

    private void textFieldSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldSearchFocusLost
        // TODO add your handling code here:
        if (textFieldSearch.getText().isEmpty()) {
            textFieldSearch.setText("Cari Barang...");
            textFieldSearch.setForeground(new java.awt.Color(153, 153, 153));
        }
    }//GEN-LAST:event_textFieldSearchFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable table;
    private javax.swing.JTextField textFieldSearch;
    // End of variables declaration//GEN-END:variables
}
