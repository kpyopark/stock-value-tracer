package post;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Observable;

public class BaseStructure extends Observable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1913388697739296789L;

	public BaseStructure() {
		
	}
	
	public BaseStructure(BaseStructure bs) throws Exception {
		super();
		copyStructure(bs);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Method[] methodList = this.getClass().getMethods();
		for ( int cnt = 0 ; cnt < methodList.length ; cnt++ ) {
			String methodName = methodList[cnt].getName();
			if( methodName.substring(0,3).equals("get") &&
				methodList[cnt].getParameterTypes().length == 0 ) {
				try {
					sb.append(methodName + "[" + methodList[cnt].invoke(this, (Object[])null) + "]:");
				} catch ( Exception e ) {}
			} else if ( methodName.substring(0,2).equals("is") && methodList[cnt].getParameterTypes().length == 0 ) {
				try {
					sb.append(methodName + "[" + methodList[cnt].invoke(this, (Object[])null) + "]:");
				} catch ( Exception e ) {}
			}
		}
		return sb.toString();
	}
	
	public void copyStructure(BaseStructure bs) throws Exception {
		
		java.beans.BeanInfo thisInfo = java.beans.Introspector.getBeanInfo(bs.getClass());
		PropertyDescriptor[] pdList = thisInfo.getPropertyDescriptors();
		
		for (int cnt = 0 ; cnt < pdList.length ; cnt++ ) {
			Object[] param = new Object[1];
			param[0] = pdList[cnt].getReadMethod().invoke(bs, (Object[])null);
			if ( pdList[cnt].getWriteMethod() != null ) {
				pdList[cnt].getWriteMethod().invoke(this, param);
			}
		}
		
	}
	
	/*
	private Class getMostSuperClass(Class bsClass) {
		Class superClass = bsClass;
		for( ; superClass.getSuperclass() != null ; superClass = superClass.getSuperclass() ) {}
		return superClass;
	}
	*/
	
}
