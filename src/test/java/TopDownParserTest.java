import org.junit.Test;

/*
 * Autor: 1705159
 */

import static org.junit.Assert.assertTrue;

public class TopDownParserTest {
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
    public void unknownOperator_Point() {
        TopDownParser parser = new TopDownParser("(a!b)#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void whiteSpace(){
        TopDownParser parser = new TopDownParser("(a b)#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void invalid_Plus() {
        TopDownParser parser = new TopDownParser("(+x)#");
        parser.start(null);
    }

    @Test
    public void Konkatenation() {
        TopDownParser parser = new TopDownParser("(gg)#");
        Visitable syntaxTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable left = new OperandNode("g");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("g");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 3;
        Visitable refTree = new BinOpNode("°", left, right);
        assertTrue(compareTrees(syntaxTree, refTree));
    }

    @Test
    public void or_Operator() {
        TopDownParser parser = new TopDownParser("(b|c)#");
        Visitable syntaxTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable left = new OperandNode("b");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("c");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("|", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 3;
        Visitable refTree = new BinOpNode("°", left, right);
        assertTrue(compareTrees(syntaxTree, refTree));
    }

    @Test
    public void KleeneStar_Operator() {
        TopDownParser parser = new TopDownParser("(s*)#");
        Visitable syntaxTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable node = new OperandNode("s");
        ((OperandNode) node).position = 1;
        Visitable left = new UnaryOpNode("*", node);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);
        assertTrue(compareTrees(syntaxTree, refTree));
    }

    @Test
    public void KleenePlus_Operator() {
        TopDownParser parser = new TopDownParser("(p+)#");
        Visitable syntaxTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable node = new OperandNode("p");
        ((OperandNode) node).position = 1;
        Visitable left = new UnaryOpNode("+", node);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);
        assertTrue(compareTrees(syntaxTree, refTree));
    }

    @Test
    public void option_Operator() {
        TopDownParser parser = new TopDownParser("(o?)#");
        Visitable syntaxTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable node = new OperandNode("o");
        ((OperandNode) node).position = 1;
        Visitable left = new UnaryOpNode("?", node);
        Visitable right = new OperandNode("#");
        ((OperandNode) right).position = 2;
        Visitable refTree = new BinOpNode("°", left, right);
        assertTrue(compareTrees(syntaxTree, refTree));
    }

    @Test
    public void multiple_Operators() {
        TopDownParser parser = new TopDownParser("((a|b)*c)#");
        Visitable syntaxTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable left = new OperandNode("a");
        ((OperandNode) left).position = 1;
        Visitable right = new OperandNode("b");
        ((OperandNode) right).position = 2;
        left = new BinOpNode("|", left, right);
        left = new UnaryOpNode("*", left);
        right = new OperandNode("c");
        ((OperandNode) right).position = 3;
        left = new BinOpNode("°", left, right);
        right = new OperandNode("#");
        ((OperandNode) right).position = 4;
        Visitable refTree = new BinOpNode("°", left, right);
        assertTrue(compareTrees(syntaxTree, refTree));
    }

    public boolean compareTrees(Visitable tree1, Visitable tree2)
    {
        //falls ein oder beide Baüme nicht initialisiert sind (null)
        if ((tree1 == null) || (tree2 == null) ||
                (tree1.getClass() != tree2.getClass())){
            return false;
        }

        if (tree1 == tree2) {
            return true;
        }

        if (tree1.getClass() == BinOpNode.class)
        {
            BinOpNode binOpNodeTree1 = (BinOpNode) tree1;
            BinOpNode binOpNodeTree2 = (BinOpNode) tree2;
            return binOpNodeTree1.operator.equals(binOpNodeTree2.operator)
                    && compareTrees(binOpNodeTree1.right, binOpNodeTree2.right)
                    && compareTrees(binOpNodeTree1.left, binOpNodeTree2.left);
        }

        if (tree1.getClass() == OperandNode.class)
        {
            OperandNode opNodeTree1 = (OperandNode) tree1;
            OperandNode opNodeTree2 = (OperandNode) tree2;
            return opNodeTree1.position == opNodeTree2.position &&
                    opNodeTree1.symbol.equals(opNodeTree2.symbol);
        }

        if (tree1.getClass() == UnaryOpNode.class)
        {
            UnaryOpNode unaryNodeTree1 = (UnaryOpNode) tree1;
            UnaryOpNode unaryNodeTree2 = (UnaryOpNode) tree2;
            return unaryNodeTree1.operator.equals(unaryNodeTree2.operator)
                    && compareTrees(unaryNodeTree1.subNode, unaryNodeTree2.subNode);
        }

        return false;
    }
}
