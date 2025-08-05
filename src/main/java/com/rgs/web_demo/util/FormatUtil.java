package com.rgs.web_demo.util;

public class FormatUtil {

	public static String numberFormat(String digits) {
		if (digits.matches("^\\d{11}$")) {
			return digits.replaceFirst("^(\\d{3})(\\d{4})(\\d{4})$", "$1-$2-$3");
		} else if (digits.matches("^\\d{10}$")) {
			if (digits.startsWith("02")) {
				return digits.replaceFirst("^(02)(\\d{3})(\\d{4})$", "$1-$2-$3");
			} else {
				return digits.replaceFirst("^(\\d{3})(\\d{3})(\\d{4})$", "$1-$2-$3");
			}
		} else if (digits.matches("^\\d{9}$") && digits.startsWith("02")) {
			return digits.replaceFirst("^(02)(\\d{3})(\\d{4})$", "$1-$2-$3");
		}

		return null;
	}

}
