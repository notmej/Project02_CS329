import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CacheGUI extends JFrame implements ActionListener {

    private Cache cache;

    private JLabel label;
    private JPanel[] ramPanels;
    private JPanel[] cachePanels;

    private JPanel centerPanel;
    private JPanel ramPanel;
    private JPanel cachePanel;

    private JTextField addressText;
    private JButton directBtn;
    private JButton fullyBtn;
    private JButton setBtn;

    private JTextField nBlocksField;
    private JTextField blockSizeField;
    private JTextField ramSizeField;
    private JButton applyBtn;

    private final Color BG          = new Color(30, 30, 46);
    private final Color EMPTY_BLOCK = new Color(68, 71, 90);
    private final Color RAM_HIT     = new Color(107, 84, 140);
    private final Color CACHE_HIT   = new Color(65, 79, 64);
    private final Color CACHE_MISS  = new Color(119, 12, 12);
    private final Color TEXT_COLOR  = new Color(248, 248, 242);

    
    
    // initializes frame and everything in it
    public CacheGUI() {
        cache = new Cache(4, 4, 32);

        setTitle("Cache Simulator");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        
        buildNorthPanel();
        buildWestPanel();
        buildCenterPanel(8, 4);
        buildSouthPanel();

        setButtonActions();
    }

    
    
    // creates north panel
    private void buildNorthPanel() {
        JPanel northPanel = new JPanel();
        northPanel.setBackground(BG);

        addressText = new JTextField(10);
        addressText.setBackground(EMPTY_BLOCK);
        addressText.setForeground(TEXT_COLOR);
        addressText.setCaretColor(TEXT_COLOR);

        directBtn = makeButton("Direct");
        fullyBtn = makeButton("Fully Associative");
        setBtn = makeButton("Set Associative");

        
        label = new JLabel("Enter an address and select a mapping method");
        label.setForeground(TEXT_COLOR);

         
        JLabel label02 = new JLabel("cpu address:");
        label02.setForeground(Color.WHITE);

        northPanel.add(label02);
        northPanel.add(addressText);
        northPanel.add(directBtn);
        northPanel.add(fullyBtn);
        northPanel.add(setBtn);
        northPanel.add(label);

        add(northPanel, BorderLayout.NORTH);
    }

    
    
    // create west panel
    private void buildWestPanel() {
        JPanel westPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        westPanel.setBackground(BG);
        westPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR), "Cache Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null,TEXT_COLOR ));

        
        JLabel nBlocksLabel = makeLabel("Cache Blocks: ");
        JLabel blockSizeLabel = makeLabel("Block Size: ");
        JLabel ramSizeLabel = makeLabel("RAM Size: ");
        JLabel defaultVals = makeLabel("Default values present\n");
 
        
        nBlocksField = makeTextField("4");
        blockSizeField = makeTextField("4");
        ramSizeField = makeTextField("32");

        applyBtn = makeButton("Apply Settings");

        
        westPanel.add(defaultVals);
        westPanel.add(new JLabel(""));
        westPanel.add(nBlocksLabel);
        westPanel.add(nBlocksField);
        westPanel.add(blockSizeLabel);
        westPanel.add(blockSizeField);
        westPanel.add(ramSizeLabel);
        westPanel.add(ramSizeField);
        westPanel.add(new JLabel(""));
        westPanel.add(applyBtn);

        
        add(westPanel, BorderLayout.WEST);
    }

    
    
    // creates middle panel
    private void buildCenterPanel(int nRamBlocks, int nCacheBlocks) {
        centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.setBackground(BG);

        ramPanel = new JPanel(new GridLayout(nRamBlocks ,1));
        cachePanel = new JPanel(new GridLayout(nCacheBlocks ,1));

        
        ramPanel.setBackground(BG);
        cachePanel.setBackground(BG);
        
        ramPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR), "RAM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, TEXT_COLOR ));
        cachePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR), "Cache",javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, TEXT_COLOR));
        ramPanels = new JPanel[nRamBlocks];
        cachePanels = new JPanel[nCacheBlocks];

        
        for (int i =0; i < nRamBlocks; i++) {
            ramPanels[i] = makeBlockPanel("RAM Block " + i);
            ramPanel.add(ramPanels[i]);
        }
        
       
        for (int i =0; i < nCacheBlocks; i++) {
            cachePanels[i] = makeBlockPanel("Cache Block " + i);
            cachePanel.add(cachePanels[i]);
        }

        centerPanel.add(ramPanel);
        centerPanel.add(cachePanel);

        add(centerPanel, BorderLayout.CENTER);
        
    }

    
    
    // create south panel
    private void buildSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        southPanel.setBackground(BG);

        
        southPanel.add(makeLegendItem("Empty", EMPTY_BLOCK));
        southPanel.add(makeLegendItem("RAM Accessed", RAM_HIT));
        southPanel.add(makeLegendItem("Cache Hit", CACHE_HIT));
        southPanel.add(makeLegendItem("Cache Miss", CACHE_MISS));

        
        add(southPanel, BorderLayout.SOUTH);
    }

    
    
    
    private void setButtonActions() {
        applyBtn.addActionListener(this);
        directBtn.addActionListener(this);
        fullyBtn.addActionListener(this);
        setBtn.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == applyBtn) {
            applySettings();
        } 
        else if (e.getSource() == directBtn) {
            runMapping("direct");
        } 
        else if (e.getSource() == fullyBtn) {
            runMapping("fully");
        } 
        else if (e.getSource() == setBtn) {
            runMapping("set");
        }
    }

    
    
    
    private void applySettings() {
        try {
            int nBlocks = Integer.parseInt(nBlocksField.getText().trim());
            int blockSize = Integer.parseInt(blockSizeField.getText().trim());
            int ramSize = Integer.parseInt(ramSizeField.getText().trim());

            
            validateSettings(nBlocks, blockSize, ramSize);

            cache = new Cache(nBlocks, blockSize, ramSize);
            int nRamBlocks = ramSize / blockSize;

            remove(centerPanel);
            buildCenterPanel(nRamBlocks, nBlocks);

            
            label.setText("settings applied successfully");

            revalidate();
            repaint();

            
        } catch (Exception ex) {
            
            label.setText(ex.getMessage());
            
        }
    }

    
    
    
    private void runMapping(String type) {
        try {
            int cpuAddress = Integer.parseInt(addressText.getText().trim());

            CacheResult result;

            if (type.equals("direct")) {
                
                result = cache.directMap(cpuAddress);
            } else if (type.equals("fully")) {
                
                result = cache.fullyMap(cpuAddress);
            } else {
                
                result = cache.setMap(cpuAddress);
            }

            
            resetHighlights();

            highlightRamBlock(result.getRamBlockNo());
            highlightCacheBlock(result.getCacheIndex(), result.isHit());

            String text = "RAM Block: " + result.getRamBlockNo() + " | Cache Block: " + result.getCacheIndex()+ " | Offset: " + result.getOffset()+ " | " + (result.isHit() ? "HIT" : "MISS") + " | Miss Type: " + result.getMissType();

            if (type.equals("set")) {
                text += " | Set: " + result.getSetIndex();
            }

            label.setText(text);

        } catch (Exception ex) {
            label.setText(ex.getMessage());
        }
    }

    
    
    private void validateSettings(int nBlocks, int blockSize, int ramSize) {
        if (nBlocks <= 0 || blockSize <= 0 || ramSize <= 0) {
            throw new IllegalArgumentException("All values must be greater than 0");
        }

        
        if (!isPowerOfTwo(nBlocks) || !isPowerOfTwo(blockSize) || !isPowerOfTwo(ramSize)) {
            
            throw new IllegalArgumentException("All values must be powers of 2");
        }

        
        if (blockSize > ramSize) {
            throw new IllegalArgumentException("Block size cannot be larger than RAM size");
        }

        
        if (ramSize % blockSize != 0) {
            
            throw new IllegalArgumentException("RAM size must be divisible by block size");
        }
        

        int ramBlocks = ramSize / blockSize;
        if (nBlocks > ramBlocks) {
            throw new IllegalArgumentException("Cache blocks cannot exceed RAM blocks");
        }
        

        int setSize = 2;

        if (nBlocks % setSize != 0) {
            throw new IllegalArgumentException("Cache blocks must be divisible by set size");
        }
        
    }

    
    
    private boolean isPowerOfTwo(int value) {
        return value > 0 && (value & (value - 1)) == 0;
    }

    
    
    
    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBackground(EMPTY_BLOCK);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        
        return button;
    }

    
    
    private JTextField makeTextField(String text) {
        JTextField field = new JTextField(text);
        field.setBackground(EMPTY_BLOCK);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        
        return field;
    }

    
    
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        
        return lbl;
    }

    
    private JPanel makeBlockPanel(String text) {
        JPanel panel = new JPanel();
        panel.setBackground(EMPTY_BLOCK);

        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        panel.add(lbl);
        
        return panel;
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
        
        for (int i =0; i < ramPanels.length; i++) {
            
            ramPanels[i].setBackground(EMPTY_BLOCK);
        }
        

        for (int i = 0; i < cachePanels.length; i++) {
            cachePanels[i].setBackground(EMPTY_BLOCK);
        }
    }

    public void highlightRamBlock(int index) {
        
        if (index >= 0 && index < ramPanels.length) {
            ramPanels[index].setBackground(RAM_HIT);
        }
    }

    
    public void highlightCacheBlock(int index, boolean hit) {
        if (index >= 0 && index < cachePanels.length) {
            cachePanels[index].setBackground(hit ? CACHE_HIT : CACHE_MISS);
        }
    }

    
    
    public static void main(String[] args) {
        
        new CacheGUI().setVisible(true);
    }
}