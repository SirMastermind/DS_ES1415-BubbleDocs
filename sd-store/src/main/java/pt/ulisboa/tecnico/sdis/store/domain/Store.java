package pt.ulisboa.tecnico.sdis.store.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import pt.ulisboa.tecnico.sdis.store.dto.DocumentDTO;
import pt.ulisboa.tecnico.sdis.store.ws.*;

public class Store {
	private Map<String, UserStore> _store;
	
	public Store() {
		_store = new HashMap<String, UserStore>();
		_store.put("alice", new UserStore());
		_store.put("bruno", new UserStore());
		_store.put("carla", new UserStore());
		_store.put("duarte", new UserStore());
		_store.put("eduardo", new UserStore());
		_store.put("francisco", new UserStore());
		_store.put("guilherme", new UserStore());
	}
	
	public UserStore getStore(String name) {
		return _store.get(name);
	}
	
	public void createDocument(String username, String docname) throws DocAlreadyExists_Exception {
		try {
			UserStore u = _store.get(username);
			u.createDocument(docname);
		} catch (NullPointerException e) {
			_store.put(username, new UserStore());
			UserStore u = _store.get(username);
			u.createDocument(docname);
		}
			
}
	
	public ArrayList<String> listDocs(String userId) throws UserDoesNotExist_Exception {
		UserStore u;
		try {
			u = _store.get(userId);
			return u.listDocs();
		} catch (NullPointerException e) {
			throw new UserDoesNotExist_Exception(userId, new UserDoesNotExist());
		}
	}
	
	public void store(String username, String docname, DocumentDTO doc) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		UserStore u;
		try {
			u = _store.get(username);
			u.store(docname, doc);
		} catch (NullPointerException e) {
			throw new UserDoesNotExist_Exception(username, new UserDoesNotExist());
		}
	}
	
	public DocumentDTO load(String username, String docname) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		UserStore u;
		try {
			u = _store.get(username);
			return u.loadDocument(docname);
		} catch (NullPointerException e) {
			throw new UserDoesNotExist_Exception(username, new UserDoesNotExist());
		}
	}
}