package pl.edu.agh.jdziennik.wrapper.wrappingaccess;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import pl.edu.agh.jdziennik.model.*;
import pl.edu.agh.jdziennik.model.dbaccess.DBAccessor;
import pl.edu.agh.jdziennik.wrapper.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.List;

/**
 * This class serves as an abstraction layer between DBAccessor and controllers.
 * It provides methods that perform certain queries accepting and returning wrapped objects.
 * Instances of this class use the underlying superclass, DBAccessor, to perform queries.
 */
public class WrappingAccessor extends DBAccessor {

    /**
     * Creates a new WrappingAccessor using the Session passed.
     */
    public WrappingAccessor(Session session) {
        super(session);
    }

    /**
     * Converts list of objects to a list of their wrapped versions.
     * Uses the 1-argument constructor in wrapClass to create wrapped
     * versions of objects in normalList. Returns a new observable
     * list of the resulting wrapped objects.
     *
     * @param wrapClass  Is a class extending pl.edu.agh.jdziennik.wrapper.AbstractWrapper<T>.
     * @param normalList Is an observable list of T, where T is a class mapped by hibernate.
     */
    private static <T, W> ObservableList<W> observableListWrap(Class<W> wrapClass,
                                                               List<T> normalList) {
        ObservableList<W> observableList =
                FXCollections.observableArrayList();

        try {
            Constructor<W> constructor =
                    (Constructor<W>) wrapClass.getConstructors()[0];

            for (T item : normalList) {
                observableList.add(constructor.newInstance(item));
            }
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        return observableList;
    }

    /**
     * Queries all teaching classes from the database.
     * Uses the underlying hibernate session to get all classes
     * stored in the database, wraps them as WrappedClasses and
     * returns them as an observable list.
     */
    public ObservableList<WrappedClass> wqueryAllClasses() {
        return observableListWrap
                (WrappedClass.class, queryAllClasses());
    }

    public ObservableList<WrappedClassroom> wqueryAllClassrooms() {
        return observableListWrap(WrappedClassroom.class, queryAllClassrooms());
    }

    /**
     * Queries all teachers from the database.
     * Uses the underlying hibernate session to get all teachers
     * stored in the database, wraps them as WrappedTeachers and
     * returns them as an observable list.
     */
    public ObservableList<WrappedTeacher> wqueryAllTeachers() {
        return observableListWrap
                (WrappedTeacher.class, queryAllTeachers());
    }

    /**
     * Queries all parents from the database.
     * Uses the underlying hibernate session to get all parents
     * stored in the database, wraps them as WrappedParents and
     * returns them as an observable list.
     */
    public ObservableList<WrappedParent> wqueryAllParents() {
        return observableListWrap
                (WrappedParent.class, queryAllParents());
    }

    /**
     * Queries all students from the database.
     * Uses the underlying hibernate session to get all students
     * stored in the database, wraps them as WrappedStudents and
     * returns them as an observable list.
     */
    public ObservableList<WrappedStudent> wqueryAllStudents() {
        return observableListWrap
                (WrappedStudent.class, queryAllStudents());
    }

    /**
     * Queries all subjects from the database.
     * Uses the underlying hibernate session to get all subjects
     * stored in the database, wraps them as WrappedSubjects and
     * returns them as an observable list.
     */
    public ObservableList<WrappedSubject> wqueryAllSubjects() {
        return observableListWrap
                (WrappedSubject.class, queryAllSubjects());
    }

    /**
     * Queries all time windows from the database.
     * Uses the underlying hibernate session to get all time windows
     * stored in the database, wraps them as WrappedTimeWindows and
     * returns them as an observable list.
     */
    public ObservableList<WrappedTimeWindow> wqueryAllTimeWindows() {
        return observableListWrap
                (WrappedTimeWindow.class, queryAllTimeWindows());
    }

    public ObservableList<WrappedGradeType> wqueryGradeTypes(WrappedTeacher teacher) {
        return observableListWrap
                (WrappedGradeType.class, queryGradeTypes(teacher.getObject()));
    }


    /**
     * Queries classes tutored by given teacher from the database.
     * Uses the underlying hibernate session to get all classes,
     * the tutor of which is teacher, wraps them as WrappedClasses and
     * returns them as an observable list.
     *
     * @param teacher Is a wrapped version of the tutor, who's classes are queried.
     */
    public ObservableList<WrappedClass> wqueryClassesTutoredByTeacher(final WrappedTeacher teacher) {
        return observableListWrap
                (WrappedClass.class,
                        queryClassesTutoredByTeacher(teacher.getObject()));
    }

    /**
     * Queries students of given class from the database.
     * Uses the underlying hibernate session to get all students,
     * who are members of teachingClass, wraps them as WrappedStudents
     * and returns them as an observable list.
     *
     * @param teachingClass Is a wrapped version of the class, students of which are queried.
     */
    public ObservableList<WrappedStudent> wqueryClassStudents(final WrappedClass teachingClass) {
        return observableListWrap
                (WrappedStudent.class,
                        queryClassStudents(teachingClass.getObject()));
    }

    /**
     * Queries students of given class from the database.
     * Uses the underlying hibernate session to get all students,
     * who are members of teachingClass, wraps them as
     * WrappedStudentWithPresence's and returns them as an observable list.
     *
     * @param teachingClass Is a wrapped version of the class, students of which are queried.
     */
    public ObservableList<WrappedStudentWithPresence> wqueryClassStudentsWithPresence(final WrappedClass teachingClass) {
        return observableListWrap
                (WrappedStudentWithPresence.class,
                        queryClassStudents(teachingClass.getObject()));
    }

    /**
     * Queries students of given class from the database.
     * Uses the underlying hibernate session to get all students,
     * who are members of teachingClass, wraps them as
     * WrappedStudentWithSubject's and returns them as an observable list.
     *
     * @param teachingClass Is a wrapped version of the class, students of which are queried.
     */
    public ObservableList<WrappedStudentWithSubject> wqueryClassStudentsWithSubject(final TeachingClass teachingClass) {
        return
                observableListWrap
                        (WrappedStudentWithSubject.class,
                                queryClassStudents(teachingClass));
    }

    /**
     * Queries students of given class from the database.
     * Uses the underlying hibernate session to get all students,
     * who are members of teachingClass, wraps them as
     * WrappedStudentWithGrade's and returns them as an observable list.
     *
     * @param teachingClass Is a wrapped version of the class, students of which are queried.
     */
    public ObservableList<WrappedStudentWithGrade> wqueryClassStudentsWithGrade(final TeachingClass teachingClass) {
        return
                observableListWrap
                        (WrappedStudentWithGrade.class,
                                queryClassStudents(teachingClass));
    }

    /**
     * Queries subjects of given teacher from the database.
     * Uses the underlying hibernate session to get the subjects
     * teacher teaches, wraps them as WrappedSubjects and returns
     * them as an observable list.
     *
     * @param teacher Is a wrapped version of the teacher, who's subjects are queried.
     */
    public ObservableList<WrappedSubject> wquerySubjectsOfTeacher(final WrappedTeacher teacher) {
        return observableListWrap
                (WrappedSubject.class,
                        querySubjectsOfTeacher(teacher.getObject()));
    }

    /**
     * Queries subjects of given teacher and teaching class from the database.
     * Uses the underlying hibernate session to get the subjects
     * teacher teaches to teachingClass, wraps them as WrappedSubjects
     * and returns them as an observable list.
     *
     * @param teacher       Is the teacher, who's subjects are queried.
     * @param teachingClass Is the class, subjects of which are queried.
     */
    public ObservableList<WrappedSubject> wquerySubjectsOfTeacherAndClass(final WrappedTeacher teacher, final WrappedClass teachingClass) {
        return observableListWrap
                (WrappedSubject.class,
                        querySubjectsOfTeacherAndClass(teacher.getObject(), teachingClass.getObject()));
    }

    /**
     * Queries classes taught by teacher from the database.
     * Uses the underlying hibernate session to get the teaching
     * classes teacher teaches, wraps them as WrappedClasses and
     * returns them as an observable list.
     *
     * @param teacher Is a wrapped version of the teacher, who's classes are queried.
     */
    public ObservableList<WrappedClass> wqueryClassesOfTeacher(final WrappedTeacher teacher) {
        return observableListWrap
                (WrappedClass.class,
                        queryClassesOfTeacher(teacher.getObject()));
    }

    /**
     * Queries classSubjects taught by teacher from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * teacher teaches, wraps them as WrappedClassSubjects and
     * returns them as an observable list.
     *
     * @param teacher Is a wrapped version of the teacher, who's classSubjects are queried.
     */
    public ObservableList<WrappedClassSubject> wqueryClassSubjectsOfTeacher(final WrappedTeacher teacher) {
        return observableListWrap
                (WrappedClassSubject.class,
                        queryClassSubjectsOfTeacher(teacher.getObject()));
    }

    /**
     * Queries classSubjects of given class, taught by teacher from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * that correspond to wclass, wraps them as WrappedClassSubjects
     * and returns them as an observable list.
     *
     * @param teacher Is the teacher, who's classSubjects are queried.
     * @param wclass  Is the teaching class, classSubjects of which are queried.
     */
    public ObservableList<WrappedClassSubject> wqueryClassSubjectsOfTeacherAndClass(final WrappedTeacher teacher, final WrappedClass wclass) {
        return observableListWrap
                (WrappedClassSubject.class,
                        queryClassSubjectsOfTeacherAndClass(teacher.getObject(), wclass.getObject()));
    }

    /**
     * Queries classSubjects taught by teacher, of a given subject from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * that correspond to wclass, wraps them as WrappedClassSubjects
     * and returns them as an observable list.
     *
     * @param teacher Is the teacher, who's classSubjects are queried.
     * @param subject Is the subject, classSubjects of which are queried.
     */
    public ObservableList<WrappedClassSubject> wqueryClassSubjectsOfTeacherAndSubject(final WrappedTeacher teacher, final WrappedSubject subject) {
        return observableListWrap
                (WrappedClassSubject.class,
                        queryClassSubjectsOfTeacherAndSubject(teacher.getObject(), subject.getObject()));
    }

    /**
     * Queries classSubjects of given class from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * that correspond to wclass, wraps them as WrappedClassSubjects
     * and returns them as an observable list.
     *
     * @param wclass Is a wrapped version of the teaching class, classSubjects of which are queried.
     */
    public ObservableList<WrappedClassSubject> wqueryClassSubjectsOfClass(final WrappedClass wclass) {
        return observableListWrap
                (WrappedClassSubject.class,
                        queryClassSubjectsOfClass(wclass.getObject()));
    }

    /**
     * Queries students of given class from the database.
     * Uses the underlying hibernate session to get the students
     * that correspond to teaching class of classSubjects, wraps them
     * as WrappedStudentWithSubject's, injects classSubjects into them
     * and returns them as an observable list.
     *
     * @param classSubject Corresponds to the teaching class, students of which are queried.
     */
    public ObservableList<WrappedStudentWithSubject> wqueryClassStudentsWithClassSubject(final WrappedClassSubject classSubject) {
        ObservableList<WrappedStudentWithSubject> studentList =
                observableListWrap
                        (WrappedStudentWithSubject.class,
                                queryClassStudents
                                        (classSubject.getObject().getTeachingClass()));

        studentList.forEach(s -> {
            s.setSubject(classSubject.getObject().getSubject());
            s.update(this);
        });

        return studentList;
    }

    /**
     * Queries attendances of given student in given time range from the database.
     * Uses the underlying hibernate session to get the attendances
     * with value attendanceValue, that correspond to student and the
     * time range from-to. Wraps them as WrappedAttendances
     * and returns them as an observable list.
     *
     * @param student         Is the student, who's attendances are queried.
     * @param attendanceValue Is the value of attendances queried.
     * @param from            Specifies the time period, attendances from which are being queried.
     * @param to              Specifies the time period, attendances from which are being queried.
     */
    public ObservableList<WrappedAttendance> wqueryPresenceWithDate(final Student student, final Attendance.AttendanceValue attendanceValue, final Date from, final Date to) {
        return observableListWrap(WrappedAttendance.class, queryPresenceWithDate(student, attendanceValue, from, to));
    }

    /**
     * Queries parent of given student from the database.
     * Uses the underlying hibernate session to get the parent
     * of student and wraps the parent as WrappedParent, which it returns.
     *
     * @param student Is a wrapped version of the student, who's parent is queried.
     */
    public WrappedParent wqueryParentOfStudent(final WrappedStudent student) {
        return new WrappedParent(queryParentOfStudent(student.getObject()));
    }

    /**
     * Queries teaching class of given student from the database.
     * Uses the underlying hibernate session to get the the class
     * student is the member of, wraps it as WrappedClass and returns it.
     *
     * @param student Is a wrapped version of the student, who's teaching class is queried.
     */
    public WrappedClass wqueryClassOfStudent(final WrappedStudent student) {
        return new WrappedClass(queryClassOfStudent(student.getObject()));
    }

    /**
     * Queries current time window from the database.
     * Uses the underlying hibernate session to get a single time window
     * with start time lower than current hour and end time higher, than
     * current hour.
     * Wraps the queried time window as WrappedTimeWindow and returns it.
     */
    public WrappedTimeWindow wqueryCurrentTimeWindow() {
        return new WrappedTimeWindow(queryCurrentTimeWindow());
    }

    /**
     * Queries specific lesson from the database.
     * Uses the underlying hibernate session to get the the lesson,
     * that took place on date in terms of weekTerm,
     * wraps it as WrappedLesson and returns it. Returns null if
     * there's no such lesson.
     *
     * @param date     Is the date when the queried lesson took place.
     * @param weekTerm Is a wrapped version of the weekTerm, that connects the queried lesson to specific time, class, teacher and subject.
     */
    public WrappedLesson wqueryLesson(final Date date, final WrappedWeekTerm weekTerm) {
        Lesson lesson = queryLesson(date, weekTerm.getObject());

        return lesson == null ? null : new WrappedLesson(lesson);
    }

    /**
     * Queries attendance of given student on given lesson from the database.
     * Uses the underlying hibernate session to get the attendance
     * of student on lesson and wraps it as WrappedAttendance,
     * which it returns. Returns null if there's no such attendance.
     *
     * @param student Is the student, who's attendance is queried.
     * @param lesson  Is the lesson, attendance on which is queried.
     */
    public WrappedAttendance wqueryAttendance(final Student student, final Lesson lesson) {
        Attendance attendance = queryAttendance(student, lesson);

        return attendance == null ? null : new WrappedAttendance(attendance);
    }


    public WrappedWeekTerm wqueryWeekTerm(final WrappedDay day, final WrappedTimeWindow timeWindow, final WrappedTeacher teacher, final WrappedClass wrappedClass) {
        return new WrappedWeekTerm(queryWeekTerm(day.getObject(), timeWindow.getObject(), teacher.getObject(), wrappedClass.getObject()));
    }

    public WrappedClassSubject wqueryClassSubject(WrappedStudentWithSubject student, WrappedTeacher teacher) {
        return new WrappedClassSubject(queryClassSubject(student.getObject().getTeachingClass(), student.getSubject(), teacher.getObject()));
    }

    public WrappedClassSubject wqueryClassSubject(WrappedStudentWithGrade student, WrappedTeacher teacher) {
        return new WrappedClassSubject(queryClassSubject(student.getTeachingClass().getObject(), student.getSubject().getObject(), teacher.getObject()));
    }

    public WrappedClassSubject wqueryClassSubject(final WrappedClass wClass, final WrappedSubject subject,
                                                  final WrappedTeacher teacher) {
        ClassSubject cs = queryClassSubject(wClass.getObject(), subject.getObject(), teacher.getObject());
        return cs == null ? null : new WrappedClassSubject(cs);
    }

    /**
     * Adds new student to the database.
     * Uses the underlying hibernate session to persist a new student
     * object created from teachingClass, name, surname, PESEL and
     * parent. Returns the new student object wrapped as WrappedStudent.
     *
     * @param teachingClass Is a wrapped version of the teaching class of the new student.
     * @param name          Is the name of the new student.
     * @param surname       Is the surname of the new student.
     * @param PESEL         Is the PESEL of the new student.
     * @param parent        Is a wrapped version of the parent of the new student.
     */
    public WrappedStudent createStudent(final WrappedClass teachingClass, final String name, final String surname,
                                        final String PESEL, final WrappedParent parent) {

        Student student = new Student(teachingClass.getObject(), name, surname, PESEL, parent.getObject());

        persistObject(student);

        return new WrappedStudent(student);
    }

    /**
     * Adds new teaching class to the database.
     * Uses the underlying hibernate session to persist a new class
     * object created from id and tutor. Returns the new teaching
     * class object wrapped as WrappedClass.
     *
     * @param id    Is the id of the new class (i.e. "3c").
     * @param tutor Is a wrapped version of the teacher to be the tutor of the new class.
     */
    public WrappedClass createClass(final String id, final WrappedTeacher tutor) {

        TeachingClass teachingClass = new TeachingClass(id, tutor.getObject());

        persistObject(teachingClass);

        return new WrappedClass(teachingClass);
    }

    /**
     * Adds new parent to the database.
     * Uses the underlying hibernate session to persist a new parent
     * object created from name, surname and phone. Returns the new
     * parent object wrapped as WrappedParent.
     *
     * @param name    Is the name of the new parent.
     * @param surname Is the surname of the new parent.
     * @param phone   Is the phone of the new parent.
     */
    public WrappedParent createParent(final String name, final String surname, final String phone) {
        Parent parent = new Parent(name, surname, phone);

        persistObject(parent);

        return new WrappedParent(parent);
    }

    /**
     * Adds new teacher to the database.
     * Uses the underlying hibernate session to persist a new teacher
     * object created from name and surname. Returns the new
     * teacher object wrapped as WrappedTeacher.
     *
     * @param name    Is the name of the new teacher.
     * @param surname Is the surname of the new teacher.
     */
    public WrappedTeacher createTeacher(final String name, final String surname) {
        Teacher teacher = new Teacher(name, surname);

        persistObject(teacher);

        return new WrappedTeacher(teacher);
    }

    // TODO replace WeekTerm with WrappedWeekTerm

    /**
     * Adds new lesson to the database.
     * Uses the underlying hibernate session to persist a new lesson
     * object created from weekTerm and topic using current date.
     * Returns the new lesson object wrapped as WrappedLesson.
     *
     * @param weekTerm Is the weekTerm on which the new lesson takes place.
     * @param topic    Is the topic of the new lesson.
     */
    public WrappedLesson createLesson(final WrappedWeekTerm weekTerm, final String topic) {
        Lesson lesson = new Lesson(weekTerm.getObject(), topic);

        persistObject(lesson);

        return new WrappedLesson(lesson);
    }

    /**
     * Adds new attendance to the database.
     * Uses the underlying hibernate session to persist a new
     * attendance object created from lesson, student and
     * attendanceValue. Returns the new attendance object wrapped as
     * WrappedAttendance.
     *
     * @param lesson  Is the lesson to which the new attendance corresponds.
     * @param student Is the student, to whom the new attendance corresponds.
     */
    public WrappedAttendance createAttendance(final WrappedLesson lesson, final WrappedStudent student, final WrappedAttendanceValue attendanceValue) {
        Attendance attendance = new Attendance(lesson.getObject(), student.getObject(), attendanceValue.getObject());

        persistObject(attendance);

        return new WrappedAttendance(attendance);
    }

    // TODO use wrapped objects

    /**
     * Adds new grade to the database.
     * Uses the underlying hibernate session to persist a new grade
     * object created from student, classSubject, gradeValue,
     * gradeType, comment using current timestamp.
     * Returns the new grade object wrapped as WrappedGrade.
     *
     * @param student      Is the student, who is being given the new grade.
     * @param classSubject Represents the subject, in which the student got the grade.
     * @param gradeValue   Is the value of the new grade.
     * @param gradeType    Is the type of the new grade.
     * @param comment      Is the comment attached to the new grade.
     */
    public WrappedGrade createGrade(final Student student, final ClassSubject classSubject,
                                    final Grade.GradeValue gradeValue, final GradeType gradeType,
                                    final String comment) {

        Grade grade = new Grade(student, classSubject, gradeValue, gradeType, comment);

        persistObject(grade);

        return new WrappedGrade(grade);
    }

    /**
     * Adds new classSubjects to the database.
     * Uses the underlying hibernate session to persist a new
     * classSubject object created from wClass, subject and teacher.
     * Returns the new classSubject object wrapped as WrappedClassSubject.
     *
     * @param wClass  Is a wrapped version of the teaching class the new classSubject shall correspond to.
     * @param subject Is a wrapped version of the subject the new classSubject shall correspond to.
     * @param teacher Is a wrapped version of the teacher teaching subject to this class.
     */
    public WrappedClassSubject createClassSubject(final WrappedClass wClass, final WrappedSubject subject,
                                                  final WrappedTeacher teacher) {
        ClassSubject classSubject = new ClassSubject(wClass.getObject(), subject.getObject(), teacher.getObject());

        persistObject(classSubject);

        return new WrappedClassSubject(classSubject);
    }

    public void updateAttendance(final WrappedAttendance wAttendance, final WrappedAttendanceValue newAttendanceValue) {

        Attendance attendance = wAttendance.getObject();
        attendance.setAttendanceValue(newAttendanceValue.getObject());

        updateObject(attendance);
    }

    public void updateLesson(final WrappedLesson wLesson, final String newTopic) {

        Lesson lesson = wLesson.getObject();
        lesson.setTopic(newTopic);

        updateObject(lesson);
    }
}
