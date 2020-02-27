package pl.edu.agh.jdziennik.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a time window. Has start hour and end hour.
 * Each WeekTerm has some time window.
 * This class is mapped by hibernate.
 */
@Entity
public class TimeWindow {

    @Id
    @Column(name = "ID")
    private int id;

    @Basic
    @Column(nullable = false, name = "StartTime")
    private Time startTime;

    @Basic
    @Column(nullable = false, name = "EndTime")
    private Time endTime;

    @OneToMany(mappedBy = "timeWindow")
    private List<WeekTerm> weekTermList = new LinkedList<>();

    /**
     * for hibernate
     */
    TimeWindow() {
    }

    /**
     * Creates new TimeWindow using the times passed.
     * The id correspond to number of lesson in a day
     */
    public TimeWindow(final int id, final Time startTime, final Time endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates new TimeWindow using the times passed.
     * The id correspond to number of lesson in a day
     * Default lesson duration is 45 min
     */
    public TimeWindow(final int id, final Time startTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = Time.valueOf(this.startTime.toLocalTime().plusMinutes(45));
    }

    public int getId() {
        return id;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public List<WeekTerm> getWeekTermList() {
        return weekTermList;
    }

    /**
     * Adds week term to list of week terms on this time window.
     * Does not set the reference to timeWindow in weekTerm.
     */
    void addWeekTerm(WeekTerm weekTerm) {
        weekTermList.add(weekTerm);
    }

    /**
     * Checks if time windows are equal
     * using id and start and end times.
     */
    @Override
    public boolean equals(Object other) {
        return
                other != null &&
                        other instanceof TimeWindow &&
                        ((TimeWindow) other).id == id &&
                        ((TimeWindow) other).endTime.equals(endTime) &&
                        ((TimeWindow) other).startTime.equals(startTime);
    }

    @Override
    public int hashCode() {
        return startTime.hashCode() ^ endTime.hashCode() ^ id;
    }
}
