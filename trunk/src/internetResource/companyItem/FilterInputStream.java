package internetResource.companyItem;

import java.io.IOException;
import java.io.InputStream;

public class FilterInputStream extends java.io.InputStream {
	InputStream is = null;
	
	public FilterInputStream(InputStream is) {
		this.is = is;
	}
	
	public int read() throws java.io.IOException {
		int rtn = is.read();
		if ( rtn == 0x0015 ) return 0x0020;
		return rtn;
	}

	public int available() throws IOException {
		return is.available();
	}

	public void close() throws IOException {
		is.close();
	}

	public boolean equals(Object arg0) {
		return is.equals(arg0);
	}

	public int hashCode() {
		return is.hashCode();
	}

	public void mark(int readlimit) {
		is.mark(readlimit);
	}

	public boolean markSupported() {
		return is.markSupported();
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}

	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	public void reset() throws IOException {
		is.reset();
	}

	public long skip(long n) throws IOException {
		return is.skip(n);
	}

	public String toString() {
		return is.toString();
	}

}
