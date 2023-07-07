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
            setBackground(Color.cyan);
            g.drawImage(buffer, 0, 0, null);
        }
    };

    JPanel paintPanel = new JPanel(new FlowLayout());
    JLabel generationLabel = new JLabel(" Generation: 0 ");
    JLabel populationLabel = new JLabel(" Population: 0 ");
    JLabel organicLabel = new JLabel(" Organic: 0 ");

    public static final Map<String, Integer> VIEW_MODE_MAP = new HashMap<>();

    static {
        VIEW_MODE_MAP.put("Base", 0);
        VIEW_MODE_MAP.put("Combined", 1);
        VIEW_MODE_MAP.put("Energy", 2);
        VIEW_MODE_MAP.put("Age", 3);
        VIEW_MODE_MAP.put("Family", 4);
    }


    private final JRadioButton baseButton = new JRadioButton("Base", true);
    private final JRadioButton combinedButton = new JRadioButton("Combined", false);
    private final JRadioButton energyButton = new JRadioButton("Energy", false);
    private final JRadioButton ageButton = new JRadioButton("Age", false);
    private final JRadioButton familyButton = new JRadioButton("Family", false);

    JSlider perlinSlider = new JSlider(JSlider.HORIZONTAL, 0, 480, 300);
    private final JButton mapButton = new JButton("Create Map");
    private final JButton startButton = new JButton("Start/Stop");
    private final JSlider drawstepSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 10);

    private final ControlsCallback controlsCallback;

    public GUI(ControlsCallback controlsCallback) {
        this.controlsCallback = controlsCallback;
    }

    public void init() {
        setTitle("я устал");
        setSize(new Dimension(700, 350));
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize(), fSize = getSize();
        if (fSize.height > sSize.height) fSize.height = sSize.height;
        if (fSize.width > sSize.width) fSize.width = sSize.width;
        //setLocation((sSize.width - fSize.width)/2, (sSize.height - fSize.height)/2);
        setSize(new Dimension(sSize.width, sSize.height));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container container = getContentPane();

        paintPanel.setLayout(new BorderLayout());// у этого лейаута приятная особенность - центральная часть растягивается автоматически
        paintPanel.add(canvas, BorderLayout.CENTER);// добавляем нашу карту в центр
        container.add(paintPanel);

        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        container.add(statusPanel, BorderLayout.SOUTH);

        generationLabel.setPreferredSize(new Dimension(140, 18));
        generationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(generationLabel);
        populationLabel.setPreferredSize(new Dimension(140, 18));
        populationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(populationLabel);
        organicLabel.setPreferredSize(new Dimension(140, 18));
        organicLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(organicLabel);

        JToolBar toolbar = new JToolBar();
        toolbar.setOrientation(1);
        container.add(toolbar, BorderLayout.WEST);

        JLabel slider1Label = new JLabel("Map scale");
        toolbar.add(slider1Label);

        perlinSlider.setMajorTickSpacing(160);
        perlinSlider.setMinorTickSpacing(80);
        perlinSlider.setPaintTicks(true);
        perlinSlider.setPaintLabels(true);
        perlinSlider.setPreferredSize(new Dimension(100, perlinSlider.getPreferredSize().height));
        perlinSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        toolbar.add(perlinSlider);
        toolbar.add(mapButton);
        toolbar.add(startButton);

        JLabel slider3Label = new JLabel("Draw step");
        toolbar.add(slider3Label);
        drawstepSlider.setMajorTickSpacing(10);
        drawstepSlider.setPaintTicks(true);
        drawstepSlider.setPaintLabels(true);
        drawstepSlider.setPreferredSize(new Dimension(100, 50));
        drawstepSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        toolbar.add(drawstepSlider);

        ButtonGroup group = new ButtonGroup();
        List<AbstractButton> radioButtons = Arrays.asList(baseButton, combinedButton, energyButton, ageButton, familyButton);
        for (AbstractButton radioButton : radioButtons) {
            group.add(radioButton);
            toolbar.add(radioButton);
        }

        this.pack();
        this.setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);

        drawstepSlider.addChangeListener(e -> {
            int ds = drawstepSlider.getValue();
            if (ds == 0) ds = 1;
            controlsCallback.stepRenderChanged(ds);
        });

        mapButton.addActionListener(e -> controlsCallback.worldGenerated(canvas.getWidth(), canvas.getHeight()));
        startButton.addActionListener(e -> {
            boolean started = controlsCallback.startStop();
            perlinSlider.setEnabled(!started);
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