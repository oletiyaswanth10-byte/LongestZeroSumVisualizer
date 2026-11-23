package gui;

import algorithms.LongestZeroSum.AlgorithmStep;
import visualization.ArrayVisualizer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Container panel for Array Visualization, complete with header,
 * interactive visual legend, and smooth horizontal scrolling.
 */
public class ArrayPanel extends JPanel {

    private final ArrayVisualizer visualizer;
    private final JScrollPane scrollPane;
    private final JLabel headerLabel;

    public ArrayPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(22, 27, 34));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        // Top Header and Legend
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        headerLabel = new JLabel("Array Traversal & Subarray Highlighting");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setForeground(new Color(240, 246, 252));
        topPanel.add(headerLabel, BorderLayout.WEST);

        // Visual Legend
        JPanel legendPanel = createLegendPanel();
        topPanel.add(legendPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Visualizer inside smooth horizontal JScrollPane
        visualizer = new ArrayVisualizer();
        scrollPane = new JScrollPane(visualizer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(24);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createLegendPanel() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        legend.setOpaque(false);

        legend.add(createLegendItem("Current Element", new Color(227, 179, 65)));
        legend.add(createLegendItem("Previous Match", new Color(163, 113, 247)));
        legend.add(createLegendItem("Candidate Subarray", new Color(88, 166, 255)));
        legend.add(createLegendItem("Longest Zero-Sum Subarray", new Color(46, 160, 67)));

        return legend;
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);

        JPanel colorBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 2, 12, 12, 4, 4);
                g2.dispose();
            }
        };
        colorBox.setPreferredSize(new Dimension(14, 16));
        colorBox.setOpaque(false);

        JLabel text = new JLabel(label);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        text.setForeground(new Color(139, 148, 158));

        item.add(colorBox);
        item.add(text);
        return item;
    }

    public void updateState(int[] array, AlgorithmStep step) {
        visualizer.updateState(array, step);
        if (step != null && step.getCurrentIndex() >= 0 && array != null && array.length > 0) {
            headerLabel.setText(String.format("Array Traversal — Processing Element %d of %d (Index %d)",
                    step.getCurrentIndex() + 1, array.length, step.getCurrentIndex()));
        } else if (step != null && step.getActionType() == algorithms.LongestZeroSum.ActionType.COMPLETED) {
            headerLabel.setText("Array Traversal — Analysis Complete");
        } else {
            headerLabel.setText("Array Traversal & Subarray Highlighting");
        }
    }

    public ArrayVisualizer getVisualizer() {
        return visualizer;
    }
}
