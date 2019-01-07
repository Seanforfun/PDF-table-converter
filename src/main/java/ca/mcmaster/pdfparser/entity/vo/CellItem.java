package ca.mcmaster.pdfparser.entity.vo;

import ca.mcmaster.pdfparser.exceptions.CommandException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 11:00
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
public class CellItem implements ParsedItem {
    @Getter @Setter
    private boolean hasKey;
    @Getter @Setter
    private String key;
    @Getter @Setter
    private String value;

    private static final String HAS_KEY = "has_key";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    public void setCellItemKV(String key, String value){
        if(hasKey) {
            this.key = key;
            this.value = value;
        }
    }

    public void setCellItemValue(String value){
        this.value = value;
    }

    public static void buildItem(CellItem item, String content, char splitor){
        if(item.hasKey){
            String[] tokens = content.split(String.valueOf(splitor));
            if(tokens.length != 2){
                throw new CommandException("Command is incorrect.");
            }
            item.setCellItemKV(tokens[0], tokens[1]);
        }else{
            item.setValue(content);
        }
    }

    @Override
    public JsonElement getJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(HAS_KEY, this.hasKey);
        jsonObject.addProperty(KEY, this.key);
        jsonObject.addProperty(VALUE, this.value);
        return jsonObject;
    }
}
