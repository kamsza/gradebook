package pl.edu.agh.jdziennik.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a classroom in school building. This class is mapped by
 * hibernate.
 */
@Entity
public class Classroom {

    /**
     * Classroom id - is meant to be something like "205a".
     */
    @Id
    @Column(name = "ID")
    private String id;

    @OneToMany(mappedBy = "classroom")
    private List<WeekTerm> weekTermList = new LinkedList<>();

    /**
     * for hibernate
     */
    Classroom() {
    }

    /**
     * Creates new classroom using value passed.
     */
    public Classroom(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Adds weekTerm to list of terms when lessons are supposed to
     * happen in this classroom. Does not set the reference to
     * classroom in weekTerm.
     */
    void addWeekTerm(WeekTerm weekTerm) {
        weekTermList.add(weekTerm);
    }

    /**
     * Removes weekTerm from list of terms when lessons are supposed to
     * happen in this classroom.
     */
    // TODO do we really need this? Aren't we going to just use
    // hibernate to delete weekTerm from db?
    void removeWeekTerm(WeekTerm weekTerm) {
        weekTermList.remove(weekTerm);
    }

    public List<WeekTerm> getWeekTermList() {
        return this.weekTermList;
    }

    /**
     * Checks if classrooms are equal using id.
     */
    @Override
    public boolean equals(Object other) {
        return
                other != null &&
                        other instanceof Classroom &&
                        ((Classroom) other).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
