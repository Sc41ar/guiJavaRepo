import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame {

    Image buffer = null;

    JPanel canvas = new JPanel() {
        public void paint(Graphics g) {
            setBackground(Color.RED);
            g.drawImage(buffer, 0, 0, null);
        }
    };

    JPanel paintPanel = new JPanel(new FlowLayout());
    JLabel generationLabel = new JLabel(" Generation: 0 ");
    JLabel populationLabel = new JLabel(" Population: 0 ");

    public static final Map<String, Integer> VIEW_MODE_MAP = new HashMap<>();

    static {
        VIEW_MODE_MAP.put("Base", 0);
        VIEW_MODE_MAP.put("Energy", 2);
        VIEW_MODE_MAP.put("Age", 3);
        VIEW_MODE_MAP.put("Family", 4);
    }


    private final JRadioButton baseButton = new JRadioButton("Base", true);
    private final JRadioButton energyButton = new JRadioButton("Energy", false);
    private final JRadioButton ageButton = new JRadioButton("Age", false);
    private final JRadioButton familyButton = new JRadioButton("Family", false);

    //    JSlider perlinSlider = new JSlider(JSlider.HORIZONTAL, 0, 480, 300);
    private final JButton mapButton = new JButton("Create Map");
    private final JButton startButton = new JButton("Start/Stop");
    private final JSlider drawstepSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 10);

    private final ControlsCallback controlsCallback;

    public GUI(ControlsCallback controlsCallback) {
        this.controlsCallback = controlsCallback;
    }

    public void init() {
        setTitle("я устал");
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize(), fSize = getSize();
        setBounds(0, 0, this.getWidth(), 750);
        if (fSize.height > sSize.height) fSize.height = sSize.height;
        if (fSize.width > sSize.width) fSize.width = sSize.width;

        this.setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        GridBagConstraints canvasConstrains = new GridBagConstraints();
//        GridBagConstraints statusConstrains = new GridBagConstraints();
//
//        canvasConstrains.gridy = 0;
//        canvasConstrains.gridx = 0;
//        canvasConstrains.fill = GridBagConstraints.HORIZONTAL;
//        canvasConstrains.gridheight = 1;
//        canvasConstrains.gridwidth = 1;
//        canvasConstrains.weighty = 1;
//
//        statusConstrains.gridy = 1;
//        statusConstrains.gridx = 0;
//        statusConstrains.fill = GridBagConstraints.BOTH;
//        statusConstrains.gridheight = 1;
//        statusConstrains.gridwidth = 1;
//        statusConstrains.weighty = 0;

        this.add(canvas, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        this.add(statusPanel, BorderLayout.PAGE_END);

        generationLabel.setPreferredSize(new Dimension(140, 18));
        generationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(generationLabel);
        populationLabel.setPreferredSize(new Dimension(140, 18));
        populationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(populationLabel);

        JLabel slider1Label = new JLabel("Map scale");
        statusPanel.add(slider1Label);
        statusPanel.add(mapButton);
        statusPanel.add(startButton);

        JLabel slider3Label = new JLabel("Draw step");
        statusPanel.add(slider3Label);
        drawstepSlider.setMajorTickSpacing(10);
        drawstepSlider.setPaintTicks(true);
        drawstepSlider.setPaintLabels(true);
        drawstepSlider.setPreferredSize(new Dimension(650, 50));
        drawstepSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        statusPanel.add(drawstepSlider);

        ButtonGroup group = new ButtonGroup();
        List<AbstractButton> radioButtons = Arrays.asList(baseButton, energyButton, ageButton, familyButton);
        for (AbstractButton radioButton : radioButtons) {
            group.add(radioButton);
            statusPanel.add(radioButton);
        }

        this.pack();
        this.setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);

        drawstepSlider.addChangeListener(e -> {
            int ds = drawstepSlider.getValue();
            if (ds == 0) ds = 1;
            controlsCallback.stepRenderChanged(ds);
        });

        mapButton.addActionListener(e -> {
            int width = 700;//canvas.getHeight();
            int height = 550;//canvas.getWidth();
            controlsCallback.worldGenerated(height, width);
        });
        startButton.addActionListener(e -> {
            boolean started = controlsCallback.startStop();
            mapButton.setEnabled(!started);
        });

        ActionListener radioListener = e -> {
            String action = e.getActionCommand();
            Integer mode = VIEW_MODE_MAP.get(action);
            if (mode != null) {
                controlsCallback.viewModeChanged(mode);
            }
        };

        for (AbstractButton radioButton : radioButtons) {
            radioButton.addActionListener(radioListener);
        }
    }
}