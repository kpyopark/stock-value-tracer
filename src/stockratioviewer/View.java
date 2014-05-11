package stockratioviewer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import analyzer.StockAnalyzerManager;

import post.Company;

public class View extends ViewPart {
	public static final String ID = "StockRatioViewer.view";

	private TableViewer viewer;
	private StockAnalyzerManager manager;

	String[] columnTitles = { "No", "Name", "ID", "C_S", "F_S", "S_S", "FE_S",
			"SE_S", "제무제표기준일", "PER RANK", "ROA RANK", "ROI RANK", "ROE RANK",
			"TOT RANK", "PER", "12M PER", "ROA", "ROI", "ROE", "배당율", "주가기준일",
			"최근주가" };

	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements IStructuredContentProvider {

		private ArrayList<Company> companyList = null;
		
		public ViewContentProvider() {
			//manager = new StockAnalyzerManager();
			//companyList = manager.getCompanyList();
			companyList = new ArrayList<Company>();
			//
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			System.out.println("parent:" + parent);
			return (companyList != null) ? companyList.toArray()
					: (Object[]) null;
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public ViewLabelProvider() {

		}

		public String getColumnText(Object obj, int index) {
			if (obj != null) {
				switch (index) {
				case 0:
					return "0";
				case 1:
					if (obj instanceof Company)
						return ((Company) obj).getName();
					else
						return "1";
				case 2:
					return "2";
				default:
					return "0";
				}
			}
			return "";
			// return columnTitles[index];
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		/*
		 * Table table; table = new Table(parent, SWT.MULTI | SWT.H_SCROLL |
		 * SWT.V_SCROLL | SWT.FULL_SELECTION ); TableLayout layout = new
		 * TableLayout(); table.setLayout(layout);
		 * 
		 * table.setLinesVisible(true); table.setHeaderVisible(true);
		 *  // 트리 컬럼 for (int i = 0; i < columnTitles.length; i++) {
		 * layout.addColumnData(new ColumnWeightData(5,40,true)); TableColumn
		 * aColumn = new TableColumn(table, SWT.NONE);
		 * aColumn.setText(columnTitles[i]); aColumn.setAlignment(SWT.LEFT);
		 * aColumn.setResizable(true); }
		 */
		
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		for (int i = 0; i < columnTitles.length; i++) {
			TableColumn aColumn = new TableColumn(viewer.getTable(), SWT.NONE);
			aColumn.setText(columnTitles[i]);
			aColumn.setWidth(100);
		}
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}