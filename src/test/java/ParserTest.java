import org.junit.Test;

/**
 * Autor: 1705159
 */

import static org.junit.Assert.assertTrue;

public class ParserTest {
    @Test(expected = Exception.class)
    public void invalidSyntax_Hash() {
        TopDownParser parser = new TopDownParser("(a)");
        Visitable syntaxTree = parser.start(null);
    }

    @Test(expected = Exception.class)
    public void invalidSyntax_ParenthesisClosing() {
        TopDownParser parser = new TopDownParser("(a#");
        Visitable syntaxTree = parser.start(null);
    }

    @Test(expected = Exception.class)
    public void invalidSyntax_ParenthesisOpening() {
        TopDownParser parser = new TopDownParser("a)#");
        Visitable syntaxTree = parser.start(null);
    }

    @Test(expected = Exception.class)
    public void invalidSyntax_Operator() {
        TopDownParser parser = new TopDownParser("(+a)#");
        Visitable syntaxTree = parser.start(null);
    }

    @Test(expected = Exception.class)
    public void invalidSyntax_OperatorUnknown() {
        TopDownParser parser = new TopDownParser("(a.b|cd)#");
        Visitable syntaxTree = parser.start(null);
    }

    @Test
    public void validSyntax_Concat() {
        TopDownParser parser = new TopDownParser("(abc)#");
        Visitable syntaxTree = parser.start(null);

        Visitable left = new OperandNode("a");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("b");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("c");
        ((OperandNode) right).position = 3;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 4;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(treeCmp(syntaxTree, refTree));
    }

    @Test
    public void validSyntax_Alternative() {
        TopDownParser parser = new TopDownParser("(a|b)#");
        Visitable syntaxTree = parser.start(null);

        Visitable left = new OperandNode("a");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("b");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("|", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 3;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(treeCmp(syntaxTree, refTree));
    }

    @Test
    public void validSyntax_KleeneStar() {
        TopDownParser parser = new TopDownParser("(a*)#");
        Visitable syntaxTree = parser.start(null);

        Visitable subNode = new OperandNode("a");
        ((OperandNode) subNode).position = 1;
        Visitable left = new UnaryOpNode("*", subNode);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(treeCmp(syntaxTree, refTree));
    }

    @Test
    public void validSyntax_KleenePlus() {
        TopDownParser parser = new TopDownParser("(a+)#");
        Visitable syntaxTree = parser.start(null);

        Visitable subNode = new OperandNode("a");
        ((OperandNode) subNode).position = 1;
        Visitable left = new UnaryOpNode("+", subNode);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(treeCmp(syntaxTree, refTree));
    }

    @Test
    public void validSyntax_Option() {
        TopDownParser parser = new TopDownParser("(a?)#");
        Visitable syntaxTree = parser.start(null);

        Visitable subNode = new OperandNode("a");
        ((OperandNode) subNode).position = 1;
        Visitable left = new UnaryOpNode("?", subNode);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(treeCmp(syntaxTree, refTree));
    }

    @Test
    public void validSyntax_Complex() {
        TopDownParser parser = new TopDownParser("((a|b)*abb)#");
        Visitable syntaxTree = parser.start(null);

        Visitable left = new OperandNode("a");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("b");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("|", left, right);
        left = new UnaryOpNode("*", left);
        right = new OperandNode("a");
        ((OperandNode) right).position = 3;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("b");
        ((OperandNode) right).position = 4;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("b");
        ((OperandNode) right).position = 5;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 6;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(treeCmp(syntaxTree, refTree));
    }

    public static boolean treeCmp(Visitable visitable1, Visitable visitable2)
    {
        if (visitable1 == visitable2) return true;
        if (visitable1 == null) return false;
        if (visitable2 == null) return false;
        if (visitable1.getClass() != visitable2.getClass()) return false;
        if (visitable1.getClass() == OperandNode.class)
        {
            OperandNode op1 = (OperandNode) visitable1;
            OperandNode op2 = (OperandNode) visitable2;
            return op1.position == op2.position && op1.symbol.equals(op2.symbol);
        }
        if (visitable1.getClass() == UnaryOpNode.class)
        {
            UnaryOpNode op1 = (UnaryOpNode) visitable1;
            UnaryOpNode op2 = (UnaryOpNode) visitable2;
            return op1.operator.equals(op2.operator)
                    && treeCmp(op1.subNode, op2.subNode);
        }
        if (visitable1.getClass() == BinOpNode.class)
        {
            BinOpNode op1 = (BinOpNode) visitable1;
            BinOpNode op2 = (BinOpNode) visitable2;
            return op1.operator.equals(op2.operator)
                    && treeCmp(op1.left, op2.left)
                    && treeCmp(op1.right, op2.right);
        }
        throw new IllegalStateException("Invalid node type");
    }
}
