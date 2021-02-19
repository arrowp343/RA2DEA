import java.util.SortedMap;
import java.util.TreeMap;

//Autor: 7862288

public class FollowposTableGenerator implements Visitor {

    //Dekleration eines TreeMaps
    private final SortedMap<Integer, FollowposTableEntry> FollowposTableEntries = new TreeMap<>();

    //Deklaration der Methode visit
    @Override
    public void visit(OperandNode node)
    {
        // Default-Zeile in followPos-Tabelle
        FollowposTableEntry followPosTableEntry = new FollowposTableEntry(node.position, node.symbol);
        FollowposTableEntries.put(
                node.position,
                followPosTableEntry);
        // mit new FollowPosTableEntry(node.position, node.symbol)); funktioniert es auch
    }
    @Override
    public void visit(BinOpNode node)
    {
        // Nur | (Alternative) und ° (Konkatenation) sind moeglich
        if (("°").equals(node.operator)) {
            // lastpos-Menge von Unterknoten node.left holen
            SyntaxNode leftNode = (SyntaxNode) node.left;

            // Algorithmus
            for(int i : leftNode.lastpos) {
                // firstpos-Menge von Unterknoten node.right holen
                SyntaxNode rightNode = (SyntaxNode) node.right;
                FollowposTableEntries.get(i).followpos.addAll(rightNode.firstpos);
            }
        }
        // return... Alternative spielt keine Rolle !!!
    }

    @Override
    public void visit(UnaryOpNode node)
    {
        // Nur * (Kleene-Operator), + (positiver Operator) und ?  (Option) möglich
        if ("+".equals(node.operator) || ("*").equals(node.operator)) // || ("?").equals(node.option) nicht nötig.
        {
            // lastpos-Menge von Unterknoten node.subNode holen
            SyntaxNode innerNode = (SyntaxNode) node.subNode;
            // firstpos-Menge von Unterknoten node.subNode holen
            // Algorithmus
            for (int i : innerNode.lastpos) {
                FollowposTableEntries.get(i).followpos.addAll(innerNode.firstpos);
            }
            // return; Option spielt keine Rolle !!! Return nicht nötig, da Rückgabetyp void.
        }

    }

    // getter fuer followpos-Tabelle
    public SortedMap<Integer, FollowposTableEntry> getFollowposTable()
    {

        return FollowposTableEntries;
    }
}