package visualization;

import algorithms.LongestZeroSum.AlgorithmStep;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;

/**
 * Visualizes the internal state of the HashMap during algorithm execution.
 * Displays Prefix Sum (Key), First Occurrence Index (Value), and highlights
 * entries when they are inserted or queried.
 */
public class HashMapVisualizer extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    private int highlightedRow = -1;
    private Color highlightColor = null;

    private static final Color CARD_BG = new Color(22, 27, 34);
    private static final Color TABLE_BG = new Color(13, 17, 23);
    private static final Color ROW_ALT_BG = new Color(18, 22, 29);
    private static final Color BORDER_COLOR = new Color(48, 54, 61);
    private static final Color TEXT_PRIMARY = new Color(240, 246, 252);
    private static final Color TEXT_MUTED = new Color(139, 148, 158);

    public HashMapVisualizer() {
        setLayout(new BorderLayout(0, 10));
        setBackground(CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Top Panel: Title and status
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("HashMap State (Prefix Sums)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);

        statusLabel = new JLabel("0 entries stored");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table Model: Columns: Prefix Sum (Key), First Index (Value), Status / Action
        String[] columnNames = {"Prefix Sum (Key)", "First Index (Value)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setBackground(TABLE_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(33, 38, 45));
        header.setForeground(new Color(201, 209, 217));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 30));

        // Custom Cell Renderer for rows & highlight
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 2 ? SwingConstants.LEFT : SwingConstants.CENTER);

                if (row == highlightedRow && highlightColor != null) {
                    c.setBackground(highlightColor);
                    c.setForeground(Color.WHITE);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    c.setBackground(row % 2 == 0 ? TABLE_BG : ROW_ALT_BG);
                    c.setForeground(column == 0 ? new Color(88, 166, 255) : TEXT_PRIMARY);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }

                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(TABLE_BG);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Note
        JLabel footerNote = new JLabel("<html><span style='color:#8b949e; font-size:10px;'>" +
                "Invariant: Only the <i>first occurrence</i> index is stored to maximize subarray span (i - firstIndex).</span></html>");
        add(footerNote, BorderLayout.SOUTH);
    }

    public void updateState(AlgorithmStep step) {
        tableModel.setRowCount(0);
        highlightedRow = -1;
        highlightColor = null;

        if (step == null) {
            statusLabel.setText("0 entries stored");
            return;
        }

        Map<Integer, Integer> map = step.getMapSnapshot();
        int runningSum = step.getRunningSum();
        int rowIndex = 0;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int key = entry.getKey();
            int val = entry.getValue();

            String status = "Preserved";
            boolean isTarget = (step.getCurrentIndex() >= 0 && key == runningSum);

            if (isTarget) {
                if (step.isSumInMap()) {
                    status = "🔍 Looked up (Found at index " + val + ")";
                    highlightedRow = rowIndex;
                    highlightColor = new Color(80, 45, 120); // Purple glow
                } else if (step.getActionType() == algorithms.LongestZeroSum.ActionType.NEW_SUM_STORED ||
                           (step.isSumZero() && val == step.getCurrentIndex())) {
                    status = "✨ Newly Inserted";
                    highlightedRow = rowIndex;
                    highlightColor = new Color(20, 70, 40); // Green glow
                }
            }

            tableModel.addRow(new Object[]{key, val, status});
            rowIndex++;
        }

        statusLabel.setText(map.size() + (map.size() == 1 ? " entry stored" : " entries stored"));

        if (highlightedRow >= 0 && highlightedRow < table.getRowCount()) {
            table.scrollRectToVisible(table.getCellRect(highlightedRow, 0, true));
        }

        table.repaint();
    }

    public void clear() {
        tableModel.setRowCount(0);
        highlightedRow = -1;
        highlightColor = null;
        statusLabel.setText("0 entries stored");
    }
}
