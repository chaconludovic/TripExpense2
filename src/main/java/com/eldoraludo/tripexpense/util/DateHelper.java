package com.eldoraludo.tripexpense.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateHelper {
	private static final String FORMAT_DATE_DB = "yyyy-MM-dd HH:mm:ss.sss";

	public static String convertirDateToString(DateTime dateAConvertir) {
		DateFormat df = new SimpleDateFormat(FORMAT_DATE_DB);
		return df.format(dateAConvertir.toDate());
	}

	public static DateTime convertirIntsToDate(int jour, int mois, int annee) {
		DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy");
		return format.parseDateTime(jour + "/" + mois + "/" + annee);
	}

	public static DateTime convertirStringToDate(String date) {
		DateTimeFormatter format = DateTimeFormat.forPattern(FORMAT_DATE_DB);
		return format.parseDateTime(date);
	}

	public static String prettyDate(DateTime dateAConvertir) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.format(dateAConvertir.toDate());
	}
}
