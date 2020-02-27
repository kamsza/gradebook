package pl.edu.agh.jdziennik.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Date;

/**
 * Represents an assignment given to students that is due on some day.
 * This class is mapped by hibernate.
 */
@Entity
public class Assignment {

    /**
     * Generated by hibernate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "Assignment_Type")
    private AssignmentType assignmentType;
    @Basic
    @Column(nullable = false, name = "Date")
    private Date date;
    @Column(name = "Comment")
    private String comment;
    @ManyToOne
    @JoinColumn(nullable = false, name = "WeekTerm_ID")
    private WeekTerm weekTerm;

    /**
     * for hibernate
     */
    Assignment() {
    }

    /**
     * Creates new Assignment using values passed. The newly created
     * object does not have id set.
     */
    public Assignment(final AssignmentType assignmentType,
                      final Date date, final String comment,
                      final WeekTerm weekTerm) {
        this.assignmentType = assignmentType;
        this.date = date;
        this.comment = comment;
        this.weekTerm = weekTerm;
    }

    public int getId() {
        return id;
    }

    /**
     * Checks if assignments are equal using assignment type, date,
     * comment and weekTerm.
     */
    @Override
    public boolean equals(final Object other) {
        return other != null &&
                other instanceof Assignment &&
                ((Assignment) other).assignmentType
                        == assignmentType &&
                ((Assignment) other).date.equals(date) &&
                ((Assignment) other).comment.equals(comment) &&
                ((Assignment) other).weekTerm.equals(weekTerm);
    }

    @Override
    public int hashCode() {
        return assignmentType.hashCode() ^ date.hashCode() ^
                comment.hashCode() ^ weekTerm.hashCode();
    }

    // TODO add getters 

    /**
     * Represents a type of assignment, e.g. homework, test.
     */
    public enum AssignmentType {
        SPRAWDZIAN,
        KARTKOWKA,
        ZADANIE_DOMOWE
    }
}