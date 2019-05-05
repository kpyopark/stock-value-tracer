package internetResource.financialReport;

import org.apache.http.client.HttpClient;
import org.htmlcleaner.HtmlCleaner;

public class FinancialReportResourceFromKrx {

	HttpClient client = null;
	static HtmlCleaner cleaner;
	
	static {
		cleaner = new HtmlCleaner();
	}
	
	
}
