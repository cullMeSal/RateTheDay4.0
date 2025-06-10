import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.util.Arrays;

public class DayManager {
    static ObjectMapper mapper = new ObjectMapper();
    public static List<Day> readDayFile(String path)throws IOException{
        
        List<Day> days;
        try {
            days = mapper.readValue(new File(path), new TypeReference<List<Day>>(){});
            return days;
        } catch (IOException ex) {
            System.getLogger(DayReader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            DayRecorder.saveDefaultData();
            System.out.println("Path not found, creating and reading from default data file.");
            days = mapper.readValue(new File("dayData.json"), new TypeReference<List<Day>>(){});
            return days;
        }
    }
    
    public static void removeData(String date) throws Exception {
        List<Day> dayEntries = DayReader.readDayFile(dataPath);

        Day found = dayEntries.stream()
                .filter(d -> d.getDateISO().equals(date))
                .findFirst()
                .orElse(null);

//        for (Day d : dayEntries) {
//            if (d.getDateISO().equals(date)) {
//                found = d;
//                break;
//            }
//        }
        if (found != null) {
            dayEntries.remove(found);
            System.out.println("Entry removed.");
        } else {
            System.out.println("Entry not found.");
        }
        DayRecorder.saveData(dayEntries);
//        System.out.println(dayEntries);

    }
    public static void saveData(String dayISO, int score) throws IOException {
        mapper.writeValue(new File(dataPath), new Day(dayISO, score));
    }
    public static void saveData(String dayISO, int score, String note) throws IOException {
        Day day = new Day(dayISO, score, note);
        mapper.writeValue(new File(dataPath), day);
        String prettyJsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(day);
        System.out.println("Pretty Json: " + prettyJsonInString);
    }
    public static void saveData(Day day) throws Exception {
        List<Day> dayEntries = DayReader.readDayFile(dataPath);
        String date = day.getDateISO();

        Day found = dayEntries.stream()
                .filter(d -> d.getDateISO().equals(date))
                .findFirst()
                .orElse(null);

//        for (Day d : dayEntries) {
//            if (d.getDateISO().equals(date)) {
//                found = d;
//                break;
//            }
//        }

        if (found != null) {
            found.setRating(day.getRating());
            found.setNote(day.getNote());
            System.out.println("Entry updated.");
        } else {
            dayEntries.add(day);
            System.out.println("New entry added.");
        }
        saveData(dayEntries);
        System.out.println(dayEntries);

//        mapper.writeValue(new File(dataPath),day);
//        String prettyJsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(day);
//        System.out.println("Pretty Json: " + prettyJsonInString);
    }

    public static void saveData(List<Day> dayList) throws IOException {
        mapper.writeValue(new File(dataPath),dayList);
        String prettyJsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dayList);
        System.out.println("Pretty Json: " + prettyJsonInString);
    }
    public static void saveDefaultData() {
        LocalDate localDate = LocalDate.now();
        Day day1 = new Day("2025-05-20",1, "default data");
        Day day2 = new Day(localDate.toString(), 1, "file not found, default data saved.");
        Day[] dayList = {day1, day2};

        try {
            saveData(Arrays.asList(dayList));
        } catch (IOException ex) {
            System.getLogger(DayRecorder.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    static String dataPath = "dayData.json";

    public static void main(String[] args) throws Exception {
        List<Day> days = readDayFile("result.json");
        System.out.println(days.get(0));
    }
    
    
}
