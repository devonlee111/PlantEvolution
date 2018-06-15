public class Plant implements Comparable<Plant> {
	// Specific genetic sequences that signify the start of a valid gene
	private String trunkCode = "AT";		// Trunks specify the main body of the plant with no leaves
	private String branchCode = "CG";		// Branches specify branches only have leaves at the end.
	private String nucleotides = "ATCG";	// The different nucleotides used in a plant's genome.
	private String genome = "";
	private double groundPos;
	public int index;
	private double fitness;
	private boolean dead = false;
	
	// The weight dictates how likely the plant is to survive to the next generation
	private double weight;
	
	// Create a new plant with a basic genome.
	public Plant(double groundPos) {
		this.groundPos = groundPos;
		for (int i = 0; i < 7992; i++) {
			genome += 'A';//nucleotides.charAt((int)(Math.random() * 4));
		}
		int startingGenePos = 0;
		String startingGene = branchCode;
		startingGene += "GGAAAAAA";
		genome = new StringBuilder(genome).insert(startingGenePos, startingGene).toString();
	}
	
	// Create a new plant from the genes of 2 parent plants
	public Plant(double groundPos, Plant p1, Plant p2) {
		// Randomly choose genes from each parent to put into the offspring's genes.
		this.groundPos = groundPos;
		for (int i = 0; i < 1000; i++) {
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
			int mutationChance = (int)(Math.random() * 100); // A 1/1000 chance of mutating each nucleotide.
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
	
	// Create a plant with only one parent, effectively cloning the parent with some mutations.
	public Plant(double groundPos, Plant parent) {
		this.groundPos = groundPos;
		this.genome = parent.genome();
		char[] temp = genome.toCharArray();
		for (int i = 0; i < genome.length(); i++) {
			int mutationChance = (int)(Math.random() * 50); // A 1/1000 chance of mutating each nucleotide.
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
	
	// Set the plant's genome to a specific genome.
	public void setGenome(String genome) {
		this.genome = genome;
	}
	
	// Set the current fitness to a specific amount.
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	// Increase the current fitness by a specific amount.
	public void addFitness(double fitnessIncrease) {
		fitness += fitnessIncrease;
	}
	
	// Return the current fitness.
	public double getFitness() {
		return fitness;
	}
	
	// Reset the fitness to only factor in the cost of the leaves.
	public void resetFitness() {
		fitness = 0;
	}

	public double weight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	// Return the position on the ground where the plant grows from.
	public void setGroundPos(double groundPos) {
		this.groundPos = groundPos;
	}
	
	// Return a point with x being the groundPos of the plant and y being 0
	public double groundPos() {
		return groundPos;
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
	
	// Return this plant's genome.
	public String genome() {
		return genome;
	}
	
	// Calculate the genetic difference between this plant's genome and a given genome.
	// Every differing nucleotide counts as a difference.
	public int geneticDifference(String otherGenome) {
		int difference = 0;
		for (int i = 0; i < genome.length(); i++) {
			if (genome.charAt(i) != otherGenome.charAt(i)) {
				difference++;
			}
		}
		return difference;
	}
	
	// Check if another plant is the same species as this one.
	// Plants of the same species must have a genetic difference of < 5%.
	public boolean sameSpecies(Plant other) {
		if (geneticDifference(other.genome()) < genome.length() * 0.05) {
			return true;
		}
		return false;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void kill() {
		dead = true;
	}
}