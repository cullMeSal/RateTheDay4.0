import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class ObjectToJson {
    public static void main(String[] args) throws Exception {
        Day day1 = new Day("2025-04-03", 2);
        Day day2 = new Day("2025-05-03", 100);
        Day[] day = {day1, day2};
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(new File("result.json"), day);

        String jsonInString = mapper.writeValueAsString(day);
        System.out.println("Json: "+ jsonInString);

        String prettyJsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(day);
        System.out.println("Pretty Json: " + prettyJsonInString);
    }
}
