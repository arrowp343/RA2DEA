import org.junit.Test;

/*
 * Autor: 1705159
 */

import static org.junit.Assert.assertTrue;

public class TopDownParserTest {
    @Test(expected = Exception.class)
    public void without_Hash() {
        TopDownParser parser = new TopDownParser("(noHash)");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void without_OpeningParenthesis() {
        TopDownParser parser = new TopDownParser("n)#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void without_ClosingParenthesis() {
        TopDownParser parser = new TopDownParser("(n#");
        parser.start(null);
    }

    @Test(expected = Exception.class)
    public void unknownOperator() {
        TopDownParser parser = new TopDownParser("(a>b)#");
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
    public void KleeneStar_Operator() {
        TopDownParser parser = new TopDownParser("(st*r)#");
        Visitable parserTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable nodeLeft = new OperandNode("s");
        Visitable nodeRight = new OperandNode("t");
        nodeRight = new UnaryOpNode("*", nodeRight);
        nodeLeft = new BinOpNode("°", nodeLeft, nodeRight);
        nodeRight = new OperandNode("r");
        nodeLeft = new BinOpNode("°", nodeLeft, nodeRight);
        nodeRight = new OperandNode("#");
        Visitable expectedTree = new BinOpNode("°", nodeLeft, nodeRight);
        assertTrue(compareTrees(parserTree, expectedTree));
    }

    @Test
    public void Konkatenation() {
        TopDownParser parser = new TopDownParser("(gg)#");
        Visitable parserTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable nodeLeft = new OperandNode("g");
        Visitable nodeRight = new OperandNode("g");
        nodeLeft = new BinOpNode("°", nodeLeft, nodeRight);
        nodeRight = new OperandNode("#");
        Visitable expectedTree = new BinOpNode("°", nodeLeft, nodeRight);
        assertTrue(compareTrees(parserTree, expectedTree));
    }

    @Test
    public void or_Operator() {
        TopDownParser parser = new TopDownParser("(o|r)#");
        Visitable parserTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable nodeLeft = new OperandNode("o");
        Visitable nodeRight = new OperandNode("r");
        nodeLeft = new BinOpNode("|", nodeLeft, nodeRight);
        nodeRight = new OperandNode("#");
        Visitable expectedTree = new BinOpNode("°", nodeLeft, nodeRight);
        assertTrue(compareTrees(parserTree, expectedTree));
    }

    @Test
    public void KleenePlus_Operator() {
        TopDownParser parser = new TopDownParser("(p+)#");
        Visitable parserTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable nodeLeft = new OperandNode("p");
        nodeLeft = new UnaryOpNode("+", nodeLeft);
        Visitable nodeRight = new OperandNode("#");
        Visitable expectedTree = new BinOpNode("°", nodeLeft, nodeRight);
        assertTrue(compareTrees(parserTree, expectedTree));
    }

    @Test
    public void option_Operator() {
        TopDownParser parser = new TopDownParser("(o?)#");
        Visitable parserTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable nodeLeft = new OperandNode("o");
        nodeLeft = new UnaryOpNode("?", nodeLeft);
        Visitable nodeRight = new OperandNode("#");
        Visitable expectedTree = new BinOpNode("°", nodeLeft, nodeRight);
        assertTrue(compareTrees(parserTree, expectedTree));
    }

    @Test
    public void multiple_Operators() {
        TopDownParser parser = new TopDownParser("(p(a|r)*se)#");
        Visitable parserTree = parser.start(null);
        //Erstellung des erwarteten Baumes
        Visitable node = new OperandNode("p");
        Visitable nodeLeft = new OperandNode("a");
        Visitable nodeRight = new OperandNode("r");
        nodeLeft = new BinOpNode("|", nodeLeft, nodeRight);
        nodeLeft = new UnaryOpNode("*", nodeLeft);
        nodeLeft = new BinOpNode("°", node, nodeLeft);
        nodeRight = new OperandNode("s");
        nodeLeft = new BinOpNode("°", nodeLeft, nodeRight);
        nodeRight = new OperandNode("e");
        nodeLeft = new BinOpNode("°", nodeLeft, nodeRight);
        nodeRight = new OperandNode("#");
        Visitable expectedTree = new BinOpNode("°", nodeLeft, nodeRight);
        assertTrue(compareTrees(parserTree, expectedTree));
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
