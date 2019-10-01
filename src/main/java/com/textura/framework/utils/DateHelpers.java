package com.textura.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelpers {

	/**
	 * Returns the currentDate using a SimpleDateFormat explained in
	 * http://docs.
	 * oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
	 * 
	 * @param sDateFormat
	 * @return
	 */
	public static String currentDateTime(String sDateFormat) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(sDateFormat);
		return sdf.format(calendar.getTime());
	}

	/**
	 * Gets the currentDate in the general format: MM/dd/yyyy
	 */
	public static String getCurrentDate() {
		return currentDateTime("MM/dd/yyyy");
	}

	/** MM-dd-yy **/
	public static String getCurrentDateTwoDigitYear() {
		return currentDateTime("MM-dd-yy");
	}

	/** yyyy-mm-dd **/
	public static String getCurrentDateYearFirst() {
		return currentDateTime("yyyy-MM-dd");
	}

	/**
	 * Gets the current date and time in the following format: MM/dd/yyyy
	 * HH:mm:ss Used for logging date time during execution.
	 */
	public static String getCurrentDateAndTime() {
		return currentDateTime("MM/dd/yyyy HH:mm:ss");
	}

	/** yyyy-MM-dd HH:mm:ss **/
	public static String getCurrentDateAndTime1() {
		return currentDateTime("yyyy-MM-dd HH:mm:ss");
	}

	/** dd-MMM-yyyy hh:mm **/
	public static String getCurrentDateAndTime2() {
		return currentDateTime("dd-MMM-yyyy hh:mm");
	}

	/** yyyy-MM-dd_HH:mm:ss **/
	public static String getCurrentDateAndTime3() {
		return currentDateTime("yyyy-MM-dd_HH:mm:ss");
	}

	/** yyyy-MM-dd_HH:mm **/
	public static String getCurrentDateAndTime4() {
		return currentDateTime("yyyy-MM-dd_HH:mm");
	}

	/** yyyy-MM-dd_HH:mm **/
	public static String getCurrentDateAndTime5() {
		return currentDateTime("EEE, dd MMM yyyy hh:mm");
	}

	/** yyyy-MM-dd_HH-mm-ss **/
	public static String getCurrentDateAndTimeExcel() {
		return currentDateTime("yyyy-MM-dd_HH-mm-ss");
	}

	/**
	 * Gets the current date and time in the following format: dd MMM yyyy HH:mm
	 * Used for logging date time during execution for errors in PDQ
	 */
	public static String getCurrentDateAndTimePDQErrors() {
		return currentDateTime("dd MMM yyyy HH:mm");
	}

	/**
	 * Gets the current time in the following format: HH:mm
	 */
	public static String getCurrentTime() {
		return currentDateTime("HH:mm");
	}

	/**
	 * Gets the current time in the following format: HH:mm:sss
	 */
	public static String getCurrentTimes() {
		return currentDateTime("HH:mm:ss");
	}

	/**
	 * Gets the currentDate in the general format: MM/yyyy
	 */
	public static String getCurrentMonthFourDigitYear() {
		return currentDateTime("MM/yyyy");
	}

	/**
	 * Compares two dates with each other in dd-MMM-yyyy format
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareTo(String date1, String date2) {
		return compareTo("dd-MMM-yyyy", date1, date2);
	}

	/**
	 * Compares two dates with each other in the given format format
	 * 
	 * @param format
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareTo(String format, String date1, String date2) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date date1_ = sdf.parse(date1);
			Date date2_ = sdf.parse(date2);
			return date1_.compareTo(date2_);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return -2;
	}

	/**
	 * Format of yyMMddHHmmss
	 */
	public static String getTimeStamp() {
		return currentDateTime("yyMMddHHmmss");
	}

	public static String getDateFromUnix(String unixDateTime) {
		long unixDate = Long.parseLong(unixDateTime);
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		return f.format(unixDate);
	}

	/**
	 * Format of dd-MMM-yyyy
	 */
	public static String getCurrentTexturaDate() {
		return currentDateTime("dd-MMM-yyyy");
	}
	
	/**
	 * Format of dd-MMM-yyyy for AU and CA
	 */
	public static String getCurrentTexturaDateAuCa() {
		return currentDateTime("dd-MMM-yyyy").replaceAll("(.*-.*)(-)(.*)", "$1.$2$3");
	}

	/*
	 * Format of d-MMM-yyyy
	 */
	public static String getCurrentTexturaDate2() {
		return currentDateTime("d-MMM-yyyy");
	}

	public static String getCurrentTexturaMonthAndYear() {
		return currentDateTime("MMM-yyyy");
	}

	public static String getCurrentTexturaMonthAndTwoDigitYear() {
		return currentDateTime("MMMyy");
	}

	public static boolean validateTexturaDateFormat(String strDate) {
		String pattern = "dd-MMM-yyyy";
		return validateTimeFormat(strDate, pattern);
	}

	public static boolean validateDocDateFormat(String strDate) {
		String pattern = "MMM dd, yyyy";
		return validateTimeFormat(strDate, pattern);
	}

	public static boolean validateMessageDateFormat(String strDate) {
		String pattern = "EEEE, dd-MMM-yyyy KK:mm:ss a";
		return validateTimeFormat(strDate, pattern);
	}

	public static String getCurrentDateAllNumbers() {
		return currentDateTime("MMddyy");
	}

	public static String getCurrentDayOfTheWeek() {
		return currentDateTime("EEEE");
	}

	public static String getCurrentNumberDayOfTheMonth() {
		return currentDateTime("dd");
	}

	public static String getCurrentMonthYear() {
		return currentDateTime("MM/yy");
	}

	public static String getCurrentMonth() {
		return currentDateTime("MM");
	}

	public static boolean validateDateSignedFormat(String strDate) {
		String pattern = "dd-MMM-yyyy HH:MM a";
		return validateTimeFormatLeniently(strDate, pattern);
	}

	public static boolean validateViewMessagesDateFormat(String strDate) {
		String pattern = "dd-MMM-yyyy HH:MM:SS a";
		return validateTimeFormatLeniently(strDate, pattern);
	}

	public static boolean validateViewMessagesDateTimeFormat(String strDate) {
		String pattern = "HH:MM:SS a";
		return validateTimeFormatLeniently(strDate, pattern);
	}

	public static boolean validateTimeFormatLeniently(String strDate, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setLenient(true);
		try {
			sdf.applyPattern(pattern);
			sdf.parse(strDate);
			return true;
		} catch (ParseException e) {
		}
		return false;
	}

	public static boolean validateTimeFormat(String strDate, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setLenient(false);
		try {
			sdf.applyPattern(pattern);
			sdf.parse(strDate);
			return true;
		} catch (ParseException e) {
		}
		return false;
	}

	public static boolean validateTimeFormatStrictly(String strDate, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setLenient(false);
		try {
			sdf.applyPattern(pattern);
			return sdf.format(sdf.parse(strDate)).equals(strDate);
		} catch (ParseException e) {
		}
		return false;
	}

	public static int stringToMonth(String s) {
		if (s.equals("Jan"))
			return 1;
		if (s.equals("Feb"))
			return 2;
		if (s.equals("Mar"))
			return 3;
		if (s.equals("Apr"))
			return 4;
		if (s.equals("May"))
			return 5;
		if (s.equals("Jun"))
			return 6;
		if (s.equals("Jul"))
			return 7;
		if (s.equals("Aug"))
			return 8;
		if (s.equals("Sep"))
			return 9;
		if (s.equals("Oct"))
			return 10;
		if (s.equals("Nov"))
			return 11;
		if (s.equals("Dec"))
			return 12;
		return -1;
	}

	public static GregorianCalendar parseDate(String s) {
		// 15-Jul-2012
		ArrayList<String> parts = new ArrayList<String>(Arrays.asList(s.split("-")));
		GregorianCalendar c = new GregorianCalendar();
		c.set(Integer.parseInt(parts.get(2)), stringToMonth(parts.get(1)), Integer.parseInt(parts.get(0)));
		return c;
	}

	public static int getDateDifference(GregorianCalendar c2, GregorianCalendar c1) {
		return (int) ((c1.getTime().getTime() - c2.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}

	/**
	 * Returns the date after X days have passed from the current date
	 * 
	 * @param days
	 * @return
	 */
	public static String getDateAfterXDays(int days) {
		return getDateAfterXDays(days, "dd-MMM-yyyy");
	}

	/**
	 * Returns the date after X days have passed from the current date for AU and CA
	 * 
	 * @param days
	 * @return
	 */
	public static String getDateAfterXDaysAuCa(int days) {
		return getDateAfterXDays(days, "dd-MMM.-yyyy");
	}

	/**
	 * Returns the date after X days have passed from the current date
	 * 
	 * @param days
	 * @param format
	 * @return
	 */
	public static String getDateAfterXDays(int days, String format) {
		return getDateAfterXDays(DateHelpers.currentDateTime("dd-MMM-yyyy"), days, "dd-MMM-yyyy", format);
	}

	public static String getDateAfterXDays(String date, int days, String inputFormat, String outputFormat) {
		String newDate = null;
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat(inputFormat).parse(date));
			c.add(Calendar.DATE, days);
			newDate = new SimpleDateFormat(outputFormat).format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return newDate;
	}

	/**
	 * Returns the date after X days have passed from the current date
	 * 
	 * @param days
	 * @return
	 */
	public static String getDateAfterXDaysForACH(int days) {
		try {
			String dt = DateHelpers.currentDateTime("yyMMdd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dt));
			c.add(Calendar.DATE, days);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static boolean validateVDRFormat(String strDate) {
		String pattern = "dd-MMM-yyyy";
		return validateTimeFormat(strDate, pattern);
	}

	public static String getDaySuffix(int day) {
		if (day >= 1 && day <= 31) {
			if (day >= 11 && day <= 13) {
				return "th";
			}
			switch (day % 10) {
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
			}
		}
		return "";
	}

	public static String getDayOfMonthWithoutSuffix() {
		String dayOfMonth = getCurrentNumberDayOfTheMonth();
		if (dayOfMonth.charAt(0) == '0') {
			dayOfMonth = dayOfMonth.substring(1);
		}
		return dayOfMonth;
	}

	public static String getDayOfMonthWithSuffix() {
		String dayOfMonth = getCurrentNumberDayOfTheMonth();
		if (dayOfMonth.charAt(0) == '0') {
			dayOfMonth = dayOfMonth.substring(1);
		}
		return dayOfMonth + getDaySuffix(Integer.parseInt(dayOfMonth));
	}

	// returns the hour 1-24
	public static String getHour() {
		return currentDateTime("kk");
	}

	public static String getMinute() {
		return currentDateTime("mm");
	}

	public static String getMonthAfterXDays(int days) {
		try {
			String dt = DateHelpers.currentDateTime("MMMM");
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dt));
			c.add(Calendar.DATE, days);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static String getYearAfterXDays(int days) {
		try {
			String dt = DateHelpers.currentDateTime("dd-MMM-yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dt));
			c.add(Calendar.DATE, days);
			return sdf.format(c.getTime()).split("-")[2];
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static String firstOfTheMonth() {
		return "01-" + currentDateTime("MMM-yyyy");
	}

	public static String endOfTheMonth() {
		String endNumber;
		String currentMonth = currentDateTime("MMM");
		int year = Integer.parseInt(currentDateTime("yyyy"));

		if (currentMonth.equals("Sep") || currentMonth.equals("Apr") || currentMonth.equals("Jun") || currentMonth.equals("Nov")) {
			endNumber = "30";
		} else if (currentMonth.equals("Feb")) { // handle leap years
			if (year % 4 == 0) {
				if (year % 100 == 0) {
					if (year % 400 == 0) {
						endNumber = "29";
					} else {
						endNumber = "28";
					}
				} else {
					endNumber = "29";
				}
			} else {
				endNumber = "28";
			}
		} else {
			endNumber = "31";
		}
		return endNumber + "-" + currentMonth + "-" + year;
	}

	public static String endOfTheMonthv2() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return sdf.format(c.getTime());
	}

	public static String endOfTheMonthv3() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return sdf.format(c.getTime());
	}

	public static String endOfTheMonthIMS() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return sdf.format(c.getTime());
	}

	public static String getFirstOfNextMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return sdf.format(c.getTime());
	}

	/**
	 * Returns the day of next month
	 * 
	 * @param day
	 * @return
	 */
	public static String getDayOfNextMonth(int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		return sdf.format(c.getTime());
	}

	/**
	 * Returns the day and month to be added to current month
	 * 
	 * @param day
	 * @return
	 */
	public static String getDayAndMonth(int day, int month) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		return sdf.format(c.getTime());
	}

	/**
	 * Adds one year to a date formatted as 'dd-MMM-yyyy'
	 * 
	 * @param date
	 * @return
	 */
	public static String addOneYear(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			c.add(Calendar.YEAR, 1);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	private static String addDaysGeneric(String date, int numberOfDays, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			c.add(Calendar.DAY_OF_YEAR, numberOfDays);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static String addDays(String date, int numberOfDays) {
		return addDaysGeneric(date, numberOfDays, "dd-MMM-yyyy");
	}

	public static String addDaysUSFormat(String date, int numberOfDays) {
		return addDaysGeneric(date, numberOfDays, "MM/dd/yyyy");
	}

	public static String addDays3(String date, int numberOfDays) {
		return addDaysGeneric(date, numberOfDays, "yyyy-MM-dd");
	}

	public static String addDays4(String date, int numberOfDays) {
		return addDaysGeneric(date, numberOfDays, "yyyyMMdd");
	}

	public static String addTime(String time, int numberOfHours) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(time));
			c.add(Calendar.HOUR, numberOfHours);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static String addMonths(String date, int numberOfMonths) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			c.add(Calendar.MONTH, 1 * numberOfMonths);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static String getDB2Date() {
		return currentDateTime("yyyy-MM-dd");
	}

	public static boolean validateDB2Date(String strDate) {
		String pattern = "yyyy-MM-dd";
		return validateTimeFormat(strDate, pattern);
	}

	public static boolean isWeekday(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
				return false;
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getMondayXWeeksFromDate(String date, int x) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int currentDay = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, 7 * x);

		switch (currentDay) {
		case Calendar.MONDAY:
			c.add(Calendar.DATE, 7);
			break;
		case Calendar.TUESDAY:
			c.add(Calendar.DATE, 6);
			break;
		case Calendar.WEDNESDAY:
			c.add(Calendar.DATE, 5);
			break;
		case Calendar.THURSDAY:
			c.add(Calendar.DATE, 4);
			break;
		case Calendar.FRIDAY:
			c.add(Calendar.DATE, 3);
			break;
		case Calendar.SATURDAY:
			c.add(Calendar.DATE, 2);
			break;
		case Calendar.SUNDAY:
			c.add(Calendar.DATE, 1);
			break;
		}
		return sdf.format(c.getTime());
	}

	/**
	 * adds x weeks to current date, THEN proceeds to look for the next Monday.
	 * 
	 * @param x
	 *            How many weeks added
	 * @return Monday x weeks from now
	 */
	public static String getMondayXWeeksFromNow(int x) {
		return getMondayXWeeksFromDate(getCurrentTexturaDate(), x);
	}

	public static String toVLWSFormat(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
		try {
			return sdf.format(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static String convertDate(String date, String inputFormat, String outputFormat) {
		try {
			SimpleDateFormat format1 = new SimpleDateFormat(inputFormat);
			Calendar c = Calendar.getInstance();
			c.setTime(format1.parse(date));
			SimpleDateFormat format2 = new SimpleDateFormat(outputFormat);
			return format2.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return "null";
		}
	}

	public static String convertNumberMonthToFullMonth(String date) {
		return convertDate(date, "M", "MMMM");
	}

	public static String convertDB2DateToTexturaDate(String date) {
		return convertDate(date, "yyyy-MM-dd", "dd-MMM-yyyy");
	}

	public static String convertTexturaDateToDB2Date(String date) {
		return convertDate(date, "dd-MMM-yyyy", "yyyy-MM-dd");
	}

	/**
	 * Converts Unix date time format to human readable format
	 * 
	 * @param unixSeconds
	 * @return
	 */
	public static String convertUnixSecondsToTimestamp(int unixSeconds) {
		Date date = new java.util.Date(unixSeconds * 1000L);
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(date);
		return timestamp;
	}

	public static String getCurrentYear() {
		return currentDateTime("yyyy");
	}

	public static String getYearAfterXYears(int years) {
		int year = Integer.parseInt(getCurrentYear()) + years;
		return Integer.toString(year);
	}

	public static String convertDateToAllNumbers(String date) {
		return convertDate(date, "dd-MMM-yyyy", "MMddyy");
	}

	public static String convertDateToCsvusFormat(String date) {
		return convertDate(date, "dd-MMM-yyyy", "MM/dd/yyyy");
	}

	public static String getDateAfterXBusinessDays(int days) {
		return getDateAfterXBusinessDays(days, "dd-MMM-yyyy");
	}

	public static String getDateAfterXBusinessDays(int days, String format) {
		return getDateAfterXBusinessDays(DateHelpers.currentDateTime("dd-MMM-yyyy"), days, "dd-MMM-yyyy", format);
	}

	public static String getDateAfterXBusinessDays(String date, int days, String inputFormat, String outputFormat) {
		Calendar startCal = Calendar.getInstance();
		try {
			startCal.setTime(new SimpleDateFormat(inputFormat).parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int i = 0;

		if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			startCal.add(Calendar.DAY_OF_WEEK, 2);
		} else if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			startCal.add(Calendar.DAY_OF_WEEK, 1);
		}

		if (days >= 0) {
			while (i < days) {
				startCal.add(Calendar.DAY_OF_YEAR, 1);
				if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					i++;
				}
			}
		} else {
			while (i > days) {

				startCal.add(Calendar.DAY_OF_YEAR, -1);
				if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					i--;
				}
			}
		}

		SimpleDateFormat form = new SimpleDateFormat(outputFormat);

		return form.format(startCal.getTime());
	}

	public static String getDateAfterXBusinessDaysForACH(int days) {

		Calendar calendar = Calendar.getInstance();

		String newDate = "";
		int i = 0;

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			calendar.add(Calendar.DAY_OF_WEEK, 2);
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.DAY_OF_WEEK, 1);
		}

		while (i < days) {

			calendar.add(Calendar.DAY_OF_YEAR, 1);
			if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				i++;
			}
		}

		newDate = new SimpleDateFormat("yyMMdd").format(calendar.getTime());

		System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
				+ "getDatePlusBusinessDaysForDateChangeTests finish");

		return newDate;
	}

	public static String getDateAfterXBusinessDaysForCSVusAPI(int days) {

		Calendar startCal = Calendar.getInstance();
		int i = 0;
		// if starting on a weekend, Monday doesn't get counted for settlement
		// date
		if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			startCal.add(Calendar.DAY_OF_WEEK, 2);
		} else if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			startCal.add(Calendar.DAY_OF_WEEK, 1);
		}

		while (i < days) {

			startCal.add(Calendar.DAY_OF_YEAR, 1);
			if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				i++;
			}
		}

		SimpleDateFormat form = new SimpleDateFormat("MMddyyyy");

		return form.format(startCal.getTime());
	}

	public static String getFeeBillDateAndTime() {
		return currentDateTime("yyyy-MM-dd HH:mm");
	}

	public static String convertServerDateToTexturaDate(String date) {
		return convertDate(date, "MM/dd/yyyy", "dd-MMM-yyyy");
	}

	public static String getMonthEndDateAfterXMonthsGeneric(int x, String format) {
		x = x + 1;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, x);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		if (sdf.format(c.getTime()).equals(DateHelpers.getCurrentTexturaDate())) {
			c.add(Calendar.MONTH, 2);
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.add(Calendar.DAY_OF_MONTH, -1);
		}
		return sdf.format(c.getTime());
	}

	/**
	 * dd-MMM-yyyy format
	 */
	public static String getMonthEndDateAfterXMonths(int x) {
		return getMonthEndDateAfterXMonthsGeneric(x, "dd-MMM-yyyy");
	}

	/**
	 * yyyy-MM-dd format
	 */
	public static String getMonthEndDateAfterXMonthsIMS(int x) {
		return getMonthEndDateAfterXMonthsGeneric(x, "yyyy-MM-dd");
	}

	public static String getCurrentRAODate() {
		return currentDateTime("yyMMdd");
	}

	/**
	 * parameter1: date1 format in MM/dd/yyyy parameter2: date2 format in
	 * MM/dd/yyyy returns: boolean
	 */
	public static boolean isDate1BeforeDate2(String date1, String date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date first = null;
		Date second = null;
		try {
			first = sdf.parse(date1);
			second = sdf.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (second.after(first)) {
			return true;
		}
		return false;
	}

	/**
	 * parameter1: date1 format in MM/dd/yyyy parameter2: date2 format in
	 * MM/dd/yyyy returns: boolean
	 */
	public static boolean isDate1AfterDate2(String date1, String date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date first = null;
		Date second = null;
		try {
			first = sdf.parse(date1);
			second = sdf.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (first.after(second)) {
			return true;
		}
		return false;

	}

	public static int getLastDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return lastDayOfMonth;
	}

	/**
	 * Returns current date in the following format yyyyMMdd
	 */
	public static String getCurrentDateNew() {
		return currentDateTime("yyyyMMdd");
	}

	public static String getCurrentDateForImport() {
		return currentDateTime("MMddyyyy");
	}

	/**
	 * Adds months to the date formatted as 'MMyy'
	 * 
	 */
	public static String getDateAfterXMonths(int months) {
		try {
			String dt = DateHelpers.currentDateTime("MMyy");
			SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dt));
			c.add(Calendar.MONTH, +months);
			return sdf.format(c.getTime());
		} catch (Exception p) {
			p.printStackTrace();
			return null;
		}
	}

	public static String getDateAfterXMonthsFormat(int months, String format) {
		try {
			String dt = DateHelpers.currentDateTime(format);
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dt));
			c.add(Calendar.MONTH, +months);
			return sdf.format(c.getTime());
		} catch (Exception p) {
			p.printStackTrace();
			return null;
		}
	}

	public static long getCurrentDateYearInUNIX() {
		return Instant.now().toEpochMilli();
	}

	/**
	 * rolls over weekends to Monday - uses "yyyy-MM-dd" date format
	 */
	public static String addDaysIMS(String date, int days) {
		return addDaysIMS(date, days, "yyyy-MM-dd");
	}

	/**
	 * rolls over weekends to Monday
	 */
	public static String addDaysIMS(String date, int days, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			c.add(Calendar.DAY_OF_YEAR, days); // either going to be Calendar.DATE or DAY_OF_YEAR
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				c.add(Calendar.DAY_OF_YEAR, 2);
			} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";

	}

	public static String addDaysForIMSPaymentDate(String date, String today, int days, int padding, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Calendar c = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			c2.setTime(sdf.parse(today));
			int dateDiff = getDateDifference((GregorianCalendar) c2, (GregorianCalendar) c);
			if (dateDiff < padding) {
				String dateAfterPadding = getDateAfterXBusinessDays(padding, format);
				c.setTime(sdf.parse(dateAfterPadding));
			} else {
				c.add(Calendar.DAY_OF_YEAR, days); // either going to be Calendar.DATE or DAY_OF_YEAR
			}
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				c.add(Calendar.DAY_OF_YEAR, 2);
			} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	/**
	 * rolls over weekends to Friday
	 */
	public static String subtractDaysIMS(String date, int days) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			c.add(Calendar.DAY_OF_YEAR, days); // either going to be Calendar.DATE or DAY_OF_YEAR
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				c.add(Calendar.DAY_OF_YEAR, -1);
			} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_YEAR, -2);
			}
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static boolean validateDateFormatMMMDD(String strDate) {
		String pattern = "MMM dd";
		return validateTimeFormat(strDate, pattern);
	}

	public static boolean validateDateFormatMMM(String strDate) {
		String pattern = "MMM";
		return validateTimeFormat(strDate, pattern);
	}

	public static boolean isMessageDateFormatDayDDMMYYYY(String strDate) {
		String pattern = "EEEE, dd-MMM-yyyy hh:mm:ss a";
		return validateTimeFormat(strDate, pattern);
	}

	public static boolean isDateFormatDayYYYYMMddhhmmss(String strDate) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		return validateTimeFormat(strDate, pattern);
	}

	/**
	 * Returns the date before X days have passed from the current date
	 * 
	 * @param days
	 * @param format
	 * @return
	 */
	public static String getDateBeforeXDays(String date, int days, String inputFormat, String outputFormat) {
		String newDate = null;
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat(inputFormat).parse(date));
			c.add(Calendar.DATE, -days);
			newDate = new SimpleDateFormat(outputFormat).format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return newDate;

	}

	public static int getLastDateOfMonth() {
		Calendar cal = Calendar.getInstance();
		int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
		return lastDayOfMonth;
	}

	public static String getPreviousMonth(int days) {
		try {
			String dt = DateHelpers.currentDateTime("MMM");
			SimpleDateFormat sdf = new SimpleDateFormat("MMM");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dt));
			c.add(Calendar.MONTH, -days);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "null";
	}

	/**
	 * Returns the number of calendar days until the next date that falls on the desired day of week.
	 * 
	 * Example: desiredDayOfWeek = Calendar.Monday, date = 12-Mar-2019, inputDateFormat= dd-MMM-yyyy,
	 * returns 6
	 * 
	 * 0 is returned if the current day is the desired day of week.
	 * 
	 * @param desiredDayOfWeek
	 *            Desired day of week eg, Calendar.Monday
	 * @param date
	 *            Starting date
	 * @param inputDateFormat
	 *            Format of date parameter
	 * 
	 * @return
	 */
	public static int getDaysUntilNextDayOfWeek(int desiredDayOfWeek, String date, String inputDateFormat) {
		SimpleDateFormat inputSdf = new SimpleDateFormat(inputDateFormat);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(inputSdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int daysToAdd = (Calendar.SATURDAY - currentDayOfWeek) - (Calendar.SATURDAY - desiredDayOfWeek);
		if (daysToAdd < 0) {
			daysToAdd += 7;
		}
		return daysToAdd;
	}

	/**
	 * getNextDayOfWeek with date format of dd-MMM-yyyy
	 * 
	 */
	public static int getDaysUntilNextDayOfWeekTexturaDateFormat(int desiredDayOfWeek, String date) {
		return getDaysUntilNextDayOfWeek(desiredDayOfWeek, date, "dd-MMM-yyyy");
	}

	/**
	 * Returns the next date(or current date) that falls on the desired day of week.
	 * 
	 * Example: desiredDayOfWeek = Calendar.Monday, date = 12-Mar-2019, inputDateFormat= dd-MMM-yyyy, outputDateFormat= dd-MMM-yyyy,
	 * returns 18-Mar-2019
	 * 
	 * The current datei returned if the current day is the desired day of week.
	 * 
	 * @param desiredDayOfWeek
	 *            Desired day of week eg, Calendar.Monday
	 * @param date
	 *            Starting date
	 * @param inputDateFormat
	 *            Format of date parameter
	 * @param outputDateFormat
	 *            Format of returned date
	 * @return
	 */
	public static String getNextDayOfWeek(int desiredDayOfWeek, String date, String inputDateFormat, String outputDateFormat) {

		SimpleDateFormat inputSdf = new SimpleDateFormat(inputDateFormat);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(inputSdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int daysToAdd = getDaysUntilNextDayOfWeek(desiredDayOfWeek, date, inputDateFormat);
		c.add(Calendar.DATE, daysToAdd);
		SimpleDateFormat outputSdf = new SimpleDateFormat(outputDateFormat);

		return outputSdf.format(c.getTime());
	}

	public static String getNextDayOfWeekTexturaDateFormat(int desiredDayOfWeek, String date) {
		return getNextDayOfWeek(desiredDayOfWeek, date, "dd-MMM-yyyy", "dd-MMM-yyyy");
	}
}
