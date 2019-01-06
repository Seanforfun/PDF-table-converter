package ca.mcmaster.pdfparser.decoder;

import ca.mcmaster.pdfparser.entity.Cell;
import ca.mcmaster.pdfparser.entity.Table;
import ca.mcmaster.pdfparser.entity.TabulaTable;
import ca.mcmaster.pdfparser.entity.vo.CellItem;
import ca.mcmaster.pdfparser.entity.vo.ParsedItem;
import ca.mcmaster.pdfparser.exceptions.CommandException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 9:56
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
@Slf4j
public class CellDecoder implements Decoder{
    private String command;
    private boolean hasParam;
    private int offset;
    private Map<String, Integer> param = new HashMap<>();

    private static final String CELL_PREFIX = "[CELL]";
    private static final char[] KEY_VALUE_SPLITOR = {':', '?'};
    private static final char BRACKET_START = '(';
    private static final char BRACKET_END = ')';
    private static final int TOKEN_MISSING = -1;
    private static final String DIGIT_REGEX = "[0-9]*";
    private static final String OFFSET = "OFFSET";

    private CellDecoder(String command){
        this.command = command;
    }

    private boolean isValidParam(String token){
        return Pattern.matches(DIGIT_REGEX, token);
    }

    @Override
    public boolean compile(String command) {
        if(!command.startsWith(CELL_PREFIX)) return false;
        int startIndex;
        if((startIndex = command.indexOf(BRACKET_START)) != TOKEN_MISSING){
            this.hasParam = true;
            String param = command.substring(startIndex, command.indexOf(BRACKET_END));
            if(!isValidParam(DIGIT_REGEX)) return false;
            this.offset = Integer.parseInt(param);
            this.param.put(OFFSET, offset);
        }
        return true;
    }

    @Override
    public Decoder getDecoder(String command) {
        Decoder cellDecoder = new CellDecoder(command);
        if(!cellDecoder.compile(command)){
            throw new CommandException(command + " is Invalid");
        }
        return cellDecoder;
    }

    @Override
    public ParsedItem decode(TabulaTable table, int row, int col) {
        String content = table.getContentFromTable(row, col);
        CellItem item = new CellItem();
        if(!hasParam){
            if(content.indexOf(KEY_VALUE_SPLITOR[0]) == -1 &&
                    content.indexOf(KEY_VALUE_SPLITOR[1]) == -1){
                item.setHasKey(false);
                item.setCellItemValue(content);
            }else{
                item.setHasKey(true);
                if(content.indexOf(KEY_VALUE_SPLITOR[0]) != -1){
                    CellItem.buildItem(item, content, KEY_VALUE_SPLITOR[0]);
                }else {
                    CellItem.buildItem(item, content, KEY_VALUE_SPLITOR[1]);
                }
            }
        }
        if(hasParam){
            item.setHasKey(true);
            int offset = param.get(OFFSET);
            item.setCellItemKV(content, table.getContentFromTable(row, col + offset));
        }
        return item;
    }
}
