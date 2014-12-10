package internetResource.financialReport;

import org.apache.http.client.HttpClient;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class FinancialReportResourceFromDart {

	HttpClient client = null;
	static HtmlCleaner cleaner;
	
	static {
		cleaner = new HtmlCleaner();
	}
	
	static String DART_OPENAPI_LIST_URL(String id) {
		return "http://dart.fss.or.kr/api/search.xml?auth=13dad8e0ffce40f399f76e45ba82dba91345db4f&start_dt=19990101&dsp_tp=A&bsn_tp=A003&sort=date&series=asc&crp_cd=" + id;
	}
	
	static String ITEM_ID_URL(String id) {
		return "http://englishdart.fss.or.kr/dsbh002/viewer.do?rcpNo=20141114001425";
	}
	
	static TagNode node(Object org) {
		return (TagNode)org;
	}

}
