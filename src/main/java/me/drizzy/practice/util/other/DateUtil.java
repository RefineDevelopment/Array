package me.drizzy.practice.util.other;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String getFormattedDate(long value) {
		Date date = new Date(value);
		String stringDateFormat = "MMMM d, yyyy";
		DateFormat dateFormat = new SimpleDateFormat(stringDateFormat);
		String formattedDate = dateFormat.format(date).replaceAll(",", "th,");
		return formattedDate;
	}
}
