import java.awt.*;
import java.util.List;

/**
 * Лучник: спавнится, идёт к башне (посередине карты),
 * останавливается и атакует, пока башня не разрушится.
 *
 * by Bebron28 & AmericanCoolBoyUSA777
 */
public class UnitArcher extends GameObject {

    private GameObject currentTarget;

    // Добавляем переменную для отслеживания времени следующей доступной атаки
    private float nextAttackTime = 0f;

    // настройки лучника
    private static final float ARCHER_SPEED = 5f;
    private static final float ARCHER_ATTACK_RANGE = 300f;
    private static final float ARCHER_ATTACK_COOLDOWN = 1.5f;  // 1.5 секунды перезарядки
    private static final float ARROW_SPEED = 600f;

    public UnitArcher() {
        this.fraction = 2;
    }

    public UnitArcher(int id, float x, float y, int size, float speed) {
        super(id, x, y, size, ARCHER_SPEED, new Color(70, 130, 180));
        attackRange = ARCHER_ATTACK_RANGE;
        attackCooldown = ARCHER_ATTACK_COOLDOWN;
        lastAttackTime = -5f;  // Ставим большое отрицательное значение, чтобы можно было сразу стрелять
        attackDamage = 25;
        health = 100;
        fraction = 2;
        nextAttackTime = 0f;  // Инициализируем
    }

    @Override
    public void update(float deltaTime) {
        if (!isAlive) return;

        // выбор цели, если её нет или она мертва
        if (currentTarget == null || !currentTarget.isAlive()) {
            currentTarget = findTower();
        }

        if (currentTarget != null) {
            float dist = distanceTo(currentTarget);

            if (dist > attackRange) {
                // движение к башне
                moveTowards(currentTarget, deltaTime);
            } else {
                // атака в радиусе поражения
                float currentTime = engine.getGameTime();
                if (currentTime >= nextAttackTime) {
                    shootAt(currentTarget);
                    nextAttackTime = currentTime + ARCHER_ATTACK_COOLDOWN;  // Устанавливаем время следующей атаки
                }
            }
        }
    }

    /**
     * Поиск башни на карте.
     */
    private GameObject findTower() {
        List<GameObject> objects = engine.getObjects();
        for (GameObject obj : objects) {
            if (obj == null || !obj.isAlive()) continue;
            if (obj.getFraction() == fraction) continue;

            // проверка по имени класса (Tower, Tower67 и т.д.)
            String className = obj.getClass().getSimpleName();
            if (className.contains("Tower")) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Выстрел по цели.
     */
    private void shootAt(GameObject target) {
        float angle = Arrow.calculateArrowAngle(x, y, target.getX(), target.getY(), ARROW_SPEED);
        Arrow arrow = new Arrow(x, y, angle, ARROW_SPEED);
        arrow.setAttackDamage(attackDamage);
        arrow.setFraction(fraction);
        engine.spawnObject(arrow);
    }

    @Override
    public void draw(Graphics g) {
        float k = this.size / 100.0f;
        if (k <= 0) k = 1.0f;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // голова
        g2d.setColor(new Color(255, 218, 185));
        g2d.fillOval(Math.round(x), Math.round(y),
                Math.round(70 * k), Math.round(80 * k));

        // глаза
        g2d.setColor(Color.BLACK);
        g2d.fillOval(Math.round(x + 25 * k), Math.round(y + 30 * k),
                Math.round(10 * k), Math.round(12 * k));
        g2d.fillOval(Math.round(x + 50 * k), Math.round(y + 30 * k),
                Math.round(10 * k), Math.round(12 * k));

        // тело
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillOval(Math.round(x - 10 * k), Math.round(y + 80 * k),
                Math.round(90 * k), Math.round(140 * k));

        // лук (дуга)
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(4.0f * k));
        g2d.drawArc(Math.round(x + 30 * k), Math.round(y + 50 * k),
                Math.round(100 * k), Math.round(150 * k),
                270, 190);

        // тетива
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f * k));
        int bowCenterX = Math.round(x + 80 * k);
        int bowTopY = Math.round(y + 52 * k);
        int bowBottomY = Math.round(y + 200 * k);
        g2d.drawLine(bowCenterX, bowTopY, bowCenterX, bowBottomY);

        // колчан
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(Math.round(x - 30 * k), Math.round(y + 100 * k),
                Math.round(25 * k), Math.round(60 * k));
        g2d.fillOval(Math.round(x - 30 * k), Math.round(y + 95 * k),
                Math.round(25 * k), Math.round(20 * k));

        // стрелы в колчане
        g2d.setColor(Color.DARK_GRAY);
        for (int i = 0; i < 3; i++) {
            int yOffset = 110 + i * 15;
            g2d.drawLine(Math.round(x - 25 * k), Math.round(y + yOffset * k),
                    Math.round(x - 10 * k), Math.round(y + yOffset * k));
        }

        // полоска здоровья
        drawHealthBar(g2d, k);
    }

    private void drawHealthBar(Graphics2D g2d, float k) {
        int barWidth = 60;
        int barHeight = 8;
        int barX = Math.round(x + 5 * k);
        int barY = Math.round(y - 10 * k);

        // фон (красный)
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // текущее здоровье (зелёный)
        g2d.setColor(Color.GREEN);
        int healthPercent = (int) ((float) health / 100f * barWidth);
        healthPercent = Math.max(0, Math.min(barWidth, healthPercent));
        g2d.fillRect(barX, barY, healthPercent, barHeight);

        // рамка
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        this.x = x;
        this.y = y;
        draw(g);
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            isAlive = false;
        }
    }
}
