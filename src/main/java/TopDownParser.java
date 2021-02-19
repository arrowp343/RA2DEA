/*
 * Autor: 1705159
 */

public class TopDownParser {
    // Variable position zeigt die Position des aktuellen Symbol
    private int position;
    //Speichert die RA
    private final String eingabe;
    //Wo muss man die OperandNode stehen
    private int leafPosition;

    public TopDownParser(String eingabe){
        position = 0;
        leafPosition = 1;
        this.eingabe=eingabe;
    }
    /**
     * Funktion match ist vorgegeben und benutzt, damit wir Codestruktur behalten
     */
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
    /**
     * Vorgegebene Funktion
     */
    private void assertEndOfInput() {
        if (this.position < this.eingabe.length()) {
            throw new RuntimeException("No end of input reached!");
        }
    }
    /**
     *
     * @param parameter
     * @return diese Funktion gibt die Syntaxbaum zurück
     */
    public Visitable start(Visitable parameter){
        if (eingabe.charAt(position) == '(')
        {
            match('(');
            Visitable regExp = RegExp(null);
            match(')');
            match('#');
            assertEndOfInput();
            OperandNode opNode = new OperandNode("#");
            opNode.position = this.leafPosition;
            return new BinOpNode("°", regExp, opNode);
        }
        else if (eingabe.charAt(position) == '#')
        {
            match('#');
            assertEndOfInput();
            OperandNode opNode = new OperandNode("#");
            opNode.position = this.leafPosition;
            return opNode;
        }
        else
            {
            throw new RuntimeException("Syntax error!");
            }
    }
    /**
     *
     * @param parameter
     * @return
     */
    private Visitable RegExp(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   // a..z, A..z
                Character.isDigit(eingabe.charAt(position)) ||    // 0..9
                eingabe.charAt(position) == '(')
        {
            Visitable term = this.term(null);
            return this.RE(term);
        }
        else throw new RuntimeException("Syntax error!");
    }
    /**
     *
     * @param parameter
     * @return
     */
    private Visitable RE(Visitable parameter) {
        if (eingabe.charAt(position) == '|') {
            this.match('|');
            // Prepare return value
            Visitable term = this.term(null);
            Visitable root = new BinOpNode("|", parameter, term);
            return this.RE(root);
        }
        else if (eingabe.charAt(position) == ')') {
            return parameter;
        }
        else throw new RuntimeException("Syntax error!");
    }
    /**
     *
     * @param parameter
     * @return
     */
    private Visitable term(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   // a..z, A..z
                Character.isDigit(eingabe.charAt(position)) ||    // 0..9
                eingabe.charAt(position) == '(')
        {
            Visitable factor = this.factor(null);
            Visitable termHolder;
            if (parameter != null) {
                termHolder = this.term(new BinOpNode("°", parameter, factor));
            } else {
                termHolder = this.term(factor);
            }
            return termHolder;
        }
        else if (eingabe.charAt(position) == '|' ||
                eingabe.charAt(position) == ')')
        {
            return parameter;
        }
        else
            {
            throw new RuntimeException("Syntax error!");
            }
    }
    /**
     *
     * @param parameter
     * @return
     */
    private Visitable factor(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   // a..z, A..z
                Character.isDigit(eingabe.charAt(position)) ||    // 0..9
                eingabe.charAt(position) == '(')
        {
            Visitable elem = this.elem(null);
            return this.hOp(elem);
        }
        else throw new RuntimeException("Syntax error!");
    }
    /**
     *
     * @param parameter
     * @return
     */
    private Visitable hOp(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   // a..z, A..z
                Character.isDigit(eingabe.charAt(position)) ||    // 0..9
                eingabe.charAt(position) == '(' ||
                eingabe.charAt(position) == '|' ||
                eingabe.charAt(position) == ')')
        {
            return parameter;
        }
        else if (eingabe.charAt(position) == '*' ||
                eingabe.charAt(position) == '+' ||
                eingabe.charAt(position) == '?')
        {
            char curChar = eingabe.charAt(position);
            this.match(curChar);
            String curStr = Character.toString(curChar);
            return new UnaryOpNode(curStr, parameter);
        }
        else throw new RuntimeException("Syntax error!");
    }
    /**
     *
     * @param parameter
     * @return
     */
    private Visitable elem(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||
                Character.isDigit(eingabe.charAt(position)))
        {
            return this.alphanum(null);
        }
        else if (eingabe.charAt(position) == '(') {
            match('(');
            Visitable regExp = RegExp(null);
            match(')');
            return regExp;
        }
        else throw new RuntimeException("Syntax error!");
    }
    /**
     *
     * @param parameter
     * @return
     */
    private Visitable alphanum(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||
                Character.isDigit(eingabe.charAt(position)))
        {
            char curChar = eingabe.charAt(position);
            this.match(curChar);
            // Prepare return value
            String symbol = Character.toString(curChar);
            OperandNode opNode = new OperandNode(symbol);
            opNode.position = leafPosition;
            leafPosition++;
            return opNode;
        }
        else throw new RuntimeException("Syntax error!");
    }
}
