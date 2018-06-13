public class IntervalNode {
	public Interval i;
	public IntervalNode left;
	public IntervalNode right;
	public double max;
	
	public IntervalNode(Interval i) {
		this.i = i;
	}
}