package kurs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class Countries {
    private final ArrayList<Country> list;

    public Countries(String text) {
        list = new ArrayList<>();

        if (text.isEmpty()) {
            list.add(new Country("Russia", 17L));
            list.add(new Country("USA", 20L));
            list.add(new Country("Japan", 19L));
            list.add(new Country("Thailand", 19L));
            return;
        }

        try {
            JSONArray a = (JSONArray) new JSONParser().parse(text);
            for (Object value : a) {
                JSONObject o = (JSONObject) value;
                list.add(new Country((String) o.get("name"), (Long) o.get("ageAdult")));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void add(String name, Long ageAdult) {
        list.add(new Country(name, ageAdult));
    }

    public Country getByName(String name) {
        for (Country c : list)
            if (c.getName().equals(name))
                return c;
        return null;
    }

    public ArrayList<Country> getList() {
        return list;
    }
}
