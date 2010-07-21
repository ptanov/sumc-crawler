package eu.tanov.sumc.crawler.util;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class WebElementHelper {
	private static final String TAG_INPUT = "input";
	private static final String TAG_SELECT = "select";
	private static final List<String> VALUE_ELEMENTS = Arrays.asList(TAG_INPUT, TAG_SELECT);

	//helper, without instance
	private WebElementHelper() {}

	public static List<WebElement> getSelectOptions(WebElement select) {
		return select.findElements(By.tagName("option")); 
	}
	
	/**
	 * Stupid way to get value or text of element? Because getText() does not
	 * return correct values in some cases
	 * 
	 * TODO find better solution
	 * XXX how to check value of labeledInputs?
	 */
	public static String getTextValue(final WebElement element) {
		if (VALUE_ELEMENTS.contains(element.getTagName().toLowerCase())) {
			return element.getValue();
		}
		// some other element or group of elements (span with radio/check box
		// and label)
		String result = element.getText();
		if ("".equals(result)) {
			// get through java script, because getText() does not work
			// correctly here
			result = (String) ((JavascriptExecutor) element).executeScript(
					"return arguments[0].textContent", element);
			// &nbsp; is converted to A0, fixing with this:
			return result.replaceAll("\\xA0", " ");
		}

		return result;
	}
	
	public static void setValue(WebElement element, String value) {
		if (TAG_SELECT.equals(element.getTagName())) {
			setValueSelect(element, value);
//		} else if (TAG_INPUT.equals(element.getTagName())) {
//			setValueInput();
		} else {
			throw new IllegalArgumentException("Unknown type of "+toString(element)+" with new value: "+value);
		}
	}

	private static void setValueSelect(WebElement select, String value) {
		final List<WebElement> availableOptions = getSelectOptions(select); 

		for (WebElement option : availableOptions) {
			if (value.equals(WebElementHelper.getTextValue(option))) {
				option.setSelected();
				return;
			}
		}
		
		throw new IllegalArgumentException(value+" not found in "+WebElementHelper.webElementsToString(availableOptions));
	}

	public static String toString(WebElement webElement) {
		final String id = getElementId(webElement);
		if (id != null) {
			return String.format("<%s id=\"%s\" (...) />", webElement.getTagName(), id);
		} else {
			return String.format("<%s (...) >%s</%s>", webElement.getTagName(), getInnerHtml(webElement), webElement.getTagName());
		}
		
	}

	public static String getElementId(WebElement webElement) {
		return webElement.getAttribute("id");
	}

	public static String getInnerHtml(final WebElement element) {
		return (String) ((JavascriptExecutor) element).executeScript(
				"return arguments[0].innerHTML", element);
	}

	public static String webElementsToString(List<WebElement> elements) {
		if (elements.size() == 0) {
			return "no elements";
		}
		final StringBuilder result = new StringBuilder();

		result.append("\n");
		for (WebElement webElement : elements) {
			result.append("\n");
			result.append(toString(webElement));
		}

		return result.toString();
	}

}
