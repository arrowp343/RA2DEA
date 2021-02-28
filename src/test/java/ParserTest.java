import org.junit.Test;

/*
 * Autor: 1705159
 */

import static org.junit.Assert.assertTrue;

public class ParserTest {
    @Test(expected = Exception.class)
    public void without_Hash() {
        TopDownParser parser = new TopDownParser("(bb)");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void without_OpeningParenthesis() {
        TopDownParser parser = new TopDownParser("cc)#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void without_ClosingParenthesis() {
        TopDownParser parser = new TopDownParser("(abc#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void unknownOperator_Punkt() {
        TopDownParser parser = new TopDownParser("(b.b)#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void unknownOperator_Comma(){
        TopDownParser parser = new TopDownParser("(b,b)#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void whiteSpace(){
        TopDownParser parser = new TopDownParser("(a b)#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void invalid_Plus() {
        TopDownParser parser = new TopDownParser("(+xxx)#");
        parser.start(null);
    }

    @Test
    public void Konkatenation() {
        TopDownParser parser = new TopDownParser("(gg)#");
        Visitable syntaxTree = parser.start(null);
        Visitable left = new OperandNode("g");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("g");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 3;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(baumVergleich(syntaxTree, refTree));
    }

    @Test
    public void or_Operator() {
        TopDownParser parser = new TopDownParser("(b|c)#");
        Visitable syntaxTree = parser.start(null);

        Visitable left = new OperandNode("b");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("c");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("|", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 3;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(baumVergleich(syntaxTree, refTree));
    }

    @Test
    public void KleeneStar_Operator() {
        TopDownParser parser = new TopDownParser("(s*)#");
        Visitable syntaxTree = parser.start(null);

        Visitable subNode = new OperandNode("s");
        ((OperandNode) subNode).position = 1;
        Visitable left = new UnaryOpNode("*", subNode);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(baumVergleich(syntaxTree, refTree));
    }

    @Test
    public void KleenePlus_Operator() {
        TopDownParser parser = new TopDownParser("(p+)#");
        Visitable syntaxTree = parser.start(null);

        Visitable subNode = new OperandNode("p");
        ((OperandNode) subNode).position = 1;
        Visitable left = new UnaryOpNode("+", subNode);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(baumVergleich(syntaxTree, refTree));
    }

    @Test
    public void option_Operator() {
        TopDownParser parser = new TopDownParser("(o?)#");
        Visitable syntaxTree = parser.start(null);

        Visitable subNode = new OperandNode("o");
        ((OperandNode) subNode).position = 1;
        Visitable left = new UnaryOpNode("?", subNode);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);

        assertTrue(baumVergleich(syntaxTree, refTree));
    }

    @Test
    public void multiple_Operators() {
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

        assertTrue(baumVergleich(syntaxTree, refTree));
    }

    public static boolean baumVergleich(Visitable visitable1, Visitable visitable2)
    {
        if ((visitable1 == null) || (visitable2 == null)){
            return false;
        }

        if (visitable1 == visitable2) {
            return true;
        }

        if (visitable1.getClass() != visitable2.getClass()){
            return false;
        }

        if (visitable1.getClass() == OperandNode.class)
        {
            OperandNode operand1 = (OperandNode) visitable1;
            OperandNode operand2 = (OperandNode) visitable2;
            return operand1.position == operand2.position &&
                    operand1.symbol.equals(operand2.symbol);
        }

        if (visitable1.getClass() == UnaryOpNode.class)
        {
            UnaryOpNode operand1 = (UnaryOpNode) visitable1;
            UnaryOpNode operand2 = (UnaryOpNode) visitable2;
            return operand1.operator.equals(operand2.operator)
                    && baumVergleich(operand1.subNode, operand2.subNode);
        }

        if (visitable1.getClass() == BinOpNode.class)
        {
            BinOpNode operand1 = (BinOpNode) visitable1;
            BinOpNode operand2 = (BinOpNode) visitable2;
            return operand1.operator.equals(operand2.operator)
                    && baumVergleich(operand1.left, operand2.left)
                    && baumVergleich(operand1.right, operand2.right);
        }

        return false;
        //throw new IllegalStateException("Invalid node type");
    }
}
