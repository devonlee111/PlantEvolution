public class Leaf implements Comparable<Leaf> {
	private Plant plant;
	private Point pos;
	public final double SIZE = 2.5;
	
	public Leaf(Plant plant, Point pos) {
		this.plant = plant;
		this.pos = pos;
	}
	
	public Plant getPlant() {
		return plant;
	}
	
	public Point getPos() {
		return pos;
	}

	@Override
	public int compareTo(Leaf l) {
		if (l.pos.getY() < pos.getY()) {
			return -1;
		}
		if (l.pos.getY() > pos.getY()) {
			return 1;
		}
		return 0;
	}
}