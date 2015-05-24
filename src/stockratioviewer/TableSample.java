package stockratioviewer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import post.Company;
import post.CompanyEx;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import post.Stock;
import post.StockEstimated;
import robot.IUpdateListener;
import analyzer.StockAnalyzerManager;

public class TableSample implements IUpdateListener {
	
	Display disp = null;
	Shell shell = null;
	
	Tree treeStatus;
	TreeItem textNode1;
	Button updateFinanceButton,updateButton,updateAllButton,refreshButton,estimButton,updateCompanyButton;
	Group treeGroup, buttonGroup;

	private StockAnalyzerManager manager = null;
	private ArrayList<CompanyEx> companyList = null;
	
	public TableSample() {
	}
	
	String[] columnTitles = { 
			"No", "Name" ,"ID" ,
			"C_S","F_S","S_S","FE_S","SE_S",
			"제무제표기준일", "PER RANK" ,"ROA RANK", "ROI RANK", "ROE RANK", "TOT RANK", 
			"PER", "12M PER", "ROA", "ROI", "ROE", "배당율", 
			"주가기준일", "최근주가" };
	
	public void init() {
		
		disp = new Display();
		shell = new Shell(disp);
		shell.setSize(900, 500);
		
		// 전체 화면에 대한 Layout을 잡는다. 
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.wrap = false;
		layout.pack = true;
		layout.justify = true;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.spacing = 0;
		shell.setLayout(layout);
		
		// 트리 그룹
		treeGroup = new Group(shell,SWT.NONE);
		RowData treeData = new RowData();
		treeData.height = 500;
		treeData.exclude = false;
		treeGroup.setLayoutData(treeData);
		treeGroup.pack(true);
		
		// 버튼 그룹
		buttonGroup = new Group(shell,SWT.NONE);
		RowData buttonData = new RowData();
		buttonData.height = 30;
		buttonGroup.setLayoutData(buttonData);
		//buttonGroup.pack(true);
		
		// 트리를 만듭니다.
		treeStatus = new Tree(treeGroup,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		treeGroup.setLayout(new FillLayout (SWT.VERTICAL));
		//treeGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		
		// 버튼을 만듭니다.
		buttonGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		RowData data = new RowData(80,25);
		updateButton = new Button(buttonGroup,SWT.PUSH);
		updateButton.setLayoutData(data);
		updateButton.setText("자료받기");
		updateButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable(){
					public void run() {
						//manager.startCompanyFinancialStatusUpdator();
						manager.startStockValueUpdator();
						manager.startAnnualEstimationUpdator();
						manager.startStockValueEstimationUpdator();
						//manager.startStockAnalyzer();
					}
				}).start();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		updateFinanceButton = new Button(buttonGroup,SWT.PUSH);
		updateFinanceButton.setLayoutData(data);
		updateFinanceButton.setText("재표수정");
		updateFinanceButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable(){
					public void run() {
						manager.startCompanyFinancialStatusUpdator();
						//manager.startStockValueUpdator();
						//manager.startAnnualEstimationUpdator();
						//manager.startStockValueEstimationUpdator();
						//manager.startStockAnalyzer();
					}
				}).start();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		updateCompanyButton = new Button(buttonGroup,SWT.PUSH);
		updateCompanyButton.setLayoutData(data);
		updateCompanyButton.setText("목록갱신");
		updateCompanyButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable(){
					public void run() {
						manager.startCompanyListUpdator();
					}
				}).start();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		updateAllButton = new Button(buttonGroup,SWT.PUSH);
		updateAllButton.setLayoutData(data);
		updateAllButton.setText("전체계산");
		updateAllButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable(){
					public void run() {
						manager.startCompanyFinancialStatusUpdator();
						manager.startStockValueUpdator();
						manager.startAnnualEstimationUpdator();
						manager.startStockValueEstimationUpdator();
						manager.startStockAnalyzer();
					}
				}).start();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});

		estimButton = new Button(buttonGroup, SWT.PUSH);
		estimButton.setLayoutData(data);
		estimButton.setText("추정계산");
		estimButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable(){
					public void run() {
						manager.startAnnualEstimationUpdator();
						manager.startStockValueEstimationUpdator();
						manager.startStockAnalyzer();
					}
				}).start();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		refreshButton = new Button(buttonGroup,SWT.PUSH);
		refreshButton.setLayoutData(data);
		refreshButton.setText("화면갱신");
		refreshButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				showCompanyList();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		buttonGroup.pack();

		// 트리 컬럼
		for (int i = 0; i < columnTitles.length; i++) {
			TreeColumn treeColumn = new TreeColumn(treeStatus, SWT.NONE);
			treeColumn.setText(columnTitles[i]);
			treeColumn.pack();
		}
		treeStatus.setSortColumn(treeStatus.getColumn(0));
		treeStatus.setHeaderVisible(true);
		treeStatus.setLinesVisible(true);
		
		shell.addControlListener(new ControlListener() {

			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
			}

			public void controlResized(ControlEvent e) {
				// TODO Auto-generated method stub
				treeGroup.pack();
				buttonGroup.pack();
			}
			
		});
		
		shell.pack();
		
	}
	
	public void companyChanged(final CompanyEx company, Throwable err) {
		final int rowCount = getRowCount(company);
		disp.syncExec( new Runnable() {
			public void run() {
				if ( rowCount >= 0 ) {
					TreeItem item = treeStatus.getItem(rowCount);
					item.setBackground(3, new Color(Display.getCurrent(),0,50,50));
					item.setText(3, "회사명변경");
				} else {
					companyList.add(company);
					showCompanyList();
				}
			}
		});
	}

	public void companyFinancialStatusChanged(CompanyFinancialStatus cfs, Throwable err) {
		final int rowCount = getRowCount(cfs.getCompany());
		if ( rowCount >= 0 ) {
			disp.syncExec( new Runnable() {
				public void run() {
					TreeItem item = treeStatus.getItem(rowCount);
					item.setBackground(4, new Color(Display.getCurrent(),0,100,50));
					item.setText(4, "재무상태변경");
				}
			});
		}
	}

	public void stockValueChanged(Stock stock, Throwable err) {
		final int rowCount = getRowCount(stock.getCompany());
		if ( rowCount >= 0 ) {
			disp.syncExec( new Runnable() {
				public void run() {
					TreeItem item = treeStatus.getItem(rowCount);
					item.setBackground(5, new Color(Display.getCurrent(),0,100,100));
					item.setText(5, "주가변경");
				}
			});
		}
	}
	
	public void companyFinancialStatusEstimatedChanged(CompanyFinancialStatusEstimated cfe, Throwable err) {
		final int rowCount = getRowCount(cfe.getCompany());
		if ( rowCount >= 0 ) {
			disp.syncExec( new Runnable() {
				public void run() {
					TreeItem item = treeStatus.getItem(rowCount);
					item.setBackground(6, new Color(Display.getCurrent(),0,150,100));
					item.setText(6, "재무추정변경");
				}
			});
		}
	}

	public void stockEstimationChanged(StockEstimated cse, Throwable err) {
		final int rowCount = getRowCount(cse.getCompany());
		if ( rowCount >= 0 ) {
			disp.syncExec( new Runnable() {
				public void run() {
					TreeItem item = treeStatus.getItem(rowCount);
					item.setBackground(7, new Color(Display.getCurrent(),0,150,150));
					item.setText(7, "주가추정변경");
				}
			});
		}
	}

	public int getRowCount(Company company) {
		int rtn=-1;
		for ( int cnt = 0 ; cnt < companyList.size() ; cnt++ ) {
			if ( companyList.get(cnt).getId().equals(company.getId()) ) rtn = cnt; 
		}
		return rtn;
	}
	
	public void setStatusData() {
		this.companyList = manager.getCompanyList();
	}
	
	public void showCompanyList() {
		for ( int cnt = 0 ; companyList != null && cnt < companyList.size(); cnt++ ) {
			TreeItem treeItem = new TreeItem(treeStatus,SWT.NONE);
			treeItem.setText(0, (cnt+1) +"");
			treeItem.setText(1, companyList.get(cnt).getName() );
			treeItem.setText(2, companyList.get(cnt).getId() );
			treeItem.setData(companyList.get(cnt));
		}
	}
	
	public void setStockAnalyzerManager(StockAnalyzerManager manager) {
		this.manager = manager;
	}
	
	public void show() {
		shell.open();
		while (! shell.isDisposed()) {
			if (! disp.readAndDispatch()) disp.sleep();
		}
		disp.dispose();
	}
	
	public static void main(String[] args) {
		
		Properties props = System.getProperties();
		Enumeration<Object> keys = props.keys();
		for ( String key = (String)keys.nextElement() ; keys.hasMoreElements() ; key = (String)keys.nextElement() ) {
			System.out.println(key +":" + props.getProperty(key));
		}
		
		TableSample sample = new TableSample();
		StockAnalyzerManager stockManager = new StockAnalyzerManager();
		stockManager.setUpdateListener(sample);
		sample.init();
		sample.setStockAnalyzerManager(stockManager);
		sample.setStatusData();
		sample.show();
	}
	
	
}
