package pl.edu.agh.jdziennik.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a student. This class is mapped by hibernate.
 */
@Entity
public class Student extends Person {

    @ManyToOne
    @JoinColumn(nullable = false, name = "Class_ID")
    private TeachingClass teachingClass;

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendanceList = new LinkedList<>();

    @OneToMany(mappedBy = "student")
    private List<Grade> gradeList = new LinkedList<>();

    @ManyToOne
    @JoinColumn(nullable = false, name = "Parent_ID")
    private Parent parent;

    private String PESEL;

    /**
     * for hibernate
     */
    Student() {
        super();
    }

    /**
     * Creates new Student using values passed. The newly created
     * object does not have id set. Sets reference to and from
     * teachingClass.
     */
    public Student(final TeachingClass teachingClass,
                   final String name, final String surname,
                   final String PESEL, final Parent parent) {
        super(name, surname);
        this.teachingClass = teachingClass;
        this.PESEL = PESEL;
        this.parent = parent;
        parent.addChild(this);
        teachingClass.addStudent(this);
    }

    /**
     * Adds attendance to list of attendances of this student.
     * Does not set the reference to student in attendance.
     * This method is intended to only be called by method in
     * Attendance class.
     */
    void addAttendance(Attendance attendance) {
        attendanceList.add(attendance);
    }

    public TeachingClass getTeachingClass() {
        return teachingClass;
    }

    public Parent getParent() {
        return parent;
    }

    public String getPESEL() {
        return PESEL;
    }

    public List<Attendance> getAttendanceList() {
        return this.attendanceList;
    }

    /**
     * Adds grade to list of student's grades. Does not set the
     * reference to student in grade.
     */
    public void addGrade(Grade grade) {
        gradeList.add(grade);
    }

    /**
     * Removes grade from list of grades given to student.
     */
    // TODO do we really need this? Aren't we going to just use
    // hibernate to delete grade from db?
    public void removeGrade(Grade grade) {
        gradeList.remove(grade);
    }

    public List<Grade> getGradeList() {
        return this.gradeList;
    }

    /**
     * Moves student to a new class. Removes the reference to student
     * in old class. Adds a reference in new class.
     */
    public void changeClass(final TeachingClass newClass) {
        teachingClass.removeStudent(this);

        teachingClass = newClass;
        newClass.addStudent(this);
    }

    /**
     * Checks if students are equal using id, name and surname.
     */
    @Override
    public boolean equals(final Object other) {
        return
                super.equals(other) &&
                        other instanceof Student &&
                        ((Student) other).PESEL.equals(PESEL);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ PESEL.hashCode();
    }
}
