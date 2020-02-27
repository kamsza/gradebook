package pl.edu.agh.jdziennik.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a subject. This class is mapped by hibernate.
 */
@Entity
public class Subject {

    @Id
    @Column(nullable = false, name = "Name")
    private String name;

    @OneToMany(mappedBy = "subject")
    private List<ClassSubject> classSubjectList = new LinkedList<>();

    /**
     * for hibernate
     */
    Subject() {
    }

    /**
     * Creates new subject using value passed.
     */
    public Subject(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<ClassSubject> getClassSubjectList() {
        return this.classSubjectList;
    }

    /**
     * Adds classSubject to list of classes taught this subject.
     * Does not set the reference to subject in classSubject.
     */
    void addClassSubject(ClassSubject classSubject) {
        this.classSubjectList.add(classSubject);
    }

    /**
     * Removes classSubject from list of classes taught this subject.
     */
    // TODO do we really need this? Aren't we going to just use
    // hibernate to delete classSubject from db?
    void removeClassSubject(ClassSubject classSubject) {
        this.classSubjectList.remove(classSubject);
    }

    /**
     * Checks if subjects are equal using name.
     */
    @Override
    public boolean equals(Object other) {
        return
                other != null &&
                        other instanceof Subject &&
                        ((Subject) other).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

