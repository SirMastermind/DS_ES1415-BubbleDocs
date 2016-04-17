package pt.tecnico.bubbledocs.dto;

public class SheetContentDTO {
	private String[][] _content;
	
	public SheetContentDTO(String[][] content) {
		_content = content;
	}
	
	public String[][] getContent() {
		return _content;
	}
	
	public void setContent(String[][] content) {
		_content = content;
	}
}
