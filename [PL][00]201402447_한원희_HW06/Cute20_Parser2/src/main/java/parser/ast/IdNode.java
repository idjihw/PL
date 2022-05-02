package parser.ast;

public class IdNode extends QuotableNodeImpl implements ValueNode {
    String idString;

    public IdNode(String text) {
        idString = text;
    }

    @Override
    public String toString() {
        return "ID:" + idString;
    }


}
