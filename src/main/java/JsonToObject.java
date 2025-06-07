import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class JsonToObject {
    public static void main(String[] args) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
//        Day day = mapper.readValue(new File("result.json"), Day.class);
        List<Day> day = mapper.readValue(new File("result.json"),new TypeReference<List<Day>>(){});
        System.out.println(day.get(1));
    }
}
