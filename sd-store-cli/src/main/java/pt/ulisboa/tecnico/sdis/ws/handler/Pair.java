package pt.ulisboa.tecnico.sdis.ws.handler;

import java.io.IOException;
import java.io.Serializable;
import java.security.Key;

import javax.crypto.Cipher;

public class Pair<Key, Cipher> implements Serializable {
	private Key _key;
	private Cipher _cipher;
	
	public Pair(Key key, Cipher cipher) {
		_key = key;
		_cipher = cipher;
	}
	
	public Key getKey() { return _key; }
	public Cipher getCipher() { return _cipher;	}
	
	public void setKey(Key key) { _key = key; }
	public void setCipher(Cipher cipher) { _cipher = cipher; }
		
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}
}