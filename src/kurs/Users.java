package kurs;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Users {
    public static final int COND_COUNTRY = 1;
    public static final int COND_AGE = 2;
    public static final int COND_AGE_OLDER = 3;
    public static final int COND_AGE_UNDER = 4;
    public static final int COND_ADULT = 5;
    public static final int COND_TEEN = 6;
    public static final int COND_WRONG = 7;
    public static final int COND_CORRECT_WRONG = 8;

    private static final String USERS_FILE_NAME = "users.json";
    private static final String COUNTRIES_FILE_NAME = "countries.json";
    private final Countries countries;
    private final ArrayList<User> list;

    Users() {
        //создаем базу стран, просто вбивая 4 страны в массив
        countries = new Countries(readFile(COUNTRIES_FILE_NAME));
        //создаем массив пользователей, пустой для начала
        list = new ArrayList<>();
        //считываем ЖСОН и по нему заполняем массив
        String text = readFile(USERS_FILE_NAME);
        try {
            JSONArray a = (JSONArray) new JSONParser().parse(text);
            for (Object value : a) {
                JSONObject o = (JSONObject) value;
                String countryName = (String) o.get("county");
                Country country = countries.getByName(countryName);
                Long id = (Long) o.get("id");
                String name = (String) o.get("name");
                String fName = (String) o.get("fname");
                Long age = (Long) o.get("age");
                Boolean teen = (Boolean) o.get("is_teen");
                list.add(new User(id, name, fName, country, age, teen));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean countryExists(String name) {
        Country c = countryByName(name);
        return c != null;
    }

    public Country countryByName(String name) {
        return countries.getByName(name);
    }

    public User byId(Long id) {
        for (User u: list)
            if (u.getId() == id) return u;
        return null;
    }

    public User[] usersByCondition(String parString, Long parLong, int condition) {
        ArrayList<User> a = new ArrayList<>();
        Country c = null;
        if (condition == COND_COUNTRY)
            c = countries.getByName(parString);
        for (User u : list) {
            boolean need = false;
            if (condition == COND_COUNTRY) need = u.getCountry() == c;
            if (condition == COND_AGE) need = u.getAge() == parLong;
            if (condition == COND_AGE_OLDER) need = u.getAge() > parLong;
            if (condition == COND_AGE_UNDER) need = u.getAge() <= parLong;
            if (condition == COND_ADULT) need = !u.getTeen();
            if (condition == COND_TEEN) need = u.getTeen();
            if (condition == COND_WRONG) need = u.getTeen() != u.getCountry().getTeen(u.getAge());
            if (condition == COND_CORRECT_WRONG) {
                Boolean correctTeen = u.getCountry().getTeen(u.getAge());
                need = u.getTeen() != correctTeen;
                if (need) u.setTeen(correctTeen);
            }
            if (!need) continue;
            a.add(u);
        }
        return a.toArray(new User[a.size()]);
    }

    public void correctWrong() {
        User[] temp = usersByCondition(null, null, COND_CORRECT_WRONG);
        if (temp.length == 0) return;
        saveUsersToFile();
    }

    public void addCountry(String name, Long ageAdult) {
        countries.add(name, ageAdult);
        saveCountriesToFile();
    }

    private String readFile(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fr = new FileReader(fileName);
            Scanner sc = new Scanner(fr);
            while (sc.hasNextLine()) {
                stringBuilder.append(sc.nextLine());
                stringBuilder.append("\n");
            }
            fr.close();
        } catch (IOException e) {
            //не нужно ничего выводить, и так все ясно
            //e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void saveUsersToFile() {
        JSONArray a = new JSONArray();
        for (User u : list) {
            JSONObject o = new JSONObject();
            o.put("id", u.getId());
            o.put("name", u.getName());
            o.put("fname", u.getfName());
            o.put("county", u.getCountry().getName());
            o.put("age", u.getAge());
            o.put("is_teen", u.getTeen());
            a.add(o);
        }
        saveToFile(USERS_FILE_NAME, a);
    }

    private void saveCountriesToFile() {
        JSONArray a = new JSONArray();
        for (Country c : countries.getList()) {
            JSONObject o = new JSONObject();
            o.put("name", c.getName());
            o.put("ageAdult", c.getAgeAdult());
            a.add(o);
        }
        saveToFile(COUNTRIES_FILE_NAME, a);
    }

    private void saveToFile(String fileName, JSONArray a) {
        try {
            FileWriter fw = new FileWriter(fileName, false);
            fw.write(a.toJSONString());
            fw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
