package looter;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

enum TransmissionType{
	declaration,
	broadcast,
	update,
	heartbeat,
	logoutrequest
}

enum DataType{
	EMPTY,
	STRING,
	WORLD,
	EVENT,
	PLAYER, 
	INT
}

public class DataPacket implements Serializable{

	private static final long serialVersionUID = -8827417661709700044L;
	TransmissionType tType;
	DataType dType;
	Object data;
	long timestamp;
	
	public DataPacket(TransmissionType tType, DataType dType ,Object data) {
		this.tType = tType;
		this.dType = dType;
		this.data = data;
		timestamp = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:SSS aa");
    	String dateString = dateFormat.format(new Date(timestamp)).toString();
		return "DataPacket [type=" + tType + ", timestamp=" + dateString + "]";
	}
}
