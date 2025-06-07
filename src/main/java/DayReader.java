import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class DayReader {
    public static List<Day> readDayFile(String path) throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        List<Day> days = mapper.readValue(new File(path), new TypeReference<List<Day>>(){});
        return days;
    }
    public static void main(String[] args) throws Exception {
        List<Day> days = readDayFile("result.json");
        System.out.println(days.get(0));
    }
}
