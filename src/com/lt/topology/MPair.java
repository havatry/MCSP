package com.lt.topology;

import randomTopology.Pair;

public class MPair extends Pair{
	private int loss; // 第二个约束
	
	public int getLoss() {
		return loss;
	}
	
	public void setLoss(int loss) {
		this.loss = loss;
	}
}
