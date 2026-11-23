package visualization;

import algorithms.LongestZeroSum.AlgorithmStep;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom graphical component that renders the array elements as modern interactive blocks,
 * highlighting the current element, previous occurrences, candidate zero-sum ranges,
 * and the confirmed longest zero-sum subarray.
 */
public class ArrayVisualizer extends JPanel {

    private int[] array;
    private AlgorithmStep currentStep;

    // Dimensions
    private static final int BLOCK_WIDTH = 68;
    private static final int BLOCK_HEIGHT = 72;
    private static final int BLOCK_GAP = 12;
    private static final int PADDING_X = 28;
    private static final int PADDING_Y = 24;

    // Palette (Dark Theme)
    private static final Color BG_COLOR = new Color(22, 27, 34);
    private static final Color BLOCK_DEFAULT_BG = new Color(33, 38, 45);
    private static final Color BLOCK_DEFAULT_BORDER = new Color(48, 54, 61);
    private static final Color TEXT_PRIMARY = new Color(240, 246, 252);
    private static final Color TEXT_MUTED = new Color(139, 148, 158);

    // Highlighting colors
    private static final Color CURRENT_ACCENT = new Color(227, 179, 65);       // Amber / Gold
    private static final Color CURRENT_BG = new Color(60, 50, 20);
    private static final Color PREV_MATCH_ACCENT = new Color(163, 113, 247);   // Purple
    private static final Color PREV_MATCH_BG = new Color(45, 30, 65);
    private static final Color LONGEST_ACCENT = new Color(46, 160, 67);         // Emerald Green
    private static final Color LONGEST_BG = new Color(22, 55, 30);
    private static final Color CANDIDATE_ACCENT = new Color(88, 166, 255);      // Sky Blue
    private static final Color CANDIDATE_BG = new Color(20, 45, 75);

    public ArrayVisualizer() {
        this.array = new int[0];
        this.currentStep = null;
        setBackground(BG_COLOR);
        setOpaque(true);
    }

    public void updateState(int[] array, AlgorithmStep step) {
        this.array = (array != null) ? array : new int[0];
        this.currentStep = step;
        recalculatePreferredSize();
        repaint();
    }

    private void recalculatePreferredSize() {
        int count = (array != null) ? array.length : 0;
        int width = PADDING_X * 2 + count * BLOCK_WIDTH + Math.max(0, count - 1) * BLOCK_GAP;
        int height = PADDING_Y * 2 + BLOCK_HEIGHT + 75; // Extra space for badges, index, and subarray brackets
        setPreferredSize(new Dimension(Math.max(width, 700), Math.max(height, 185)));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        if (array == null || array.length == 0) {
            drawEmptyState(g2);
            g2.dispose();
            return;
        }

        int curIdx = (currentStep != null) ? currentStep.getCurrentIndex() : -1;
        int prevMatchIdx = (currentStep != null) ? currentStep.getPrevOccurrenceIndex() : -1;
        int bestStart = (currentStep != null) ? currentStep.getBestStartIndex() : -1;
        int bestEnd = (currentStep != null) ? currentStep.getBestEndIndex() : -1;
        int candStart = (currentStep != null) ? currentStep.getCandidateSubarrayStart() : -1;
        int candEnd = (currentStep != null) ? currentStep.getCandidateSubarrayEnd() : -1;

        int startY = PADDING_Y + 22;

        // Draw individual blocks
        for (int i = 0; i < array.length; i++) {
            int blockX = PADDING_X + i * (BLOCK_WIDTH + BLOCK_GAP);
            drawBlock(g2, i, array[i], blockX, startY, curIdx, prevMatchIdx, bestStart, bestEnd, candStart, candEnd);
        }

        // Draw Subarray Brackets / Connectors underneath
        drawSubarrayIndicators(g2, startY + BLOCK_HEIGHT + 14, bestStart, bestEnd, candStart, candEnd);

        g2.dispose();
    }

    private void drawBlock(Graphics2D g2, int index, int value, int x, int y,
                           int curIdx, int prevMatchIdx, int bestStart, int bestEnd, int candStart, int candEnd) {
        boolean isCurrent = (index == curIdx);
        boolean isPrevMatch = (index == prevMatchIdx);
        boolean isInLongest = (bestStart != -1 && bestEnd != -1 && index >= bestStart && index <= bestEnd);
        boolean isInCandidate = (candStart != -1 && candEnd != -1 && index >= candStart && index <= candEnd);

        // Determine background and border colors based on priority
        Color bg = BLOCK_DEFAULT_BG;
        Color border = BLOCK_DEFAULT_BORDER;
        int strokeWidth = 1;

        if (isCurrent) {
            bg = CURRENT_BG;
            border = CURRENT_ACCENT;
            strokeWidth = 3;
        } else if (isPrevMatch) {
            bg = PREV_MATCH_BG;
            border = PREV_MATCH_ACCENT;
            strokeWidth = 2;
        } else if (isInLongest) {
            bg = LONGEST_BG;
            border = LONGEST_ACCENT;
            strokeWidth = 2;
        } else if (isInCandidate) {
            bg = CANDIDATE_BG;
            border = CANDIDATE_ACCENT;
            strokeWidth = 2;
        }

        // Draw top badge or label (Index)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fmIdx = g2.getFontMetrics();
        String idxStr = "[" + index + "]";
        int idxWidth = fmIdx.stringWidth(idxStr);
        g2.setColor(isCurrent ? CURRENT_ACCENT : TEXT_MUTED);
        g2.drawString(idxStr, x + (BLOCK_WIDTH - idxWidth) / 2, y - 6);

        // Draw rounded block
        RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(x, y, BLOCK_WIDTH, BLOCK_HEIGHT, 14, 14);
        g2.setColor(bg);
        g2.fill(roundRect);

        g2.setColor(border);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.draw(roundRect);

        // Draw Element Value
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fmVal = g2.getFontMetrics();
        String valStr = String.valueOf(value);
        int valWidth = fmVal.stringWidth(valStr);
        int valHeight = fmVal.getAscent();

        g2.setColor(isCurrent ? Color.WHITE : TEXT_PRIMARY);
        g2.drawString(valStr, x + (BLOCK_WIDTH - valWidth) / 2, y + (BLOCK_HEIGHT + valHeight) / 2 - 4);

        // Draw status tag underneath block
        if (isCurrent) {
            drawPillTag(g2, x + 4, y + BLOCK_HEIGHT - 18, BLOCK_WIDTH - 8, 14, "CURRENT", CURRENT_ACCENT, Color.BLACK);
        } else if (isPrevMatch) {
            drawPillTag(g2, x + 4, y + BLOCK_HEIGHT - 18, BLOCK_WIDTH - 8, 14, "LOOKUP", PREV_MATCH_ACCENT, Color.WHITE);
        } else if (isInLongest) {
            drawPillTag(g2, x + 4, y + BLOCK_HEIGHT - 18, BLOCK_WIDTH - 8, 14, "ZERO-SUM", LONGEST_ACCENT, Color.WHITE);
        }
    }

    private void drawPillTag(Graphics2D g2, int x, int y, int w, int h, String text, Color bg, Color fg) {
        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, 6, 6);
        g2.setColor(fg);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text);
        g2.drawString(text, x + (w - tw) / 2, y + h - 3);
    }

    private void drawSubarrayIndicators(Graphics2D g2, int bracketY, int bestStart, int bestEnd, int candStart, int candEnd) {
        // Draw candidate zero-sum span if currently active
        if (candStart != -1 && candEnd != -1 && candStart <= candEnd) {
            int x1 = PADDING_X + candStart * (BLOCK_WIDTH + BLOCK_GAP) + 2;
            int x2 = PADDING_X + candEnd * (BLOCK_WIDTH + BLOCK_GAP) + BLOCK_WIDTH - 2;
            drawBracketLine(g2, x1, x2, bracketY, CANDIDATE_ACCENT,
                    "Current Subarray Candidate [" + candStart + ".." + candEnd + "] (Len: " + (candEnd - candStart + 1) + ")");
            bracketY += 24;
        }

        // Draw confirmed maximum zero-sum subarray span
        if (bestStart != -1 && bestEnd != -1 && bestStart <= bestEnd) {
            int x1 = PADDING_X + bestStart * (BLOCK_WIDTH + BLOCK_GAP);
            int x2 = PADDING_X + bestEnd * (BLOCK_WIDTH + BLOCK_GAP) + BLOCK_WIDTH;
            drawBracketLine(g2, x1, x2, bracketY, LONGEST_ACCENT,
                    "★ Longest Zero-Sum Subarray [" + bestStart + ".." + bestEnd + "] (Length: " + (bestEnd - bestStart + 1) + ")");
        }
    }

    private void drawBracketLine(Graphics2D g2, int x1, int x2, int y, Color color, String label) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Bracket horizontal line
        g2.drawLine(x1, y + 6, x2, y + 6);
        // Bracket end ticks
        g2.drawLine(x1, y, x1, y + 6);
        g2.drawLine(x2, y, x2, y + 6);

        // Center label
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        int centerX = (x1 + x2) / 2;

        // Label background pill
        int pillX = centerX - labelWidth / 2 - 8;
        int pillY = y + 9;
        int pillW = labelWidth + 16;
        int pillH = 18;

        g2.setColor(new Color(13, 17, 23, 230));
        g2.fillRoundRect(pillX, pillY, pillW, pillH, 8, 8);
        g2.setColor(color);
        g2.drawRoundRect(pillX, pillY, pillW, pillH, 8, 8);

        g2.drawString(label, centerX - labelWidth / 2, pillY + 13);
    }

    private void drawEmptyState(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2.setColor(TEXT_MUTED);
        String msg = "Enter an integer array above and click 'Analyze' to visualize.";
        FontMetrics fm = g2.getFontMetrics();
        int msgWidth = fm.stringWidth(msg);
        g2.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
    }
}
