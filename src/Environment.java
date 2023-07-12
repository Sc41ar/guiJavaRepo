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
//    public int[][] environmentMap;
    Bot[][] matrix; //matrix that contains Bot pointers
    public Bot firstBot;//first Bot pointer
    public Bot currentBot; //current Bot pointer
    int step;//steps counter
    int population;//population counter
    int viewMode = 0;//
    int drawStep = 10;
    private Thread thread = null;//thread pointer
    private boolean isSimStarted;//flag that simulation sis started
    public static Environment currentEnvironment; //variable of the world
    private final GUI gui; //variable that contains all gui information & functions

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

    public void paint1() {
        Image buf = gui.canvas.createImage(width, height); //Создаем временный буфер для рисования
        Graphics g = buf.getGraphics(); //подеменяем графику на временный буфер

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] rgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        population = 0;
        while (currentBot != firstBot) {
            if (currentBot.isAlive) {                      // живой бот
                population++;
                if (viewMode == 0) {
                    image.setRGB(currentBot.x, currentBot.y, ((255 << 24) | (currentBot.redColor << 16) | (currentBot.greenColor << 8) | (currentBot.blueColor)));
                } else if (viewMode == 2) {
                    int greenChannel = 255 - (int) (currentBot.energy * 0.25);
                    image.setRGB(currentBot.x, currentBot.y, ((255 << 24) | (170 << 16) | (greenChannel << 8) | 0));
                } else if (viewMode == 3) {
                    int redChannel = 255 - /*(int)*/ (int)Math.sqrt(currentBot.age)*4;
                    image.setRGB(currentBot.x, currentBot.y, ((255 << 24) | (redChannel << 16) | (255 << 8) | 0));
                } else if (viewMode == 4) {
                    image.setRGB(currentBot.x, currentBot.y, currentBot.familyColor);
                }
            }
            currentBot = currentBot.nextBot;
        }
        currentBot = currentBot.nextBot;

        RenderedImage renderedImage = (RenderedImage) image;
        Graphics canvasGraphics = gui.canvas.getGraphics();
        Dimension screen = gui.getSize();
        canvasGraphics.drawImage(image, screen.width / 2 - 350, screen.height / 2 - 275, Color.white, null);

        gui.populationLabel.setText(" Population: " + String.valueOf(population));
        gui.generationLabel.setText("Steps: " + String.valueOf(step));
        File out = new File("filerender.png");
        try {
            ImageIO.write(renderedImage, "png", out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //событие создания мира
    @Override
    public void worldGenerated(int worldHeight, int worldWidth) {
        width = worldWidth;
        height = worldHeight;
        worldCreation();
        createAdam();
        paint1();
    }

    //событие изменения режима отображения
    @Override
    public void viewModeChanged(int viewMode) {
        this.viewMode = viewMode;
    }

    //событие начала/остановления симуляции
    @Override
    public boolean startStop() {
        if (thread == null) {
            isSimStarted = true;
            thread = new Worker();
            thread.start();
            return true;
        } else {
            isSimStarted = false;
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

    //изменение отображения ходов
    @Override
    public void stepRenderChanged(int delta) {
        this.drawStep = delta;
    }

    //метод инициализации переменных
    void worldCreation() {
        this.matrix = new Bot[width][height];
    }

    //создание первого бота с псевдослучайным геномом
    void createAdam() {
        Bot adam = new Bot();
        firstBot.previousBot = adam;
        firstBot.nextBot = adam;
        adam.PC = 0;
        adam.x = (int) Math.floor(Math.random() * width) - 1;
        adam.y = (int) Math.floor(Math.random() * height) - 1;
        adam.energy = 666666;
        adam.age = -110;
        adam.blueColor = 150;
        adam.redColor = 150;
        adam.greenColor = 150;
        adam.familyColor = ((255 << 24) | (123 << 16) | (70 << 8) | (255));
        adam.nextBot = firstBot;
        adam.previousBot = firstBot;
        for (int i = 0; i < 64; i++)
            adam.genome[i] = (byte) (Math.random() * 41);
        matrix[adam.x][adam.y] = adam;
        currentBot = adam;
    }

    //внутренний класс потока, который выполняет обход массива ботов и вызывает метод действия
    class Worker extends Thread {
        public void run() {
            while (isSimStarted) {
                System.out.println(this.getState() + " <> " + step);
                long time = System.currentTimeMillis();
                while (currentBot != firstBot) {
                    if (currentBot.isAlive) {
                        currentBot.step();
                        currentBot.age++;
                    }
                    currentBot = currentBot.nextBot;
                }
                currentBot = currentBot.nextBot;
                long time2 = System.currentTimeMillis();
                System.out.println(time2 - time);
                step++;
                if (step % drawStep == 0) {
                    paint1();
                }
                long time3 = System.currentTimeMillis();
                System.out.println(time3 - time2);
            }
            isSimStarted = false;
        }
    }
}