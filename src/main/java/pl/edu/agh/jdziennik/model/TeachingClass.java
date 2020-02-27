package pl.edu.agh.jdziennik.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a class of students. This class is mapped by hibernate.
 */
@Entity
@Table(name = "Class")
public class TeachingClass {

    /**
     * Class id - is meant to be something like "3c".
     */
    @Id
    @Column(name = "ID")
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "Tutor_ID")
    private Teacher tutor;

    @OneToMany(mappedBy = "teachingClass")
    private List<Student> studentList = new LinkedList<>();

    @OneToMany(mappedBy = "teachingClass", cascade = CascadeType.REMOVE)
    private List<ClassSubject> classSubjectList = new LinkedList<>();

    /**
     * for hibernate
     */
    TeachingClass() {
    }

    /**
     * Creates new TeachingClass using values passed. Sets reference
     * to and from tutor.
     */
    public TeachingClass(final String id, final Teacher tutor) {
        this.id = id;

        this.tutor = tutor;
        tutor.addClass(this);
    }

    public String getId() {
        return id;
    }

    public Teacher getTutor() {
        return tutor;
    }

    public List<Student> getStudentList() {
        return this.studentList;
    }

    public List<ClassSubject> getClassSubjectList() {
        return this.classSubjectList;
    }


    /**
     * Adds student to list of students in this class.
     * Does not set the reference to class in student.
     */
    void addStudent(Student student) {
        studentList.add(student);
    }

    /**
     * Removes classSubject from list of subjects taught to class.
     */
    // unlike other removeSomething() methods this is probably needed,
    // because student can change class (and that's when this could
    // be used)
    void removeStudent(Student student) {
        studentList.remove(student);
    }

    /**
     * Adds classSubject to list of subjects taught to class.
     * Does not set the reference to class in classSubject.
     */
    void addClassSubject(ClassSubject classSubject) {
        classSubjectList.add(classSubject);
    }

    /**
     * Removes classSubject from list of subjects taught to class.
     */
    // TODO do we really need this? Aren't we going to just use
    // hibernate to delete classSubject from db?
    void removeClassSubject(ClassSubject classSubject) {
        classSubjectList.remove(classSubject);
    }


    /**
     * Checks if classes are equal using id.
     */
    @Override
    public boolean equals(Object other) {
        return
                other != null &&
                        other instanceof TeachingClass &&
                        ((TeachingClass) other).id == id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
