/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Components;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author HP
 */
public class DataTable extends javax.swing.JPanel {
    
    public interface TableActionListener {
        void onEdit(int row);
        void onDelete(int row);
    }

    private String searchPlaceholder = "Cari...";
    private List<Integer> statusColumns = new ArrayList<>();
    private List<Integer> actionColumns = new ArrayList<>();
    private TableActionListener tableActionListener;

    public DataTable() {
        initComponents();
        
        table.setEnabled(true);
        table.setFocusable(true);
        
        setupTableDesign();
    }
    
    private void setupTableDesign() {
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel headerLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                headerLabel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15)); 
                headerLabel.setBackground(new Color(240, 244, 248));
                headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return headerLabel;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 

                if (!isSelected) {
                    if (row % 2 == 0) cell.setBackground(Color.WHITE);
                    else cell.setBackground(new Color(252, 253, 254)); 
                    cell.setForeground(Color.BLACK);
                    cell.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }

                if (statusColumns.contains(column) && value != null) {
                    String textValue = value.toString().toUpperCase();
                    if (textValue.contains("THRESHOLD") || textValue.contains("KRITIS") || textValue.contains("RENDAH")) {
                        cell.setForeground(new Color(204, 0, 0)); 
                        cell.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (textValue.contains("AMAN") || textValue.contains("CUKUP")) {
                        cell.setForeground(new Color(0, 153, 76)); 
                        cell.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    }
                }
                return cell;
            }
        });

        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0)); 
        table.setBackground(Color.WHITE); 
        table.setSelectionBackground(new Color(242, 244, 248));
        table.setSelectionForeground(Color.BLACK);        
    }
    
    public void setColumns(String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return actionColumns.contains(column);
            }
        };
        table.setModel(model);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
        header.setBackground(new Color(240, 242, 245)); 
        header.setForeground(new Color(100, 110, 120));
        header.setReorderingAllowed(false); 

        if (table.getParent() != null && table.getParent().getParent() instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) table.getParent().getParent();
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
        }
    }
    
    public void addStatusColumn(int columnIndex) {
        if (!statusColumns.contains(columnIndex)) {
            statusColumns.add(columnIndex);
        }
    }
    
    public void addActionColumn(int columnIndex) {
        if (!actionColumns.contains(columnIndex)) {
            actionColumns.add(columnIndex);
        }
        if (columnIndex < table.getColumnCount()) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(new ActionButtonRenderer());
            table.getColumnModel().getColumn(columnIndex).setCellEditor(new ActionButtonEditor(new JCheckBox()));
        }
    }

    public void setTableActionListener(TableActionListener listener) {
        this.tableActionListener = listener;
    }

    public void setSearchPlaceholder(String placeholder) {
        this.searchPlaceholder = placeholder;
        if (textFieldSearch.hasFocus()) {
            textFieldSearch.setText("");
            textFieldSearch.setForeground(java.awt.Color.BLACK);
        } else {
            textFieldSearch.setText(placeholder);
            textFieldSearch.setForeground(new java.awt.Color(197, 197, 211));
        }
    }
    
    public void setButtonText(String text) {
        btnTambah.setText(text);
    }
    
    public void setComboBoxModel(String[] items) {
        jComboBox1.setModel(new DefaultComboBoxModel<>(items));
    }

    public void setComboBoxVisible(boolean visible) {
        jComboBox1.setVisible(visible);
    }

    public DefaultTableModel getModel() { return (DefaultTableModel) table.getModel(); }
    public JButton getBtnTambah() { return btnTambah; }
    public JTextField getSearchField() { return textFieldSearch; }
    public javax.swing.JComboBox<String> getComboBox() { return jComboBox1; }
    public JTable getTable() { return table; }
    
    class ActionButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
            panel.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(252, 253, 254)));

            JButton btnEdit = new JButton("Edit");
            JButton btnHapus = new JButton("Hapus");

            styleMinibutton(btnEdit, new Color(0, 102, 204));
            styleMinibutton(btnHapus, new Color(204, 0, 0));

            panel.add(btnEdit);
            panel.add(btnHapus);

            return panel;
        }

        private void styleMinibutton(JButton btn, Color color) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(color);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false); 
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setMargin(new Insets(0, 0, 0, 0)); 
        }
    }
    
    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnEdit;
        private JButton btnHapus;
        private int currentRow;

        public ActionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
            btnEdit = new JButton("Edit");
            btnHapus = new JButton("Hapus");

            styleMinibutton(btnEdit, new Color(0, 102, 204));
            styleMinibutton(btnHapus, new Color(204, 0, 0));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                if (tableActionListener != null) tableActionListener.onEdit(currentRow);
            });
            btnHapus.addActionListener(e -> {
                fireEditingStopped();
                if (tableActionListener != null) tableActionListener.onDelete(currentRow);
            });

            panel.add(btnEdit);
            panel.add(btnHapus);
        }

        private void styleMinibutton(JButton btn, Color color) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(color);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false); 
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMargin(new Insets(0, 0, 0, 0)); 
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
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
        String currentText = textFieldSearch.getText().trim();
        if (currentText.equalsIgnoreCase(searchPlaceholder.trim())) {
           textFieldSearch.setText("");
           textFieldSearch.setForeground(java.awt.Color.BLACK); 
        }
    }//GEN-LAST:event_textFieldSearchFocusGained

    private void textFieldSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldSearchFocusLost
        // TODO add your handling code here:
        if (textFieldSearch.getText().trim().isEmpty()) {
            textFieldSearch.setText(searchPlaceholder);
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
