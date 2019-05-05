package robot.listenter;

import post.CompanyEx;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import post.Stock;
import post.StockEstimated;
import robot.IUpdateListener;

public class ExamUpdateListener implements IUpdateListener {

	public void companyChanged(CompanyEx company, Throwable err) {
		if ( company != null ) {
			if ( err != null  ) { 
				System.out.println(company.getId() + "[" + company.getName() + "] 정보 수정중 에러 발생[" + err.getMessage() + "]" );
				err.printStackTrace();
			} else {
				System.out.println(company.getId() + "[" + company.getName() + "] 회사정보 변경" );
			}
		}
	}

	public void companyFinancialStatusChanged(CompanyFinancialStatus cfs,
			Throwable err) {
		if ( cfs != null ) {
			if ( err != null  ) { 
				System.out.println(cfs.getCompany().getId() + "[" + cfs.getCompany().getName() + "] [" + cfs.getStandardDate() + "] 재무재표 정보 수정중 에러 발생[" + err.getMessage() + "]" );
				err.printStackTrace();
			} else {
				System.out.println(cfs.getCompany().getId() + "[" + cfs.getCompany().getName() + "] [" + cfs.getStandardDate() + "] 재무재표 정보 변경" );
			}
		}
	}

	public void companyFinancialStatusEstimatedChanged(
			CompanyFinancialStatusEstimated cfe, Throwable err) {
		if ( cfe != null ) {
			if ( err != null  ) { 
				System.out.println(cfe.getCompany().getId() + "[" + cfe.getCompany().getName() + "] [" + cfe.getStandardDate() + "] 재무재표 추산치 정보 수정중 에러 발생[" + err.getMessage() + "]" );
				err.printStackTrace();
			} else {
				System.out.println(cfe.getCompany().getId() + "[" + cfe.getCompany().getName() + "] [" + cfe.getStandardDate() + "] 재무재표 추산치 정보 변경" );
			}
		}
	}

	public void stockEstimationChanged(StockEstimated cse, Throwable err) {
		if ( cse != null ) {
			if ( err != null  ) { 
				System.out.println(cse.getCompany().getId() + "[" + cse.getCompany().getName() + "] [" + cse.getStandardDate() + "] 주가 추산치 정보 수정중 에러 발생[" + err.getMessage() + "]" );
				err.printStackTrace();
			} else {
				System.out.println(cse.getCompany().getId() + "[" + cse.getCompany().getName() + "] [" + cse.getStandardDate() + "] 주가 추산치 정보 변경" );
			}
		}
	}

	public void stockValueChanged(Stock stock, Throwable err) {
		if ( stock != null ) {
			if ( err != null  ) { 
				System.out.println(stock.getCompany().getId() + "[" + stock.getCompany().getName() + "] [" + stock.getStandardDate() + "] 주가 정보 수정중 에러 발생[" + err.getMessage() + "]" );
				err.printStackTrace();
			} else {
				System.out.println(stock.getCompany().getId() + "[" + stock.getCompany().getName() + "] [" + stock.getStandardDate() + "] 주가 정보 변경" );
			}
		}
	}

}
