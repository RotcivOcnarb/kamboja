package com.mygdx.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class Pathfinding implements Runnable{
	
	volatile int sx, sy, ex, ey;
	volatile int[][] map;
	
	volatile ArrayList<Vector2> path;
	boolean end = false;
	
	Thread thread;
	
	public Pathfinding() {
		path = new ArrayList<Vector2>();
		thread = new Thread(this);
		thread.start();
	}
	
	public ArrayList<Vector2> getPath(){
		return path;
	}
	
	public void end() {
		end = true;
	}
	
	public void run() {
		System.out.println("Thread start");
		while(!end) {
			if(map != null) {
				getPathBetween();
			}
		}
		
	}
	
	private Node getNeighboor(Node n, ArrayList<Node> all, int i, int j, int width, int height) {
		if(i == -1 && n.getX() == 0) {
			return null;
		}
		if(i == 1 && n.getX() == width - 1) {
			return null;
		}
		if(j == -1 && n.getY() == 0) {
			return null;
		}
		if(j == 1 && n.getY() == height - 1) {
			return null;
		}
		if(i == 0 && j == 0) {
			return null;
		}
		
		for(Node n2 : all) {
			if(n2.getX() == n.getX() + i && n2.getY() == n.getY() + j) {
				return n2;
			}
		}
		return null;
		
	}
	
	private Node getNode(ArrayList<Node> all, int i, int j) {
		for(Node n2 : all) {
			if(n2.getX() == i && n2.getY() == j) {
				return n2;
			}
		}
		return null;
	}
	
	public void getPathBetween(){
		ArrayList<Vector2> path_aux = new ArrayList<Vector2>();
		
		ArrayList<Node> all = new ArrayList<Pathfinding.Node>();
		ArrayList<Node> open = new ArrayList<Pathfinding.Node>();
		ArrayList<Node> closed = new ArrayList<Pathfinding.Node>();
		
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[0].length; j ++) {
				all.add(new Node(i, j, 0, 0, null, map[i][j] == 1));
			}
		}
		
		Node firstPoint = getNode(all, sx, sy);
		float heuristic = (float) Math.sqrt(Math.pow(ex - sx, 2) + Math.pow(ey - sy, 2));
		firstPoint.setHeuristic(heuristic);
		closed.add(firstPoint);
		
		
		Node found = null;
		int k = 0;
		//pra cada ponto na lista fechada;
		while(found == null) {
			Node root = closed.get(k);
			//checa os pontos em volta
			for(int i = -1; i < 2; i ++) {
				for(int j = -1; j < 2; j ++) {
					Node neighboor = getNeighboor(root, all, i, j, map.length, map[0].length);
					//se o ponto tá incluso na lista
					if(neighboor != null) {
						//se o ponto não for um bloco
						if(!neighboor.isBlock()) {
							//se o ponto não estiver na lista fechada
							if(!closed.contains(neighboor)) {
								//se o ponto já for meu objetivo
								if(neighboor.equals(getNode(all, ex, ey))) {
									neighboor.setParent(root);
									closed.add(neighboor);
									found = neighboor;
									break;
								}
								
								//se o ponto está na lista aberta
								if(open.contains(neighboor)) {
									//checa se é mais rápido ir por esse nodo ou pelo nodo anterior
									float heu = (float) Math.abs(ex - neighboor.getX()) + Math.abs(ey - neighboor.getY());
									float cost = neighboor.getX() == root.getX() ? 10 : (neighboor.getY() == root.getY() ? 10 : 10);
									if(root.getCost() + cost < neighboor.getCost()) {
										neighboor.setHeuristic(heu);
										neighboor.setCost(cost);
										neighboor.setParent(root);
									}
								}
								//se o ponto não está na lista aberta
								else{
									//seta os valores e coloca na lista aberta
									neighboor.setHeuristic((float) Math.sqrt(Math.pow(ex - neighboor.getX(), 2) + Math.pow(ey - neighboor.getY(), 2)));
									neighboor.setCost(neighboor.getX() == root.getX() ? 10 : (neighboor.getY() == root.getY() ? 10 : 14));
									neighboor.setParent(root);
									open.add(neighboor);
								}
							}
						}
					}
				}
				if(found != null) break;
			}
			if(open.size() == 0) {
				path = path_aux;
				return;
			}
			//Depois de colocar os pontos em volta à lista aberta, checa qual dos pontos da aberta tem a soma menor
			Node smallest = open.get(0);
			for(Node n : open) {
				if(n.getSum() < smallest.getSum()) {
					smallest = n;
				}
			}
			
			//tira esse da aberta e poe na fechada
			open.remove(smallest);
			closed.add(smallest);
			k++;
		}
		
		while (found != null) {
			path_aux.add(new Vector2(found.getX(), found.getY()));
			found = found.getParent();
		}
		path = path_aux;
	}
	
	public int getSx() {
		return sx;
	}

	public void setSx(int sx) {
		this.sx = sx;
	}

	public int getSy() {
		return sy;
	}

	public void setSy(int sy) {
		this.sy = sy;
	}

	public int getEx() {
		return ex;
	}

	public void setEx(int ex) {
		this.ex = ex;
	}

	public int getEy() {
		return ey;
	}

	public void setEy(int ey) {
		this.ey = ey;
	}

	public int[][] getMap() {
		return map;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}

	class Node{
		int x, y;
		float heuristic;
		float cost;
		Node parent;
		boolean block;
		
		public Node(int x, int y, float heuristic, float cost, Node parent, boolean block) {
			this.block = block;
			this.x = x;
			this.y = y;
			this.heuristic = heuristic;
			this.cost = cost;
			this.parent = parent;
		}

		public float getHeuristic() {
			return heuristic;
		}

		public void setHeuristic(float heuristic) {
			this.heuristic = heuristic;
		}

		public float getCost() {
			return cost;
		}

		public void setCost(float cost) {
			this.cost = cost;
		}

		public float getSum() {
			return heuristic + cost;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public boolean isBlock() {
			return block;
		}
		
		public String toString(){
			return "Node at " + x + ", " + y + " - " + (!block ? "not " : "") + "blocked, and sum: " + getSum();
		}

	}



}
