/*
 * Autor: 1705159
 */

public class TopDownParser {
    // Variable position zeigt die Position des aktuellen Symbol
    private int position;
    //Speichert die RA
    private final String eingabe;
    //Wo muss das OperandNode stehen
    private int leafPosition;

    public TopDownParser(String eingabe){
        position = 0;
        leafPosition = 1;
        this.eingabe = eingabe;
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
    /**
     * Vorgegebene Funktion
     */
    private void assertEndOfInput() {
        if (this.position < this.eingabe.length()) {
            throw new RuntimeException("No end of input reached!");
        }
    }
    /**
     * Das ist die einzige oeffentliche Funktion, die das parsen beginnt
     * @return diese Funktion gibt die Syntaxbaum zurück oder wirft eine Exception
     */
    public Visitable start(Visitable parameter){
        if (eingabe.charAt(position) == '(')
        {
            match('(');
            Visitable subTree = RegExp(null);
            match(')');
            match('#');
            assertEndOfInput();
            OperandNode operandNode = new OperandNode("#");
            //OperandNode position initialisieren
            operandNode.position = leafPosition;
            return new BinOpNode("°", subTree, operandNode);
        }
        else if (eingabe.charAt(position) == '#')
        {
            match('#');
            assertEndOfInput();
            OperandNode operandNode = new OperandNode("#");
            //OperandNode position initialisieren
            operandNode.position = leafPosition;
            return operandNode;
        }
        else
        {
            throw new RuntimeException("Syntax error!");
        }
    }

    /**  pro Nichtterminal eine Methode */

    private Visitable RegExp(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   // a..z, A..z
                Character.isDigit(eingabe.charAt(position)) ||    // 0..9
                eingabe.charAt(position) == '(')
        {
            return RE(term(null));
        }
        else
        {
            throw new RuntimeException("Syntax error!");
        }
    }

    private Visitable RE(Visitable parameter) {
        if (eingabe.charAt(position) == '|') {
            match('|');
            return RE(new BinOpNode("|", parameter, term(null)));
        }
        else if (eingabe.charAt(position) == ')') {
            return parameter;
        }
        else
        {
            throw new RuntimeException("Syntax error!");
        }
    }

    private Visitable term(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   // a..z, A..z
                Character.isDigit(eingabe.charAt(position)) ||    // 0..9
                eingabe.charAt(position) == '(')
        {
            if (parameter != null) {
                return term(new BinOpNode("°", parameter, factor(null)));
            }
            return term(factor(null));
        } else if (eingabe.charAt(position) == '|' ||
                eingabe.charAt(position) == ')')
        {
            return parameter;
        }
        else
        {
            throw new RuntimeException("Syntax error!");
        }
    }

    private Visitable factor(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   // a..z, A..z
                Character.isDigit(eingabe.charAt(position)) ||    // 0..9
                eingabe.charAt(position) == '(')
        {
            return HOp(elem(null));
        }
        else
        {
            throw new RuntimeException("Syntax error!");
        }
    }

    private Visitable HOp(Visitable parameter) {
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
            char currentChar = eingabe.charAt(position);
            match(currentChar);
            return new UnaryOpNode(Character.toString(currentChar), parameter);
        }
        else
        {
            throw new RuntimeException("Syntax error!");
        }
    }

    private Visitable elem(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||   //a..z, A..Z
                Character.isDigit(eingabe.charAt(position)))  // 0..9
        {
            return alphanum(null);
        }
        else if (eingabe.charAt(position) == '(') {
            match('(');
            Visitable regExp = RegExp(null);
            match(')');
            return regExp;
        }
        else throw new RuntimeException("Syntax error!");
    }

    private Visitable alphanum(Visitable parameter) {
        if (Character.isLetter(eingabe.charAt(position)) ||  //a..z, A..Z
                Character.isDigit(eingabe.charAt(position))) // 0..9
        {
            char currentChar = eingabe.charAt(position);
            match(currentChar);
            String symbol = Character.toString(currentChar);
            OperandNode operandNode = new OperandNode(symbol);
            operandNode.position = leafPosition;
            leafPosition++;
            return operandNode;
        }
        else
        {
            throw new RuntimeException("Syntax error!");
        }
    }
}
