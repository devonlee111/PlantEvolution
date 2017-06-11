import java.util.ArrayList;

public class Plant implements Comparable<Plant> {
	private String trunkCode = "AGTT";
	private String branchCode = "GCGA";
	private String nucleotides = "ATCG";
	private ArrayList<Point> leaves = new ArrayList<Point>();
	private String genome = "";
	private double groundPos;
	private double fitness;
	
	// Create a new plant with a basic genome.
	public Plant(double groundPos) {
		this.groundPos = groundPos;
		genome += branchCode;
		genome += "GGAAAA";
		for (int i = 0; i < 190; i++) {
			genome += 'A';
		}
		System.out.println(genome);
	}
	
	// Create a new plant from the genes of 2 parent plants
	public Plant(double groundPos, Plant p1, Plant p2) {
		// Randomly choose genes from each parent to put into the offspring's genes.
		this.groundPos = groundPos;
		for (int i = 0; i < 20; i++) {
			int geneIndex = 10 * i;
			int parent = (int)(Math.random() * 100) + 1;
			if (parent <= 50) {
				genome += p1.genome.substring(geneIndex, geneIndex + 10);
			}
			else {
				genome += p2.genome.substring(geneIndex, geneIndex + 10);
			}
		}
		// Give each nucleotide in the plant a very small chance to mutate into a different nucleotide.
		char[] temp = genome.toCharArray();
		for (int i = 0; i < genome.length(); i++) {
			int mutationChance = (int)(Math.random() * 1000);
			if (mutationChance == 0) {
				char newNucleotide;
				do {
					newNucleotide = nucleotides.charAt((int)(Math.random() * 4));
				} while(newNucleotide != temp[i]);
				temp[i] = newNucleotide;
			}
		}
		genome = new String(temp);
		System.out.println(genome);
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
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public Point groundPos() {
		return new Point(groundPos, 0);
	}
	
	public int geneType(int geneIndex) {
		if(genome.substring(geneIndex, geneIndex + 4).equals(branchCode)) {
			return 1;
		}
		if(genome.substring(geneIndex, geneIndex + 4).equals(trunkCode)) {
			return 0;
		}
		return -1;
	}
	
	public double branchAngle(int geneIndex) {
		double angle = 0.0;
		char[] nucleotides = genome.substring(geneIndex + 6, geneIndex + 10).toCharArray();
		String temp = "";
		for (int i = 0; i < 4; i++) {
			temp += Integer.toString(this.nucleotides.indexOf(nucleotides[i]));
		}
		angle = Integer.parseInt(temp, 4);
		System.out.println(angle);
		if (angle > 127) {
			angle = 128 - angle;
		}
		return angle;
	}
	
	public double branchLength(int geneIndex) {
		double length = 0.0;
		char[] nucleotides = genome.substring(geneIndex + 4, geneIndex + 6).toCharArray();
		String temp = "";
		for (int i = 0; i < 2; i++) {
			temp += Integer.toString(this.nucleotides.indexOf(nucleotides[i]));
		}
		length = Integer.parseInt(temp, 4);
		return length + 1;
	}

	// Process the genome to get the structure of the plant
	public ArrayList<Point> processPlant() {
		return null;
	}
	
	public ArrayList<Point> getLeafPos() {
		return leaves;
	}
}