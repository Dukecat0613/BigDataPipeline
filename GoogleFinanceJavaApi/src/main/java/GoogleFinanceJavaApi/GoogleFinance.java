package GoogleFinanceJavaApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

public class GoogleFinance {
	public final static Map<String, String> googleFinanceKeyToFullName = new HashMap<>();
	static {
	googleFinanceKeyToFullName.put(new String("id"), "ID");
	googleFinanceKeyToFullName.put("t", "StockSymbol");
	googleFinanceKeyToFullName.put("e", "Index");
	googleFinanceKeyToFullName.put("l","LastTradePrice");
	googleFinanceKeyToFullName.put("l_cur","LastTradeWithCurrency");
	googleFinanceKeyToFullName.put("ltt","LastTradeTime");
	googleFinanceKeyToFullName.put("lt_dts","LastTradeDateTime");
	googleFinanceKeyToFullName.put("lt","LastTradeDateTimeLong");
	googleFinanceKeyToFullName.put("div","Dividend");
	googleFinanceKeyToFullName.put("yld","Yield");
	googleFinanceKeyToFullName.put("c","Change");
	googleFinanceKeyToFullName.put("s","LastTradeSize");
	googleFinanceKeyToFullName.put("c","ChangePercent");
	googleFinanceKeyToFullName.put("el","ExtHrsLastTradePrice");
	googleFinanceKeyToFullName.put("el_cur","ExtHrsLastTradeWithCurrency");
	googleFinanceKeyToFullName.put("elt","ExtHrsLastTradeDateTimeLong");
	googleFinanceKeyToFullName.put("ec","ExtHrsChange");
	googleFinanceKeyToFullName.put("ecp","ExtHrsChangePercent");
	googleFinanceKeyToFullName.put("pcls_fix","PreviousClosePrice");
	}

	public static String buildUrl(String symbol) {
		return "http://finance.google.com/finance/info?client=ig&q=" + symbol;
	}

	public static String buildNewsUrl(String symbol) {
		return "http://www.google.com/finance/company_news?output=json&q=" + symbol + "&start=0&num=1000";
	}

	public static String request(String symbol) {
		String url = buildUrl(symbol);
		String content = getWebSource(url);
		try {
			return content.substring(5, content.length() - 2);
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
	}

	public static String getWebSource(String link) {
		try {
			URL url = new URL(link);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static Map<String, String> replaceKeys(JSONObject content) {
		Map<String, String> res = new HashMap<>();
		for (String q: content.keySet()) {
            if (googleFinanceKeyToFullName.keySet().contains(q)) {
            	res.put(googleFinanceKeyToFullName.get(q), content.get(q).toString());
			}
		}
		return res;
	}

	public static Map<String, String> getQuotes(String symbol) {
		return replaceKeys(new JSONObject(request(symbol)));
	}

}
