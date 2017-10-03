package com.mygdx.game.objects.deprecated;

public class Population {

	DNA dnas[];
	
	public static float mutationRate = 0.01f;
	public static int popNum = 200;
	float maxFitness = 0;
	private int generation = 0;
	
	float highestFitness = 0;
	float averageFitness = 0;
	float lowestFitness = 0;
	
	float lastHighFitness = 0.001f;
	float lastAverageFitness = 0.001f;
	float lastLowestFitness = 0.001f;

	public Population(DNA[] dnas) {
		this.dnas = dnas;
	}
	
	public void print(){
		for(DNA dna : dnas){
			System.out.println("Index: " + dna.index);
			System.out.println("\tHas fitness: " + dna.hasFitness);
			System.out.println("\tFitness: " + dna.fitness);
			//dna.print();
		}
	}

	public void setFitness(int index, float score) {
		if(index != -1){
			dnas[index].setFitness(score);
		}
	}
	
	public DNA getFirstAvailable(int start){
		try{
		int cont = 0;
		
		while(dnas[cont].hasFitness){
			cont++;
		}
		
		if(cont == dnas.length) return null;

		return dnas[cont + start];
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public float getAverageFitness(){
		if(averageFitness == 0){
			averageFitness = getMaxFitness()/(float)popNum;
		}
		return averageFitness;
	}
	
	public float getHigherFitness(){
		if(highestFitness == 0){
			float higher = 0;
			for(int i = 0; i < popNum; i ++){
				if(dnas[i].fitness > higher){
					higher = dnas[i].fitness;
				}
			}
			highestFitness = higher;
		}
		return highestFitness;
	}
	
	public float getMaxFitness(){
		maxFitness = 0;
		for(DNA dna : dnas){
			maxFitness += dna.fitness;
		}
		return maxFitness;
	}
	
	public float getLowestFitness(){
		if(lowestFitness == 0){
			float lower = 100;
			for(int i = 0; i < popNum; i ++){
				if(dnas[i].fitness < lower){
					lower = dnas[i].fitness;
				}
			}
			lowestFitness = lower;
		}
		return lowestFitness;
	}
	
	public void calculateMetrics(){
		getHigherFitness();
		getMaxFitness();
		getLowestFitness();
		getAverageFitness();
	}
	
	public void resetMetrics(){
		highestFitness = 0;
		maxFitness = 0;
		averageFitness = 0;
		lowestFitness = 0;
	}

	public void generateNew() {
		
		lastHighFitness = getHigherFitness();
		lastLowestFitness = getLowestFitness();
		lastAverageFitness = getAverageFitness();
		
		generation ++;
		DNA[] newGen = new DNA[dnas.length];
		
		for(int i = 0; i < newGen.length; i ++){
			
			DNA parentA = getParent();
			DNA parentB = getParent();
			
			DNA child = new DNA(parentA.crossover(parentB), i);
			child.mutate(mutationRate);
			
			newGen[i] = child;
			
		}
		
		dnas = newGen;

		resetMetrics();

	}
	
	public DNA getParent(){
		
		//to be sure maxFitness is correct
		maxFitness = 0;
		for(DNA dna : dnas){
			maxFitness += dna.fitness;
		}
		
		int index = 0;
		int random = (int) (Math.random() * maxFitness);
		float r = random;
				
		while(r >= 0){
			r -= dnas[index].fitness;
			index ++;
		}
		
		index--;
		
		return dnas[index];
		
		
	}

	public int getGeneration() {
		return generation;
	}

	public float getLastHigherFitness() {
		return lastHighFitness;
	}
	public float getLastLowestFitness() {
		return lastLowestFitness;
	}
	public float getLastAverageFitness() {
		return lastAverageFitness;
	}


}
