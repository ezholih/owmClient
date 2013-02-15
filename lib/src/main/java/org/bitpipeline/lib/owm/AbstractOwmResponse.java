package org.bitpipeline.lib.owm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

abstract class AbstractOwmResponse {
	static private final String JSON_COD = "cod";
	static private final String JSON_MESSAGE = "message";
	static protected final String JSON_CALCTIME = "calctime";
	static private final String JSON_CALCTIME_TOTAL = "total";
	static protected final String JSON_LIST = "list";


	private final int code;
	private final String message;
	private final double calctime;

	public AbstractOwmResponse (JSONObject json) {
		this.code = json.optInt (AbstractOwmResponse.JSON_COD, Integer.MIN_VALUE);
		this.message = json.optString (AbstractOwmResponse.JSON_MESSAGE);
		String calcTimeStr = json.optString (AbstractOwmResponse.JSON_CALCTIME);
		double calcTimeTotal = Double.NaN;
		if (!calcTimeStr.isEmpty ()) {
			try {
				calcTimeTotal = Double.valueOf (calcTimeStr); 
			} catch (NumberFormatException nfe) { // So.. it's not a number.. let's see if we can still find it's value.
				String totalCalcTimeStr = AbstractOwmResponse.getValueStrFromCalcTimePart (calcTimeStr, AbstractOwmResponse.JSON_CALCTIME_TOTAL);
				if (totalCalcTimeStr != null) {
					try {
						calcTimeTotal = Double.valueOf (totalCalcTimeStr);
					} catch (NumberFormatException nfe2) {
						// we tried... tried and fail. Oh despair!
					}
				}
			}
		}
		this.calctime = calcTimeTotal;
	}

	public boolean hasCode () {
		return this.code != Integer.MIN_VALUE;
	}
	public int getCode () {
		return this.code;
	}

	public boolean hasMessage () {
		return this.message != null && !this.message.isEmpty ();
	}
	public String getMessage () {
		return this.message;
	}

	public boolean hasCalcTime () {
		return !Double.isNaN (this.calctime);
	}
	public double getCalcTime () {
		return this.calctime;
	}

	static String getValueStrFromCalcTimePart (final String calcTimeStr, final String part) {
		Pattern keyValuePattern = Pattern.compile (part + "\\s*=\\s*([\\d\\.]*)");
		Matcher matcher = keyValuePattern.matcher (calcTimeStr);
		if (matcher.find () && matcher.groupCount () == 1) {
			return matcher.group (1);
		}
		return null;
	}

	static double getValueFromCalcTimeStr (final String calcTimeStr, final String part) {
		if (calcTimeStr == null || calcTimeStr.isEmpty ())
			return Double.NaN;
		double value = Double.NaN;
		String valueStr = AbstractOwmResponse.getValueStrFromCalcTimePart (calcTimeStr, part);
		try {
			value = Double.valueOf (valueStr);
		} catch (NumberFormatException nfe) {
			// Unparsable double... just leave it as NaN
		}
		return value;
	}


}