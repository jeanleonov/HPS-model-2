package point.components;


public class IndividualsGroup implements Comparable<IndividualsGroup> {
	
	private String genotype;
	private int age;
	
	public IndividualsGroup(String genotype, int age) {
		this.genotype = genotype;
		this.age = age;
	}

	public String getGenotype() {
		return genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	
	@Override
	public int hashCode() {
		return genotype.hashCode()+age;
	}
	
	@Override
	public boolean equals(Object obj) {
		IndividualsGroup other = (IndividualsGroup) obj;
		return genotype.equals(other.genotype) && age==other.age;
	}

	@Override
	public int compareTo(IndividualsGroup other) {
		int genotypesComparation = genotype.compareTo(other.genotype);
		return genotypesComparation==0? (age-other.age) : genotypesComparation;
	}
}
