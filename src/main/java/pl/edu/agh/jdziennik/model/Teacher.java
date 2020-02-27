package pl.edu.agh.jdziennik.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a teacher. This class is mapped by hibernate.
 */
@Entity
public class Teacher extends Person {

    @OneToMany(mappedBy = "tutor")
    private List<TeachingClass> classList = new LinkedList<>();

    @OneToMany(mappedBy = "teacher")
    private List<ClassSubject> classSubjectList = new LinkedList<>();

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<GradeType> gradeTypeList = new LinkedList<>();

    /**
     * for hibernate
     */
    Teacher() {
        super();
    }

    /**
     * Creates new Teacher using values passed. The newly created
     * object does not have id set. Also creates a few default grade
     * types for use by this teacher.
     */
    public Teacher(final String name, final String surname) {
        super(name, surname);

        new GradeType(this, "Sprawdzian", 4);
        new GradeType(this, "Kartkówka", 2);
        new GradeType(this, "Odpowiedź ustna", 2);
        new GradeType(this, "Aktywność", 1);
        new GradeType(this, "Zadanie domowe", 1);
    }

    /**
     * Adds class to list of supervised classes (ones this teacher is
     * the tutor of). Does not set the reference to teacher in
     * teachingClass.
     */
    public void addClass(TeachingClass teachingClass) {
        classList.add(teachingClass);
    }

    /**
     * Removes class from list of supervised (as tutor) classes.
     */
    // TODO do we really need this? Aren't we going to just use
    // hibernate to delete teachingClass from db?
    public void removeClass(TeachingClass teachingClass) {
        classList.remove(teachingClass);
    }

    public List<TeachingClass> getClassList() {
        return this.classList;
    }

    public List<ClassSubject> getClassSubjectList() {
        return this.classSubjectList;
    }

    /**
     * Adds class to list of taught classes. Does not set the
     * reference to teacher in classSubject.
     */
    public void addClassSubject(ClassSubject classSubject) {
        classSubjectList.add(classSubject);
    }

    /**
     * Removes class from list of taught classes.
     */
    // unlike other removeSomething() methods this is probably needed,
    // because teacher of a class can be changed (and that's when
    // this could be used)
    public void removeClassSubject(ClassSubject classSubject) {
        classSubjectList.remove(classSubject);
    }

    /**
     * Adds grade type to list of teacher's grade types. Does not set
     * the reference to teacher in gradeType.
     */
    public void addGradeType(GradeType gradeType) {
        // This might not be needed
        //        for(GradeType type: gradeTypeList)
        //            if(type.equals(gradeType)) return;
        gradeTypeList.add(gradeType);
    }

    public List<GradeType> getGradeTypes() {
        return this.gradeTypeList;
    }

    /**
     * Checks if teachers are equal using id, name and surname.
     */
    @Override
    public boolean equals(final Object other) {
        return
                super.equals(other) &&
                        other instanceof Teacher;
    }
}
