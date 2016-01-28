import java.awt.Graphics2D;
 
public interface GameState {
    public void update();
    public void paint(Graphics2D g2d);
    public String get_name();
}