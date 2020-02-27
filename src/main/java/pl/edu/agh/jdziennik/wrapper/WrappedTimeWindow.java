package pl.edu.agh.jdziennik.wrapper;


import pl.edu.agh.jdziennik.model.TimeWindow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WrappedTimeWindow extends AbstractWrapper<TimeWindow> {

    public WrappedTimeWindow(TimeWindow timeWindow) {
        super(timeWindow);
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("HH:mm");

        if (getObject() == null) return null;
        else
            return getObject().getId() + ":   " + format.format(getObject().getStartTime().getTime()) + " - " + format.format(getObject().getEndTime().getTime());
    }
}
