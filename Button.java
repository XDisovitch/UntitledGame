import javax.swing.*;
import java.awt.*;

public class Button extends JButton {
    private boolean isOnCooldown = false;
    private Timer cooldownTimer;
    private long cooldownDuration;
    private long cooldownStartTime;
    private Event originalEvent;
    private float progress = 0f;
    private long cooldownSeconds = 0;

    public Button(String text, Icon icon, Event event) {
        this(text, icon, event, 0);
    }

    public Button(String text, Icon icon, Event event, long cooldownSeconds) {
        super(text, icon);
        this.originalEvent = event;
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownDuration = cooldownSeconds * 1000;

        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.TOP);
        setHorizontalAlignment(SwingConstants.CENTER);
        setMargin(new Insets(5, 5, 5, 5));
        
        // Настройки для кастомной отрисовки
        setContentAreaFilled(false);
        setOpaque(false);
        setBorderPainted(false);

        addActionListener(e -> {
            if (cooldownSeconds <= 0) {
                if (originalEvent != null) originalEvent.action();
            } else {
                if (!isOnCooldown && originalEvent != null) {
                    originalEvent.action();
                    startCooldown();
                }
            }
        });
    }

    private void startCooldown() {
        isOnCooldown = true;
        cooldownStartTime = System.currentTimeMillis();
        setEnabled(false);
        progress = 0f;
        repaint();

        cooldownTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - cooldownStartTime;
            if (elapsed >= cooldownDuration) {
                cooldownTimer.stop();
                isOnCooldown = false;
                setEnabled(true);
                repaint();
            } else {
                progress = (float) elapsed / cooldownDuration;
                repaint();
            }
        });
        cooldownTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Фон кнопки
        if (isOnCooldown) {
            g2d.setColor(new Color(80, 80, 100));
        } else {
            g2d.setColor(new Color(70, 130, 200));
        }
        g2d.fillRoundRect(0, 0, w, h, 15, 15);
        
        // Рисуем иконку
        Icon icon = getIcon();
        if (icon != null) {
            int iconW = icon.getIconWidth();
            int iconH = icon.getIconHeight();
            int iconX = (w - iconW) / 2;
            int iconY = 10;
            icon.paintIcon(this, g2d, iconX, iconY);
        }
        
        // Рисуем текст
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        String text = getText();
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (w - fm.stringWidth(text)) / 2;
        int textY = h - 12;
        g2d.drawString(text, textX, textY);
        
        // ===== ГРАДИЕНТ ПЕРЕЗАРЯДКИ (сверху вниз) =====
        if (isOnCooldown) {
            // Затемнение всей кнопки
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRoundRect(0, 0, w, h, 15, 15);
            
            // Вычисляем высоту заполнения (от верха к низу)
            int fillHeight = (int)(h * (1 - progress));
            
            if (fillHeight > 0) {
                // Создаём градиент от светло-голубого к тёмно-синему
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(100, 200, 255, 180),  // верх - светлый
                    0, fillHeight, new Color(0, 100, 200, 180)  // низ - тёмный
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, w, fillHeight, 15, 15);
            }
            
            // Рисуем текст с таймером
            long remaining = (cooldownDuration - (System.currentTimeMillis() - cooldownStartTime)) / 1000;
            if (remaining < 0) remaining = 0;
            
            String timeText = "⏱ " + remaining + "s";
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.WHITE);
            fm = g2d.getFontMetrics();
            int tx = (w - fm.stringWidth(timeText)) / 2;
            int ty = h - 30;
            g2d.drawString(timeText, tx, ty);
        }
        
        // Рамка кнопки
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(0, 0, w - 1, h - 1, 15, 15);
        
        g2d.dispose();
    }
}
