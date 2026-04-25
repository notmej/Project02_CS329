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

    
    private Cache cache;
    private JLabel label;
    private JPanel[] ramPanels;
    private JPanel[] cachePanels;

    private JTextField text;
    private JButton directBtn;
    private JButton fullyBtn;
    private JButton setBtn;

    private final Color BG          = new Color(30, 30, 46);
    private final Color EMPTY_BLOCK = new Color(68, 71, 90);
    private final Color RAM_HIT     = new Color(107, 84, 140);
    private final Color CACHE_HIT   = new Color(65, 79, 64);
    private final Color CACHE_MISS  = new Color(119, 12, 12);
    private final Color TEXT_COLOR  = new Color(248, 248, 242);

    public CacheGUI() {
        cache = new Cache(4, 4, 32);

        setTitle("Cache Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        // NORTH
        JPanel northPanel = new JPanel();
        northPanel.setBackground(BG);

        text = new JTextField(10);
        text.setBackground(EMPTY_BLOCK);
        text.setForeground(TEXT_COLOR);
        text.setCaretColor(TEXT_COLOR);

        directBtn = new JButton("Direct");
        directBtn.setOpaque(true);
        directBtn.setBorderPainted(false);
        directBtn.setBackground(EMPTY_BLOCK);
        directBtn.setForeground(TEXT_COLOR);
        directBtn.setFocusPainted(false);

        fullyBtn = new JButton("Fully associative");
        fullyBtn.setOpaque(true);
        fullyBtn.setBorderPainted(false);
        fullyBtn.setBackground(EMPTY_BLOCK);
        fullyBtn.setForeground(TEXT_COLOR);
        fullyBtn.setFocusPainted(false);

        setBtn = new JButton("Set associative");
        setBtn.setOpaque(true);
        setBtn.setBorderPainted(false);
        setBtn.setBackground(EMPTY_BLOCK);
        setBtn.setForeground(TEXT_COLOR);
        setBtn.setFocusPainted(false);

        label = new JLabel("Enter an address and select a mapping method");
        label.setForeground(TEXT_COLOR);

        northPanel.add(text);
        northPanel.add(directBtn);
        northPanel.add(fullyBtn);
        northPanel.add(setBtn);
        northPanel.add(label);

        add(northPanel, BorderLayout.NORTH);

        // CENTER
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.setBackground(BG);
        JPanel ramPanel   = new JPanel(new GridLayout(8, 1));
        JPanel cachePanel = new JPanel(new GridLayout(4, 1));
        ramPanel.setBackground(BG);
        cachePanel.setBackground(BG);

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

        ramPanels   = new JPanel[8];
        cachePanels = new JPanel[4];

        for (int i = 0; i < 8; i++) {
            ramPanels[i] = new JPanel();
            ramPanels[i].setBackground(EMPTY_BLOCK);
            ramPanels[i].add(new JLabel("RAM " + i));
            ramPanel.add(ramPanels[i]);
        }

        for (int i = 0; i < 4; i++) {
            cachePanels[i] = new JPanel();
            cachePanels[i].setBackground(EMPTY_BLOCK);
            cachePanels[i].add(new JLabel("Cache " + i));
            cachePanel.add(cachePanels[i]);
        }

        centerPanel.add(ramPanel);
        centerPanel.add(cachePanel);
        add(centerPanel, BorderLayout.CENTER);

        // SOUTH - legend
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        southPanel.setBackground(BG);
        southPanel.add(makeLegendItem("Empty", EMPTY_BLOCK));
        southPanel.add(makeLegendItem("RAM Accessed", RAM_HIT));
        southPanel.add(makeLegendItem("Cache Hit", CACHE_HIT));
        southPanel.add(makeLegendItem("Cache Miss", CACHE_MISS));
        add(southPanel, BorderLayout.SOUTH);

        // Listeners
        directBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cpuAddress = Integer.parseInt(text.getText().trim());
                    CacheResult result = cache.directMap(cpuAddress);
                    resetHighlights();
                    highlightRamBlock(result.getRamBlockNo());
                    highlightCacheBlock(result.getCacheIndex(), result.isHit());
                    label.setText("RAM Block: " + result.getRamBlockNo()
                        + " | Cache Block: " + result.getCacheIndex()
                        + " | Offset: " + result.getOffset()
                        + " | " + (result.isHit() ? "HIT" : "MISS"));
                } catch (Exception ex) {
                    label.setText("Invalid input. Enter a number between 0 and 31.");
                }
            }
        });

        fullyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cpuAddress = Integer.parseInt(text.getText().trim());
                    CacheResult result = cache.fullyMap(cpuAddress);
                    resetHighlights();
                    highlightRamBlock(result.getRamBlockNo());
                    highlightCacheBlock(result.getCacheIndex(), result.isHit());
                    label.setText("RAM Block: " + result.getRamBlockNo()
                        + " | Cache Block: " + result.getCacheIndex()
                        + " | Offset: " + result.getOffset()
                        + " | " + (result.isHit() ? "HIT" : "MISS"));
                } catch (Exception ex) {
                    label.setText("Invalid input. Enter a number between 0 and 31.");
                }
            }
        });

        setBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cpuAddress = Integer.parseInt(text.getText().trim());
                    CacheResult result = cache.setMap(cpuAddress);
                    resetHighlights();
                    highlightRamBlock(result.getRamBlockNo());
                    highlightCacheBlock(result.getCacheIndex(), result.isHit());
                    label.setText("RAM Block: " + result.getRamBlockNo()
                        + " | Set: " + result.getSetIndex()
                        + " | Cache Block: " + result.getCacheIndex()
                        + " | Offset: " + result.getOffset()
                        + " | " + (result.isHit() ? "HIT" : "MISS"));
                } catch (Exception ex) {
                    label.setText("Invalid input. Enter a number between 0 and 31.");
                }
            }
        });
    }

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

    public void resetHighlights() {
        for (int i = 0; i < 8; i++) ramPanels[i].setBackground(EMPTY_BLOCK);
        for (int i = 0; i < 4; i++) cachePanels[i].setBackground(EMPTY_BLOCK);
    }

    public void highlightRamBlock(int index) {
        ramPanels[index].setBackground(RAM_HIT);
    }

    public void highlightCacheBlock(int index, boolean hit) {
        cachePanels[index].setBackground(hit ? CACHE_HIT : CACHE_MISS);
    }

    public static void main(String[] args) {
        new CacheGUI().setVisible(true);
    }
}