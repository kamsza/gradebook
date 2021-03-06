package pl.edu.agh.jdziennik.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a lesson that took place. This class is mapped by
 * hibernate.
 */
@Entity
public class Lesson {

    /**
     * Generated by hibernate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "WeekTerm_ID")
    private WeekTerm weekTerm;

    @Column(nullable = false, name = "Thema")
    private String topic;

    @Basic
    @Column(nullable = false, name = "Date")
    private Date date;

    @OneToMany(mappedBy = "lesson")
    private List<Attendance> attendanceList = new LinkedList<>();

    /**
     * for hibernate
     */
    Lesson() {
    }

    /**
     * Creates new lesson using the values passed. Sets reference
     * to and from weekTerm. Current date is used. The newly created
     * object does not have id set.
     */
    public Lesson(final WeekTerm weekTerm, final String topic) {
        this(weekTerm, topic,
                new Date(System.currentTimeMillis()));
    }

    /**
     * Creates new lesson using the values passed. Sets reference
     * to and from weekTerm. The newly created
     * object does not have id set.
     */
    public Lesson(final WeekTerm weekTerm, final String topic,
                  final Date date) {
        this.topic = topic;
        this.date = date;

        this.weekTerm = weekTerm;
        weekTerm.addLesson(this);
    }

    public int getId() {
        return id;
    }

    public WeekTerm getWeekTerm() {
        return weekTerm;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getDate() {
        return date;
    }

    public List<Attendance> getAttendanceList() {
        return this.attendanceList;
    }

    /**
     * Adds attendance to list of attendances on this lesson. Does not
     * set the reference to lesson in attendance.
     * This method is intended to only be called by method in
     * Attendance class.
     */
    void addAttendance(Attendance attendance) {
        attendanceList.add(attendance);
    }

    /**
     * Checks if lessons are equal using date and weekTerm.
     */
    @Override
    public boolean equals(Object other) {
        return
                other != null &&
                        other instanceof Lesson &&
                        ((Lesson) other).date.equals(date) &&
                        ((Lesson) other).weekTerm.equals(weekTerm);
    }

    @Override
    public int hashCode() {
        return id ^ date.hashCode() ^ weekTerm.hashCode();
    }
}

