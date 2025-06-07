/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

import java.time.*;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
/**
 *
 * @author ADMIN
 */
public class RateTheDay40 {

    public static void main(String[] args) {
        String[] months = Arrays.stream(Month.values())
                .map(m -> m.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                .toArray(String[]::new);
        for (String s : months){
            System.out.println(s);
        }
        
        
    }
}
