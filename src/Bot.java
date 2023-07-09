public class Bot {
    public int energy;
    ///    порог смерти от отсутствия энергии
    private int energyDeathThreshold;
    //    порог смерти от старости
    private int ageDeathThreshold;
    ///    программный счетчик для генома
    public int PC;
    ///   возраст в шагах симуляции
    public int age;
    // координаты на сетке
    public int x;
    public int y;
    public Direction sightDir;
    private int duplicateThreshold;

    public int redColor;
    public int greenColor; //цвета бота, в зависимости от еды
    public int blueColor;
    public int familyColor;
    public boolean isAlive;
    public Bot previousBot;
    public Bot nextBot;

    // сам геном бота
    public byte[] genome = new byte[64];

    public Bot() {
        this.energy = 100;
        isAlive = true;
        energyDeathThreshold = 0;
        ageDeathThreshold = 35;
        PC = 0;
        age = 0;
        x = (int) Math.floor(Math.random() * Environment.currentEnvironment.width/*длина матрицы, в которой живут бооты*/);
        y = (int) Math.floor(Math.random() * Environment.currentEnvironment.height/*высота мира ботов*/);
        sightDir = Direction.N;
        duplicateThreshold = 555;
        for (int i = 0; i < 64; i++) {
            genome[i] = (byte) Math.floor(Math.random() * 63);
        }
    }

    void step() {
        if (PC >= 63) {
            PC = ((int) Math.floor(Math.random() * 63)) % 8;
        }
        if (energy >= duplicateThreshold) {
            botDublicate();
            return;
        }
        if (energy <= energyDeathThreshold) {
            killBot(this);
            return;
        }
        if (age >= ageDeathThreshold) {
            isAlive = false;
            killBot(this);
            return;
        }
        int breakFlag = 0;
        int command;
        for (int i = 0; i < 15; i++) {
            if (breakFlag == 1)
                break;
            command = genome[PC];
            switch (command) {
                case 0, 1, 2, 3, 4, 5, 6, 7:
                    rotateBot(botGetParam() % 8);
                    increasePC(1);
                    break;
                case 9://движение
                    increasePC(move() + 1);
                    breakFlag = 1;
                    break;
                case 10: //проверка
                    increasePC(faceCheck());
                    break;
                case 11:
                    botPhotosynthesis();
                    increasePC(1);
                    breakFlag = 1;
                    break;
                case 12:
                    increasePC(attack() + 1);//атака
                    breakFlag = 1;
                    break;
                case 13: //проверка энергии
                    increasePC(getEnergy());
                    increasePC(1);
                case 15:
                    botDublicate();
                    if (energy <= energyDeathThreshold || !isAlive)
                        killBot(this);
                    increasePC(1);
                    breakFlag = 1;
                    break;
                case 16:
                    botMutate();
                    increasePC(1);
                    break;
                default:
                    increasePC(command);
                    breakFlag = 1;
                    break;
            }
        }

    }

    private void increasePC(int value) {
        PC = (PC + value) % 64;
    }

    /**
     * получение параметра для некоторых команд
     */
    private int botGetParam() {
        return genome[(PC + 1) % 64];
    }

    /**
     * получение черепашки ниндзя
     * n - кол-во мутаций
     */
    private void botMutate() {
        int mutationAdr = (int) (Math.random() * 64);
        byte mutation = (byte) (Math.random() * 64);
        genome[mutationAdr] = mutation;
    }


//--/-/-/-/-/-/-/-/-/-/-/-/--/-/-/-/-/-/-/-/-/--/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/--//-/-/-/-/-/-

    /**
     * кручение бота
     */
    private void rotateBot(int n) {
        switch (n) {
            case 0 -> sightDir = Direction.N;
            case 1 -> sightDir = Direction.NE;
            case 2 -> sightDir = Direction.E;
            case 3 -> sightDir = Direction.SE;
            case 4 -> sightDir = Direction.S;
            case 5 -> sightDir = Direction.SW;
            case 6 -> sightDir = Direction.W;
            case 7 -> sightDir = Direction.NW;
        }
    }

//**********************************************************************************************************************

    /**
     * фотосинтез
     * пока так, буду усложнять позднее
     */
    private void botPhotosynthesis() {
        int energyCount;
        energyCount = 111;
        energy += energyCount;
        goGreen(energyCount);
    }

//пощупать клетку перед собой
//возвращаемые значения анологичны методу передвижения

    /**
     * пощупать клетку перед собой
     * возвращаемые значения анологичны методу передвижения
     */
    private int faceCheck() {
        if (isSurrounded())
            return 4;
        int xt = getSightX();
        int yt = getSightY();
        if (xt >= Environment.currentEnvironment.width)
            xt = xt % Environment.currentEnvironment.width;
        else if (xt < 0)
            xt = 0;
        if (yt >= Environment.currentEnvironment.height)
            yt = yt % Environment.currentEnvironment.height;
        else if (yt < 0)
            yt = 0;
        if (Environment.currentEnvironment.matrix[xt][yt] == null)
            return 0;
        else if (isRelative(Environment.currentEnvironment.matrix[xt][yt]))
            return 1;
        else if (yt <= 0 || yt >= Environment.currentEnvironment.height)
            return 2;
        return 3;
    }


//передвижение
//    вернет то, куда лбом ударился бот
//    0 - ничего, и он двинулся; 1 - родня; 2 - стена; 3 - бот; 4 - окружен со всех сторо

    /**
     * вернет то, куда лбом ударился бот
     * 0 - ничего, и он двинулся; 1 - родня; 2 - стена; 3 - бот; 4 - окружен со всех сторон
     */
    private int move() {
        if (isSurrounded()) return 4;
        int xt = getSightX();
        int yt = getSightY();
        if (xt >= Environment.currentEnvironment.width)
            xt = xt % Environment.currentEnvironment.width;
        else if (xt < 0)
            xt = 0;
        if (yt >= Environment.currentEnvironment.height)
            yt = yt % Environment.currentEnvironment.height;
        else if (yt < 0)
            yt = 0;
        if (Environment.currentEnvironment.matrix[xt][yt] == null) {
//            move bot
            Environment.currentEnvironment.matrix[xt][yt] = this;
            Environment.currentEnvironment.matrix[x][y] = null;
            x = xt;
            y = yt;
            return 0;
        } else if (yt <= 0 || yt >= Environment.currentEnvironment.height) return 2;
        else if (isRelative(Environment.currentEnvironment.matrix[xt][yt])) return 1;
        return 3;
    }

    //атака
    // на выходе будет то, что бот атаковал
//    если погибнет в ходе охоты вернет 1, атаковал пустую клетку - 0, атаковал стену - 2 и потеряет энергию
//    съел бота - получил его энергию и убил его

    private int attack() {
        energy -= 40;
        int xt = this.xFromVektorR(botGetParam() % 8);
        int yt = this.yFromVektorR(botGetParam() % 8);
        if (yt <= 0 || yt >= Environment.currentEnvironment.height) {
            energy -= 100;
            return 2;
        } else if (Environment.currentEnvironment.matrix[xt][yt] == null)
            return 0;

        Bot target = Environment.currentEnvironment.matrix[xt][yt];
        if (target.energy >= 2 * energy) {
            target.goRed(energy);
            target.energy += energy;
            isAlive = false;
            killBot(this);
            return 1;
        } else if (target.energy >= energy) {
            double coin = Math.random();
            if (coin < 0.49) {
                target.goRed(energy);
                target.energy += energy;
                isAlive = false;
                killBot(this);
                return 1;
            } else {
                goRed(target.energy);
                energy += target.energy;
                target.isAlive = false;
                killBot(target);
                return 3;
            }
        }
        energy += target.energy - 100;
        return 3;

    }


    //**********************************************************************************************************************
//    тут все ясно
    private void killBot(Bot bot) {
        Environment.currentEnvironment.matrix[bot.x][bot.y] = null;
        bot.previousBot.nextBot = bot.nextBot;
        bot.nextBot.previousBot = bot.previousBot;
    }

    //**********************************************************************************************************************
// группа методов вычесления координат ячейки, на которую смотрит бот
    private int getSightX() {
        int xTemp = this.x;
        if (sightDir == Direction.NE || sightDir == Direction.E || sightDir == Direction.SE) {
            xTemp--;

        } else if (sightDir == Direction.NW || sightDir == Direction.W || sightDir == Direction.SW) {
            xTemp++;
        }
        if (x < 0 || x >= Environment.currentEnvironment.width)
            xTemp = x;
        return xTemp;
    }

    private int getSightY() {
        int yTemp = this.y;
        if (sightDir == Direction.N || sightDir == Direction.NW || sightDir == Direction.NE) yTemp++;
        else if (sightDir == Direction.SW || sightDir == Direction.S || sightDir == Direction.SE) yTemp--;
        if (yTemp < 0 || yTemp > Environment.currentEnvironment.height)
            yTemp = y;
        return yTemp;
    }

    private int xFromVektorR(int n) {
        int xt = x;
        n = n + sightDir.ordinal();
        if (n >= 8) n = n - 8;
        if (n >= 5 && n <= 7) {
            xt--;
            if (xt < 0) xt = Environment.currentEnvironment.width - 1;
        } else if (n >= 1 && n <= 3) {
            xt++;
            if (xt >= Environment.currentEnvironment.width) xt = 0;
        }
        return xt;
    }

    private int yFromVektorR(int n) {
        int yt = y;
        n = n + sightDir.ordinal();
        if (n >= 8) n = n - 8;
        if (n <= 5 && n >= 3) {
            yt--;
        } else if (n == 7 || n <= 1) {
            yt++;
        }
        return yt;
    }

    //******************************************************************************************************************
// различные проверки
    private boolean isSurrounded() {
        if ((y > 0) && (y < Environment.currentEnvironment.height - 1) && (x > 0) && (x > Environment.currentEnvironment.width - 1)) {
            if (Environment.currentEnvironment.matrix[x - 1][y - 1] == null
                    || Environment.currentEnvironment.matrix[x - 1][y] == null
                    || Environment.currentEnvironment.matrix[x - 1][y + 1] == null
                    || Environment.currentEnvironment.matrix[x][y - 1] == null
                    || Environment.currentEnvironment.matrix[x][y + 1] == null
                    || Environment.currentEnvironment.matrix[x + 1][y - 1] == null
                    || Environment.currentEnvironment.matrix[x + 1][y] == null
                    || Environment.currentEnvironment.matrix[x + 1][y + 1] == null)
                return false;
        } else {
            int xt, yt;
            for (int i = 0; i < 8; i++) {
                xt = xFromVektorR(i);
                yt = yFromVektorR(i);
                if ((yt >= 0) && yt < Environment.currentEnvironment.height) {
                    if (Environment.currentEnvironment.matrix[xt][yt] == null) return false;
                }
            }
        }
        return true;
    }

    //проверяет сколько у бота энергии
    //если достаточно для атаки - вернет 1
    //если достаточно для размножения вернет 5
//    если недостаточно вернет 3
    private int getEnergy() {
        if (energy >= 209)
            return 5;
        else if (energy >= 50)
            return 1;
        return 3;
    }

    private int findEmptyDirection() {
        int xt, yt;
        for (int i = 0; i < 8; i++) {
            xt = xFromVektorR(i);
            yt = yFromVektorR(i);
            if ((yt >= 0) && yt < Environment.currentEnvironment.height) {
                if (Environment.currentEnvironment.matrix[xt][yt] == null) return i;
            }
        }
        return 8; //если свободного направления нет, ыффыафыв
    }

    //******************************************************************************************************************
//рождение нового бота
    private void botDublicate() {
        if (energy <= 0) {
            isAlive = false;
            killBot(this);
            return;
        }

        int emptyDir = findEmptyDirection();
        if (emptyDir == 8) {
            energy = 0;
            isAlive = false;
            return;
        }

        Bot child = new Bot();

        int xt = xFromVektorR(emptyDir);
        int yt = yFromVektorR(emptyDir);
        System.arraycopy(genome, 0, child.genome, 0, 63);
        child.PC = 0;
        child.x = xt;
        child.y = yt;
        child.energy = energy / 2;
        energy /= 2;

        child.redColor = redColor;
        child.greenColor = greenColor;
        child.blueColor = blueColor;
        child.familyColor = familyColor;

        double mutatechance = Math.random();
        if (mutatechance < 0.20) {
            int mutationAdr = (int) Math.floor(Math.random() * 64);
            byte mutation = (byte) Math.floor(Math.random() * 64);
            child.genome[mutationAdr] = mutation;
            child.familyColor = getNewColor(familyColor);
        }
        child.sightDir = Direction.S;
        //цепочка выполнения команд ботами
        child.nextBot = this.nextBot;
        this.nextBot = child;
        child.previousBot = this;

        Environment.currentEnvironment.matrix[xt][yt] = child;
        Environment.currentEnvironment.population++;

    }

//******************************************************************************************************************
    //    группа получения цветов в формате ARGB и получения определенных каналов
//    нет, альфа канал делать не буду

    private int randomColorChannelChange(int x) {
        double coinFlip = Math.random();
        if (coinFlip > 0.49) {
            x += (int) Math.floor(Math.random() * 20);
            if (x > 255) x = 255;
        } else {
            x -= (int) Math.floor(Math.random() * 20 - 20);
            if (x < 0) x = 0;
        }
        return x;
    }

    private int getNewColor(int oldColor) {
        int R, G, B;
        R = getRed(oldColor);
        G = getGreen(oldColor);
        B = getBlue(oldColor);
        R = randomColorChannelChange(R);
        G = randomColorChannelChange(G);
        B = randomColorChannelChange(B);
        return getColorInInt(R, G, B);
    }

    private int getColorInInt(int R, int G, int B) {
        int A = 255;
        int result = (A << 24) | (R << 16) | (G << 8) | B;
        return result;
    }

    private int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    private int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    private int getBlue(int color) {
        return color & 0xFF;
    }

    //******************************************************************************************************************
    // чтобы узнать родственники ли боты их фамильный цвет представляем как
    // координаты точки в 3-х мерном пространстве
    // и находим дистанцию между ними
    // TODO: придумать минимальную дистанцию
    private boolean isRelative(Bot target) {
        int redDelta = getRed(familyColor) - getRed(target.familyColor);
        int greenDelta = getGreen(familyColor) - getGreen(target.familyColor);
        int blueDelta = getBlue(familyColor) - getBlue(target.familyColor);
        double distance = Math.sqrt(redDelta * redDelta + greenDelta * greenDelta + blueDelta * blueDelta);
        return (distance < 50); //честно скажу магическое число, которое я прикинул на глаз,
        // TODO потом проверь насколько жизнеспособно это
    }

    //*******************************************************************************************************************
//группа изменения цветов, в зависимости от питания
    //    увеличиваем просто параметр цвета на определенную величину
//    и чуть меньше уменьшаем остальные цвета
    private void goGreen(int value) {
        greenColor += value;
        if (greenColor > 255) greenColor = 255;
        value = value / 2;
        blueColor -= value;
        if (blueColor < 0) blueColor = 0;
        redColor -= value;
        if (redColor < 0) redColor = 0;
    }

    private void goBlue(int value) {
        blueColor += value;
        if (blueColor > 255) blueColor = 255;
        value = value / 2;
        greenColor -= value;
        if (greenColor < 0) greenColor = 0;
        redColor -= value;
        if (redColor < 0) redColor = 0;
    }

    private void goRed(int value) {
        redColor += value;
        if (redColor > 255) redColor = 255;
        value = value / 2;
        greenColor -= value;
        if (greenColor < 0) greenColor = 0;
        blueColor -= value;
        if (blueColor < 0) blueColor = 0;
    }

}
