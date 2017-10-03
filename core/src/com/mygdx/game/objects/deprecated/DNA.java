package com.mygdx.game.objects.deprecated;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DNA {

	float data[];
	static int geneSize = 43;
	float fitness = 0;
	public int index;
	boolean hasFitness = false;
	
	public DNA(float[] data, int index) {
		this.data = data;
		this.index = index;
	}
	
	public static void createRandomPopulation(String fileName){
		try{
			File f = new File("training/" + fileName + ".json");
		if(!f.exists()){
			f.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		DNA[] dnas = new DNA[Population.popNum];
		
		for(int i = 0; i < dnas.length; i ++){
			dnas[i] = new DNA(i);
		}
		
		Population pop = new Population(dnas);
		
		 Gson gson = new GsonBuilder().setPrettyPrinting().create();
         bw.write(gson.toJson(pop));
         
         bw.close();
         
         System.out.println("Random Population generated");
         
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void print(){
		int cont = 0;
		for(float f : data){
			System.out.println("Gene " + cont + ": " + f);
			
			cont++;
		}
	}
	
	public boolean equals(DNA dna){
		for(int i = 0; i < data.length; i ++){
			if(dna.data[i] == data[i]){
				continue;
			}
			else{
				return false;
			}
		}
		
		return true;
	}
	
	public float[] crossover(DNA partner){
		
		float child[] = new float[geneSize];
		
		for(int i = 0; i < geneSize; i ++){
			child[i] = (Math.random() < 0.5f) ? data[i] : partner.data[i];
		}
		
		return child;
		
	}
	
	public boolean hasFitness(){
		return hasFitness;
	}
	
	public void setFitness(float fitness){
		this.fitness = fitness;
		hasFitness = true;
	}
	
	public void mutate(float mutationRate){
			if(Math.random() < mutationRate)data[0] = rand(-1, 1);
			if(Math.random() < mutationRate)data[1] = rand(-1, 1);
			if(Math.random() < mutationRate)data[2] = rand(-1, 1);
			if(Math.random() < mutationRate)data[3] = rand(-1, 1);
			if(Math.random() < mutationRate)data[4] = rand(-1, 1);
			if(Math.random() < mutationRate)data[5] = rand(-1, 1);
			if(Math.random() < mutationRate)data[6] = rand(-1, 1);
			if(Math.random() < mutationRate)data[7] = rand(-1, 1);
			if(Math.random() < mutationRate)data[8] = rand(-1, 1);
			if(Math.random() < mutationRate)data[9] = rand(-1, 0);
			if(Math.random() < mutationRate)data[10] = rand(-1, 0);
			if(Math.random() < mutationRate)data[11] = rand(-0.5f, 0.5f);
			if(Math.random() < mutationRate)data[12] = rand(0, 1);
			if(Math.random() < mutationRate)data[13] = rand(0, 1);
			if(Math.random() < mutationRate)data[14] = rand(0, 1);
			if(Math.random() < mutationRate)data[15] = rand(0, 1);
			if(Math.random() < mutationRate)data[16] = rand(0, 1);
			if(Math.random() < mutationRate)data[17] = rand(0, 1);
			if(Math.random() < mutationRate)data[18] = rand(-2, 0);
			if(Math.random() < mutationRate)data[19] = rand(-2, 0);
			if(Math.random() < mutationRate)data[20] = rand(-2, 0);
			if(Math.random() < mutationRate)data[21] = rand(-2, 0);
			if(Math.random() < mutationRate)data[22] = rand(-2, 0);
			if(Math.random() < mutationRate)data[23] = rand(-1, 0);
			if(Math.random() < mutationRate)data[24] = rand(-1, 0);
			if(Math.random() < mutationRate)data[25] = rand(-1, 0);
			if(Math.random() < mutationRate)data[26] = rand(0, 1);
			if(Math.random() < mutationRate)data[27] = rand(0, 1);
			if(Math.random() < mutationRate)data[28] = rand(0, 1);
			if(Math.random() < mutationRate)data[29] = rand(0, 1);
			if(Math.random() < mutationRate)data[30] = rand(0, 1);
			if(Math.random() < mutationRate)data[31] = rand(0, 1);
			if(Math.random() < mutationRate)data[32] = rand(0, 1);
			if(Math.random() < mutationRate)data[33] = rand(0, 1);
			if(Math.random() < mutationRate)data[34] = rand(0, 1);
			if(Math.random() < mutationRate)data[35] = rand(0, 1);
			if(Math.random() < mutationRate)data[36] = rand(0, 1);
			if(Math.random() < mutationRate)data[37] = rand(0, 1);
			if(Math.random() < mutationRate)data[38] = rand(0, 1);
			if(Math.random() < mutationRate)data[39] = rand(0, 1);
			if(Math.random() < mutationRate)data[40] = rand(0, 1);
			if(Math.random() < mutationRate)data[41] = rand(0, 1);
			if(Math.random() < mutationRate)data[42] = rand(0, 1);
	}
	
	public float rand(float start, float end){
		return (float) (Math.random() * (end-start) + start);
	}
	
	public DNA(int index){
		this.index = index;
		data = new float[geneSize];

		data[0] = rand(-1, 1);
		data[1] = rand(-1, 1);
		data[2] = rand(-1, 1);
		data[3] = rand(-1, 1);
		data[4] = rand(-1, 1);
		data[5] = rand(-1, 1);
		data[6] = rand(-1, 1);
		data[7] = rand(-1, 1);
		data[8] = rand(-1, 1);
		data[9] = rand(-1, 0);
		data[10] = rand(-1, 0);
		data[11] = rand(-0.5f, 0.5f);
		data[12] = rand(0, 1);
		data[13] = rand(0, 1);
		data[14] = rand(0, 1);
		data[15] = rand(0, 1);
		data[16] = rand(0, 1);
		data[17] = rand(0, 1);
		data[18] = rand(-2, 1);
		data[19] = rand(-2, 1);
		data[20] = rand(-2, 1);
		data[21] = rand(-2, 1);
		data[22] = rand(-2, 1);
		data[23] = rand(-1, 0);
		data[24] = rand(-1, 0);
		data[25] = rand(-1, 0);
		data[26] = rand(0, 1);
		data[27] = rand(0, 1);
		data[28] = rand(0, 1);
		data[29] = rand(0, 1);
		data[30] = rand(0, 1);
		data[31] = rand(0, 1);
		data[32] = rand(0, 1);
		data[33] = rand(0, 1);
		data[34] = rand(0, 1);
		data[35] = rand(0, 1);
		data[36] = rand(0, 1);
		data[37] = rand(0, 1);
		data[38] = rand(0, 1);
		data[39] = rand(0, 1);
		data[40] = rand(0, 1);
		data[41] = rand(0, 1);
		data[42] = rand(0, 1);

	}
	
	public float getMovementBazooka(){
		return data[0];
	}
	public float getMovementDoublePistol(){
		return data[1];
	}
	public float getMovementFlamethrower(){
		return data[2];
	}
	public float getMovementLaser(){
		return data[3];
	}
	public float getMovementMinigun(){
		return data[4];
	}
	public float getMovementMolotov(){
		return data[5];
	}
	public float getMovementMP5(){
		return data[6];
	}
	public float getMovementPistol(){
		return data[7];
	}
	public float getMovementShotgun(){
		return data[8];
	}
	public float getMovementTurret(){
		return data[9];
	}
	public float getMovementBarrier(){
		return data[10];
	}
	public float getMovementBlock(){
		return data[11];
	}
	public float getMovementLifeItem(){
		return data[12];
	}
	public float getMovementAttackItem(){
		return data[13];
	}
	public float getMovementDeffenseItem(){
		return data[14];
	}
	public float getMovementSpeedItem(){
		return data[15];
	}
	public float getMovementTurretItem(){
		return data[16];
	}
	public float getMovementBarrierItem(){
		return data[17];
	}
	public float getMovementPlayerWithDeffense(){
		return data[18];
	}
	public float getMovementPlayerWithAttack(){
		return data[19];
	}
	public float getMovementPlayerWithSpeed(){
		return data[20];
	}
	public float getMovementPlayerWithTurret(){
		return data[21];
	}
	public float getMovementPlayerWithBarrier(){
		return data[22];
	}
	public float getMovementBullet(){
		return data[23];
	}
	public float getMovementFlame(){
		return data[24];
	}
	public float getMovementMolotovPool(){
		return data[25];
	}
	public float getAimBazooka(){
		return data[26];
	}
	public float getAimDoublePistol(){
		return data[27];
	}
	public float getAimFlamethrower(){
		return data[28];
	}
	public float getAimLaser(){
		return data[29];
	}
	public float getAimMinigun(){
		return data[30];
	}
	public float getAimMolotov(){
		return data[31];
	}
	public float getAimMP5(){
		return data[32];
	}
	public float getAimPistol(){
		return data[33];
	}
	public float getAimShotgun(){
		return data[34];
	}
	public float getAimTurret(){
		return data[35];
	}
	public float getAimBarrier(){
		return data[36];
	}
	public float getAimBlock(){
		return data[37];
	}
	public float getAimPlayerWithDeffense(){
		return data[38];
	}
	public float getAimPlayerWithAttack(){
		return data[39];
	}
	public float getAimPlayerWithSpeed(){
		return data[40];
	}
	public float getAimPlayerWithTurret(){
		return data[41];
	}
	public float getAimPlayerWithBarrier(){
		return data[42];
	}

	public float getFitness() {
		return fitness;
	}

}
