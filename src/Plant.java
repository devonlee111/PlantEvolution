import java.util.ArrayList;

public class Plant implements Comparable<Plant> {
	// Specific genetic sequences that signify the start of a valid gene
	// Trunks specify the main body of the plant with branches and trunks starting at the end of each trunk segment.
	private String trunkCode = "AT";
	
	// Branches specify branches of the plant that can't split into more branches and have leaves at the end of them.
	private String branchCode = "CG";
	
	// The nucleotides used in a plant's genome with the index of each nucleotide being the value of the nucleotide in quaternary.
	private String nucleotides = "ATCG";
	private String genome = "";
	
	// The x position on the ground where the plant should start from.
	private double groundPos;
	private double fitness;
	public final double LEAFSIZE = 2.5;
	private ArrayList<Point> leafPos = new ArrayList<Point>();
	
	// Create a new plant with a basic genome.
	public Plant(double groundPos) {
		this.groundPos = groundPos;
		for (int i = 0; i < 152; i++) {
			genome += 'A';
		}
		genome += branchCode;
		genome += "GGAAAA";
	}
	
	// Create a new plant from the genes of 2 parent plants
	public Plant(double groundPos, Plant p1, Plant p2) {
		// Randomly choose genes from each parent to put into the offspring's genes.
		this.groundPos = groundPos;
		for (int i = 0; i < 20; i++) {
			int geneIndex = 8 * i;
			int parent = (int)(Math.random() * 100) + 1;
			if (parent <= 50) {
				genome += p1.genome.substring(geneIndex, geneIndex + 8);
			}
			else {
				genome += p2.genome.substring(geneIndex, geneIndex + 8);
			}
		}
		// Give each nucleotide in the plant a very small chance to mutate into a different nucleotide.
		char[] temp = genome.toCharArray();
		for (int i = 0; i < genome.length(); i++) {
			int mutationChance = (int)(Math.random() * 100);
			if (mutationChance == 0) {
				char newNucleotide;
				do {
					newNucleotide = nucleotides.charAt((int)(Math.random() * 4));
				} while(newNucleotide == temp[i]);
				temp[i] = newNucleotide;
			}
		}
		genome = new String(temp);
	}
	
	// Comparison for the sorting of plants based on their fitness.
	@Override
	public int compareTo(Plant p) {
		if (p.fitness < fitness) {
			return -1;
		}
		else if (p.fitness == fitness) {
			return 0;
		}
		return 1;
	}
	
	public void setGenome(String genome) {
		this.genome = genome;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public void addFitness(double fitnessIncrease) {
		fitness += fitnessIncrease;
	}
	
	public double getFitness() {
		return fitness;
	}

	public void setGroundPos(double groundPos) {
		this.groundPos = groundPos;
	}
	
	// Return a point with x being the groundPos of the plant and y being 0
	public Point groundPos() {
		return new Point(groundPos, 0);
	}
	
	// Check whether the gene at the given index is valid (-1) a trunk (0) or a branch (1)
	public int geneType(int geneIndex) {
		if(genome.substring(geneIndex, geneIndex + 2).equals(trunkCode)) {
			return 0;
		}
		if(genome.substring(geneIndex, geneIndex + 2).equals(branchCode)) {
			return 1;
		}
		return -1;
	}
	
	public void addLeaf(Point leaf) {
		//System.out.println(leaf.x + ", " + leaf.y);
		if (!hasLeaf(leaf)) {
			leafPos.add(leaf);
		}
	}
	
	public boolean hasLeaf(Point leaf) {
		for (Point existingLeaf : leafPos) {
			if (existingLeaf.x == leaf.x && existingLeaf.y == leaf.y) {
				return true;
			}
		}
		return false;
	}
	
	public boolean pointInLeaf(Point p) {
		for (Point leaf : leafPos) {
			MyCircle temp = new MyCircle(leaf.x, leaf.y, LEAFSIZE);
			if (temp.contains(p)) {
				return true;
			}
		}
		return false;
	}
	
	// Return the angle of the branch/trunk at the gene starting at the given index.
	public double branchAngle(int geneIndex) {
		double angle = 0.0;
		char[] nucleotides = genome.substring(geneIndex + 4, geneIndex + 8).toCharArray();
		// Get the string of nucleotides that represent the angle and convert it to its quaternary representation.
		String temp = "";
		for (int i = 0; i < 4; i++) {
			temp += Integer.toString(this.nucleotides.indexOf(nucleotides[i]));
		}
		// Parse the quaternary string to an integer.
		angle = Integer.parseInt(temp, 4);
		// Make the angle negative if it goes above half the maximum angle.
		// This does mean -0 angle is possible giving 0 a slightly higher chance of being represented
		if (angle > 127) {
			angle = 128 - angle;
		}
		return angle;
	}
	
	// Return the angle of the branch/trunk at the gene starting at the given index.
	public double branchLength(int geneIndex) {
		double length = 0.0;
		char[] nucleotides = genome.substring(geneIndex + 2, geneIndex + 4).toCharArray();
		// Get the string of nucleotides that represent the angle and convert it to its quaternary representation.
		String temp = "";
		for (int i = 0; i < 2; i++) {
			temp += Integer.toString(this.nucleotides.indexOf(nucleotides[i]));
		}
		// Parse the quaternary string to an integer.
		length = Integer.parseInt(temp, 4);
		return length + 1;
	}
	
	public String genome() {
		return genome;
	}
}