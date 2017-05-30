public class Plant implements Comparable<Plant>{
	// Genome is represented by a 43 bit string.
	// First gene from bits 0 - 2 represents stem length
	// Second gene from bits 3 - 6 represents leaf size
	// Third gene from bits 7 - 13 represents angle of new stems
	// Fourth gene from bits 14 - 42 represents a array implemented heap for structure of plant
	private String genome = "";
	public double fitness;
	
	// Create a new random plant
	public Plant() {
		for (int i = 0; i < 43; i++) {
			genome += Integer.toString((int)(Math.random() * 2));
		}
		fitness = Math.random();
	}
	
	public Plant(String genome) {
		this.genome = genome;
	}
	
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
	
	// Produce a new plant by combining genomes and mutating
	public Plant reproduce(Plant mate) {
		String newGenome = "";
		String genome2 = mate.genome;
		String gene11, gene12, gene21, gene22, gene31, gene32, gene41, gene42;
		gene11 = genome.substring(0, 3);
		gene21 = genome.substring(3, 7);
		gene31 = genome.substring(7, 14);
		gene41 = genome.substring(14);
		
		gene12 = mate.genome.substring(0, 3);
		gene22 = mate.genome.substring(3, 7);
		gene32 = mate.genome.substring(7, 14);
		gene42 = mate.genome.substring(14);
		
		newGenome += combineGenes(gene11, gene12);
		newGenome += combineGenes(gene21, gene22);
		newGenome += combineGenes(gene31, gene32);
		newGenome += combineGenes(gene41, gene42);
		char[] genomeArr = newGenome.toCharArray();
		for (int i = 0; i <  43; i++) {
			if ((int)(Math.random() * 101) == 0) {
				if (genomeArr[i] == '1') {
					genomeArr[i] = '0';
				}
				else {
					genomeArr[i] = '1';
				}
			}
		}
		newGenome = new String(genomeArr);
		Plant offspring = new Plant(newGenome);
		return offspring;
	}
	
	public String combineGenes(String gene1, String gene2) {
		char[] g1 = gene1.toCharArray();
		char[] g2 = gene2.toCharArray();
		int geneLength = gene1.length();
		int numSwap = geneLength/2;
		int startPos = (int)(Math.random() * ((geneLength - numSwap) + 1));
		int endPos = startPos + (numSwap);
		for (int i = startPos; i < endPos; i++) {
			g1[i] = g2[i];
		}
		String newGene = new String(g1);
		return newGene;
	}
	
	public int stemLength() {
		return Integer.parseInt(genome.substring(0, 3), 2);
	}
	
	public int leafSize() {
		return Integer.parseInt(genome.substring(3,  7), 2);
	}
	
	public int stemAngle() {
		return Integer.parseInt(genome.substring(7, 14), 2);
	}
}