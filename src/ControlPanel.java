//import javax.swing.*;
//import javax.swing.text.View;
//import java.awt.*;
//import java.awt.event.ActionListener;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ControlPanel extends JPanel {
//    JLabel populationLabel;
//    JLabel stepsLabel;
//    JRadioButton baseButton = new JRadioButton("Base", true);
//    JRadioButton energyButton = new JRadioButton("Energy", false);
//    JRadioButton ageButton = new JRadioButton("Age", false);
//    JRadioButton familyButton = new JRadioButton("Family", false);
//    JButton mapButton = new JButton("Create Map");
//    JButton startButton = new JButton("Start/Stop");
//    ButtonGroup viewModeGroup = new ButtonGroup();
//    JSlider drawStepSlider = new JSlider(0, 100);
//
//
//
//
//    public static Map<String, Integer> VIEW_MODE_MAP = new HashMap<>();
//    static {
//        VIEW_MODE_MAP.put("Base", 0);
//        VIEW_MODE_MAP.put("Combined", 1);
//        VIEW_MODE_MAP.put("Energy", 2);
//        VIEW_MODE_MAP.put("Age", 3);
//        VIEW_MODE_MAP.put("Genome", 4);
//    }
//    private  ;
//
//    public ControlPanel( callback)
//    {
//        super();
//        ControlsCallback = callback;
////        задача всех параметров для компонентов gui
//        this.setPreferredSize(new Dimension(150,800));
//        setLayout(new FlowLayout());
//        populationLabel = new JLabel("Population: 0");
//        stepsLabel = new JLabel("Steps: 0");
//        stepsLabel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
//        populationLabel.setForeground(Color.black);
//        populationLabel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
//        populationLabel.setPreferredSize( new Dimension(350, 15));
//        stepsLabel.setPreferredSize(new Dimension(100, 15));
//        baseButton.setBackground(new Color(255, 230, 200, 170));
//        energyButton.setBackground(new Color(255, 230, 200, 170));
//        ageButton.setBackground(new Color(255, 230, 200, 170));
//        familyButton.setBackground(new Color(255, 230, 200, 170));
//        startButton.setBackground(new Color(155, 135, 125, 255));
//        drawStepSlider.setMajorTickSpacing(10);
//        drawStepSlider.setPaintTicks(true);
//        drawStepSlider.setPaintLabels(true);
//        drawStepSlider.setPreferredSize(new Dimension(250, 50));
//        drawStepSlider.setBackground(new Color(255, 230, 200, 170));
//        drawStepSlider.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
//        drawStepSlider.setSnapToTicks(true);
//        drawStepSlider.setToolTipText("Изменение скорости отрисовки");
//        drawStepSlider.setName("Step Render");
//        JToolBar toolBar = new JToolBar();
//        toolBar.setOrientation(1);
//        toolBar.setBackground(new Color(255, 230, 200, 170));
//        toolBar.add(drawStepSlider);
//        toolBar.setFloatable(false);
//
//
////      добавление listener'ов
//
//        drawStepSlider.addChangeListener(
//                e -> {
//                    int currentValue = drawStepSlider.getValue();
//                    if(currentValue < 1)
//                        currentValue ++;
//                    .stepRenderChanged(currentValue);
//                }
//        );
//
//        startButton.addActionListener(
//                e -> {
//                    boolean isStarted = ControlsCallback.startStop();
//
//                }
//        );
//        ActionListener viewModeListener = e -> {
//            String action = e.getActionCommand();
//            Integer mode = VIEW_MODE_MAP.get(action);
//            if(mode != null)
//                ControlsCallback.viewModeChanged(mode);
//        };
//        baseButton.addActionListener(viewModeListener);
//        energyButton.addActionListener(viewModeListener);
//        ageButton.addActionListener(viewModeListener);
//        familyButton.addActionListener(viewModeListener);
//
//
//
//        //добавление элементов
//        this.add(startButton);
//        this.add(populationLabel);
//        this.add(stepsLabel);
//        this.add(toolBar);
//        viewModeGroup.add(baseButton);
//        viewModeGroup.add(energyButton);
//        viewModeGroup.add(ageButton);
//        viewModeGroup.add(familyButton);
//        this.add(baseButton);
//        this.add(energyButton);
//        this.add(ageButton);
//        this.add(familyButton);
//    }
//
//
//
//}
