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
    private final TreeMap<java.lang.Float, Line> lineTable;
    private final Map<Integer, java.lang.Float> keyMap;
    private final List<Cell> cellList;

    private int rowCount = 0;
    private final List<Integer> colCount;

    private final TableIterator<String> it;
    private final Comparator<Cell> lineComparator;
    private final Comparator<TextChunk> chunkComparator;
    private final Comparator<java.lang.Float> treemapComparator;

    private static final float VALID_CELL_WIDTH_THRESHOLD = 2F;
    private static final float VALID_CELL_HEIGHT_THRESHOLD = 2F;
    public static final String CELL_SPLITOR = " | ";
    public static final String LINE_SPILITOR = System.getProperty("line.separator");

    public TabulaTable(List<Cell> cells, List<TextChunk> textChunks){
        this(new BasicExtractionAlgorithm(), cells, textChunks);
    }

    private TabulaTable(ExtractionAlgorithm extractionAlgorithm, List<Cell> cells, List<TextChunk> textChunks) {
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
        treemapComparator = (f1, f2) -> {
            if(f1 < f2) return -1;
            else if(f1 > f2) return 1;
            else return 0;
        };
        keyMap = new HashMap<>();
        this.table = new TreeMap<>(treemapComparator);
        colCount = new ArrayList<>();
        it = new TableIterator<>();
        lineTable = new TreeMap<>(treemapComparator);
        createTreeMapTable(cells, textChunks);
    }

    private void createTreeMapTable(final List<Cell> cells,  final List<TextChunk> textChunks){
        //Step1:Add all cells and create the tree map.
        addCells(cells);
        //Step2: Add all textchunks to correct cell.
        addTextChunks(textChunks);
        //Step3: Create line table
        createLineTable();
    }

    private Line getLineByY(float yStart, float yEnd){
        List<Line> lines = new LinkedList<>(lineTable.values());
        for(int i = 0 ; i < lines.size(); i++){
            Line line = lines.get(i);
            if((float)line.getY() <= yStart && (float)line.getMaxY() >= yEnd) {
                return line;
            }
        }
        Line line = new Line();
        line.setY(yStart);
        line.setMaxY(yEnd);
        lineTable.put(yStart, line);
        return line;
    }

    private Line getBelongLine(Cell cell){
        Collection<Line> lines = lineTable.values();
        float yStart = (float) cell.getY();
        float yEnd = (float) cell.getMaxY();
        for(Line line : lines){
            float tempY = (float)line.getY();
            float tempMaxY = (float)line.getMaxY();
            if(within(yStart, yEnd, tempY, tempMaxY)){
                return line;
            }
        }
        return new Line();
    }

    private void createLineTable(){
        NavigableSet<java.lang.Float> keySet = table.navigableKeySet();
        Iterator<java.lang.Float> it = keySet.iterator();

        while(it.hasNext()){
            // Get the yStart of current line, we try to get the line object.
            float key = it.next();
            List<Cell> lineList = table.get(key);
            float max = 0F;

            // Create or get line first
            for(Cell cell : lineList){
                max = Math.max(max, (float) cell.getMaxY());
            }
            Line belongLine = getLineByY(key, max);
            // Add cell to correct line
            for(Cell cell : lineList){
                belongLine.addCell(cell);
            }
        }

        // Sort all list in single line
        Collection<Line> lines = lineTable.values();
        for(Line line : lines){
            line.sort();
        }
    }

    /**
     * Add all cells into the map.
     * All keys should be sorted and all values in the list should
     * be sorted according to customized algorithm.
     * @param cells
     */
    private void addCells(final List<Cell> cells){
        for (Cell cell : cells) {
            if (checkContainer(cell)) {
                float y = (float) cell.getY();
                /**
                 * Currently, lines are not sorted and we
                 * will sort them at the end.
                 */
                List<Cell> line = table.containsKey(y) ? table.get(y) : new ArrayList<>();
                line.add(cell);
                table.put(y, line);
            }
        }

        /*
          Now we finished adding all cells in current page and
          we can sort the lists by their x coordinate values.
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
            double y = chunk.getY();    // Start coordinate of current chunk
            double maxY = chunk.getMaxY();  // End coordinate of current chunk
            double x = chunk.getX();
            double maxX = chunk.getMaxX();
            OUT1:
            while(iterator.hasNext()){
                final java.lang.Float k = iterator.next();
                List<Cell> cells = table.get(k);

                //Check all cells in current line,
                //make sure all y starts of text chunk is equal to
                //the start of current line.
                for(int j = 0; j < cells.size(); j++){
                    Cell cell = cells.get(j);
                    double tempY = cell.getY();
                    double tempMaxY = cell.getMaxY();
                    if(!within(y, maxY, tempY, tempMaxY)) continue OUT1;
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


    public String getContentFromTable(int row, int col){
        if(row >= rowCount || col >= colCount.get(row)){
            if(row >= rowCount)
                throw new InvalidArgumentException(row + " is out of range.");
            throw new InvalidArgumentException(col + "is out of range.");
        }
       return Cell.getContentFromCell(getCell(row, col));
    }

    public String getLine(List<Cell> line){
        StringBuilder res = new StringBuilder();
        for(Cell cell : line){
            res.append(Cell.getContentFromCell(cell) + CELL_SPLITOR );
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

    public String showTableByLine(){
        StringBuilder stringBuilder = new StringBuilder();
        Collection<Line> lines = lineTable.values();
        for(Line line : lines){
            stringBuilder.append(line.toString());
        }
        return stringBuilder.toString();
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
