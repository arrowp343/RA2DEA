public class TopDownParser {
    private int position;
    private final String eingabe;
    private int leafPosition;

    public TopDownParser(String eingabe){
        position = 0;
        leafPosition = 1;
        this.eingabe=eingabe;
    }

    private void match(char symbol){
        if((eingabe == null) || ("".equals(eingabe))){
            throw new RuntimeException("Syntax error!");
        }
        if(position >= eingabe.length()){
            throw new RuntimeException("End of input reached!");
        }
        if(eingabe.charAt(position) != symbol){
            throw new RuntimeException("Syntax error!");
        }

        position++;
    }

    public static void main(String[] args) {

        TopDownParser parser = new TopDownParser("(aa*a)#");
        parser.start(null);
    }

    private char currentSymbol() {
        return eingabe.charAt(position);
    }

    private void assertEndOfInput() {
        if (this.position < this.eingabe.length()) {
            throw new RuntimeException("No end of input reached!");
        }
    }

    public Visitable start(Visitable parameter){
        if (this.currentSymbol() == '(') {
            this.match('(');
            Visitable regExp = this.RegExp(null);
            this.match(')');
            this.match('#');
            this.assertEndOfInput();
            // Prepare return value
            OperandNode leaf = new OperandNode("#");
            leaf.position = this.leafPosition;
            return new BinOpNode("°", regExp, leaf);
        }
        else if (this.currentSymbol() == '#') {
            this.match('#');
            this.assertEndOfInput();
            // Prepare return value
            OperandNode leaf = new OperandNode("#");
            leaf.position = this.leafPosition;
            return leaf;
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable RegExp(Visitable parameter) {
        if (Character.isLetter(this.currentSymbol()) ||   // a..z, A..z
                Character.isDigit(this.currentSymbol()) ||    // 0..9
                this.currentSymbol() == '(')
        {
            // Prepare return value
            Visitable term = this.term(null);
            return this.RE(term);
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable RE(Visitable parameter) {
        if (this.currentSymbol() == '|') {
            this.match('|');
            // Prepare return value
            Visitable term = this.term(null);
            Visitable root = new BinOpNode("|", parameter, term);
            return this.RE(root);
        }
        else if (this.currentSymbol() == ')') {
            return parameter;
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable term(Visitable parameter) {
        if (Character.isLetter(this.currentSymbol()) ||   // a..z, A..z
                Character.isDigit(this.currentSymbol()) ||    // 0..9
                this.currentSymbol() == '(')
        {
            // Prepare return value
            Visitable factor = this.factor(null);
            Visitable term;
            if (parameter != null) {
                Visitable root = new BinOpNode("°", parameter, factor);
                term = this.term(root);
            } else {
                term = this.term(factor);
            }
            return term;
        }
        else if (this.currentSymbol() == '|' ||
                this.currentSymbol() == ')')
        {
            return parameter;
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable factor(Visitable parameter) {
        if (Character.isLetter(this.currentSymbol()) ||   // a..z, A..z
                Character.isDigit(this.currentSymbol()) ||    // 0..9
                this.currentSymbol() == '(')
        {
            // Prepare return value
            Visitable elem = this.elem(null);
            return this.hOp(elem);
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable hOp(Visitable parameter) {
        if (Character.isLetter(this.currentSymbol()) ||   // a..z, A..z
                Character.isDigit(this.currentSymbol()) ||    // 0..9
                this.currentSymbol() == '(' ||
                this.currentSymbol() == '|' ||
                this.currentSymbol() == ')')
        {
            return parameter;
        }
        else if (this.currentSymbol() == '*' ||
                this.currentSymbol() == '+' ||
                this.currentSymbol() == '?')
        {
            char curChar = this.currentSymbol();
            this.match(curChar);
            String curStr = Character.toString(curChar);
            return new UnaryOpNode(curStr, parameter);
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable elem(Visitable parameter) {
        if (Character.isLetter(this.currentSymbol()) ||
                Character.isDigit(this.currentSymbol()))
        {
            return this.alphanum(null);
        }
        else if (this.currentSymbol() == '(') {
            this.match('(');
            Visitable regExp = this.RegExp(null);
            this.match(')');
            return regExp;
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable alphanum(Visitable parameter) {
        if (Character.isLetter(this.currentSymbol()) ||
                Character.isDigit(this.currentSymbol()))
        {
            char curChar = this.currentSymbol();
            this.match(curChar);
            // Prepare return value
            String symbol = Character.toString(curChar);
            OperandNode opNode = new OperandNode(symbol);
            opNode.position = this.leafPosition;
            this.leafPosition++;
            return opNode;
        }
        else throw new RuntimeException("Syntax error!");
    }
}
