import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class Environment implements ControlsCallback {
    public int width;
    public int height;
    public int[][] environmentMap;
    Bot[][] matrix;
    public Bot firstBot;
    public Bot currentBot;
    int generation;
    int population;
    //    int steps;
    int viewMode = 0;
    int drawStep = 10;
    private Thread thread = null;
    private boolean isSimStarteted;

    Image buffer = null;

    public int[] mapInGPU;

    public static Environment currentEnvironment;
    private final GUI gui;

    public Environment() {
        currentEnvironment = this;
        gui = new GUI(this);
        gui.init();
        firstBot = new Bot();
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Hell.");
        currentEnvironment = new Environment();
    }

    public void paintMapView() {
        int mapred;
        int mapgreen;
        int mapblue;
        Image mapbuffer = gui.canvas.createImage(width, height); // ширина - высота картинки
        Graphics g = mapbuffer.getGraphics();

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final int[] rgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < rgb.length; i++) {
            mapred = (int) (150 + (mapInGPU[i]) * 2.5);
            mapgreen = (int) (100 + (mapInGPU[i]) * 2.6);
            mapblue = 50 + (mapInGPU[i]) * 3;
            if (mapred > 255) mapred = 255;
            if (mapgreen > 255) mapgreen = 255;
            if (mapblue > 255) mapblue = 255;

            rgb[i] = (mapred << 16) | (mapgreen << 8) | mapblue;
        }
        g.drawImage(image, 0, 0, null);
    }

    public void paint1() {
        Image buf = gui.canvas.createImage(width, height); //Создаем временный буфер для рисования
        Graphics g = buf.getGraphics(); //подеменяем графику на временный буфер

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] rgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        population = 0;

        while (currentBot != firstBot) {
            if (currentBot.isAlive) {                      // живой бот
                image.setRGB(currentBot.x, currentBot.y, ((255 << 24) | (currentBot.redColor << 16) | (currentBot.greenColor << 8) | (currentBot.blueColor)));
                population++;
            }
            currentBot = currentBot.nextBot;
        }
        currentBot = currentBot.nextBot;

        RenderedImage killme = (RenderedImage) image;
        Graphics canvasGraphics = gui.canvas.getGraphics();
        canvasGraphics.drawImage(image, 0, 0, null);

        gui.populationLabel.setText(" Population: " + String.valueOf(population));
        gui.generationLabel.setText("Steps: " + String.valueOf(generation));
        File out = new File("IWANTTOKILLMYSELF.png");
        try {
            ImageIO.write(killme, "png", out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void worldGenerated(int worldHeight, int worldWidth) {
        width = worldWidth;
        height = worldHeight;
        worldCreation((int) (Math.random() * 10000));
        createAdam();
        paintMapView();
        paint1();
    }

    @Override
    public void viewModeChanged(int viewMode) {
        this.viewMode = viewMode;
    }

    @Override
    public boolean startStop() {
        if (thread == null) {
            isSimStarteted = true;
            thread = new Worker();
            thread.start();
            return true;
        } else {
            isSimStarteted = false;
            try {
                thread.interrupt();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
            return false;
        }
    }

    @Override
    public void stepRenderChanged(int delta) {
        this.drawStep = delta;
    }

    void worldCreation(int seed) {
        this.environmentMap = new int[width][height];
        this.matrix = new Bot[width][height];
        mapInGPU = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mapInGPU[j * width + 1] = environmentMap[i][j];
            }
        }
        //TODO: ДОДЕЛАТЬ
    }

    void createAdam() {
        Bot adam = new Bot();
        firstBot.previousBot = adam;
        firstBot.nextBot = adam;
        adam.PC = 0;
        adam.x = (int) Math.floor(Math.random() * width) - 1;
        adam.y = (int) Math.floor(Math.random() * height) - 1;
        adam.energy = 666;
        adam.age = 0;
        adam.blueColor = 150;
        adam.redColor = 150;
        adam.greenColor = 150;
        adam.nextBot = firstBot;
        adam.previousBot = firstBot;
        for (int i = 0; i < 64; i++)
            adam.genome[i] = 11;
        matrix[adam.x][adam.y] = adam;
        currentBot = adam;
    }

    class Worker extends Thread {
        public void run() {
            while (isSimStarteted) {
                long time = System.currentTimeMillis();
                while (currentBot != firstBot) {
                    if (currentBot.isAlive)
                        currentBot.step();
                    currentBot = currentBot.nextBot;
                }
                long time2 = System.currentTimeMillis();
                currentBot.step();
                currentBot.age++;
                currentBot = currentBot.nextBot;
                generation++;
                if (generation % drawStep == 0) {
                    paint1();
                }
                long time3 = System.currentTimeMillis();
            }
            isSimStarteted = false;
        }
    }
}