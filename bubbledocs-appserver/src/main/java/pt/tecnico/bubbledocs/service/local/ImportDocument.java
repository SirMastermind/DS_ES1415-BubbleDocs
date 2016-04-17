package pt.tecnico.bubbledocs.service.local;

import java.io.IOException;

import org.jdom2.JDOMException;

import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidDataException;
import pt.tecnico.bubbledocs.toolkit.XMLConverter;

public class ImportDocument extends SessionBasedService {

	private byte[] fileBytes;
	private Sheet sheet;
	
	public ImportDocument(String userToken){
		super(userToken);
	}
	
	public void setFileBytes(byte[] fileBytes){
		this.fileBytes = fileBytes;
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		
		try {
			this.sheet = XMLConverter.convertToSheet(fileBytes);
			getUser().addOwnedSheets(sheet);
			getBubbleDocsServer().addSheets(sheet);
			
		} catch (JDOMException e) {
			throw new InvalidDataException();
		} catch (IOException e) {
			throw new InvalidDataException();
		}
		
	}

	public Sheet getSheet() {
		return this.sheet;
	}

}
