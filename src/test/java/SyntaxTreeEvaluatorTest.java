import org.junit.Test;
import static org.junit.Assert.*;

public class SyntaxTreeEvaluatorTest {
    @Test
    public void syntaxTreeEvaluatorTest() {
        // Regulärer Ausdruck aus Vorlesung: (a|b)*cd*
        SyntaxTreeEvaluator firstVisitor = new SyntaxTreeEvaluator();
        Visitable tempLeft, tempRight, expectedTree, actualTree;

        //erstellung des Baumes der erwarted wird
        tempLeft = new OperandNode("a");
        ((SyntaxNode) tempLeft).nullable = false;
        ((SyntaxNode) tempLeft).firstpos.add(1);
        ((SyntaxNode) tempLeft).lastpos.add(1);
        ((OperandNode) tempLeft).position = 1;

        tempRight = new OperandNode("b");
        ((SyntaxNode) tempRight).nullable = false;
        ((SyntaxNode) tempRight).firstpos.add(2);
        ((SyntaxNode) tempRight).lastpos.add(2);
        ((OperandNode) tempRight).position = 2;

        tempLeft = new BinOpNode("|", tempLeft, tempRight);
        ((SyntaxNode) tempLeft).nullable = false;
        ((SyntaxNode) tempLeft).firstpos.add(1);
        ((SyntaxNode) tempLeft).firstpos.add(2);
        ((SyntaxNode) tempLeft).lastpos.add(1);
        ((SyntaxNode) tempLeft).lastpos.add(2);

        tempLeft = new UnaryOpNode("*", tempLeft);
        ((SyntaxNode) tempLeft).nullable = true;
        ((SyntaxNode) tempLeft).firstpos.add(1);
        ((SyntaxNode) tempLeft).firstpos.add(2);
        ((SyntaxNode) tempLeft).lastpos.add(1);
        ((SyntaxNode) tempLeft).lastpos.add(2);

        tempRight = new OperandNode("c");
        ((SyntaxNode) tempRight).nullable = false;
        ((SyntaxNode) tempRight).firstpos.add(3);
        ((SyntaxNode) tempRight).lastpos.add(3);
        ((OperandNode) tempRight).position = 3;

        tempLeft = new BinOpNode("°", tempLeft, tempRight);
        ((SyntaxNode) tempLeft).nullable = false;
        ((SyntaxNode) tempLeft).firstpos.add(1);
        ((SyntaxNode) tempLeft).firstpos.add(2);
        ((SyntaxNode) tempLeft).firstpos.add(3);
        ((SyntaxNode) tempLeft).lastpos.add(3);

        tempRight = new OperandNode("d");
        ((SyntaxNode) tempRight).nullable = false;
        ((SyntaxNode) tempRight).firstpos.add(4);
        ((SyntaxNode) tempRight).lastpos.add(4);
        ((OperandNode) tempRight).position = 4;

        tempRight = new UnaryOpNode("*", tempRight); // evtl unsicher
        ((SyntaxNode) tempRight).nullable = true;
        ((SyntaxNode) tempRight).firstpos.add(4);
        ((SyntaxNode) tempRight).lastpos.add(4);

        tempLeft = new BinOpNode("°", tempLeft, tempRight);
        ((SyntaxNode) tempLeft).nullable = false;
        ((SyntaxNode) tempLeft).firstpos.add(1);
        ((SyntaxNode) tempLeft).firstpos.add(2);
        ((SyntaxNode) tempLeft).firstpos.add(3);
        ((SyntaxNode) tempLeft).lastpos.add(3);
        ((SyntaxNode) tempLeft).lastpos.add(4);

        tempRight = new OperandNode("#");
        ((SyntaxNode) tempRight).nullable = false;
        ((SyntaxNode) tempRight).firstpos.add(5);
        ((SyntaxNode) tempRight).lastpos.add(5);
        ((OperandNode) tempRight).position = 5;

        expectedTree = new BinOpNode("°", tempLeft, tempRight);
        ((SyntaxNode) expectedTree).nullable = false;
        ((SyntaxNode) expectedTree).firstpos.add(1);
        ((SyntaxNode) expectedTree).firstpos.add(2);
        ((SyntaxNode) expectedTree).firstpos.add(3);
        ((SyntaxNode) expectedTree).lastpos.add(5);

        //erstellung des Baumes, den der erste Visitor bekommt
        tempLeft = new OperandNode("a");
        ((OperandNode) tempLeft).position = 1;
        tempRight = new OperandNode("b");
        ((OperandNode) tempRight).position = 2;
        tempLeft = new BinOpNode("|", tempLeft, tempRight);

        tempLeft = new UnaryOpNode("*", tempLeft);
        tempRight = new OperandNode("c");
        ((OperandNode) tempRight).position = 3;
        tempLeft = new BinOpNode("°", tempLeft, tempRight);

        tempRight = new OperandNode("d");
        ((OperandNode) tempRight).position = 4;
        tempRight = new UnaryOpNode("*", tempRight);
        tempLeft = new BinOpNode("°", tempLeft, tempRight);

        tempRight = new OperandNode("#");
        ((OperandNode) tempRight).position = 5;
        actualTree = new BinOpNode("°", tempLeft, tempRight);

        DepthFirstIterator.traverse(actualTree, firstVisitor);

        assertTrue(compareEquality(actualTree, expectedTree));
    }

    private boolean compareEquality(Visitable actual, Visitable expected){
        //wenn beide null sind -> gleich
        if(actual == null && expected == null)
            return true;
        //wenn nur einer von beiden null ist und der andere nicht ODER nicht gleiche Klasse -> ungleich
        if(actual == null || expected == null || actual.getClass() != expected.getClass())
            return false;
        if(actual.getClass() == OperandNode.class){
            OperandNode a = (OperandNode) actual,
                        e = (OperandNode) expected;
            return a.nullable.equals(e.nullable) &&
                    a.firstpos.equals(e.firstpos) &&
                    a.lastpos.equals(e.lastpos);
        }
        if(actual.getClass() == UnaryOpNode.class){
            UnaryOpNode a = (UnaryOpNode) actual,
                        e = (UnaryOpNode) expected;
            return a.nullable.equals(e.nullable) &&
                    a.firstpos.equals(e.firstpos) &&
                    a.lastpos.equals(e.lastpos) &&
                    compareEquality(a.subNode, e.subNode);
        }
        if(actual.getClass() == BinOpNode.class){
            BinOpNode a = (BinOpNode) actual,
                      e = (BinOpNode) expected;
            return  a.nullable.equals(e.nullable) &&
                    a.firstpos.equals(e.firstpos) &&
                    a.lastpos.equals(e.lastpos) &&
                    compareEquality(a.left, e.left) &&
                    compareEquality(a.right, e.right);
        }
        return false;
    }
}