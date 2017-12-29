package com.dhkun.test.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AmountUtils {
	
	/**金额为分的格式 */    
    public static final String CURRENCY_FEN_REGEX = "^\\-?[0-9]+$";
    /**金额为元的格式,要求精度保留小数点后两位 */ 
    public static final String CURRENCY_YUAN_REGEX = "^\\-?[0-9]+(.[0-9]{2})?$";

    public static void testRegex() {
    	System.out.println("0".matches(CURRENCY_FEN_REGEX)); // true
    	System.out.println("1".matches(CURRENCY_FEN_REGEX)); // true
    	System.out.println("-1".matches(CURRENCY_FEN_REGEX)); // true
    	System.out.println("0".matches(CURRENCY_YUAN_REGEX)); // true
    	System.out.println("1".matches(CURRENCY_YUAN_REGEX)); // true
    	System.out.println("-1".matches(CURRENCY_YUAN_REGEX)); // true
    	System.out.println("1.00".matches(CURRENCY_YUAN_REGEX)); // true
    	System.out.println("-1.00".matches(CURRENCY_YUAN_REGEX)); // true
    	System.out.println("1.0".matches(CURRENCY_YUAN_REGEX)); // false
    	System.out.println("-1.0".matches(CURRENCY_YUAN_REGEX)); // false
    	System.out.println("-21474836.48".matches(CURRENCY_YUAN_REGEX)); // true
    }
    
    public static void test1() {
    	for (int i = -100; i <= 100; i++) {
			String yuanStr = changeF2Y(i).toString();
			int fen = changeY2F(yuanStr).intValue();
			if (i != fen) {
				System.out.println("转换结果不对,目标为" + i + ",实际为" + fen);
			}
		}
    }
    
    public static void test2() {
    	for (int i = -100; i <= 100; i++) {
			double yuan = changeF2Y(i).doubleValue();
			//System.out.println(yuan);
			int fen = changeY2F(yuan).intValue();
			if (i != fen) {
				System.out.println("转换结果不对,目标为" + i + ",实际为" + fen);
			}
		}
    }
    
    public static void main(String[] args) {
    	//testRegex();
    	//test1();
    	test2();
    }
    
	public static BigDecimal changeF2Y(int fen) {
		BigDecimal yuan = new BigDecimal(fen).movePointLeft(2);
		return yuan;
	}
	
	public static BigDecimal changeF2Y(long fen) {
		BigDecimal yuan = new BigDecimal(fen).movePointLeft(2);
		return yuan;
	}
	
	public static BigDecimal changeF2Y(String fen) {
		if (!fen.matches(CURRENCY_FEN_REGEX)) {
			throw new IllegalArgumentException("入参" + fen + "不满足单位'分'的格式");
		}
		BigDecimal yuan = new BigDecimal(Long.valueOf(fen)).movePointLeft(2);
		return yuan;
	}
	
	public static BigDecimal changeY2F(double yuan) {
		/**
		 * 因为double类型双精度的问题
		 * BigDecimal d1 = new BigDecimal(0.6);
		 * 结果为d1=0.59999999999999997779553950749686919152736663818359375
		 * 无限接近0.6但并不是0.6,所以需要通过四舍五入方式获取目标的结果
		 * {@code (0,RoundingMode.HALF_UP):不保留小数点后数字,并且小数点后第一位 >= 0.5时,进1位}
		 */
		BigDecimal fen = new BigDecimal(yuan).movePointRight(2).setScale(0, RoundingMode.HALF_UP);
		return fen;
	}
	
	public static BigDecimal changeY2F(String yuan) {
		if (!yuan.matches(CURRENCY_YUAN_REGEX)) {
			throw new IllegalArgumentException("入参" + yuan + "不满足单位'元'的格式,要求精确到小数点后两位");
		}
		/**
		 * 通过传入double类型的字符串,会比直接传入double类型,更能得到目标结果
		 * 理由可以参看{@code changeY2F(double)}
		 * 并且不需要设置小数点后保留位
		 */
		BigDecimal fen = new BigDecimal(yuan).movePointRight(2);//.setScale(0, RoundingMode.DOWN);
		return fen;
	}
}
