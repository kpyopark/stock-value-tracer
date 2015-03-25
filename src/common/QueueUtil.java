package common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class QueueUtil {
	
	public static String QUEUE_SELECTKRX = "SELECTKRX";
	public static String QUEUE_INSERTKRX = "INSERTKRX";

	public static byte[] getBytesFromObject(Kryo kryo, byte[] buffer, int buffSize, Object object) {
		Output output = new Output(buffer, buffSize);
		kryo.writeObject(output, object);
		return output.toBytes();
	}
	
	public static <T> T getInstanceFromBytes(Kryo kryo, byte[] buffer, Class<T> klaz) {
		Input input = new Input(buffer);
		return kryo.readObject(input, klaz);
	}
	
}
