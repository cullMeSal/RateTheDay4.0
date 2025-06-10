import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DayReader {
    public static List<Day> readDayFile(String path)throws IOException{
        ObjectMapper mapper = new ObjectMapper();

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
    public static void main(String[] args) throws Exception {
        List<Day> days = readDayFile("result.json");
        System.out.println(days.get(0));
    }
}
