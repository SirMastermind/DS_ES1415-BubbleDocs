package pt.tecnico.bubbledocs.toolkit;

import org.jdom2.Element;
import pt.tecnico.bubbledocs.domain.*;

public abstract class Visitor {
    
    
    public abstract Element processCell(Cell c);
    public abstract Element processADD(ADD add);
    public abstract Element processAVG(AVG avg);
    public abstract Element processDIV(DIV div);
    public abstract Element processMUL(MUL mul);
    public abstract Element processPRD(PRD prd);
    public abstract Element processSUB(SUB sub);
    public abstract Element processReference(Reference ref);
    public abstract Element processLiteral(Literal l);
    public abstract Element processNullContent(NullContent n);
    public abstract Element processSheet(Sheet sheet, String newOwner);
    public abstract Element processReferenceArg(ReferenceArg ref);
    public abstract Element processLiteralArg(LiteralArg l);
    
}
