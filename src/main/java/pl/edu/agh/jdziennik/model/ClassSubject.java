package pl.edu.agh.jdziennik.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a connection between class of students and subject
 * taught to it. This class is mapped by hibernate.
 */
@Entity
public class ClassSubject {

    @EmbeddedId
    private ClassSubjectID id;

    @ManyToOne
    @JoinColumn(updatable = false, insertable = false,
            name = "Class_ID")
    private TeachingClass teachingClass;

    @ManyToOne
    @JoinColumn(updatable = false, insertable = false,
            name = "Subject_Name")
    private Subject subject;

    @ManyToOne
    @JoinColumn(nullable = false, name = "Teacher")
    private Teacher teacher;

    @OneToMany(mappedBy = "classSubject")
    private List<WeekTerm> weekTermList = new LinkedList<>();

    @OneToMany(mappedBy = "classSubject")
    private List<Grade> gradeList = new LinkedList<>();

    /**
     * for hibernate
     */
    ClassSubject() {
    }

    /**
     * Creates new ClassSubject using values passed. Sets reference
     * to and from teachingClass, subject and teacher.
     */
    // TODO Perhaps make ClassSubjectID a nested class
    // in ClassSubject?
    public ClassSubject(final TeachingClass teachingClass,
                        final Subject subject, final Teacher teacher) {
        this.teachingClass = teachingClass;
        this.subject = subject;
        this.teacher = teacher;
        this.id = new ClassSubjectID(teachingClass, subject);

        teachingClass.addClassSubject(this);
        subject.addClassSubject(this);
        teacher.addClassSubject(this);
    }

    public ClassSubjectID getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public TeachingClass getTeachingClass() {
        return teachingClass;
    }

    public List<WeekTerm> getWeekTermList() {
        return weekTermList;
    }

    public List<Grade> getGradeList() {
        return gradeList;
    }

    /**
     * Adds weekTerm to list of terms when subject is taught to
     * students of this class. Does not set the reference
     * to classSubject in weekTerm.
     */
    void addWeekTerm(WeekTerm weekTerm) {
        weekTermList.add(weekTerm);
    }

    /**
     * Removes weekTerm from list of terms when subject is taught to
     * students of this class.
     */
    // TODO do we really need this? Aren't we going to just use
    // hibernate to delete weekTerm from db?
    void removeWeekTerm(WeekTerm weekTerm) {
        weekTermList.remove(weekTerm);
    }

    /**
     * Changes teacher of the class to another one. Removes the
     * reference to classSubject in old teacher. Adds a reference in
     * new teacher.
     */
    void changeTeacher(Teacher newTeacher) {
        teacher.removeClassSubject(this);
        teacher = newTeacher;
        newTeacher.addClassSubject(this);
    }


    void addGrade(Grade grade) {
        gradeList.add(grade);
    }

    /**
     * Checks if classSubjects are equal using teachingClass
     * and subject.
     */
    @Override
    public boolean equals(Object other) {
        return
                other != null &&
                        other instanceof ClassSubject &&
                        ((ClassSubject) other).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
