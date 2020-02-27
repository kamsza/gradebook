package pl.edu.agh.jdziennik.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Represents a teacher-defined grade type (like exam, homework, etc.).
 * While not referenced directly from Grade, GradeType can be used to
 * create it's instance. This class is mapped by hibernate.
 */
@Entity
public class GradeType {

    /**
     * Generated by hibernate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private int id;

    @Column(nullable = false, name = "Type_Name")
    private String typeName;

    @Column(nullable = false, name = "Weight")
    private int weight;

    @ManyToOne
    @JoinColumn(nullable = false, name = "Teacher")
    private Teacher teacher;

    /**
     * for hibernate
     */
    GradeType() {
    }

    /**
     * Creates new GradeType using the values passed. Should set
     * reference to and from teacher (needs fixing). The newly
     * created object does not have id set.
     */
    public GradeType(final Teacher teacher, final String typeName, final int weight) {
        this.typeName = typeName;
        this.weight = weight;
        this.teacher = teacher;

        teacher.addGradeType(this);
    }

    public int getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getWeight() {
        return weight;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    /**
     * Checks if grade types are equal using id, teacher, type name
     * and weight.
     */
    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof GradeType
                && ((GradeType) other).id == id
                && ((GradeType) other).teacher.equals(teacher)
                && ((GradeType) other).typeName.equals(typeName)
                && ((GradeType) other).weight == weight;

    }

    @Override
    public int hashCode() {
        return id ^ teacher.hashCode() ^ typeName.hashCode() ^ weight;
    }
}
