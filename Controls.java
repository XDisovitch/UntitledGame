import javax.swing.*;
import java.awt.*;

public class Controls extends JPanel {
    public Controls(Engine game) {
        setLayout(new FlowLayout());

        // Базовая юнит - перезарядка 2 секунды
        add(new Button("Spawn BaseUnit", new BaseUnit(), () -> {
            BaseUnit baseUnit = new BaseUnit();
            baseUnit.setFraction(0);
            baseUnit.setX(100);
            baseUnit.setY(game.getScreenHeight() - 150);
            baseUnit.setSize(90);
            baseUnit.setEngine(game);
            game.spawnObject(baseUnit);
        }, 2));  // <- 2 секунды

        // Лучник - перезарядка 3 секунды
        add(new Button("Spawn Archer", new UnitArcher(), () -> {
            UnitArcher unitArcher = new UnitArcher();
            unitArcher.setFraction(0);
            unitArcher.setX(0);
            unitArcher.setAttackRange(250);
            unitArcher.setSize(50);
            unitArcher.setY(game.getScreenHeight() - 250);
            unitArcher.setSpeed(1);
            unitArcher.setDirection(1);
            game.spawnObject(unitArcher);
        }, 3));  // <- 3 секунды

        // Танк (Динозавр) - перезарядка 5 секунд (самый сильный юнит)
        add(new Button("Spawn Tank", new UnitDinoRider(), () -> {
            UnitDinoRider unitDinoRider = new UnitDinoRider();
            unitDinoRider.setFraction(0);
            unitDinoRider.setX(0);
            unitDinoRider.setY(game.getScreenHeight() - 250);
            unitDinoRider.setSize(100);
            unitDinoRider.setSpeed(1);
            unitDinoRider.setDirection(1);
            game.spawnObject(unitDinoRider);
        }, 5));  // <- 5 секунд
    }
}
