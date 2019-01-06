package ca.mcmaster.pdfparser.entity;

import ca.mcmaster.pdfparser.exceptions.LineFinalizedMissingException;
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
    @Setter @Getter
    private double x = java.lang.Double.MAX_VALUE, maxX = 0D;
    List<TextChunk> textChunks = new ArrayList<>();
    private boolean finalized;

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
    private static final Map<Integer, Integer> keyMap = new HashMap<>();
    private static final Integer FIRST_CELL_IN_CURRENT_LINE = 0;
    private static final Integer EMPTY_LINE = 0;

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

    private boolean closeTo(float pre, float next){
        if(Math.abs(pre - next) < 4)
            return true;
        return false;
    }

    public void lineFinalize(){
        finalized = true;
        // Start creating keyMap
        int count = 0;
        NavigableSet<Integer> keySet = lines.navigableKeySet();
        List<Integer> keyList = new ArrayList<>(keySet);
        for (int i = 0; i < keyList.size(); i++){
            final List<Cell> cells = lines.get(keyList.get(i));
            int len = EMPTY_LINE;
            for(Cell cell : cells){
                len += Cell.getContentFromCell(cell).length();
            }
            if(len == EMPTY_LINE){
                this.lines.remove(keyList.get(i));
                continue;
            }
            keyMap.put(count++, keyList.get(i));
        }
        // Set x and maxX
        final Collection<List<Cell>> values = lines.values();

        for (List<Cell> cells : values){
            this.x = Math.min(cells.get(FIRST_CELL_IN_CURRENT_LINE).getX(), this.x);
            this.maxX = Math.max(cells.get(cells.size() - 1).getMaxX(), this.maxX);
        }
        this.lineHold = lines.size();
    }

    /**
     * Try to align all cells to have all table
     * being completed.
     */
    public void lineAlign(){
        if(!finalized){
            throw new LineFinalizedMissingException("lineFinalize method should be called before lineAlgin.");
        }
        if(lineHold <= 1) return;
        for(int i = 1; i < lineHold; i++){
            List<Cell> cells = lines.get(keyMap.get(i));
            List<Cell> preCells = lines.get(keyMap.get(i - 1));
            int size = cells.size();
            if(size <= 1) continue;
            float preMaxX = (float) this.x;
            for(int j = 0; j < cells.size(); j++){
                final Cell cell = cells.get(j);
                float currentX = (float)cell.getX();
                if(Cell.getContentFromCell(cell).contains("Out")) {
                    System.out.println("+++++++++++++++++++++++++++++++++");
                    System.out.println(preMaxX + " " + currentX);
                    System.out.println("+++++++++++++++++++++++++++++++++");
                }
                if(!closeTo(preMaxX, currentX)){
                    System.out.println("==================================================");
                    System.out.println(Cell.getContentFromCell(cell));
                    System.out.println("==================================================");
                    for (int z = 0; i < preCells.size(); i++){
                        if(closeTo((float)cell.getX(), (float)preCells.get(z).getMaxX())){
                            System.out.println("==================================================");
                            System.out.println(Cell.getContentFromCell(preCells.get(z)));
                            System.out.println("==================================================");
                        }
                    }
                }
                preMaxX = (float)cell.getMaxX();
            }
        }
    }
}
