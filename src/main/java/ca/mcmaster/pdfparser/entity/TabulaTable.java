package ca.mcmaster.pdfparser.entity;


import java.lang.*;

import ca.mcmaster.pdfparser.algorithm.BasicExtractionAlgorithm;
import ca.mcmaster.pdfparser.algorithm.ExtractionAlgorithm;
import ca.mcmaster.pdfparser.exceptions.InvalidArgumentException;
import lombok.extern.slf4j.Slf4j;
import technology.tabula.RectangularTextContainer;

import java.util.*;

@Slf4j
public class TabulaTable extends Table {
    private final TreeMap<java.lang.Float, List<Cell>> table;
    private final Map<Integer, java.lang.Float> keyMap;
    private final List<Cell> cellList;

    private int rowCount = 0;
    private final List<Integer> colCount;

    private final TableIterator<String> it;
    private final Comparator<Cell> lineComparator;
    private final Comparator<TextChunk> chunkComparator;

    private static final int FIRST_INDEX = 0;
    private static final float VALID_CELL_WIDTH_THRESHOLD = 2F;
    private static final float VALID_CELL_HEIGHT_THRESHOLD = 2F;
    private static final String CELL_SPLITOR = " | ";
    private static final String LINE_SPILITOR = System.getProperty("line.separator");

    public TabulaTable(List<Cell> cells, List<TextChunk> textChunks){
        this(new BasicExtractionAlgorithm(), cells, textChunks);
    }

    public TabulaTable(ExtractionAlgorithm extractionAlgorithm, List<Cell> cells, List<TextChunk> textChunks) {
        super(extractionAlgorithm);
        this.cellList = cells;
        lineComparator = (c1, c2) -> {
            if((float)c1.getX() < (float)c2.getX()) return -1;
            else if((float)c1.getX() > (float)c2.getX()) return 1;
            else return 0;
        };
        chunkComparator = (c1, c2) ->{
            double x1 = c1.getX();
            double x2 = c2.getX();
            if(x1 == x2) return 0;
            return x1 < x2 ? -1: 1;
        };
        keyMap = new HashMap<>();
        this.table = new TreeMap<>((f1, f2) -> {
            if(f1 < f2) return -1;
            else if(f1 > f2) return 1;
            else return 0;
        });
        colCount = new ArrayList<>();

        //Step1:Add all cells and create the tree map.
        addCells(cells);
        //Step2: Add all textchunks to correct cell.
        addTextChunks(textChunks);
        //Step3: Check all cells again and remove invalid cells.
        it = new TableIterator<>();
    }

    /**
     * Add all cells into the map.
     * All keys should be sorted and all values in the list should
     * be sorted according to customized algorithm.
     * @param cells
     */
    private void addCells(final List<Cell> cells){
        for(int i = 0; i < cells.size(); i++){
            Cell cell = cells.get(i);
            if(checkContainer(cell)){
                float y = (float)cell.getY();
                /**
                 * Currently, lines are not sorted and we
                 * will sort them at the end.
                 */
                List<Cell> line = table.containsKey(y) ? table.get(y) : new ArrayList<>();
                line.add(cell);
                table.put(y, line);
            }
        }

        /**
         * Now we finished adding all cells in current page and
         * we can sort the lists by their x coordinate values.
         */
        Iterator<java.lang.Float> iterator = table.keySet().iterator();
        int count = 0;
        while (iterator.hasNext()){
            rowCount++;
            final java.lang.Float key = iterator.next();
            keyMap.put(count++, key);
            List<Cell> list = table.get(key);
            colCount.add(list.size());
            list.sort(lineComparator);
        }
    }

    /**
     * Add all trunks to correct cell.
     * @param textChunks
     */
    private void addTextChunks(final List<TextChunk> textChunks){
        // Add all text chunks to corresponding cell.
        final NavigableSet<java.lang.Float> keySet = this.table.navigableKeySet();
        OUT:
        for(int i = 0; i < textChunks.size(); i++){
            TextChunk chunk = textChunks.get(i);
            if(!checkContainer(chunk) || chunk.getText().length() == 0) continue;
            final Iterator<java.lang.Float> iterator = keySet.iterator();
            double y = chunk.getY();
            double maxY = chunk.getMaxY();
            double x = chunk.getX();
            double maxX = chunk.getMaxX();

            while(iterator.hasNext()){
                final java.lang.Float k = iterator.next();
                List<Cell> cells = table.get(k);
                Cell cell = cells.get(FIRST_INDEX);
                double tempY = cell.getY();
                double tempMaxY = cell.getMaxY();
                if(!within(y, maxY, tempY, tempMaxY)) continue;
                for(int j = 0; j < cells.size(); j++){
                    Cell cur = cells.get(j);
                    double tempX = cur.getX();
                    double tempMaxX = cur.getMaxX();
                    if((x >= tempX && x < tempMaxX) && (within(y, maxY, tempY, tempMaxY))){
                        cur.getTextElements().add(chunk);
                        continue OUT;
                    }
                }
            }
        }

        /**
         * Sort all textChunk in the Cell to make words in order.
         */
        for(int k = 0; k < this.cellList.size(); k++){
            Cell cell = this.cellList.get(k);
            if(checkContainer(cell)){
                List<TextChunk> textElements = cell.getTextElements();
                textElements.sort(chunkComparator);
            }
        }
    }

    public Cell getCell(int row, int col){
        java.lang.Float key = keyMap.get(row);
        return table.get(key).get(col);
    }

    public String getContentFromCell(Cell cell){
        StringBuilder res = new StringBuilder();
        final List<TextChunk> textElements = cell.getTextElements();
        if(textElements.size() == 0) return "";
        for(TextChunk chunk : textElements){
            res.append(chunk.getText());
        }
        return res.toString();
    }

    public String getContentFromTable(int row, int col){
        if(row >= rowCount || col >= colCount.get(row)){
            if(row >= rowCount)
                throw new InvalidArgumentException(row + " is out of range.");
            throw new InvalidArgumentException(col + "is out of range.");
        }
       return getContentFromCell(getCell(row, col));
    }

    public String getLine(List<Cell> line){
        StringBuilder res = new StringBuilder();
        for(Cell cell : line){
            res.append(getContentFromCell(cell) + CELL_SPLITOR );
        }
        int len = res.length();
        final int spiltorLength = CELL_SPLITOR.length();
        res.delete(len - spiltorLength, len);
        res.append(LINE_SPILITOR);
        return res.toString();
    }

    public String showTable(){
        StringBuilder tableString = new StringBuilder();
        //Get all rows first
        for(int i = 0; i < rowCount; i++){
            List<Cell> cells = table.get(keyMap.get(i));
            tableString.append(getLine(cells));
        }
        return tableString.toString();
    }

    private boolean checkContainer(RectangularTextContainer container){
        float width = (float)container.getWidth();
        float height = (float)container.getHeight();
        return width >=  VALID_CELL_WIDTH_THRESHOLD && height >= VALID_CELL_HEIGHT_THRESHOLD;
    }

    private boolean within(double aStart, double aEnd, double bStart, double bEnd){
        return aStart >= bStart && aStart <= bEnd && aEnd >= bStart && aEnd <= bEnd;
    }

    private boolean withinHead(double aStart, double bStart){
        return aStart >= bStart;
    }

    public TableIterator<String> getTableIterator(){
        return it;
    }

    /**
     * For extension.
     * @param table
     */
    public static void removeEmptyLines(TabulaTable table){

    }

    public class TableIterator<String> implements Iterator<String>{
        /**
         * Original x and y should be 0, 0.
         */
        private int x, y;

        @Override
        public boolean hasNext() {
            return !(x == rowCount - 1 && y == colCount.get(rowCount - 1) - 1);
        }

        @Override
        public String next() {
            if(y == colCount.get(x) - 1){
                x++;
                y = 0;
            }else y++;
            return (String) getContentFromTable(x, y);
        }

        public void reset(){
            x = 0;
            y = 0;
        }
    }
}
