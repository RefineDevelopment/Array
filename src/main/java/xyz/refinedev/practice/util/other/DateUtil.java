package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class DateUtil {

	String stringDateFormat = "MMMM d, yyyy";
	private final DateFormat dateFormat = new SimpleDateFormat(stringDateFormat);

	public String getFormattedDate(long value) {
		Date date = new Date(value);
		return dateFormat.format(date).replaceAll(",", "th,");
	}
}
