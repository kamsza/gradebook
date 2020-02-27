package pl.edu.agh.jdziennik.wrapper;

import javafx.util.StringConverter;
import pl.edu.agh.jdziennik.model.WeekTerm;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class WrappedDay extends AbstractWrapper<WeekTerm.Day> {

    public WrappedDay(WeekTerm.Day day) {
        super(day);
    }

    public static List<WrappedDay> values() {
        List<WrappedDay> values = new LinkedList<>();

        for(WeekTerm.Day value : WeekTerm.Day.values())
            values.add(new WrappedDay(value));

        return values;
    }

    public static WrappedDay today() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        return new WrappedDay(WeekTerm.Day.values()[(day + 5) % 7]);
    }
    
    @Override
    public String toString() {
        return getObject().stringValue;
    }

    public static WrappedDay fromString(String string) {
        for(WeekTerm.Day value : WeekTerm.Day.values()) {
            if (value.stringValue.equals(string))
                return new WrappedDay(value);
        }

        return null;
    }
}
