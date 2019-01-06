package ca.mcmaster.pdfparser.decoder;

import ca.mcmaster.pdfparser.entity.TabulaTable;
import ca.mcmaster.pdfparser.entity.vo.ParsedItem;
import ca.mcmaster.pdfparser.exceptions.CommandException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 11:51
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
@Slf4j
public class ListDecoder implements Decoder {
    private String command;
    private List<String> columns;
    private boolean horizontal;

    private static final String LIST_PREFIX = "[LIST]";
    private static final char LIST_SPLITOR = '|';
    private static final String HORIZONTAL = "HORIZONTAL";
    private static final String VERTIVAL = "VERTIVAL";

    @Override
    public boolean compile(String command) {
        if(!command.startsWith(LIST_PREFIX))
            throw new CommandException(command + " is not a valid list command.");
        int angleBracketStart;
        int angleBracketEnd;
        int bracketStart;
        int bracketEnd;
        if((angleBracketStart = command.indexOf(ANGLE_BRACKET_START)) == TOKEN_MISSING ||
                (angleBracketEnd = command.indexOf(ANGLE_BRACKET_END)) == TOKEN_MISSING){
            throw new CommandException(command + " is invalid");
        }
        if((bracketStart = command.indexOf(BRACKET_START)) == TOKEN_MISSING ||
                (bracketEnd = command.indexOf(BRACKET_END)) == TOKEN_MISSING){
            throw new CommandException(command + " is invalid");
        }
        String columns = command.substring(angleBracketStart + 1, angleBracketEnd);
        this.columns = Arrays.asList(columns.split(LIST_PREFIX));
        if(this.columns.size() == 0) throw new CommandException(command + " is invalid");
        String options = command.substring(bracketStart + 1, bracketEnd);
        return false;
    }

    @Override
    public ParsedItem decode(TabulaTable table, int row, int col) {
        return null;
    }
}
