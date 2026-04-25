import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
public class CacheGUI extends JFrame {
 
    // --- Cache logic ---
    private Cache cache;
 
    // --- Config inputs ---
    private JTextField nBlocksField;
    private JTextField blockSizeField;
    private JTextField ramSizeField;
    private JTextField cpuAddressField;
 
    // --- Buttons ---
    private JButton initBtn;
    private JButton directBtn;
    private JButton fullyBtn;
    private JButton setBtn;
 
    // --- Display ---
    private JLabel resultLabel;
    private JPanel[] ramPanels;
    private JPanel[] cachePanels;
    private JPanel ramGrid;
    private JPanel cacheGrid;
    private JPanel ramPanel;
    private JPanel cachePanel;
    private JPanel centerPanel;
 
    // --- Colors ---
    private final Color BG          = new Color(30, 30, 46);
    private final Color EMPTY_BLOCK = new Color(68, 71, 90);
    private final Color RAM_HIT     = new Color(189, 147, 249);
    private final Color CACHE_HIT   = new Color(80, 250, 123);
    private final Color CACHE_MISS  = new Color(255, 85, 85);
    private final Color TEXT_COLOR  = new Color(248, 248, 242);
 
    public CacheGUI() {
 
        setTitle("Cache Simulator");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);
 
        // ── NORTH: two rows ───────────────────────────────────────────────
        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.setBackground(BG);
 
        // Row 1: text inputs
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        inputRow.setBackground(BG);
 
        nBlocksField   = makeField("Num Blocks", 6);
        blockSizeField = makeField("Block Size", 6);
        ramSizeField   = makeField("RAM Size", 6);
        cpuAddressField = makeField("CPU Address", 6);
 
        inputRow.add(makeInputGroup("Num Blocks:", nBlocksField));
        inputRow.add(makeInputGroup("Block Size:", blockSizeField));
        inputRow.add(makeInputGroup("RAM Size:", ramSizeField));
        inputRow.add(makeInputGroup("CPU Address:", cpuAddressField));
 
        // Row 2: buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttonRow.setBackground(BG);
 
        initBtn   = makeButton("Initialize");
        directBtn = makeButton("Direct");
        fullyBtn  = makeButton("Fully Associative");
        setBtn    = makeButton("Set Associative");
 
        buttonRow.add(initBtn);
        buttonRow.add(directBtn);
        buttonRow.add(fullyBtn);
        buttonRow.add(setBtn);
 
        northPanel.add(inputRow);
        northPanel.add(buttonRow);
        add(northPanel, BorderLayout.NORTH);
 
        // ── CENTER: RAM + Cache panels (empty at start) ───────────────────
        centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.setBackground(BG);
 
        ramPanel = new JPanel(new BorderLayout());
        ramPanel.setBackground(BG);
 
        cachePanel = new JPanel(new BorderLayout());
        cachePanel.setBackground(BG);
 
        ramGrid   = new JPanel();
        cacheGrid = new JPanel();
        ramGrid.setBackground(BG);
        cacheGrid.setBackground(BG);
 
        ramPanel.add(ramGrid, BorderLayout.CENTER);
        cachePanel.add(cacheGrid, BorderLayout.CENTER);
 
        centerPanel.add(ramPanel);
        centerPanel.add(cachePanel);
        add(centerPanel, BorderLayout.CENTER);
 
        // ── SOUTH: result label + legend ──────────────────────────────────
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.setBackground(BG);
 
        resultLabel = new JLabel("Initialize the cache to begin", JLabel.CENTER);
        resultLabel.setForeground(TEXT_COLOR);
 
        JPanel legendRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        legendRow.setBackground(BG);
        legendRow.add(makeLegendItem("Empty", EMPTY_BLOCK));
        legendRow.add(makeLegendItem("RAM Accessed", RAM_HIT));
        legendRow.add(makeLegendItem("Cache Hit", CACHE_HIT));
        legendRow.add(makeLegendItem("Cache Miss", CACHE_MISS));
 
        southPanel.add(resultLabel);
        southPanel.add(legendRow);
        add(southPanel, BorderLayout.SOUTH);
 
        // ── Listeners ─────────────────────────────────────────────────────
 
        // Initialize button
        initBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int nBlocks   = Integer.parseInt(nBlocksField.getText().trim());
                    int blockSize = Integer.parseInt(blockSizeField.getText().trim());
                    int ramSize   = Integer.parseInt(ramSizeField.getText().trim());
 
                    // number of RAM blocks
                    int nRamBlocks = ramSize / blockSize;
                    int setSize    = 2; // 2-way set associative (fixed)
                    int nSets      = nBlocks / setSize;
 
                    // Validate
                    if (blockSize > ramSize) {
                        resultLabel.setText("Error: Block size must be <= RAM size.");
                        resultLabel.setForeground(CACHE_MISS);
                        return;
                    }
                    if (nBlocks > nRamBlocks) {
                        resultLabel.setText("Error: Num blocks must be <= RAM blocks (" + nRamBlocks + ").");
                        resultLabel.setForeground(CACHE_MISS);
                        return;
                    }
                    if (setSize > nBlocks) {
                        resultLabel.setText("Error: Set size must be <= num blocks.");
                        resultLabel.setForeground(CACHE_MISS);
                        return;
                    }
                    if (nSets < 1) {
                        resultLabel.setText("Error: Must have at least 1 set.");
                        resultLabel.setForeground(CACHE_MISS);
                        return;
                    }
 
                    // Create new cache
                    cache = new Cache(nBlocks, blockSize, ramSize);
 
                    // Rebuild panels
                    buildPanels(nRamBlocks, nBlocks);
 
                    resultLabel.setText("Cache initialized: " + nBlocks + " cache blocks, "
                        + nRamBlocks + " RAM blocks, block size " + blockSize + ".");
                    resultLabel.setForeground(CACHE_HIT);
 
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Error: Please enter valid numbers in all fields.");
                    resultLabel.setForeground(CACHE_MISS);
                }
            }
        });
 
        // Direct button
        directBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cache == null) { resultLabel.setText("Please initialize the cache first."); return; }
                try {
                    int cpuAddress = Integer.parseInt(cpuAddressField.getText().trim());
                    CacheResult result = cache.directMap(cpuAddress);
                    resetHighlights();
                    highlightRamBlock(result.getRamBlockNo());
                    highlightCacheBlock(result.getCacheIndex(), result.isHit());
                    resultLabel.setText("Direct  |  RAM Block: " + result.getRamBlockNo()
                        + "  |  Cache Block: " + result.getCacheIndex()
                        + "  |  Offset: " + result.getOffset()
                        + "  |  " + (result.isHit() ? "HIT" : "MISS"));
                    resultLabel.setForeground(result.isHit() ? CACHE_HIT : CACHE_MISS);
                } catch (Exception ex) {
                    resultLabel.setText("Invalid CPU address.");
                    resultLabel.setForeground(CACHE_MISS);
                }
            }
        });
 
        // Fully Associative button
        fullyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cache == null) { resultLabel.setText("Please initialize the cache first."); return; }
                try {
                    int cpuAddress = Integer.parseInt(cpuAddressField.getText().trim());
                    CacheResult result = cache.fullyMap(cpuAddress);
                    resetHighlights();
                    highlightRamBlock(result.getRamBlockNo());
                    highlightCacheBlock(result.getCacheIndex(), result.isHit());
                    resultLabel.setText("Fully Associative  |  RAM Block: " + result.getRamBlockNo()
                        + "  |  Cache Block: " + result.getCacheIndex()
                        + "  |  Offset: " + result.getOffset()
                        + "  |  " + (result.isHit() ? "HIT" : "MISS"));
                    resultLabel.setForeground(result.isHit() ? CACHE_HIT : CACHE_MISS);
                } catch (Exception ex) {
                    resultLabel.setText("Invalid CPU address.");
                    resultLabel.setForeground(CACHE_MISS);
                }
            }
        });
 
        // Set Associative button
        setBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cache == null) { resultLabel.setText("Please initialize the cache first."); return; }
                try {
                    int cpuAddress = Integer.parseInt(cpuAddressField.getText().trim());
                    CacheResult result = cache.setMap(cpuAddress);
                    resetHighlights();
                    highlightRamBlock(result.getRamBlockNo());
                    highlightCacheBlock(result.getCacheIndex(), result.isHit());
                    resultLabel.setText("Set Associative  |  RAM Block: " + result.getRamBlockNo()
                        + "  |  Set: " + result.getSetIndex()
                        + "  |  Cache Block: " + result.getCacheIndex()
                        + "  |  Offset: " + result.getOffset()
                        + "  |  " + (result.isHit() ? "HIT" : "MISS"));
                    resultLabel.setForeground(result.isHit() ? CACHE_HIT : CACHE_MISS);
                } catch (Exception ex) {
                    resultLabel.setText("Invalid CPU address.");
                    resultLabel.setForeground(CACHE_MISS);
                }
            }
        });
    }
 
    // ── Rebuild RAM and Cache panels with new sizes ───────────────────────
    private void buildPanels(int nRamBlocks, int nCacheBlocks) {
        // Clear old panels
        ramGrid.removeAll();
        cacheGrid.removeAll();
 
        ramGrid.setLayout(new GridLayout(nRamBlocks, 1, 0, 4));
        cacheGrid.setLayout(new GridLayout(nCacheBlocks, 1, 0, 4));
 
        // Rebuild RAM panels
        ramPanels = new JPanel[nRamBlocks];
        for (int i = 0; i < nRamBlocks; i++) {
            ramPanels[i] = new JPanel();
            ramPanels[i].setBackground(EMPTY_BLOCK);
            ramPanels[i].add(new JLabel("RAM " + i));
            ramGrid.add(ramPanels[i]);
        }
 
        // Rebuild Cache panels
        cachePanels = new JPanel[nCacheBlocks];
        for (int i = 0; i < nCacheBlocks; i++) {
            cachePanels[i] = new JPanel();
            cachePanels[i].setBackground(EMPTY_BLOCK);
            cachePanels[i].add(new JLabel("Cache " + i));
            cacheGrid.add(cachePanels[i]);
        }
 
        // Update borders
        ramPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR), "RAM",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            null, TEXT_COLOR));
 
        cachePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR), "Cache",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            null, TEXT_COLOR));
 
        // Refresh UI
        ramGrid.revalidate();
        ramGrid.repaint();
        cacheGrid.revalidate();
        cacheGrid.repaint();
    }
 
    // ── Helper: styled text field ─────────────────────────────────────────
    private JTextField makeField(String placeholder, int cols) {
        JTextField field = new JTextField(cols);
        field.setBackground(EMPTY_BLOCK);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 88, 130), 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return field;
    }
 
    // ── Helper: label + field group ───────────────────────────────────────
    private JPanel makeInputGroup(String labelText, JTextField field) {
        JPanel group = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        group.setBackground(BG);
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(TEXT_COLOR);
        group.add(lbl);
        group.add(field);
        return group;
    }
 
    // ── Helper: styled button ─────────────────────────────────────────────
    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBackground(EMPTY_BLOCK);
        btn.setForeground(TEXT_COLOR);
        btn.setFocusPainted(false);
        return btn;
    }
 
    // ── Helper: legend item ───────────────────────────────────────────────
    private JPanel makeLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setBackground(BG);
        JPanel square = new JPanel();
        square.setBackground(color);
        square.setPreferredSize(new java.awt.Dimension(14, 14));
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        item.add(square);
        item.add(lbl);
        return item;
    }
 
    // ── Reset all blocks to empty ─────────────────────────────────────────
    public void resetHighlights() {
        if (ramPanels != null)
            for (JPanel p : ramPanels) p.setBackground(EMPTY_BLOCK);
        if (cachePanels != null)
            for (JPanel p : cachePanels) p.setBackground(EMPTY_BLOCK);
    }
 
    // ── Highlight RAM block ───────────────────────────────────────────────
    public void highlightRamBlock(int index) {
        if (ramPanels != null && index < ramPanels.length)
            ramPanels[index].setBackground(RAM_HIT);
    }
 
    // ── Highlight Cache block ─────────────────────────────────────────────
    public void highlightCacheBlock(int index, boolean hit) {
        if (cachePanels != null && index < cachePanels.length)
            cachePanels[index].setBackground(hit ? CACHE_HIT : CACHE_MISS);
    }
 
    public static void main(String[] args) {
        new CacheGUI().setVisible(true);
    }
}