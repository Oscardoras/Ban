package org.bungeeplugin.ban.command;

import java.util.Date;

public enum Unit {
	
	s(1000l), m(60000l), h(3600000l), d(86400000l), M(2592000000l), y(31557600000l);
	
	public final long milliseconds;
	
	private Unit(long milliseconds) {
		this.milliseconds = milliseconds;
	}
	
	public static long getTime(String arg) {
		try {
			if (arg.equalsIgnoreCase("forlife")) return 0l;
			Unit unit = Unit.valueOf(arg.substring(arg.length() - 1));
			return new Date().getTime() + (Long.parseLong(arg.substring(0, arg.length() - 1)) * unit.milliseconds);
		} catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
}