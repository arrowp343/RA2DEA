import java.util.SortedMap;
import java.util.TreeMap;

//Ersteller: 7862288

public class FollowposTableGenerator implements Visitor {

    //Dekleration eines TreeMaps
    private final SortedMap<Integer, FollowposTableEntry> FollowposEntry = new TreeMap<>();

    //Deklaration der Methode visit
    @Override
    public void visit(OperandNode node) {
        // Default-Zeile in followPos-Tabelle
        FollowposTableEntry followPosTableEntry = new FollowposTableEntry(node.position, node.symbol);
        FollowposEntry.put(
                node.position,
                followPosTableEntry);
        // mit new FollowPosTableEntry(node.position, node.symbol)); funktioniert es auch
    }

    @Override
    public void visit(BinOpNode node) {
        // Nur | (Alternative) und ° (Konkatenation) sind moeglich
        if (("°").equals(node.operator)) {
            // lastpos-Menge von Unterknoten node.left holen
            SyntaxNode leftNode = (SyntaxNode) node.left;
            // firstpos-Menge von Unterknoten node.right holen
            SyntaxNode rightNode = (SyntaxNode) node.right;
            // Algorithmus
            for (int i : leftNode.lastpos) {
                FollowposEntry.get(i).followpos.addAll(rightNode.firstpos);
            }
        }
    }

    @Override
    public void visit(UnaryOpNode node) {
        // Nur * (Kleene-Operator), + (positiver Operator) und ?  (Option) möglich
        if ("+".equals(node.operator) || ("*").equals(node.operator)) // || ("?").equals(node.option) nicht nötig.
        {
            // lastpos-Menge von Unterknoten node.subNode holen
            SyntaxNode innerNode = (SyntaxNode) node.subNode;
            // firstpos-Menge von Unterknoten node.subNode holen
            // Algorithmus
            for (int i : innerNode.lastpos) {
                FollowposEntry.get(i).followpos.addAll(innerNode.firstpos);
            }
        }

    }

    // getter fuer followpos-Tabelle
    public SortedMap<Integer, FollowposTableEntry> getFollowposTable() {
        return FollowposEntry;
    }
}