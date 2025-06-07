import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DayRecorder {
    static String dataPath = "dayData.json";
    public DayRecorder() throws Exception {
    }

    static ObjectMapper mapper = new ObjectMapper();

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

        Day found = null;

        for (Day d : dayEntries) {
            if (d.getDateISO().equals(date)) {
                found = d;
                break;
            }
        }

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

    public static void main(String[] args) throws Exception {
        LocalDate localDate = LocalDate.now();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter score: ");
        int score = scanner.nextInt();
        System.out.println(localDate.toString());

        Day day1 = new Day("2025-01-03",2);
        Day day2 = new Day(localDate.toString(),score);
        Day[] dayList = {day1, day2};

        saveData(Arrays.asList(dayList));
//        saveData(day2);
        System.out.println("end of file");

    }
}
