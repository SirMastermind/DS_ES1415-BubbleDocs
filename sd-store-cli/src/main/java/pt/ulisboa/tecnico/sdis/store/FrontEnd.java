package pt.ulisboa.tecnico.sdis.store;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import pt.ulisboa.tecnico.sdis.exception.DocHasBeenChanged_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.CreateDocResponse;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.ListDocsResponse;
import pt.ulisboa.tecnico.sdis.store.ws.LoadResponse;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.StoreResponse;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.handler.TagHandler;

public class FrontEnd {

	private SDStore_Service _service = null;
	private ArrayList<SDStore> _ports = null;
	private String _name;
	private String _uddiURL;
	private int _cid = 0;
	private int _RT;
	private int _WT;

	public FrontEnd(String name, String uddiURL, int RT, int WT) throws JAXRException {
		_ports = new ArrayList<SDStore>();
		_name  = name;
		_uddiURL = uddiURL;
		Collection<String> endpointAddresses = connectUDDI(name, uddiURL);
		for(String endpointAddress: endpointAddresses){
			createStub(endpointAddress);
		}
		_RT = RT;
		_WT = WT;
		int nservers = _ports.size();
		if (RT < 0) {
			_RT = 1;
		}
		if (WT < 0) {
			_WT = 1;
		}
		if (RT == 0 || WT == 0) {
			_RT = nservers;
			_WT = _RT;
			if (_RT%2 == 0) {
				_RT = _RT/2 + 1;
			}
			else {
				_RT = (_RT+1)/2;
			}
			_WT = _RT;
		}
		if (RT > nservers) {
			_RT = nservers;
		}
		if (WT > nservers) {
			_WT = nservers;
		}
		
	}
	
	public FrontEnd(String name, String uddiURL) throws JAXRException {
		this(name, uddiURL, 0,0);
	}
	
	public void createStub(String endpointAddress) {
        System.out.println("Creating stub ...");
        _service = new SDStore_Service();
        _ports.add(_service.getSDStoreImplPort());
        System.out.println("Number of servers: " + _ports.size());
        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) _ports.get(_ports.size() - 1);
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }
	
	public Collection<String> connectUDDI(String name, String uddiURL) throws JAXRException {
		System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming;
		uddiNaming = new UDDINaming(uddiURL);
		System.out.printf("Looking for '%s'%n", name);
	    Collection<String> endpointAddresses = uddiNaming.list(name);
	    
	
	    if (endpointAddresses == null) {
	        System.out.println("Not found!");
	        return null;
	    } 
	    
	   return endpointAddresses;
	}
	
	/*
	 * SD-STORE-B
	 * In the particular cases of createDocument and listDocument we do not need any kind of tag
	 * 
	 * We just need to ensure that we write and read always from a maiority of the server stores
	 * given the nature of the problem the listDocument function will always be the join of all
	 * diferent elements from all listDocument responses and if we ensure that all create Document
	 * write this will always work despite the fact that its possible that none of the servers
	 * will have all the documents
	 * 
	 */
	public void createDocument(DocUserPair doc) throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
		/*
		 * check addrs again in case some of them have changed
		 */
		
		try { renewStubs(); } catch(JAXRException e) { e.printStackTrace(); }
		
		/*
		 * Read first to get max Tag
		 */
		
		List<Response<CreateDocResponse>> responsesCreate = new ArrayList<Response<CreateDocResponse>>();
		
		for(SDStore port: _ports){
			responsesCreate.add(port.createDocAsync(doc));
		}
		
		
		/*
		 * wait for responses and ensure that we write on a maiority of the servers
		 */
		
		int acks = 0;
		
		while(!responsesCreate.isEmpty() && acks < _WT){

			for(int i = 0; i < responsesCreate.size(); i++) {
				
				if(responsesCreate.get(i).isDone()){
		
					try{
						//check for errors from server response
						responsesCreate.get(i).get();
					}
					catch(ExecutionException e){
						if (e.getCause().getClass().equals(DocAlreadyExists_Exception.class)) {
					        throw (DocAlreadyExists_Exception) e.getCause();
					    }
						
					} catch (InterruptedException e) {
						//NOTHING TO DO
					}
					
					responsesCreate.remove(responsesCreate.get(i));
					acks++;	
				}	
			}					
		}
		

	}
	
	public List<String> listDocuments(String username) throws UserDoesNotExist_Exception {
		System.out.println("<<<<<<<<<<<ListDocuments>>>>>>>>>>>>");
		/*
		 * check addrs again in case some of them have changed
		 */
		
		try { renewStubs(); } catch(JAXRException e) { e.printStackTrace(); }

		/*
		 * send async requests to all sd-store servers
		 */
		
		
		ArrayList<List<String>> answers = new ArrayList<List<String>>();		
		List<Response<ListDocsResponse>> responses = new ArrayList<Response<ListDocsResponse>>();
		
		for(SDStore port: _ports){
			responses.add(port.listDocsAsync(username));
		}
		
		/*
		 * wait for responses
		 */
		
		int acks = 0;
		
		while(!responses.isEmpty() && acks < _RT){
			
			for(int i = 0; i < responses.size(); i++) {
				
				if(responses.get(i).isDone()){
					
					try{
						answers.add(responses.get(i).get().getDocumentId());
					}
					catch(ExecutionException e){
						if (e.getCause().getClass().equals(UserDoesNotExist_Exception.class)) {
					        throw (UserDoesNotExist_Exception) e.getCause();
					    }
					} catch (InterruptedException e) {
						//NOTHING TO DO
					}
					
					responses.remove(responses.get(i));
					acks++;	
				
				}	
			}					
		}
		
		return joinListDocumentAnswers(answers);
		
	}
	
	/*
	 * method that will receive all list document responses from the servers and generate one with all 
	 * diferent docs
	 */
	
	private List<String> joinListDocumentAnswers(ArrayList<List<String>> answers) {
		
		List<String> result = new ArrayList<String>();
		
		for(List<String> list: answers){
			for(String doc: list){
				if(!result.contains(doc))
					result.add(doc);
			}
		}
		
		return result;
		
	}

	public byte[] loadDocument(DocUserPair doc) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception, DocHasBeenChanged_Exception {
		try { renewStubs(); } catch(JAXRException e) { e.printStackTrace(); }
		System.out.println("loadDocument");
		ArrayList<byte[]> combined = new ArrayList<byte[]>();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		ArrayList<Response<LoadResponse>> rep = new ArrayList<Response<LoadResponse>>();
		ArrayList<Boolean> repsDone = new ArrayList<Boolean>();
		
		BindingProvider bindingProvider;
		System.out.println("Enviar mensagens");
		for(SDStore port: _ports){
			bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(TagHandler.REQUEST_TYPE, "load");
			// FIXME requestContext.put(REQUEST_PROPERTY_USERNAME, doc.getUserId());
			Response<LoadResponse> response = port.loadAsync(doc);
			rep.add(response);
			repsDone.add(false);
			System.out.println("Mensagem enviada");
		}
		System.out.println("Todas as mensagens foram enviadas");
		int loads = 0;
		while (loads < _RT) {
			for(int i = 0; i < rep.size(); i++) {
				if(rep.get(i).isDone() && repsDone.get(i).equals(false)) {
					System.out.println("Response obtained");
					loads++;
					repsDone.set(i, true);
					try {
						combined.add(i, rep.get(i).get().getContents());
						String s = new String(rep.get(i).get().getContents());
						System.out.println("Null Content????? >>>> " + s);
						tags.add(i, getTag(rep.get(i)));
					} catch (ExecutionException e) {
						if (e.getCause().getClass() == UserDoesNotExist_Exception.class) {
					        throw (UserDoesNotExist_Exception) e.getCause();
					    } else if (e.getCause().getClass() == DocDoesNotExist_Exception.class) {
					    	throw (DocDoesNotExist_Exception) e.getCause();
					    } else{
					    	e.printStackTrace();
					    }
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		//System.out.println("All Responses obtained");
		Tag maxTag = tags.get(0);
		for (Tag t : tags) { if(t.greaterThan(maxTag)) maxTag = t; }
		System.out.println("MaxTag Obtained");
		doWriteBack(tags, doc, combined.get(tags.indexOf(maxTag)), maxTag);
		System.out.println("WriteBack done");
		
		return combined.get(tags.indexOf(maxTag));
	}
	
	private void doWriteBack(ArrayList<Tag> tags, DocUserPair doc, byte[] combined, Tag tagToReturn) 
			throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		int acks = 0;
		ArrayList<Response<StoreResponse>> rep = new ArrayList<Response<StoreResponse>>();
		ArrayList<Boolean> repsDone = new ArrayList<Boolean>();
		System.out.println("Before writing back on old tags");
		for(int index = 0; index < tags.size(); index++) {
			System.out.println("Tag on index: " + index);
			Tag newTag = new Tag(tagToReturn.getSeq(), tagToReturn.getCid());
			System.out.println("New Tag: " + newTag.toString());
			BindingProvider bindingProvider = (BindingProvider) _ports.get(index);
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(TagHandler.REQUEST_TYPE, "store");
			requestContext.put(TagHandler.REQUEST_PROPERTY, newTag);
			
			Response<StoreResponse> response = _ports.get(index).storeAsync(doc, combined);
			rep.add(response);
			repsDone.add(false);
			System.out.println("Store message sent");
		}
		System.out.println("All store messages sent");
		while (acks < _WT) {
			for(int i = 0; i < rep.size(); i++) {
				if(rep.get(i).isDone() && repsDone.get(i).equals(false)) {
					try {
						rep.get(i).get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						if (e.getCause().getClass() == UserDoesNotExist_Exception.class) {
					        throw (UserDoesNotExist_Exception) e.getCause();
					    } else if (e.getCause().getClass() == DocDoesNotExist_Exception.class) {
					    	throw (DocDoesNotExist_Exception) e.getCause();
					    } else{
					    	e.printStackTrace();
					    }
					}
					acks++;
					repsDone.set(i, true);
				}
			}
		}
		System.out.println("all responses obtained");
	}

	public void storeDocument(DocUserPair doc, byte[] content) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		try { renewStubs(); } catch(JAXRException e) { e.printStackTrace(); }
		
		ArrayList<byte[]> combined = new ArrayList<byte[]>();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		ArrayList<Response<LoadResponse>> rep = new ArrayList<Response<LoadResponse>>();
		ArrayList<Boolean> repsDone = new ArrayList<Boolean>();
		
		BindingProvider bindingProvider;
		System.out.println("Obter tags para store");
		for(SDStore port: _ports){
			bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(TagHandler.REQUEST_TYPE, "load");
			Response<LoadResponse> response = port.loadAsync(doc);
			rep.add(response);
			repsDone.add(false);
			System.out.println("load-enviado");
		}
		System.out.println("Enviadas mensagens load");
		int loads = 0;
		while (loads < _RT) {
			for(int i = 0; i < rep.size(); i++) {
				if(rep.get(i).isDone() && repsDone.get(i).equals(false)) {
					System.out.println("response received");
					loads++;
					repsDone.set(i, true);
					try {
						combined.add(i, rep.get(i).get().getContents());
						tags.add(i, getTag(rep.get(i)));
					} catch (ExecutionException e) {
						if (e.getCause().getClass() == UserDoesNotExist_Exception.class) {
					        throw (UserDoesNotExist_Exception) e.getCause();
					    } else if (e.getCause().getClass() == DocDoesNotExist_Exception.class) {
					    	throw (DocDoesNotExist_Exception) e.getCause();
					    	
					    } else{
					    	e.printStackTrace();
					    }
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		Tag maxTag = tags.get(0);
		for (Tag t : tags) { if(t.greaterThan(maxTag)) maxTag = t; }
		Tag newTag = new Tag(maxTag.getSeq()+1, _cid);
		System.out.println("Maxtag obtained");
		
		int acks = 0;
		ArrayList<Response<StoreResponse>> repStore = new ArrayList<Response<StoreResponse>>();
		ArrayList<Boolean> repsDoneStore = new ArrayList<Boolean>();
		
		
		System.out.println("Mandar mensagens store");
		for(SDStore port: _ports) {
			bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(TagHandler.REQUEST_TYPE, "store");
			requestContext.put(TagHandler.REQUEST_PROPERTY, newTag);
			Response<StoreResponse> response = port.storeAsync(doc, content);
			repStore.add(response);
			repsDoneStore.add(false);
			System.out.println("store-enviado");
		}
		System.out.println("Mensagens store enviadas");
		while (acks < _WT) {
			for(int i = 0; i < rep.size(); i++) {
				if(repStore.get(i).isDone() && repsDoneStore.get(i).equals(false)) {
					try {
						repStore.get(i).get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						if (e.getCause().getClass() == UserDoesNotExist_Exception.class) {
					        throw (UserDoesNotExist_Exception) e.getCause();
					    } else if (e.getCause().getClass() == DocDoesNotExist_Exception.class) {
					    	throw (DocDoesNotExist_Exception) e.getCause();
					    } else{
					    	e.printStackTrace();
					    }
					}
					acks++;
					repsDone.set(i, true);
				}
			}
		}
		System.out.println("All Responses from store obtained");
	}
	
	private Tag getTag(Response response) {
		Tag tag = (Tag) response.getContext().get(TagHandler.RESPONSE_PROPERTY);
		return tag;
	}
	
	private void renewStubs() throws JAXRException{
		Collection<String> endpointAddresses = connectUDDI(_name, _uddiURL);
		int indice=0;
		for(String endpointAddress: endpointAddresses){
	       BindingProvider bindingProvider = (BindingProvider) _ports.get(indice++);
	       Map<String, Object> requestContext = bindingProvider.getRequestContext();
		   requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress); 
		}
	}
}
