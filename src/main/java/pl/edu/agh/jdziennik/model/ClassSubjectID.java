package pl.edu.agh.jdziennik.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Represents embedded, 2-field id of ClassSubject.
 * This class is mapped by hibernate.
 */
@Embeddable
public class ClassSubjectID implements Serializable {

    @Column(nullable = false, name = "Class_ID")
    private String teachingClass;

    @Column(nullable = false, name = "Subject_Name")
    private String subject;

    /**
     * for hibernate
     */
    ClassSubjectID() {
    }

    /**
     * Creates new ClassSubjectID using values passed.
     */
    public ClassSubjectID(TeachingClass teachingClass,
                          Subject subject) {
        this.teachingClass = teachingClass.getId();
        this.subject = subject.getName();
    }

    public String getTeachingClass() {
        return teachingClass;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * Checks if classSubjectIDs are equal using teachingClass
     * and subject.
     */
    @Override
    public boolean equals(Object other) {
        return
                other != null &&
                        other instanceof ClassSubjectID &&
                        ((ClassSubjectID) other).teachingClass
                                .equals(teachingClass) &&
                        ((ClassSubjectID) other).subject.equals(subject);
    }

    @Override
    public int hashCode() {
        return teachingClass.hashCode() ^ subject.hashCode();
    }
}
