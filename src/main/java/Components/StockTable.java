/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Components;

/**
 *
 * @author HP
 */
public class StockTable extends javax.swing.JPanel {

    /**
     * Creates new form DataTable
     */
    public StockTable() {
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

                if (column == 2) {
                    cell.setHorizontalAlignment(javax.swing.JLabel.LEFT);
                    cell.setHorizontalTextPosition(javax.swing.JLabel.LEFT);
                }
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        cell.setBackground(java.awt.Color.WHITE);
                    } else {
                        cell.setBackground(new java.awt.Color(252, 253, 254)); 
                    }
                    cell.setForeground(java.awt.Color.BLACK);
                    cell.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
                }

                

                if (!isSelected && column == 0 && value != null) {  // HANYA KOLOM PERTAMA (TIPE)
                    String tipe = value.toString();
                    if (tipe.equalsIgnoreCase("Masuk")) {
                        cell.setForeground(new java.awt.Color(0, 153, 76)); // Hijau
                    } else if (tipe.equalsIgnoreCase("Keluar")) {
                        cell.setForeground(new java.awt.Color(204, 0, 0)); // Merah
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
    
    public void loadStokMasuk() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Sesuaikan header jika perlu
        model.setColumnIdentifiers(new String[]{"Tipe", "Barang", "Jumlah", "Supplier", "Tanggal"});

        try {
            java.sql.Connection conn = db.koneksi.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT b.nama_barang, sm.jumlah, sm.supplier, sm.tanggal " +
                "FROM stok_masuk sm JOIN barang b ON sm.id_barang = b.id_barang " +
                "ORDER BY sm.tanggal DESC"
            );
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    "Masuk",
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah"),
                    rs.getString("supplier"),
                    rs.getTimestamp("tanggal").toString()                    
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load data stok keluar dari database
    public void loadStokKeluar() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0);

        model.setColumnIdentifiers(new String[]{"Tipe", "Barang", "Jumlah", "Departemen", "Tanggal"});

        try {
            java.sql.Connection conn = db.koneksi.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT b.nama_barang, sk.jumlah, sk.departemen, sk.tanggal " +
                "FROM stok_keluar sk JOIN barang b ON sk.id_barang = b.id_barang " +
                "ORDER BY sk.tanggal DESC"
            );
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    "Keluar",
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah"),
                    rs.getString("departemen"),
                    rs.getTimestamp("tanggal").toString()                    
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reset header ke default (Tipe, Barang, Jumlah, Supplier/Tujuan, Tanggal)
    public void resetHeader() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(new String[]{"Tipe", "Barang", "Jumlah", "Supplier/Tujuan", "Tanggal"});
        model.setRowCount(0);
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
    
    
    
    public void addRow(Object[] rowData) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.addRow(rowData);
    }
    
    // Method untuk mengisi banyak row sekaligus
    public void addRows(Object[][] rowsData) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        for (Object[] row : rowsData) {
            model.addRow(row);
        }
    }    
    
    // Method untuk mengisi data contoh (sample data)
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label1 = new java.awt.Label();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        label1.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        label1.setForeground(new java.awt.Color(30, 58, 138));
        label1.setText("Riwayat Transaksi");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tipe", "Barang", "Jumlah", "Supplier/Tujuan", "Tanggal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table.setFocusable(false);
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setHeaderValue("Tipe");
            table.getColumnModel().getColumn(1).setHeaderValue("Barang");
            table.getColumnModel().getColumn(2).setHeaderValue("Jumlah");
            table.getColumnModel().getColumn(3).setHeaderValue("Supplier/Tujuan");
            table.getColumnModel().getColumn(4).setHeaderValue("Tanggal");
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Label label1;
    public javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
