package com.lt.topology;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.PriorityQueue;

import randomTopology.Constant;
import randomTopology.Node;
import randomTopology.Topology;

public class MTopology extends Topology{
	// 额外加入loss的随机生成
	@Override
	public void writeIdToFile() {
		// 下面给生成的网络拓扑加延时和代价
		PriorityQueue<MPair> pq = new PriorityQueue<>(new MPair());// 优先队列
		for (Node node : graph.getNodes()) {// 遍历每个节点
			for (Integer value : node.getNeighbors()) {
				MPair tmp = new MPair();
				tmp.setStart(node.getIdentifier());
				tmp.setEnd(value);
				double distance = Math.sqrt(Math.pow(points[node.getIdentifier()].getX() - points[value].getX(), 2)
						+ Math.pow(points[node.getIdentifier()].getY() - points[value].getY(), 2));
				tmp.setDistance(distance);
				int cost = (int) (Math.random() * 30) + 1;// 1-15
				tmp.setCost(cost);
				// 将另一个相关的删除，因为一个边对应的是两个链路。这两个链路除了起点和终点恰好相反外
				// 其余的都相同。实际上这里就是删除起点和终点和当前这个链路恰好相反的那个链路
				// 也就是将起点和终点的邻居调整下
				graph.getNodes().get(value).removeEdgeTo(node.getIdentifier());
				pq.offer(tmp);// 将当前的链路对象加入到优先队列中，这里链路对象还没有对延时赋值
			}
		}
		// 对前75%个进行设置延时1-5,后5%设置20-30,其余设置5-8
		int pre = (int) (pq.size() * 0.75);
		int post = (int) (pq.size() * 0.95);
		int size = pq.size();
		PrintWriter idout = null;
		try {
			idout = new PrintWriter(Constant.idFile.replace(".", "_" + Constant.WriteFile_TimeFor + "."));// 通信
			idout.println("id\tsource\ttarget\tcost\tdelay\tloss");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 在写入前完成对延时的赋值，一并写入
		for (int i = 0; i < size; i++) {
			int delay, loss;
			if (i < pre) {
				delay = (int) (Math.random() * 10) + 1;// 1-5
				loss = (int) (Math.random() * 10) + 1;
			}
			else if (i >= pre && i < post) {
				delay = (int) (Math.random() * 11) + 10;// 5-8
				loss = (int) (Math.random() * 11) + 10;
			}
			else {
				delay = (int) (Math.random() * 11) + 20;// 20-30
				loss = (int) (Math.random() * 11) + 20;
			}
			MPair p = pq.poll();
			p.setDelay(delay);
			p.setLoss(loss);
			idout.println(2 * i + "\t" + p.getStart() + "\t" + p.getEnd() + "\t"
					+ p.getCost() + "\t" + p.getDelay() + "\t" + p.getLoss());
			idout.println((2 * i + 1) + "\t" + p.getEnd() + "\t" + p.getStart() + "\t"
					+ p.getCost() + "\t" + p.getDelay() + "\t" + p.getLoss());
		}
		idout.close();
	}
}
