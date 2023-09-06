package com.tarento.upsmf.examsAndAdmissions.util;

import com.tarento.upsmf.examsAndAdmissions.exceptions.ProjectCommonException;
import com.tarento.upsmf.examsAndAdmissions.model.ResponseDto;
import com.tarento.upsmf.examsAndAdmissions.model.ResponseParams;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class will contains all the common utility methods.
 *
 * @author Manzarul
 */
public class ProjectUtil {

	public static ProjectLogger logger = new ProjectLogger();

	public static PropertiesCache propertiesCache;

	static {
		propertiesCache = PropertiesCache.getInstance();
	}

	public static String getConfigValue(String key) {
		if (StringUtils.isNotBlank(System.getenv(key))) {
			return System.getenv(key);
		}
		return propertiesCache.readProperty(key);
	}

	/**
	 * This method will check incoming value is null or empty it will do empty check
	 * by doing trim method. in case of null or empty it will return true else
	 * false.
	 *
	 * @param value
	 * @return
	 */
	public static boolean isStringNullOREmpty(String value) {
		return (value == null || "".equals(value.trim()));
	}

	/**
	 * This method will create and return server exception to caller.
	 *
	 * @param responseCode ResponseCode
	 * @return ProjectCommonException
	 */
	public static ProjectCommonException createServerError(ResponseCode responseCode) {
		return new ProjectCommonException(responseCode.getErrorCode(), responseCode.getErrorMessage(),
				Constants.SERVER_ERROR);
	}

	public static ProjectCommonException createClientException(ResponseCode responseCode) {
		return new ProjectCommonException(responseCode.getErrorCode(), responseCode.getErrorMessage(),
				Constants.CLIENT_ERROR);
	}

	public static ResponseDto createDefaultResponse(String api) {
		ResponseDto response = new ResponseDto();
		response.setId(api);
		response.setVer(Constants.API_VERSION_1);
		response.setParams(new ResponseParams());
		response.getParams().setStatus(Constants.SUCCESSFUL);
		response.setResponseCode(HttpStatus.OK);
		response.setTs(DateTime.now().toString());
		return response;
	}

	public static Map<String, String> getDefaultHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
		return headers;
	}

	public enum Method {
		GET, POST, PUT, DELETE, PATCH
	}

	public static String convertSecondsToHrsAndMinutes(int seconds) {
		String time = "";
		if (seconds > 60) {
			int min = (seconds / 60) % 60;
			int hours = (seconds / 60) / 60;
			String minutes = (min < 10) ? "0" + min : Integer.toString(min);
			String strHours = (hours < 10) ? "0" + hours : Integer.toString(hours);
			if (min > 0 && hours > 0)
				time = strHours + "h " + minutes + "m";
			else if (min == 0 && hours > 0)
				time = strHours + "h";
			else if (min > 0) {
				time = minutes + "m";
			}
		}
		return time;
	}

	public static String firstLetterCapitalWithSingleSpace(final String words) {
		return Stream.of(words.trim().split("\\s")).filter(word -> word.length() > 0)
				.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" "));
	}
	/**
	 * Check the email id is valid or not
	 *
	 * @param email String
	 * @return Boolean
	 */

	public static Boolean validateEmailPattern(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Boolean retValue = Boolean.FALSE;
		Pattern pat = Pattern.compile(emailRegex);
		if (pat.matcher(email).matches()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Check the contact number is valid or not
	 *
	 * @param contactNumber String
	 * @return Boolean
	 */

	public static Boolean validateContactPattern(String contactNumber) {
		String contactNumberRegex = "^\\d{10}$";
		Pattern pat = Pattern.compile(contactNumberRegex);
		if (pat.matcher(contactNumber).matches()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public static Boolean validateFullName(String firstName ) {
		return firstName.matches("^[a-zA-Z]+(?:['\\s][a-zA-Z]+)*(?<!\\.|\\s)$");
	}

	public static Boolean validateTag(List<String> tags) {
		for (String tag : tags) {
			if (!tag.matches("^[a-zA-Z]+(?: [a-zA-Z]+)*$")) {
				return false;
			}
		}
		return true;
	}

	public static Boolean validateExternalSystemId(String externalSystemId) {
		return externalSystemId.matches("^(?=.{1,30}$)[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*$");
	}

	public static Boolean validateExternalSystem(String externalSystem) {
		return externalSystem.matches("[a-zA-Z ]{0,255}$");
	}
}