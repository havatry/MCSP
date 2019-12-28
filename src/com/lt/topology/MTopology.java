package com.lt.topology;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.PriorityQueue;

import randomTopology.Constant;
import randomTopology.Node;
import randomTopology.Topology;

public class MTopology extends Topology{
	// �������loss���������
	@Override
	public void writeIdToFile() {
		// ��������ɵ��������˼���ʱ�ʹ���
		PriorityQueue<MPair> pq = new PriorityQueue<>(new MPair());// ���ȶ���
		for (Node node : graph.getNodes()) {// ����ÿ���ڵ�
			for (Integer value : node.getNeighbors()) {
				MPair tmp = new MPair();
				tmp.setStart(node.getIdentifier());
				tmp.setEnd(value);
				double distance = Math.sqrt(Math.pow(points[node.getIdentifier()].getX() - points[value].getX(), 2)
						+ Math.pow(points[node.getIdentifier()].getY() - points[value].getY(), 2));
				tmp.setDistance(distance);
				int cost = (int) (Math.random() * 30) + 1;// 1-15
				tmp.setCost(cost);
				// ����һ����ص�ɾ������Ϊһ���߶�Ӧ����������·����������·���������յ�ǡ���෴��
				// ����Ķ���ͬ��ʵ�����������ɾ�������յ�͵�ǰ�����·ǡ���෴���Ǹ���·
				// Ҳ���ǽ������յ���ھӵ�����
				graph.getNodes().get(value).removeEdgeTo(node.getIdentifier());
				pq.offer(tmp);// ����ǰ����·������뵽���ȶ����У�������·����û�ж���ʱ��ֵ
			}
		}
		// ��ǰ75%������������ʱ1-5,��5%����20-30,��������5-8
		int pre = (int) (pq.size() * 0.75);
		int post = (int) (pq.size() * 0.95);
		int size = pq.size();
		PrintWriter idout = null;
		try {
			idout = new PrintWriter(Constant.idFile.replace(".", "_" + Constant.WriteFile_TimeFor + "."));// ͨ��
			idout.println("id\tsource\ttarget\tcost\tdelay\tloss");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ��д��ǰ��ɶ���ʱ�ĸ�ֵ��һ��д��
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
