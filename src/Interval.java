class Interval implements Comparable<Interval> {

    public double start;
    public double end;
    
    public Interval(double start, double end) {

        this.start = start;
        this.end = end;
    }
	
    @Override
    public int compareTo(Interval i) {
        if (this.start < i.start) {
            return -1;
        }
        else if (this.start == i.start) {
            return this.end <= i.end ? -1 : 1;
        }
        else {
            return 1;
        }
    }
}