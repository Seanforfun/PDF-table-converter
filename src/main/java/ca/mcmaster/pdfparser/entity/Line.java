package ca.mcmaster.pdfparser.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import technology.tabula.Rectangle;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@SuppressWarnings("serial")
@Slf4j
/**
 * @author: Seanforfun
 * @date: Created in 2019/1/4 10:47
 * @description: This is a container of cells.
 * @modified:
 * @version: 0.0.1
 */
public class Line extends Rectangle {

    @Setter @Getter
    private int lineHold;
    @Setter @Getter
    private TreeMap<Integer, List<Cell>> lines = null;
    @Setter @Getter
    private double y, maxY;
    List<TextChunk> textChunks = new ArrayList<>();

    private static final String CELL_SPLITOR = TabulaTable.CELL_SPLITOR;
    private static final String LINE_SPILITOR = TabulaTable.LINE_SPILITOR;
    private static final Comparator<Integer> lineComparator = (f1, f2) -> {
        if(f1.equals(f2)) return 0;
        return f1 < f2 ? -1: 1;
    };
    private static final  Comparator<Cell> cellLineComparator = (c1, c2) ->{
        double x1 = c1.getX();
        double x2 = c2.getX();
        if(x1 == x2) return 0;
        return x1 < x2 ? -1: 1;
    };
    public static final Character[] WHITE_SPACE_CHARS = { ' ', '\t', '\r', '\n', '\f' };

    public Line(){
        lines = new TreeMap<>(lineComparator);
        lineHold = 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        NavigableSet<Integer> keySet = lines.navigableKeySet();
        Iterator<Integer> it = keySet.iterator();
        while(it.hasNext()){
            int key = it.next();
            List<Cell> singleLine = lines.get(key);
            for(Cell cell : singleLine){
                String cellInfo = Cell.getContentFromCell(cell);
                sb.append(cellInfo + CELL_SPLITOR);
            }
            sb.append(LINE_SPILITOR);
        }
        return sb.toString();
    }

    public void addCell(Cell cell){
        float y = (float) cell.getY();
        int key = (int)y;
        List<Cell> currentLine = lines.containsKey(key) ? lines.get(key) : new ArrayList<>();
        currentLine.add(cell);
        lines.put(key, currentLine);
    }

    public void sort(){
        Collection<List<Cell>> lists = lines.values();
        for(List<Cell> cells : lists){
            cells.sort(cellLineComparator);
        }
    }

    public void addTextChunk(TextChunk textChunk) {
        if (this.textChunks.isEmpty()) {
            this.setRect(textChunk);
        }
        else {
            this.merge(textChunk);
        }
        this.textChunks.add(textChunk);
    }

    public List<TextChunk> getTextElements() {
        return textChunks;
    }

    static Line removeRepeatedCharacters(Line line, Character c, int minRunLength) {

        Line rv = new Line();

        for(TextChunk t: line.getTextElements()) {
            for (TextChunk r: t.squeeze(c, minRunLength)) {
                rv.addTextChunk(r);
            }
        }

        return rv;
    }
}
