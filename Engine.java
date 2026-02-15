import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {

    private int screenWidth = 800;
    private int screenHeight = 600;
    private final List<GameObject> objects = new ArrayList<>();
    private final Random random = new Random();

    public Engine() {
        objects.add(new GameObject(0, 0, 100, 50, 2, Color.RED));
        objects.add(new GameObject(1, 0, 200, 30, 4, Color.BLUE));
        objects.add(new GameObject(2, 0, 300, 60, 1, Color.GREEN));
    }

    public void update(float deltaTime) {
        synchronized (objects) {
            for (GameObject obj : objects) {
                obj.update();
            }
        }
    }

    public void draw(Graphics g) {
    }

    public void spawnObject() {
    }

    public void spawnObject(GameObject gameObject) {
    }

    public boolean collisionAABB(GameObject a, GameObject b) {
        return false;
    }

    public void spawnObjectPattern(List<GameObject> pattern, long delay) {
        Thread spawnThread = new Thread(new Runnable() { // создаёт новый поток
            @Override
            public void run() {
                for (int i = 0; i < pattern.size(); i++) {
                    GameObject elem = pattern.get(i);
                
                    // копия объекта создаётся
                    GameObject newObject = new GameObject(
                        elem.getId(),
                        elem.getX(),
                        elem.getY(),
                        elem.getSize(),
                        elem.getSpeed(),
                        elem.getColor()
                    );
                
                    // она добавляется в список
                    synchronized (objects) {
                        objects.add(newObject);
                        System.out.println("Объект " + newObject.getId() + " заспавнен");
                    }
                
                    // задержка (очередь)
                    if (i < pattern.size() - 1) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            System.out.println("Спавн прерван!");
                            break;
                        }
                    }
                }
                System.out.println("Общий спавн завершен");
            }
        });
        
        spawnThread.start(); // запуск потока
    }

    // supplier of Pythagoras
    public void moveTowards(GameObject attaker, GameObject target) {
    }

    public List<GameObject> getObjects() {
        return new ArrayList<>(objects);
    }

    public void clearObjects() {
        objects.clear();
    }


}
