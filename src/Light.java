import java.util.ArrayList;

public class Light {
	public ArrayList<Point> touchedLeaves = new ArrayList<Point>();
	public double strength = 10;
	public Point pos;
	public Light(Point pos) {
		this.pos = pos;
	}
}
