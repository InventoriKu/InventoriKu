package View;

import db.koneksi;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Halaman Laporan Stok
 * Menampilkan log pergerakan stok dengan fitur:
 * - Search berdasarkan nama barang
 * - Filter berdasarkan tanggal (dari - sampai)
 * - Filter berdasarkan kategori
 * - Filter berdasarkan tipe (Masuk / Keluar / Semua)
 */
public class LaporanStok extends javax.swing.JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;

    // Filter components
    private JTextField txtCari;
    private JComboBox<String> cmbKategori;
    private JComboBox<String> cmbTipe;
    private JSpinner spinnerDari;
    private JSpinner spinnerSampai;
    private JButton btnFilter;
    private JButton btnReset;
    private JLabel lblTotal;

    // Data kategori: [id, nama]
    private java.util.List<Integer> kategoriIds = new java.util.ArrayList<>();

    public LaporanStok() {
        initUI();
        loadKategori();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // ── Header ──────────────────────────────────────────────
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        java.awt.Label lblJudul = new java.awt.Label("Laporan Stok");
        lblJudul.setFont(new Font("Dialog", Font.BOLD, 36));
        lblJudul.setForeground(new Color(30, 58, 138));
        pnlHeader.add(lblJudul, BorderLayout.WEST);

        lblTotal = new JLabel("0 data");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotal.setForeground(new Color(100, 110, 130));
        pnlHeader.add(lblTotal, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // ── Filter Panel ─────────────────────────────────────────
        JPanel pnlFilter = buildFilterPanel();
        add(pnlFilter, BorderLayout.CENTER);

        // ── Tabel ────────────────────────────────────────────────
        String[] columns = {"Tipe", "Nama Barang", "Kategori", "Jumlah", "Supplier/Tujuan", "Catatan", "Dicatat Oleh", "Tanggal"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable();

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(scrollPane, BorderLayout.SOUTH);

        // Atur agar tabel mengisi sisa ruang
        setLayout(new BorderLayout());
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(pnlHeader, BorderLayout.NORTH);
        pnlTop.add(pnlFilter, BorderLayout.CENTER);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildFilterPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        pnl.setBackground(new Color(248, 250, 252));
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(220, 226, 234)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // ── Cari nama barang ──
        pnl.add(makeLabel("Cari Barang:"));
        txtCari = new JTextField(14);
        styleTextField(txtCari);
        txtCari.setToolTipText("Ketik nama barang...");
        pnl.add(txtCari);

        // ── Tipe ──
        pnl.add(makeLabel("Tipe:"));
        cmbTipe = new JComboBox<>(new String[]{"Semua", "Masuk", "Keluar"});
        styleCombo(cmbTipe);
        pnl.add(cmbTipe);

        // ── Kategori ──
        pnl.add(makeLabel("Kategori:"));
        cmbKategori = new JComboBox<>();
        cmbKategori.addItem("Semua");
        styleCombo(cmbKategori);
        pnl.add(cmbKategori);

        // ── Tanggal dari ──
        pnl.add(makeLabel("Dari:"));
        SpinnerDateModel modelDari = new SpinnerDateModel();
        spinnerDari = new JSpinner(modelDari);
        JSpinner.DateEditor editorDari = new JSpinner.DateEditor(spinnerDari, "dd/MM/yyyy");
        spinnerDari.setEditor(editorDari);
        spinnerDari.setPreferredSize(new Dimension(110, 32));
        // Default: 30 hari lalu
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
        spinnerDari.setValue(cal.getTime());
        pnl.add(spinnerDari);

        // ── Tanggal sampai ──
        pnl.add(makeLabel("Sampai:"));
        SpinnerDateModel modelSampai = new SpinnerDateModel();
        spinnerSampai = new JSpinner(modelSampai);
        JSpinner.DateEditor editorSampai = new JSpinner.DateEditor(spinnerSampai, "dd/MM/yyyy");
        spinnerSampai.setEditor(editorSampai);
        spinnerSampai.setPreferredSize(new Dimension(110, 32));
        spinnerSampai.setValue(new Date()); // default: hari ini
        pnl.add(spinnerSampai);

        // ── Tombol Filter & Reset ──
        btnFilter = new JButton("Filter");
        styleButton(btnFilter, new Color(30, 58, 138), Color.WHITE);
        btnFilter.addActionListener(e -> loadData());
        pnl.add(btnFilter);

        btnReset = new JButton("Reset");
        styleButton(btnReset, new Color(220, 226, 234), new Color(50, 60, 80));
        btnReset.addActionListener(e -> resetFilter());
        pnl.add(btnReset);

        return pnl;
    }

    private void loadKategori() {
        try {
            Connection conn = koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id_kategori, nama_kategori FROM kategori ORDER BY nama_kategori");
            ResultSet rs = ps.executeQuery();
            kategoriIds.clear();
            cmbKategori.removeAllItems();
            cmbKategori.addItem("Semua");
            kategoriIds.add(-1); // index 0 = Semua
            while (rs.next()) {
                kategoriIds.add(rs.getInt("id_kategori"));
                cmbKategori.addItem(rs.getString("nama_kategori"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);

        String keyword = txtCari.getText().trim();
        String tipe = (String) cmbTipe.getSelectedItem();
        int idxKategori = cmbKategori.getSelectedIndex();
        int idKategori = (idxKategori >= 0 && idxKategori < kategoriIds.size()) ? kategoriIds.get(idxKategori) : -1;

        Date dari = (Date) spinnerDari.getValue();
        Date sampai = (Date) spinnerSampai.getValue();

        // Normalisasi sampai ke akhir hari
        java.util.Calendar calSampai = java.util.Calendar.getInstance();
        calSampai.setTime(sampai);
        calSampai.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calSampai.set(java.util.Calendar.MINUTE, 59);
        calSampai.set(java.util.Calendar.SECOND, 59);
        sampai = calSampai.getTime();

        try {
            Connection conn = koneksi.getConnection();

            StringBuilder sb = new StringBuilder();
            sb.append(
                "SELECT 'Masuk' AS tipe, b.nama_barang, k.nama_kategori, sm.jumlah, " +
                "sm.supplier AS target, sm.catatan, u.nama_lengkap AS dicatat_oleh, sm.tanggal " +
                "FROM stok_masuk sm " +
                "JOIN barang b ON sm.id_barang = b.id_barang " +
                "LEFT JOIN kategori k ON b.id_kategori = k.id_kategori " +
                "LEFT JOIN users u ON sm.id_user = u.id_user " +
                "WHERE sm.tanggal BETWEEN ? AND ? "
            );
            if (!keyword.isEmpty()) sb.append("AND b.nama_barang LIKE ? ");
            if (idKategori > 0)     sb.append("AND b.id_kategori = ? ");

            sb.append(
                "UNION ALL " +
                "SELECT 'Keluar' AS tipe, b.nama_barang, k.nama_kategori, sk.jumlah, " +
                "sk.departemen AS target, sk.catatan, u.nama_lengkap AS dicatat_oleh, sk.tanggal " +
                "FROM stok_keluar sk " +
                "JOIN barang b ON sk.id_barang = b.id_barang " +
                "LEFT JOIN kategori k ON b.id_kategori = k.id_kategori " +
                "LEFT JOIN users u ON sk.id_user = u.id_user " +
                "WHERE sk.tanggal BETWEEN ? AND ? "
            );
            if (!keyword.isEmpty()) sb.append("AND b.nama_barang LIKE ? ");
            if (idKategori > 0)     sb.append("AND b.id_kategori = ? ");

            sb.append("ORDER BY tanggal DESC");

            // Jika filter tipe, bungkus sebagai subquery
            String sql;
            if (!"Semua".equals(tipe)) {
                sql = "SELECT * FROM (" + sb + ") AS log WHERE tipe = ?";
            } else {
                sql = sb.toString();
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            int idx = 1;

            // Bind parameter untuk UNION bagian pertama (stok_masuk)
            ps.setTimestamp(idx++, new Timestamp(dari.getTime()));
            ps.setTimestamp(idx++, new Timestamp(sampai.getTime()));
            if (!keyword.isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            if (idKategori > 0)     ps.setInt(idx++, idKategori);

            // Bind parameter untuk UNION bagian kedua (stok_keluar)
            ps.setTimestamp(idx++, new Timestamp(dari.getTime()));
            ps.setTimestamp(idx++, new Timestamp(sampai.getTime()));
            if (!keyword.isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            if (idKategori > 0)     ps.setInt(idx++, idKategori);

            // Bind tipe jika bukan Semua
            if (!"Semua".equals(tipe)) ps.setString(idx++, tipe);

            ResultSet rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            int count = 0;
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("tanggal");
                String tanggalStr = (ts != null) ? sdf.format(new Date(ts.getTime())) : "-";
                tableModel.addRow(new Object[]{
                    rs.getString("tipe"),
                    rs.getString("nama_barang"),
                    rs.getString("nama_kategori") != null ? rs.getString("nama_kategori") : "-",
                    rs.getInt("jumlah"),
                    rs.getString("target") != null ? rs.getString("target") : "-",
                    rs.getString("catatan") != null ? rs.getString("catatan") : "-",
                    rs.getString("dicatat_oleh") != null ? rs.getString("dicatat_oleh") : "-",
                    tanggalStr
                });
                count++;
            }
            lblTotal.setText(count + " data ditemukan");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilter() {
        txtCari.setText("");
        cmbTipe.setSelectedIndex(0);
        cmbKategori.setSelectedIndex(0);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
        spinnerDari.setValue(cal.getTime());
        spinnerSampai.setValue(new Date());
        loadData();
    }

    // ── Styling helpers ──────────────────────────────────────────

    private void styleTable() {
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSel, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, isSel, hasFocus, row, col);
                lbl.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
                lbl.setBackground(new Color(240, 244, 248));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setForeground(new Color(50, 60, 80));
                return lbl;
            }
        });
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSel, boolean hasFocus, int row, int col) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(t, value, isSel, hasFocus, row, col);
                cell.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
                if (!isSel) {
                    cell.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 253, 254));
                    cell.setForeground(Color.BLACK);
                    cell.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                    // Warna kolom Tipe
                    if (col == 0 && value != null) {
                        String v = value.toString();
                        if (v.equalsIgnoreCase("Masuk")) {
                            cell.setForeground(new Color(0, 140, 70));
                            cell.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        } else if (v.equalsIgnoreCase("Keluar")) {
                            cell.setForeground(new Color(200, 0, 0));
                            cell.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        }
                    }
                }
                return cell;
            }
        });
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(50, 60, 80));
        return lbl;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private void styleCombo(JComboBox<?> cmb) {
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmb.setPreferredSize(new Dimension(130, 32));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 32));
    }
}
