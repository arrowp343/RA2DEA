import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

//Autor: 7862288

public class FollowposTableGeneratorTest {
    // TODO zweiter Vistor: Eyyüp
    // test fuer zweiten Visitor bzw. dem FollowposTableGenerator
    @Test
    @DisplayName("Test for FollowposTableGenerator")
    public void followPosTabeGeneratorTest() {
        // Ertellen eines Syntaxbaum
        Visitable syntaxTreeWithValues = createSyntaxTree();

        SortedMap<Integer, FollowposTableEntry> followPosTableEntries = createFollowPosTable();
        FollowposTableGenerator generator = new FollowposTableGenerator();
        DepthFirstIterator.traverse(syntaxTreeWithValues, generator);

        //Vergleich der hart kodierten und generierten Followpos-Tabelle
        assertEquals(
                followPosTableEntries,              // erwartetes Ergebnis
                generator.getFollowposTable());     // tatsaechliches Ergebnis
    }

    //Hard kodierte SyntaxTree
    private Visitable createSyntaxTree() {
        //Verwendung der Regex mit terminierendes Symbol (a|b)^*cd^*#

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
        left2 = new BinOpNode("°", (Visitable) left2, right);
        left2.firstpos.addAll(Arrays.asList(1, 2, 3));
        left2.lastpos.add(5);
        left2.nullable = false;

        //Rückgabe
        return (Visitable) left2;
    }

    //FollowPos Tabelle mit den Eingabesymbolen a,b,c,d und #
    protected SortedMap<Integer, FollowposTableEntry> createFollowPosTable() {
        SortedMap<Integer, FollowposTableEntry> followPosTableGenerated = new TreeMap<>();

        FollowposTableEntry entry = new FollowposTableEntry(1, "a");
        entry.followpos.addAll(Arrays.asList(1, 2, 3));
        followPosTableGenerated.put(1, entry);

        entry = new FollowposTableEntry(2, "b");
        entry.followpos.addAll(Arrays.asList(1, 2, 3));
        followPosTableGenerated.put(2, entry);

        entry = new FollowposTableEntry(3, "c");
        entry.followpos.addAll(Arrays.asList(4,5));
        followPosTableGenerated.put(3, entry);

        entry = new FollowposTableEntry(4, "d");
        entry.followpos.addAll(Arrays.asList(4,5));
        followPosTableGenerated.put(4, entry);

        entry = new FollowposTableEntry(5, "#");
        followPosTableGenerated.put(5, entry);

        return followPosTableGenerated;
    }
}