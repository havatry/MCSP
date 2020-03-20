package com.lt.multiAlg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import randomTopology.Constant;

import java.util.List;

/**
 * Main Algorithm
 * 2020年1月4日 下午1:45:28
 */
public class MBiLAD extends AbstractMCSPMethods{
	private Integer value;
	private Double lambda1star;
	private Double lambda2star;
	private List<Integer> p_positive;
	private List<Integer> p_negative; // 最佳路径
	private double detal_below;
	private double detal_top;
	private List<Integer> p_below_negative;
	private List<Integer> p_below_postive;
	private List<Integer> p_top_negative;
	private List<Integer> p_top_positive;
	private double v2_positive;
	private double v2_negative;
	private double c_positive;
	private double c_negative;
	private double delta;
	private double lambda2;
	private double[][] origin;
	private Logger log = LoggerFactory.getLogger(MBiLAD.class);
	
	@Override
	public void OptimalPath(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) { // integer can swap? not
		origin = MCommon.copyArray(Id);
		compute(Node, Id, IdLink, start, end, v1, v2);
	}
	
	private void compute(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		List<Integer> p1 = getPath(Node, Id, Math.PI / 2, start, end);
		log.info("执行第0步, 求得延时最小的路径是 p1 = {}, 对应的延时是f1(p1) = {}, 延时约束v1 = {}", new Object[]{p1, Ptheta(p1, Id, IdLink), v1});
		if (MCommon.great(Ptheta(p1, origin, IdLink), v1)) {
			value = 0;
			return;
		}
		MCommon.swap(Id, 3, 4); // c f2 f1, 计算出f2的最小
		List<Integer> p2 = getPath(Node, Id, Math.PI / 2, start, end);
		log.info("执行第0步, 求得丢包最小的路径是 p2 = {}, 对应的丢包是f1(p2) = {}, 丢包约束v1 = {}", new Object[]{p2, Ptheta(p2, Id, IdLink), v2});
		if (MCommon.great(Ltheta(p2, origin, IdLink), v2)) {
			value = 0;
			return;
		}
		step_1(Node, Id, IdLink, start, end, v1, v2);
	}
	
	private void step_1(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		List<Integer> pc = getPath(Node, Id, 0, start, end); // c f2 f1, 计算出c最小
//		double f1_value = Ltheta(pc, origin, IdLink); // 计算出路径的延时f1
//		double f2_value = Ptheta(pc, origin, IdLink); // 计算出路径的丢包f2
        // 由于使用origin 因此这里换Ptheta和Lthea顺序
        double f1_value = Ptheta(pc, origin, IdLink); // 计算出路径的延时f1
		double f2_value = Ltheta(pc, origin, IdLink); // 计算出路径的丢包f2
		log.info("执行第1步, 求得代价最小的路径是 pc = {}, 对应的延时是f1(pc) = {}, 对应的丢包是f2(pc) = {}, 延时约束v1 = {}, 丢包约束v2 = {}", 
				new Object[]{pc, f1_value, f2_value, v1, v2});
		if (MCommon.smallEqual(f1_value, v1) && MCommon.smallEqual(f2_value, v2)) {
			p_negative = pc;
			value = 1;
			return;
		}
		if (MCommon.great(f1_value, v1)) {
			MCommon.swap(Id, 3, 4); // reset, c f1 f2
			step_2_3_6(Node, Id, IdLink, start, end, v1, v2, 2);
		} else {
			log.info("交换f1和f2 以及v1和v2");
			MCommon.swap(origin, 3, 4);
			step_2_3_6(Node, Id, IdLink, start, end, v2, v1, 2);
		}
	}
	
	private void step_2_3_6(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2, int mode) {
		log.info("执行第{}步", mode);
		if (mode == 3) {
			// f2 f1 c
			MCommon.swap(Id, 2, 4);
		} else if (mode == 6) {
		    // 步骤6只能从步骤5过来 而步骤的Id格式是f2 f1 c
            MCommon.swap(Id, 2, 4); // c f1 f2
            MCommon.add(Id, 2, 4, 1, lambda2); // c+lambda2*f2 f1 f2
        }
		ExtendBiLAD extendBiLAD = new ExtendBiLAD(); // c + lambda1*f1
		extendBiLAD.OptimalPath(Node, Id, IdLink, v1, start, end);
		CallDijkstraTime += extendBiLAD.getCallDijkstraTime();
		List<List<Integer>> paths = extendBiLAD.getTwoPaths();
		// 0 is positive and 1 is negative
		if (paths == null || paths.size() < 2) {
			value = 0;
			return;
		}
		double f1_value_1 = Ptheta(paths.get(0), origin, IdLink); // +
		double f1_value_2 = Ptheta(paths.get(1), origin, IdLink); // -
		double alpha = 0;
		log.info("调用BiLAD算法求的两个路径和lambda分别是, lambda1 = {}, p+ = {}, p- = {}, f1(p+) = {}, f1(p-) = {}, v2 = {}",
				new Object[]{Math.abs(Math.tan(extendBiLAD.getTheta())), paths.get(0), paths.get(1), f1_value_1, f1_value_2, v2});
		if (!MCommon.equal(f1_value_1, f1_value_2)) {
			alpha = (v1 - f1_value_2) / (f1_value_1 - f1_value_2);
		}
		double v2_wave = (1 - alpha) * Ltheta(paths.get(1), origin, IdLink) + alpha * Ltheta(paths.get(0), origin, IdLink);
		log.info("求得alpha = {}, v2波浪= {}", new Object[]{alpha, v2_wave});
		switch (mode) {
            case 2:
                if (MCommon.smallEqual(v2_wave, v2)) {
                    log.info("v2波浪小于等于v2");
                    lambda1star = Math.abs(Math.tan(extendBiLAD.getTheta()));
                    lambda2star = 0.0;
                    p_negative = paths.get(1);
                    p_positive = paths.get(0);
                    value = 2;
                    log.info("求得最优解, lambda1star = {}, lambda2star = {}, p- = {}, p+ = {}, flag = {}",
                            new Object[]{lambda1star, lambda2star, p_negative, p_positive, value});
                    return;
                } else {
                    log.info("v2波浪大于v2");
                    detal_below = 0;
                    p_below_negative = paths.get(1);
                    p_below_postive = paths.get(0);
                    v2_positive = v2_wave;
                    c_positive = (1 - alpha) * Ctheta(paths.get(1), origin, IdLink) + alpha * Ctheta(paths.get(0), origin, IdLink);
                    log.info("初始化delta_below = {}, p-_below = {}, p+_below = {}, v2+ = {}, c+ = {}, 进入第3步",
                            new Object[]{0, p_below_negative, p_below_postive, v2_positive, c_positive});
                    step_2_3_6(Node, Id, IdLink, start, end, v1, v2, 3);
                }
                break;
            case 3:
                if (MCommon.great(v2_wave, v2)) {
                    log.info("v2波浪大于v2");
                    value = 0;
                    log.info("there is no optimal solution and flag = {}", value);
                    return;
                } else if (MCommon.small(v2_wave, v2)) {
                    // <
                    log.info("v2波浪小于v2");
                    detal_top = Math.PI / 2;
                    p_top_negative = paths.get(1);
                    p_top_positive = paths.get(0);
                    v2_negative = v2_wave;
                    c_negative = (1 - alpha) * Ctheta(paths.get(1), origin, IdLink) + alpha * Ctheta(paths.get(0), origin, IdLink);
                    value = 1;
                    lambda1star = Math.abs(Math.tan(theta));
                    log.info("初始化delta_top = {}, p-_top = {}, p+_top = {}, v2- = {}, c- = {}, 进入第4步",
                            new Object[]{"PI / 2", p_top_negative, p_top_positive, v2_negative, c_negative});
                    step_4(Node, Id, IdLink, start, end, v1, v2);
                } else {
                    log.info("v2波浪等于v2");
                    // call yen
                    MDijkstra mDijkstra = new MDijkstra();
                    List<Integer> p = mDijkstra.YenFindPath(Node, MCommon.getEdge(Node, Id, IdLink, theta),
                            Id, IdLink, start, end, v1, v2);
                    log.info("调用yen算法 获取精确解 p = {}", p);
                    CallDijkstraTime += mDijkstra.getCallDijkstraTime(); // update
                    if (p == null) {
                        value = 0;
                        log.info("yen算法找不到路径");
                        return;
                    } else {
                        p_negative = p;
                        value = 1;
                        return;
                    }
                }
                break;
            case 6:
                // 撤销步骤的改变，因为其可能到步骤4然后步骤5，这样会出现Id矩阵的叠加
                MCommon.add(Id, 2, 4, 1, -lambda2); // 这步必须在前面
                MCommon.swap(Id, 2, 4);
        //            MCommon.add(Id, 2, 4, 1, -lambda2);
                if (MCommon.equal(v2_wave, v2)) {
                    log.info("v2波浪等于v2");
                    lambda1star = Math.abs(Math.tan(theta));
                    lambda2star = lambda2;
                    value = 6;
                    log.info("程序退出, 输出lamda1star = {}, lambda2star = {}, p- = {}, p+ = {}, flag = {}",
                            new Object[] {lambda1star, lambda2star, p_negative, p_positive, value});
                    return;
                } else if (MCommon.small(v2_wave, v2)) {
                    log.info("v2波浪小于v2");
                    detal_top = delta;
                    p_top_negative = paths.get(1);
                    p_top_positive = paths.get(0);
                    v2_negative = v2_wave;
                    c_negative = (1 - alpha) * Ctheta(paths.get(1), origin, IdLink) + alpha * Ctheta(paths.get(0), origin, IdLink);
                    lambda1star = Math.abs(Math.tan(theta));
                    log.info("设置delta_top = {}, p-_top = {}, p+_top = {}, v2- = {}, c- = {}, lambda1start = {}, 进入第4步",
                            new Object[] {delta, p_top_negative, p_top_positive, v2_negative, c_negative, lambda1star});
                } else {
                    detal_below = delta;
                    p_below_negative = paths.get(1);
                    p_below_postive = paths.get(0);
                    v2_positive = v2_wave;
                    c_positive = (1 - alpha) * Ctheta(paths.get(1), origin, IdLink) + alpha * Ctheta(paths.get(0), origin, IdLink);
                    log.info("设置delta_below = {}, p-_below = {}, p+_below = {}, v2+ = {}, c+ = {}, 进入第4步",
                            new Object[] {delta, p_below_negative, p_below_postive, v2_positive, c_positive});
                }
                step_4(Node, Id, IdLink, start, end, v1, v2);
                break;
        }
	}
	
	private void step_4(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		if (MCommon.smallEqual(detal_top, detal_below)) {
			lambda2star = Math.abs(Math.tan(detal_top));
			value = 4;
			log.info("delta_top和delta_below之间的误差小于epsion, 程序退出 lamda2star = {}, lambda1star = {}, p-_top = {}, p+_top = {}, flag = {}",
					new Object[]{lambda2star, lambda1star, p_top_negative, p_top_positive, value});
			return;
		}
		updateMultiplier();
		log.info("delta_top = {}, delta_below = {}, c+ = {}, c- = {}, v2+ = {}, v2- = {}, 进入第5步",
				new Object[]{detal_top, detal_below, c_positive, c_negative, v2_positive, v2_negative});
		step_5(Node, Id, IdLink, start, end, v1, v2);
	}
	
	
	private void step_5(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		MCommon.swap(Id, 2, 4); // c f1 f2
		MCommon.add(Id, 2, 4, 1, lambda2); // c + lamda2*f2 f1 f2
		List<Integer> pc = getPath(Node, Id, 0, start, end);
		double f1_value = Ptheta(pc, origin, IdLink); // 这里可能出现链路矩阵返回-1的情况，从而报错数组下标异常
		double f2_value = Ltheta(pc, origin, IdLink);
		// 撤销步骤5的改变, 因为步骤5可能会跳转到步骤4，然后再回到步骤5，这样会出现c+lambda1*f2的叠加
        MCommon.add(Id, 2, 4, 1, -lambda2); // c f1 f2， 这步必须在交换2和4d前面*
        MCommon.swap(Id, 2, 4);
//        MCommon.add(Id, 2, 4, 1, -lambda2); // c f1 f2
		log.info("给定lambda2 = {}, 计算pc波浪 = {}, f1(pc波浪) = {}, f2(pc波浪) = {}, v2 = {}",
				new Object[]{lambda2, pc, f1_value, f2_value, v2});
		if (MCommon.smallEqual(f1_value, v1)) {
			lambda1star = 0.0;
			if (MCommon.equal(f2_value, v2)) {
				lambda2star = lambda2;
				value = 5;
				log.info("f2(pc波浪)和v2相等, 输出lambda1star = {}, lambda2star = {}, pc波浪 = {}, flag = {}",
						new Object[] {lambda1star, lambda2star, pc, value});
				return;
			} else if (MCommon.small(f2_value, v2)) {
				detal_top = delta;
				p_top_negative = pc;
				log.info("f2(pc波浪)比v2小, 设置delta_top = {}, p-_top = {}, p+_top = {}, 进入第4步", new Object[] {delta, pc, null});
				step_4(Node, Id, IdLink, start, end, v1, v2);
			} else {
				detal_below = delta;
				p_below_postive = pc;
				log.info("f2(pc波浪)比v2大, 设置delta_below = {}, p-_below = {}, p+_below = {}, 进入第4步", new Object[] {delta, null, pc});
				step_4(Node, Id, IdLink, start, end, v1, v2);
			}
		} else {
			step_2_3_6(Node, Id, IdLink, start, end, v1, v2, 6);
		}
	}
	
	private void updateMultiplier() {
		assert v2_negative != v2_positive;
		lambda2 = (c_positive - c_negative) / (v2_negative - v2_positive);
		delta = Math.atan(lambda2);
		if (MCommon.greatEqual(Math.abs(delta - (detal_below + detal_top) / 2), (0.5 - Constant.gama) * (detal_top - detal_below))) {
			delta = (detal_below + detal_top) / 2;
			lambda2 = Math.abs(Math.tan(delta));
		}
	}

    // get methods
	public Double getLambda1star() {
		return lambda1star;
	}
	
	public Double getLambda2star() {
		return lambda2star;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public List<Integer> getP_negative() {
		return p_negative;
	}
	
	public List<Integer> getP_positive() {
		return p_positive;
	}

    public double[][] getOrigin() {
        return origin;
    }
}
