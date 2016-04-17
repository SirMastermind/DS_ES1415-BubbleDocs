package pt.ulisboa.tecnico.sdis.protocol.toolkit;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;

public final class DataConvertor {
	
	
	public static Date createDateT2(Date dateT1, int ttl){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(dateT1);
	    cal.add(Calendar.HOUR_OF_DAY, ttl); 
	    return cal.getTime();
	}
	/*
	 * String to Key conversion
	 */
	
	public static Key stringToKey(String key) throws InvalidDataException{
		return new SecretKeySpec(getBytesFromBase64String(key), "AES");
	}
	
	/*
	 * Key to string conversion
	 */
	
	public static String keyToString(Key key) throws InvalidDataException{
		if(key == null) throw new InvalidDataException();
		return getStringFromBytes(key.getEncoded());
	}
	
	/*
	 * Date to string conversion
	 */
	
	public static String dateToString(Date date) throws InvalidDataException {
		if(date == null) throw new InvalidDataException();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", new Locale("en"));
		return df.format(date);
	}
	
	
	/*
	 * String to Date conversion
	 */

	public static Date stringToDate(String stringDate) throws InvalidDataException{
		
		try {
			if(stringDate == null || stringDate == "") throw new InvalidDataException();
			
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", new Locale("en"));
			return df.parse(stringDate);
		} catch (ParseException e) {
			throw new InvalidDataException();
		}
	}
	
	/*
	 * method that converts the given byte array to base64 String
	 */
	 
	public static String getStringFromBytes(byte[] data) throws InvalidDataException{
		if(data == null) throw new InvalidDataException();
		return DatatypeConverter.printBase64Binary(data);
	}	
	
	/*
	 * method that converts the given base64 string to a byte array
	 */
	
	public static byte[] getBytesFromBase64String(String data) throws InvalidDataException{
		
		if(data == null || !data.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$"))
			throw new InvalidDataException();
		
		return DatatypeConverter.parseBase64Binary(data);
	}
	
}
