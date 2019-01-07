package ca.mcmaster.pdfparser.decoder;

import ca.mcmaster.pdfparser.entity.Cell;
import ca.mcmaster.pdfparser.entity.TabulaTable;
import ca.mcmaster.pdfparser.entity.vo.ListItem;
import ca.mcmaster.pdfparser.entity.vo.ParsedItem;
import ca.mcmaster.pdfparser.exceptions.CommandException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;

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
    private int lineNum;

    private static final String LIST_PREFIX = "[LIST]";
    private static final String LIST_SPLITOR = "|";
    private static final String HORIZONTAL = "HORIZONTAL";
    private static final String VERTICAL = "VERTICAL";
    private static final String OPTION_SPLITOR = ",";
    private static final int LINE_NUMBER_OPTION_INDEX = 1;

    public ListDecoder(String command){
        this.command = command;
        if(!compile(command))
            throw new CommandException(command + " has syntax error.");
    }

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
        this.columns = Arrays.asList(columns.split(LIST_SPLITOR));
        if(this.columns.size() == 0) throw new CommandException(command + " is invalid");
        String option = command.substring(bracketStart + 1, bracketEnd);
        if(!option.toUpperCase().startsWith(HORIZONTAL) && !option.toUpperCase().startsWith(VERTICAL))
            throw new CommandException("HORIZONTAL or VERTICAL must be provided in bracket");
        this.horizontal = option.toUpperCase().startsWith(HORIZONTAL);
        this.lineNum = Integer.parseInt(option.split(OPTION_SPLITOR)[LINE_NUMBER_OPTION_INDEX]);
        return false;
    }

    @Override
    public ParsedItem decode(TabulaTable table, int row, int col) throws Exception {
        ListItem item = new ListItem(this.columns);
        List<Map<String, String>> instances = null;
        int index = 0;
        if(horizontal){
            List<Integer> indexes = new ArrayList<>();
            List<Cell> line = table.getLineFromTable(row);
            for(int i = 0; i < table.getColInRow(row); i++){
                Cell cell = line.get(i);
                if(cell.hasValue(columns.get(index))){
                    indexes.add(i);
                    index++;
                }
            }
            for(int i = 1; i < lineNum; i++){
                Map<String, String> instance = new HashMap<>();
                List<Cell> lineFromTable = table.getLineFromTable(row + i);
                for(int j = 0; j < indexes.size(); j++){
                    instance.put(columns.get(i), lineFromTable.get(indexes.get(j)).getContent());
                }
                instances.add(instance);
            }
        }else{
            for(int i = 1; i < lineNum; i++){
                Map<String, String> instance = new HashMap<>();
                for (int j = 0; j < columns.size(); i++){
                    instance.put(columns.get(j), table.getCell(row + i, j).getContent());
                }
                instances.add(instance);
            }
        }
        item.setInstances(instances);
        return item;
    }
}
