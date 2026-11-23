package gui;

import algorithms.LongestZeroSum;
import algorithms.LongestZeroSum.AlgorithmResult;
import algorithms.LongestZeroSum.AlgorithmStep;
import utils.InputValidator;
import visualization.HashMapVisualizer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

/**
 * Main application dashboard providing a comprehensive, dark-themed,
 * interactive environment for the Longest Zero-Sum Subarray visualizer.
 */
public class Dashboard extends JFrame {

    // Core Data
    private int[] currentArray;
    private AlgorithmResult algorithmResult;
    private int currentStepIndex = 0;
    private Timer playbackTimer;
    private boolean isPlaying = false;

    // GUI Components
    private JTextField inputField;
    private JComboBox<String> presetComboBox;
    private JButton analyzeBtn;
    private JButton resetBtn;
    private JButton randomBtn;

    // Stepper Controls
    private JButton firstStepBtn;
    private JButton prevStepBtn;
    private JButton playPauseBtn;
    private JButton nextStepBtn;
    private JButton lastStepBtn;
    private JSlider speedSlider;
    private JLabel speedLabel;
    private JLabel stepProgressLabel;
    private JProgressBar progressBar;

    // Visualizers
    private ArrayPanel arrayPanel;
    private HashMapVisualizer hashMapVisualizer;

    // State / Metric Badges
    private JPanel prevSumBadge;
    private JPanel currentElementBadge;
    private JPanel runningSumBadge;
    private JPanel mapLookupBadge;
    private JPanel currentMaxBadge;
    private JTextArea narrativeTextArea;
    private JLabel validationAlertLabel;

    // Results Dashboard Components
    private JLabel resultSubarrayLabel;
    private JLabel resultRangeLabel;
    private JLabel resultLengthLabel;
    private JLabel resultIterationsLabel;
    private JLabel resultStatusBadge;

    // Theme Colors
    private static final Color BG_DARK = new Color(13, 17, 23);
    private static final Color CARD_BG = new Color(22, 27, 34);
    private static final Color INSET_BG = new Color(33, 38, 45);
    private static final Color BORDER_COLOR = new Color(48, 54, 61);
    private static final Color TEXT_PRIMARY = new Color(240, 246, 252);
    private static final Color TEXT_SECONDARY = new Color(139, 148, 158);
    private static final Color ACCENT_BLUE = new Color(31, 111, 235);
    private static final Color ACCENT_GREEN = new Color(46, 160, 67);
    private static final Color ACCENT_AMBER = new Color(227, 179, 65);
    private static final Color ACCENT_RED = new Color(248, 81, 73);

    public Dashboard() {
        setTitle("HashMap-Based Longest Zero-Sum Subarray Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 920);
        setMinimumSize(new Dimension(1080, 780));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        initializePlaybackTimer();
        initUI();

        // Load Test 1 by default
        loadPreset(0);
    }

    private void initializePlaybackTimer() {
        int initialDelay = 800;
        playbackTimer = new Timer(initialDelay, e -> {
            if (algorithmResult != null && currentStepIndex < algorithmResult.getSteps().size() - 1) {
                goToStep(currentStepIndex + 1);
            } else {
                pausePlayback();
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // Outer wrapper with scroll pane so dashboard never clips on smaller displays
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBackground(BG_DARK);
        contentContainer.setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. Header Section
        contentContainer.add(createHeaderPanel());
        contentContainer.add(Box.createVerticalStrut(14));

        // 2. Input & Preset Toolbar
        contentContainer.add(createInputToolbar());
        contentContainer.add(Box.createVerticalStrut(14));

        // 3. Stepper & Playback Controls Bar
        contentContainer.add(createStepperControlBar());
        contentContainer.add(Box.createVerticalStrut(14));

        // 4. Array Visualization Panel
        arrayPanel = new ArrayPanel();
        arrayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        contentContainer.add(arrayPanel);
        contentContainer.add(Box.createVerticalStrut(14));

        // 5. Middle Live Calculation & Explanation Card
        contentContainer.add(createCalculationMetricsPanel());
        contentContainer.add(Box.createVerticalStrut(14));

        // 6. Bottom Split: HashMap State on Left, Final Results & Complexity on Right
        contentContainer.add(createBottomSplitPanel());

        JScrollPane rootScrollPane = new JScrollPane(contentContainer);
        rootScrollPane.setBorder(null);
        rootScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        rootScrollPane.getViewport().setBackground(BG_DARK);
        add(rootScrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("HashMap-Based Longest Zero-Sum Subarray Visualizer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Educational DSA Visualizer demonstrating the O(n) Prefix Sum and First-Occurrence HashMap technique");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);

        // Algorithm Tag Badge
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        badgePanel.setOpaque(false);
        badgePanel.add(createTagBadge("Time: O(n)", ACCENT_BLUE));
        badgePanel.add(createTagBadge("Space: O(n)", ACCENT_GREEN));
        badgePanel.add(createTagBadge("Data Structure: HashMap", new Color(163, 113, 247)));

        panel.add(titleBox, BorderLayout.WEST);
        panel.add(badgePanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createInputToolbar() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(12, 8));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Top Row: Presets and Direct Input
        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);

        // Preset selector
        JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        presetPanel.setOpaque(false);

        JLabel presetLbl = new JLabel("Presets:");
        presetLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        presetLbl.setForeground(TEXT_PRIMARY);

        String[] presets = {
                "Test 1: [1, 2, -2, 4, -4] (Length 4)",
                "Test 2: [1, 2, 3, 4] (No Zero Sum)",
                "Test 3: [0, 0, 0] (Length 3)",
                "Test 4: [15, -2, 2, -8, 1, 7, 10, 23] (Length 5)",
                "Test 5: [2, 8, -3, -5, 2, -4, 6, 1, 2, 1, -3, 4] (Length 11)",
                "Test 6: [6, 3, -1, -3, 4, -2, 2, 4, 6, -12, -7] (Length 8)",
                "Test 7: [-1, 1, -1, 1] (Length 4)"
        };
        presetComboBox = new JComboBox<>(presets);
        presetComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        presetComboBox.setBackground(INSET_BG);
        presetComboBox.setForeground(TEXT_PRIMARY);
        presetComboBox.setPreferredSize(new Dimension(310, 32));
        presetComboBox.addActionListener(e -> {
            int sel = presetComboBox.getSelectedIndex();
            if (sel >= 0) {
                loadPreset(sel);
            }
        });

        presetPanel.add(presetLbl);
        presetPanel.add(presetComboBox);

        // Input Field Panel
        JPanel inputWrapper = new JPanel(new BorderLayout(8, 0));
        inputWrapper.setOpaque(false);

        JLabel inputLbl = new JLabel("Array Input:");
        inputLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        inputLbl.setForeground(TEXT_PRIMARY);

        inputField = new JTextField("1, 2, -2, 4, -4");
        inputField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputField.setBackground(INSET_BG);
        inputField.setForeground(TEXT_PRIMARY);
        inputField.setCaretColor(TEXT_PRIMARY);
        inputField.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        inputField.addActionListener(e -> triggerAnalyze());

        inputWrapper.add(inputLbl, BorderLayout.WEST);
        inputWrapper.add(inputField, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonGroup.setOpaque(false);

        analyzeBtn = createStyledButton("▶ Analyze", ACCENT_BLUE, Color.WHITE);
        analyzeBtn.addActionListener(e -> triggerAnalyze());

        resetBtn = createStyledButton("↺ Reset", INSET_BG, TEXT_PRIMARY);
        resetBtn.addActionListener(e -> resetVisualizer());

        randomBtn = createStyledButton("🎲 Random", INSET_BG, TEXT_PRIMARY);
        randomBtn.addActionListener(e -> generateRandomArray());

        buttonGroup.add(analyzeBtn);
        buttonGroup.add(resetBtn);
        buttonGroup.add(randomBtn);

        topRow.add(presetPanel, BorderLayout.WEST);
        topRow.add(inputWrapper, BorderLayout.CENTER);
        topRow.add(buttonGroup, BorderLayout.EAST);

        card.add(topRow, BorderLayout.CENTER);

        // Validation / Warning Alert Label
        validationAlertLabel = new JLabel(" ");
        validationAlertLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        validationAlertLabel.setForeground(ACCENT_RED);
        card.add(validationAlertLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createStepperControlBar() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(14, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        // Stepper Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.setOpaque(false);

        firstStepBtn = createStyledButton("⏮ First", INSET_BG, TEXT_PRIMARY);
        prevStepBtn = createStyledButton("◀ Prev", INSET_BG, TEXT_PRIMARY);
        playPauseBtn = createStyledButton("▶ Play", ACCENT_GREEN, Color.WHITE);
        nextStepBtn = createStyledButton("Next ▶", INSET_BG, TEXT_PRIMARY);
        lastStepBtn = createStyledButton("Last ⏭", INSET_BG, TEXT_PRIMARY);

        firstStepBtn.addActionListener(e -> goToStep(0));
        prevStepBtn.addActionListener(e -> {
            pausePlayback();
            goToStep(Math.max(0, currentStepIndex - 1));
        });
        playPauseBtn.addActionListener(e -> togglePlayPause());
        nextStepBtn.addActionListener(e -> {
            pausePlayback();
            if (algorithmResult != null) {
                goToStep(Math.min(algorithmResult.getSteps().size() - 1, currentStepIndex + 1));
            }
        });
        lastStepBtn.addActionListener(e -> {
            pausePlayback();
            if (algorithmResult != null) {
                goToStep(algorithmResult.getSteps().size() - 1);
            }
        });

        btnPanel.add(firstStepBtn);
        btnPanel.add(prevStepBtn);
        btnPanel.add(playPauseBtn);
        btnPanel.add(nextStepBtn);
        btnPanel.add(lastStepBtn);

        // Middle: Step Progress & Bar
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setOpaque(false);

        stepProgressLabel = new JLabel("Step 0 of 0 (Initialization)");
        stepProgressLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stepProgressLabel.setForeground(TEXT_PRIMARY);
        stepProgressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(220, 8));
        progressBar.setMaximumSize(new Dimension(280, 8));
        progressBar.setForeground(ACCENT_BLUE);
        progressBar.setBackground(INSET_BG);
        progressBar.setBorder(null);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressPanel.add(stepProgressLabel);
        progressPanel.add(Box.createVerticalStrut(4));
        progressPanel.add(progressBar);

        // Right: Playback Speed Slider
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        speedPanel.setOpaque(false);

        speedLabel = new JLabel("Speed: 800 ms");
        speedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        speedLabel.setForeground(TEXT_SECONDARY);

        speedSlider = new JSlider(JSlider.HORIZONTAL, 150, 1500, 800);
        speedSlider.setPreferredSize(new Dimension(130, 24));
        speedSlider.setOpaque(false);
        speedSlider.addChangeListener(e -> {
            int delay = speedSlider.getValue();
            speedLabel.setText("Speed: " + delay + " ms");
            playbackTimer.setDelay(delay);
        });

        speedPanel.add(speedLabel);
        speedPanel.add(speedSlider);

        card.add(btnPanel, BorderLayout.WEST);
        card.add(progressPanel, BorderLayout.CENTER);
        card.add(speedPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createCalculationMetricsPanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Badges Bar: Calculation & Lookup breakdown
        JPanel badgeGrid = new JPanel(new GridLayout(1, 5, 10, 0));
        badgeGrid.setOpaque(false);

        currentElementBadge = createMetricBadge("Current Element", "arr[i] = -", TEXT_PRIMARY);
        prevSumBadge = createMetricBadge("Previous Sum", "0", TEXT_SECONDARY);
        runningSumBadge = createMetricBadge("Running Sum", "0", ACCENT_AMBER);
        mapLookupBadge = createMetricBadge("HashMap Lookup", "Awaiting execution", new Color(163, 113, 247));
        currentMaxBadge = createMetricBadge("Max Subarray Len", "0", ACCENT_GREEN);

        badgeGrid.add(currentElementBadge);
        badgeGrid.add(prevSumBadge);
        badgeGrid.add(runningSumBadge);
        badgeGrid.add(mapLookupBadge);
        badgeGrid.add(currentMaxBadge);

        card.add(badgeGrid, BorderLayout.NORTH);

        // Detailed Explanation Narrative Box
        JPanel narrativePanel = new JPanel(new BorderLayout(0, 4));
        narrativePanel.setOpaque(false);

        JLabel narrativeTitle = new JLabel("Step Execution Breakdown & Algorithmic Reasoning:");
        narrativeTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        narrativeTitle.setForeground(TEXT_SECONDARY);
        narrativePanel.add(narrativeTitle, BorderLayout.NORTH);

        narrativeTextArea = new JTextArea(3, 80);
        narrativeTextArea.setEditable(false);
        narrativeTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        narrativeTextArea.setBackground(INSET_BG);
        narrativeTextArea.setForeground(TEXT_PRIMARY);
        narrativeTextArea.setLineWrap(true);
        narrativeTextArea.setWrapStyleWord(true);
        narrativeTextArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollNarrative = new JScrollPane(narrativeTextArea);
        scrollNarrative.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollNarrative.getViewport().setBackground(INSET_BG);
        narrativePanel.add(scrollNarrative, BorderLayout.CENTER);

        card.add(narrativePanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBottomSplitPanel() {
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        splitPanel.setOpaque(false);
        splitPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 260));

        // Left Side: Live HashMap Table
        hashMapVisualizer = new HashMapVisualizer();
        splitPanel.add(hashMapVisualizer);

        // Right Side: Final Results & Complexity Info
        JPanel resultsCard = createResultsDashboardCard();
        splitPanel.add(resultsCard);

        return splitPanel;
    }

    private JPanel createResultsDashboardCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Algorithm Results & Complexity Analysis");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT_PRIMARY);

        resultStatusBadge = new JLabel("Ready");
        resultStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        resultStatusBadge.setForeground(ACCENT_BLUE);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(resultStatusBadge, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);

        // Center: Results Grid
        JPanel centerGrid = new JPanel(new GridLayout(4, 2, 8, 8));
        centerGrid.setOpaque(false);
        centerGrid.setBorder(new EmptyBorder(4, 0, 4, 0));

        centerGrid.add(createResultItemLabel("Longest Subarray:"));
        resultSubarrayLabel = createResultValueLabel("None");
        centerGrid.add(resultSubarrayLabel);

        centerGrid.add(createResultItemLabel("Subarray Range:"));
        resultRangeLabel = createResultValueLabel("N/A");
        centerGrid.add(resultRangeLabel);

        centerGrid.add(createResultItemLabel("Maximum Length:"));
        resultLengthLabel = createResultValueLabel("0");
        centerGrid.add(resultLengthLabel);

        centerGrid.add(createResultItemLabel("Total Iterations:"));
        resultIterationsLabel = createResultValueLabel("0");
        centerGrid.add(resultIterationsLabel);

        card.add(centerGrid, BorderLayout.CENTER);

        // Bottom: Time & Space Complexity Cards
        JPanel complexityPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        complexityPanel.setOpaque(false);

        complexityPanel.add(createComplexityCard("Time Complexity: O(n)",
                "Single pass through array. Each HashMap insert and lookup runs in O(1) average time. Total: O(n)."));
        complexityPanel.add(createComplexityCard("Space Complexity: O(n)",
                "HashMap stores at most n distinct prefix sums and their first occurrence indices. Total: O(n)."));

        card.add(complexityPanel, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createResultItemLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JLabel createResultValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JPanel createComplexityCard(String title, String desc) {
        JPanel box = new JPanel(new BorderLayout(0, 3));
        box.setBackground(INSET_BG);
        box.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(ACCENT_BLUE);

        JLabel descLbl = new JLabel("<html><span style='color:#8b949e; font-size:10px;'>" + desc + "</span></html>");

        box.add(titleLbl, BorderLayout.NORTH);
        box.add(descLbl, BorderLayout.CENTER);
        return box;
    }

    private JPanel createMetricBadge(String title, String initialValue, Color valueColor) {
        JPanel badge = new JPanel(new BorderLayout(0, 4));
        badge.setBackground(INSET_BG);
        badge.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(TEXT_SECONDARY);

        JLabel valLbl = new JLabel(initialValue);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valLbl.setForeground(valueColor);

        badge.add(titleLbl, BorderLayout.NORTH);
        badge.add(valLbl, BorderLayout.CENTER);

        return badge;
    }

    private void updateMetricBadge(JPanel badgePanel, String newValue, Color valueColor) {
        if (badgePanel.getComponentCount() > 1 && badgePanel.getComponent(1) instanceof JLabel) {
            JLabel valLbl = (JLabel) badgePanel.getComponent(1);
            valLbl.setText(newValue);
            if (valueColor != null) {
                valLbl.setForeground(valueColor);
            }
        }
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        return panel;
    }

    private JPanel createTagBadge(String text, Color accent) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        badge.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
        badge.setBorder(new LineBorder(accent, 1, true));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(accent);

        badge.add(label);
        return badge;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==========================================
    // Core Actions & State Management
    // ==========================================

    private void loadPreset(int presetIndex) {
        pausePlayback();
        String presetText;
        switch (presetIndex) {
            case 0:
                presetText = "1, 2, -2, 4, -4";
                break;
            case 1:
                presetText = "1, 2, 3, 4";
                break;
            case 2:
                presetText = "0, 0, 0";
                break;
            case 3:
                presetText = "15, -2, 2, -8, 1, 7, 10, 23";
                break;
            case 4:
                presetText = "2, 8, -3, -5, 2, -4, 6, 1, 2, 1, -3, 4";
                break;
            case 5:
                presetText = "6, 3, -1, -3, 4, -2, 2, 4, 6, -12, -7";
                break;
            case 6:
                presetText = "-1, 1, -1, 1";
                break;
            default:
                presetText = "1, 2, -2, 4, -4";
                break;
        }
        inputField.setText(presetText);
        triggerAnalyze();
    }

    private void generateRandomArray() {
        pausePlayback();
        Random rand = new Random();
        int size = 7 + rand.nextInt(5); // 7 to 11 elements
        int[] arr = new int[size];

        // Ensure at least one zero-sum segment exists
        int zeroSumLen = 3 + rand.nextInt(3); // length 3 to 5
        int zeroSumStart = rand.nextInt(size - zeroSumLen + 1);

        int partialSum = 0;
        for (int i = 0; i < size; i++) {
            if (i >= zeroSumStart && i < zeroSumStart + zeroSumLen - 1) {
                int val = rand.nextInt(15) - 7;
                arr[i] = (val == 0) ? 2 : val;
                partialSum += arr[i];
            } else if (i == zeroSumStart + zeroSumLen - 1) {
                arr[i] = -partialSum; // make the slice sum to 0
            } else {
                int val = rand.nextInt(20) - 10;
                arr[i] = (val == 0) ? 3 : val;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        inputField.setText(sb.toString());
        triggerAnalyze();
    }

    private void triggerAnalyze() {
        pausePlayback();
        validationAlertLabel.setText(" ");

        InputValidator.ValidationResult result = InputValidator.validateAndParse(inputField.getText());
        if (!result.isValid()) {
            validationAlertLabel.setText("⚠ " + result.getErrorMessage());
            return;
        }

        currentArray = result.getArray();
        algorithmResult = LongestZeroSum.solve(currentArray);
        currentStepIndex = 0;

        updateResultDashboard(false);
        goToStep(0);
    }

    private void resetVisualizer() {
        pausePlayback();
        validationAlertLabel.setText(" ");
        currentStepIndex = 0;
        if (algorithmResult != null) {
            goToStep(0);
        } else {
            arrayPanel.updateState(new int[0], null);
            hashMapVisualizer.clear();
            narrativeTextArea.setText("Ready to analyze. Enter an array and click 'Analyze'.");
        }
    }

    private void togglePlayPause() {
        if (algorithmResult == null || algorithmResult.getSteps().isEmpty()) {
            return;
        }

        if (isPlaying) {
            pausePlayback();
        } else {
            // If already at end, restart from beginning
            if (currentStepIndex >= algorithmResult.getSteps().size() - 1) {
                currentStepIndex = 0;
                goToStep(0);
            }
            startPlayback();
        }
    }

    private void startPlayback() {
        isPlaying = true;
        playPauseBtn.setText("⏸ Pause");
        playPauseBtn.setBackground(ACCENT_AMBER);
        playbackTimer.start();
    }

    private void pausePlayback() {
        isPlaying = false;
        playPauseBtn.setText("▶ Play");
        playPauseBtn.setBackground(ACCENT_GREEN);
        if (playbackTimer != null) {
            playbackTimer.stop();
        }
    }

    private void goToStep(int stepIndex) {
        if (algorithmResult == null || algorithmResult.getSteps().isEmpty()) {
            return;
        }

        currentStepIndex = Math.max(0, Math.min(stepIndex, algorithmResult.getSteps().size() - 1));
        AlgorithmStep step = algorithmResult.getSteps().get(currentStepIndex);

        // Update Array View
        arrayPanel.updateState(currentArray, step);

        // Update HashMap View
        hashMapVisualizer.updateState(step);

        // Update Stepper Progress Bar & Labels
        int totalSteps = algorithmResult.getSteps().size();
        stepProgressLabel.setText(String.format("Step %d of %d (%s)",
                currentStepIndex + 1, totalSteps, step.getActionType().name()));
        int progressPercent = (int) (((currentStepIndex + 1) / (double) totalSteps) * 100);
        progressBar.setValue(progressPercent);

        // Update Calculation Metrics
        if (step.getCurrentIndex() >= 0 && step.getCurrentIndex() < currentArray.length) {
            updateMetricBadge(currentElementBadge, "arr[" + step.getCurrentIndex() + "] = " + step.getCurrentElement(), TEXT_PRIMARY);
            updateMetricBadge(prevSumBadge, String.valueOf(step.getPreviousSum()), TEXT_SECONDARY);
            updateMetricBadge(runningSumBadge, String.valueOf(step.getRunningSum()), ACCENT_AMBER);

            if (step.isSumZero()) {
                updateMetricBadge(mapLookupBadge, "Sum is 0! (Span: 0.." + step.getCurrentIndex() + ")", ACCENT_GREEN);
            } else if (step.isSumInMap()) {
                updateMetricBadge(mapLookupBadge, "MATCHED at Index " + step.getPrevOccurrenceIndex(), new Color(163, 113, 247));
            } else {
                updateMetricBadge(mapLookupBadge, "NOT FOUND (New Sum)", ACCENT_BLUE);
            }
        } else if (step.getActionType() == LongestZeroSum.ActionType.INITIALIZE) {
            updateMetricBadge(currentElementBadge, "None (Start)", TEXT_SECONDARY);
            updateMetricBadge(prevSumBadge, "0", TEXT_SECONDARY);
            updateMetricBadge(runningSumBadge, "0", TEXT_SECONDARY);
            updateMetricBadge(mapLookupBadge, "HashMap Empty", TEXT_SECONDARY);
        } else if (step.getActionType() == LongestZeroSum.ActionType.COMPLETED) {
            updateMetricBadge(currentElementBadge, "Done", ACCENT_GREEN);
            updateMetricBadge(prevSumBadge, String.valueOf(step.getPreviousSum()), TEXT_SECONDARY);
            updateMetricBadge(runningSumBadge, String.valueOf(step.getRunningSum()), TEXT_SECONDARY);
            updateMetricBadge(mapLookupBadge, "Complete", ACCENT_GREEN);
        }

        updateMetricBadge(currentMaxBadge, step.getCurrentMaxLength() +
                (step.getBestStartIndex() >= 0 ? " [" + step.getBestStartIndex() + ".." + step.getBestEndIndex() + "]" : ""), ACCENT_GREEN);

        // Update Narrative
        narrativeTextArea.setText(step.getDetailedExplanation());
        narrativeTextArea.setCaretPosition(0);

        // Update Result Dashboard
        boolean isComplete = (currentStepIndex == algorithmResult.getSteps().size() - 1);
        updateResultDashboard(isComplete);

        // Stepper button enable/disable
        firstStepBtn.setEnabled(currentStepIndex > 0);
        prevStepBtn.setEnabled(currentStepIndex > 0);
        nextStepBtn.setEnabled(currentStepIndex < totalSteps - 1);
        lastStepBtn.setEnabled(currentStepIndex < totalSteps - 1);
    }

    private void updateResultDashboard(boolean isComplete) {
        if (algorithmResult == null) {
            resultSubarrayLabel.setText("None");
            resultRangeLabel.setText("N/A");
            resultLengthLabel.setText("0");
            resultIterationsLabel.setText("0");
            resultStatusBadge.setText("Ready");
            resultStatusBadge.setForeground(ACCENT_BLUE);
            return;
        }

        if (isComplete) {
            if (algorithmResult.hasZeroSumSubarray()) {
                resultSubarrayLabel.setText(Arrays.toString(algorithmResult.getLongestSubarray()));
                resultRangeLabel.setText("[" + algorithmResult.getStartIndex() + " .. " + algorithmResult.getEndIndex() + "]");
                resultLengthLabel.setText(String.valueOf(algorithmResult.getMaxLength()));
                resultIterationsLabel.setText(String.valueOf(algorithmResult.getTotalIterations()));
                resultStatusBadge.setText("✔ Subarray Found");
                resultStatusBadge.setForeground(ACCENT_GREEN);
            } else {
                resultSubarrayLabel.setText("No zero-sum subarray exists");
                resultRangeLabel.setText("None");
                resultLengthLabel.setText("0");
                resultIterationsLabel.setText(String.valueOf(algorithmResult.getTotalIterations()));
                resultStatusBadge.setText("⚠ No Zero-Sum Found");
                resultStatusBadge.setForeground(ACCENT_AMBER);
            }
        } else {
            resultSubarrayLabel.setText("In progress...");
            resultRangeLabel.setText("Executing step " + (currentStepIndex + 1) + " of " + algorithmResult.getSteps().size());
            resultLengthLabel.setText(String.valueOf(algorithmResult.getSteps().get(currentStepIndex).getCurrentMaxLength()));
            resultIterationsLabel.setText(Math.max(0, currentStepIndex) + " / " + algorithmResult.getTotalIterations());
            resultStatusBadge.setText("Executing");
            resultStatusBadge.setForeground(ACCENT_AMBER);
        }
    }
}
