import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class DayRemover {
    static String dataPath = "dayData.json";

    public static void removeData(String date) throws Exception {
        List<Day> dayEntries = DayReader.readDayFile(dataPath);

        Day found = null;

        for (Day d : dayEntries) {
            if (d.getDateISO().equals(date)) {
                found = d;
                break;
            }
        }

        if (found != null) {
            dayEntries.remove(found);
            System.out.println("Entry removed.");
        } else {
            System.out.println("Entry not found.");
        }
        DayRecorder.saveData(dayEntries);
        System.out.println(dayEntries);

    }

    public static void main(String[] args) throws Exception {
        removeData("2025-05-29");
        System.out.println("Finished running.");
    }
}
