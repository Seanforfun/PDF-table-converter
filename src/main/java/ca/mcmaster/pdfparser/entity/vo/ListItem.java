package ca.mcmaster.pdfparser.entity.vo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 11:52
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
public class ListItem implements ParsedItem {
    private List<String> columns;
    @Getter @Setter
    private List<Map<String, String>> instances;

    public ListItem(List<String> columns){
        this.columns = new ArrayList<>(columns);
        this.instances = new ArrayList<>();
    }

    @Override
    public JsonElement getJsonObject() {
        JsonArray elements = new JsonArray();
        for (Map<String, String> instance : instances){
            Iterator<String> it = instance.keySet().iterator();
            JsonObject jsonObject = new JsonObject();
            while(it.hasNext()){
                String key = it.next();
                jsonObject.addProperty(key, instance.get(key));
            }
            elements.add(jsonObject);
        }
        return elements;
    }
}
