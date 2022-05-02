package parser.ast;


public class IntNode extends QuotableNodeImpl implements ValueNode {
    private Integer value;

    public IntNode(String text) {
        this.value = new Integer(text);
    }

    @Override
    public String toString() {
        return "INT:" + value;
    }

}
