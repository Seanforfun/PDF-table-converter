package ca.mcmaster.pdfparser.decoder;

import ca.mcmaster.pdfparser.entity.TabulaTable;
import ca.mcmaster.pdfparser.entity.vo.ParsedItem;
import ca.mcmaster.pdfparser.exceptions.CommandException;

import java.util.Arrays;
import java.util.List;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 21:30
 * @description: ${description}
 * @modified:
 * @version: 0.01
 */
public class TableDecoder implements Decoder {
    private String command;
    private List<String> rows;
    private List<String> columns;

    private static final String TABLE_PREFIX = "[TABLE]";
    private static final String ROW_COLUMN_SPLITOR = ";";
    private static final String NAME_SPLITOR = "|";
    private static final int ROW_INDEX = 0;
    private static final int COLUMN_INDEX = 1;

    public TableDecoder(String command){
        this.command = command;
    }

    @Override
    public boolean compile(String command) {
        if(!command.startsWith(TABLE_PREFIX))
            throw new CommandException(command + " is invalid");
        int angleStart = 0;
        int angleEnd = 0;
        if((angleStart = command.indexOf(ANGLE_BRACKET_START)) == TOKEN_MISSING &&
                (angleEnd = command.indexOf(ANGLE_BRACKET_END)) == TOKEN_MISSING)
            throw new CommandException(command + " is invalid");
        String rowColumnInfo = command.substring(angleStart + 1, angleEnd);
        rows = Arrays.asList(rowColumnInfo.split(ROW_COLUMN_SPLITOR)[ROW_INDEX].split(NAME_SPLITOR));
        columns = Arrays.asList(rowColumnInfo.split(ROW_COLUMN_SPLITOR)[COLUMN_INDEX].split(NAME_SPLITOR));
        return true;
    }

    @Override
    public ParsedItem decode(TabulaTable table, int row, int col) throws Exception {
        return null;
    }
}
