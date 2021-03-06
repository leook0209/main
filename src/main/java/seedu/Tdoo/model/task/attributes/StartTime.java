package seedu.Tdoo.model.task.attributes;

import seedu.Tdoo.commons.exceptions.IllegalValueException;

/**
 * Represents a Event's start time in the TodoList. Guarantees: immutable; is
 * valid as declared in {@link #isValidStartTime(String)}
 */
public class StartTime {

	public static final String MESSAGE_STARTTIME_CONSTRAINTS = "Event Start time should be written in this format, must be 4 digits '10:00' and within 24 hrs format (0000 to 2359)";
	public static final String STARTTIME_VALIDATION_REGEX = "^(\\d{2}:\\d{2})$";

	public final String startTime;
	public final String saveStartTime;

	/**
	 * Validates given start time.
	 *
	 * @throws IllegalValueException
	 *             if given start time is invalid.
	 */
	public StartTime(String startTime) throws IllegalValueException {
		assert startTime != null;
		startTime = startTime.trim();
		saveStartTime = startTime.trim();
		if (!isValidStartTime(startTime)) {
			throw new IllegalValueException(MESSAGE_STARTTIME_CONSTRAINTS);
		}

		// Checking time in 24-Hr format
		String[] stimeArr = startTime.split(":");
		String hour = "";
		switch (stimeArr[0]) {
		case "00":
			hour = "12:";
			break;
		case "23":
			hour = "11:";
			break;
		case "22":
			hour = "10:";
			break;
		case "21":
			hour = "9:";
			break;
		case "20":
			hour = "8:";
			break;
		case "19":
			hour = "7:";
			break;
		case "18":
			hour = "6:";
			break;
		case "17":
			hour = "5:";
			break;
		case "16":
			hour = "4:";
			break;
		case "15":
			hour = "3:";
			break;
		case "14":
			hour = "2:";
			break;
		case "13":
			hour = "1:";
			break;
		default:
			hour = stimeArr[0] + ":";
		}
		if (Integer.parseInt(stimeArr[0]) > 11) {
			startTime = hour + stimeArr[1] + "pm";
			this.startTime = startTime;
		} else {
			startTime = hour + stimeArr[1] + "am";
			this.startTime = startTime;
		}
	}

	/**
	 * Returns if a given string is a valid event start time.
	 */
	public static boolean isValidStartTime(String starttime) {
		String[] stimeArr = starttime.split(":");
		boolean checkTime = true;

		// Check if time has hour and min, hour not more than 24hrs and min not
		// more than 59mins
		if (stimeArr.length < 2 || Integer.parseInt(stimeArr[0]) > 23 || Integer.parseInt(stimeArr[1]) > 59) {
			checkTime = false;
		}

		if (starttime.matches(STARTTIME_VALIDATION_REGEX) && checkTime) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return startTime;
	}

	@Override
	public boolean equals(Object other) {
		return other == this // short circuit if same object
				|| (other instanceof StartTime // instanceof handles nulls
						&& this.startTime.equals(((StartTime) other).startTime)); // state
																					// check
	}

	@Override
	public int hashCode() {
		return startTime.hashCode();
	}

}
