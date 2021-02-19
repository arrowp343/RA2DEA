import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Test;
import static org.junit.Assert.*;

//Autor: 7862288

public class FollowposTableGeneratorTest {
    // test fuer zweiten Visitor bzw. dem FollowposTableGenerator
    @Test
    public void followPosTableGeneratorTest() {
        // Erstellen eines Syntaxbaum
        Visitable syntaxTreeWithValues = createSyntaxTree();

        SortedMap<Integer, FollowposTableEntry> followPosTableEntries = createFollowPosTable();
        FollowposTableGenerator generator = new FollowposTableGenerator();
        DepthFirstIterator.traverse(syntaxTreeWithValues, generator);

        //Vergleich der hart kodierten und generierten Followpos-Tabelle
        assertEquals(
                followPosTableEntries,              // erwartetes Ergebnis
                generator.getFollowposTable());     // tatsaechliches Ergebnis
    }

    //Hart kodierte SyntaxTree
    private Visitable createSyntaxTree() {
        //Verwendung der Regex aus der Vorlesung mit terminierendes Symbol (a|b)*cd*#

        //linkes Blatt "a" auf der Position 1
        OperandNode left = new OperandNode("a");
        left.firstpos.add(1);
        left.lastpos.add(1);
        left.position = 1;
        left.nullable = false;

        //rechtes Blatt "b" auf der Position 2
        OperandNode right = new OperandNode("b");
        right.firstpos.add(2);
        right.lastpos.add(2);
        right.position = 2;
        right.nullable = false;

        // Unterknoten "|"
        SyntaxNode left1 = new BinOpNode("|", left, right);
        left1.firstpos.addAll(Arrays.asList(1, 2));
        left1.lastpos.addAll(Arrays.asList(1, 2));
        left1.nullable = false;

        // linkes Unterknoten "*"
        left1 = new UnaryOpNode("*", (Visitable) left1);
        left1.firstpos.addAll(Arrays.asList(1, 2));
        left1.lastpos.addAll(Arrays.asList(1, 2));
        left1.nullable = true;

        // rechtes Blatt "c" in auf Position 3
        right = new OperandNode("c");
        right.firstpos.add(3);
        right.lastpos.add(3);
        right.position = 3;
        right.nullable = false;

        //linkes Unterknoten "°" auf der position 3
        left1 = new BinOpNode("°", (Visitable) left1, right);
        left1.firstpos.addAll(Arrays.asList(1, 2, 3));
        left1.lastpos.add(3);
        left1.nullable = false;

        //rechtes Blatt "d" auf der position 4
        right = new OperandNode("d");
        right.firstpos.add(4);
        right.lastpos.add(4);
        right.position = 4;
        right.nullable = false;

        //rechter Knoten "*"
        SyntaxNode right2 = new UnaryOpNode("*", right);
        right2.firstpos.add(4);
        right2.lastpos.add(4);
        right2.nullable = true;

        //rechtes Unterknoten "°" auf der position 4
        SyntaxNode left2 = new BinOpNode("°", (Visitable) left1, (Visitable) right2);
        left2.firstpos.addAll(Arrays.asList(1,2,3));
        left2.lastpos.addAll(Arrays.asList(3,4));
        left2.nullable = false;

        //rechtes Blatt "#" terminalzeichen in position 5
        right = new OperandNode("#");
        right.firstpos.add(5);
        right.lastpos.add(5);
        right.position = 5;
        right.nullable = false;

        //abschließender Knoten "°"
        SyntaxNode newTree = new BinOpNode("°", (Visitable) left2, right);
        newTree.firstpos.addAll(Arrays.asList(1, 2, 3));
        newTree.lastpos.add(5);
        newTree.nullable = false;

        //Rückgabe abschließender Syntaxbaum
        return (Visitable) newTree;
    }

    //Erstellen Followpos-Tabelle mit den Eingabesymbolen a,b,c,d und #
    private SortedMap<Integer, FollowposTableEntry> createFollowPosTable() {
        SortedMap<Integer, FollowposTableEntry> followPosTableGenerated = new TreeMap<>();

        //Eintrag des Eingabesymbols "a"
        FollowposTableEntry followposEntry = new FollowposTableEntry(1, "a");
        followposEntry.followpos.addAll(Arrays.asList(1, 2, 3));
        followPosTableGenerated.put(1, followposEntry);

        //Eintrag des Eingabesymbols "b"
        followposEntry = new FollowposTableEntry(2, "b");
        followposEntry.followpos.addAll(Arrays.asList(1, 2, 3));
        followPosTableGenerated.put(2, followposEntry);

        //Eintrag des Eingabesymbols "c"
        followposEntry = new FollowposTableEntry(3, "c");
        followposEntry.followpos.addAll(Arrays.asList(4,5));
        followPosTableGenerated.put(3, followposEntry);

        //Eintrag des Eingabesymbols "d"
        followposEntry = new FollowposTableEntry(4, "d");
        followposEntry.followpos.addAll(Arrays.asList(4,5));
        followPosTableGenerated.put(4, followposEntry);

        //Eintrag des Eingabesymbols "#"
        followposEntry = new FollowposTableEntry(5, "#");
        followPosTableGenerated.put(5, followposEntry);

        //Rückgabe des generierten Followpos Tabelle
        return followPosTableGenerated;
    }
}