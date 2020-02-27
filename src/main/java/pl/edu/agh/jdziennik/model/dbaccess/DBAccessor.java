package pl.edu.agh.jdziennik.model.dbaccess;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.agh.jdziennik.model.Attendance;
import pl.edu.agh.jdziennik.model.ClassSubject;
import pl.edu.agh.jdziennik.model.Classroom;
import pl.edu.agh.jdziennik.model.Grade;
import pl.edu.agh.jdziennik.model.GradeType;
import pl.edu.agh.jdziennik.model.Lesson;
import pl.edu.agh.jdziennik.model.Parent;
import pl.edu.agh.jdziennik.model.Student;
import pl.edu.agh.jdziennik.model.Subject;
import pl.edu.agh.jdziennik.model.Teacher;
import pl.edu.agh.jdziennik.model.TeachingClass;
import pl.edu.agh.jdziennik.model.TimeWindow;
import pl.edu.agh.jdziennik.model.WeekTerm;

import java.sql.Date;
import java.util.List;

/**
 * This class serves as an abstraction layer between hibernate and the rest of the application.
 * It provides methods that perform certain queries.
 * Instances of this class hold a reference to a hibernate session,
 * which they use to perform queries.
 */
public class DBAccessor {
    private Session session;

    /**
     * Creates a new DBAccessor using the Session passed.
     */
    public DBAccessor(final Session session) {
        this.session = session;
    }

    /**
     * Closes the underlying session, making the accessor object unusable.
     * Any further calls of this accessor's methods
     * produce undefined behaviour.
     */
    public void close() {
        session.close();
        session = null;
    }

    /**
     * Queries all teaching classes from the database.
     * Uses the underlying hibernate session to get all classes
     * stored in the database and returns them as a list.
     */
    public List<TeachingClass> queryAllClasses() {
        return session.createQuery("from TeachingClass as tc order by tc.id").list();
    }

    /**
     * Queries all teachers from the database.
     * Uses the underlying hibernate session to get all teachers
     * stored in the database and returns them as a list.
     */
    public List<Teacher> queryAllTeachers() {
        return session.createQuery("from Teacher as t order by t.surname, t.name").list();
    }

    /**
     * Queries all parents from the database.
     * Uses the underlying hibernate session to get all parents
     * stored in the database and returns them as a list.
     */
    public List<Parent> queryAllParents() {
        return session.createQuery("from Parent as p order by p.surname, p.name").list();
    }

    /**
     * Queries all students from the database.
     * Uses the underlying hibernate session to get all students
     * stored in the database and returns them as a list.
     */
    public List<Student> queryAllStudents() {
        return session.createQuery("from Student as s order by s.surname, s.name").list();
    }

    /**
     * Queries all subjects from the database.
     * Uses the underlying hibernate session to get all subjects
     * stored in the database and returns them as a list.
     */
    public List<Student> queryAllSubjects() {
        return session.createQuery("from Subject").list();
    }

    /**
     * Queries all classrooms from the database.
     * Uses the underlying hibernate session to get all classrooms
     * stored in the database and returns them as a list.
     */
    public List<Classroom> queryAllClassrooms() {
        return session.createQuery("from Classroom as c order by c.id").list();
    }

    /**
     * Queries all time windows from the database.
     * Uses the underlying hibernate session to get all time windows
     * stored in the database and returns them as a list.
     */
    public List<TimeWindow> queryAllTimeWindows() {
        return session.createQuery("from TimeWindow").list();
    }


    /**
     * Queries all grade types from the database.
     * Uses the underlying hibernate session to get all grade types
     * stored in the database and returns them as a list.
     */
    public List<GradeType> queryGradeTypes(Teacher teacher) {
        return session.createQuery("from GradeType as gt"
                                        + " where gt.teacher=:teacher"
                                        + " order by gt.typeName")
                                        .setParameter("teacher",teacher)
                                        .list();
}

    /**
     * Queries classes tutored by given teacher from the database.
     * Uses the underlying hibernate session to get all classes,
     * the tutor of which is teacher and returns them as a list.
     *
     * @param teacher Is the tutor, who's classes are queried.
     */
    public List<TeachingClass> queryClassesTutoredByTeacher(final Teacher teacher) {
        return
                session.createQuery
                        ("from TeachingClass as tc "
                                + " where tc.tutor=:teacher"
                                + " order by tc.id")
                        .setParameter("teacher", teacher)
                        .list();
    }

    /**
     * Queries students of given class from the database.
     * Uses the underlying hibernate session to get all students,
     * who are members of teachingClass and returns them as a list.
     *
     * @param teachingClass Is the class, students of which are queried.
     */
    public List<Student> queryClassStudents(final TeachingClass teachingClass) {
        return
                session.createQuery
                        ("from Student as s "
                                + " where s.teachingClass=:teachingClass"
                                + " order by s.surname, s.name")
                        .setParameter("teachingClass", teachingClass)
                        .list();
    }

    /**
     * Queries parent of given student from the database.
     * Uses the underlying hibernate session to get the parent
     * of student and returns that parent.
     *
     * @param student Is the student, who's parent is queried.
     */
    public Parent queryParentOfStudent(final Student student) {
        return (Parent)
                session.createQuery
                        ("select s.parent from Student as s "
                                + " where s=:student")
                        .setParameter("student", student)
                        .uniqueResult();
    }

    /**
     * Queries teaching class of given student from the database.
     * Uses the underlying hibernate session to get the the class
     * student is the member of and returns it.
     *
     * @param student Is the student, who's teaching class is queried.
     */
    public TeachingClass queryClassOfStudent(final Student student) {
        return (TeachingClass)
                session.createQuery
                        ("select s.teachingClass from Student as s "
                                + " where s=:student")
                        .setParameter("student", student)
                        .uniqueResult();
    }

    /**
     * Computes average grade of given student in given subject.
     * Uses the underlying hibernate session to get the grades of
     * student in subject, then computes and returns their average
     * value. Returns 0.0 in case of lack of grades.
     *
     * @param student Is the student, who's grade averge is computed.
     * @param subject Is the subject, grade average in which is computed.
     */
    public Float queryAverageGradeOfStudent(final Student student,
                                            final Subject subject) {
        List<Grade.GradeValue> gradeValues =
                session.createQuery
                        ("select g.gradeValue from Grade as g "
                                + " where g.student=:student and "
                                + " g.classSubject.subject=:subject")
                        .setParameter("student", student)
                        .setParameter("subject", subject)
                        .list();

        float sum = 0.0f;

        if (gradeValues.isEmpty()) return 0.0f;

        for (Grade.GradeValue gv : gradeValues)
            sum += gv.numberValue;

        return sum / gradeValues.size();
    }

    /**
     * Queries subjects of given teacher from the database.
     * Uses the underlying hibernate session to get the subjects
     * teacher teaches and returns them as a list.
     *
     * @param teacher Is the teacher, who's subjects are queried.
     */
    public List<Subject> querySubjectsOfTeacher
    (final Teacher teacher) {
        return
                session.createQuery
                        ("select cs.subject from ClassSubject as cs "
                                + " where cs.teacher=:teacher"
                                + " order by cs.subject.name")
                        .setParameter("teacher", teacher)
                        .list();
    }

    /**
     * Queries subjects of given teacher and teaching class from the database.
     * Uses the underlying hibernate session to get the subjects
     * teacher teaches to teachingClass and returns them as a list.
     *
     * @param teacher       Is the teacher, who's subjects are queried.
     * @param teachingClass Is the class, subjects of which are queried.
     */
    public List<Subject> querySubjectsOfTeacherAndClass
    (final Teacher teacher, final TeachingClass teachingClass) {
        return
                session.createQuery
                        ("select cs.subject from ClassSubject as cs "
                                + " where cs.teacher=:teacher and cs.teachingClass=:teachingClass"
                                + " order by cs.subject.name")
                        .setParameter("teacher", teacher)
                        .setParameter("teachingClass", teachingClass)
                        .list();
    }

    /**
     * Queries classes taught by teacher from the database.
     * Uses the underlying hibernate session to get the teaching
     * classes teacher teaches and returns them as a list.
     *
     * @param teacher Is the teacher, who's classes are queried.
     */
    public List<TeachingClass> queryClassesOfTeacher
    (final Teacher teacher) {
        return
                session.createQuery
                        ("select distinct cs.teachingClass from ClassSubject as cs "
                                + " where cs.teacher=:teacher")
                        .setParameter("teacher", teacher)
                        .list();
    }

    /**
     * Queries classSubjects taught by teacher from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * teacher teaches and returns them as a list.
     *
     * @param teacher Is the teacher, who's classSubjects are queried.
     */
    public List<ClassSubject> queryClassSubjectsOfTeacher
    (final Teacher teacher) {
        return
                session.createQuery
                        ("from ClassSubject as cs "
                                + " where cs.teacher=:teacher"
                                + " order by cs.subject.name, cs.teachingClass.id")
                        .setParameter("teacher", teacher)
                        .list();
    }

    /**
     * Queries classSubjects of given class from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * that correspond to tclass and returns them as a list.
     *
     * @param tclass Is the teaching class, classSubjects of which are queried.
     */
    public List<ClassSubject> queryClassSubjectsOfClass
    (final TeachingClass tclass) {
        return
                session.createQuery
                        ("from ClassSubject as cs "
                                + " where cs.teachingClass=:teachingClass"
                                + " order by cs.subject.name, cs.teacher.surname, cs.teacher.name")
                        .setParameter("teachingClass", tclass)
                        .list();
    }

    /**
     * Queries classSubjects of given class and teacher from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * that correspond to tclass and returns them as a list.
     *
     * @param teacher Is the teacher, who's classSubjects are queried.
     * @param tclass  Is the teaching class, classSubjects of which are queried.
     */
    public List<ClassSubject> queryClassSubjectsOfTeacherAndClass
    (final Teacher teacher, final TeachingClass tclass) {
        return
                session.createQuery
                        ("from ClassSubject as cs "
                                + " where cs.teachingClass=:teachingClass"
                                + " and cs.teacher=:teacher"
                                + " order by cs.subject.name, cs.teacher.surname, cs.teacher.name")
                        .setParameter("teachingClass", tclass)
                        .setParameter("teacher", teacher)
                        .list();
    }

    /**
     * Queries classSubjects of given class and teacher from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * that correspond to tclass and returns them as a list.
     *
     * @param teacher Is the teacher, who's classSubjects are queried.
     * @param tclass  Is the teaching class, classSubjects of which are queried.
     */
    public ClassSubject queryClassSubject
    (final Teacher teacher, final TeachingClass tclass, final Subject subject) {
        return
                (ClassSubject) session.createQuery
                        ("from ClassSubject as cs "
                                + " where cs.teachingClass=:teachingClass"
                                + " and cs.teacher=:teacher"
                                + " and cs.subject=:subject"
                                + " order by cs.subject.name, cs.teacher.surname, cs.teacher.name")
                        .setParameter("teachingClass", tclass)
                        .setParameter("teacher", teacher)
                        .setParameter("subject", subject)
                        .uniqueResult();
    }

    /**
     * Queries classSubjects of given teacher, and for given subject from the database.
     * Uses the underlying hibernate session to get the classSubjects
     * that correspond to tclass and returns them as a list.
     *
     * @param teacher Is the teacher, who's classSubjects are queried.
     * @param subject Is the subject, classSubjects of which are queried.
     */
    public List<ClassSubject> queryClassSubjectsOfTeacherAndSubject
    (final Teacher teacher, final Subject subject) {
        return
                session.createQuery
                        ("from ClassSubject as cs "
                                + " where cs.teacher=:teacher"
                                + " and cs.subject=:subject"
                                + " order by cs.subject.name, cs.teacher.surname, cs.teacher.name")
                        .setParameter("teacher", teacher)
                        .setParameter("subject", subject)
                        .list();
    }

    /**
     * Queries grades of given student in given subject from the database.
     * Uses the underlying hibernate session to get the grades
     * student got in subject and returns them as a list.
     *
     * @param student Is the student, who's grades are queried.
     * @param subject Is the subject, grades in which are queried.
     */
    public List<Grade> queryStudentGrades
    (final Student student, final Subject subject) {
        return
                session.createQuery
                        ("from Grade as g "
                                + " where g.student=:student and "
                                + " g.classSubject.subject=:subject")
                        .setParameter("student", student)
                        .setParameter("subject", subject)
                        .list();
    }

    /**
     * Queries attendances of given student from the database.
     * Uses the underlying hibernate session to get the attendances
     * of student and returns them as a list.
     *
     * @param student Is the student, who's attendances are queried.
     */
    public List<Attendance> queryStudentAttendance
    (final Student student) {
        return
                session.createQuery
                        ("from Attendance as a"
                                + " where a.student=:student"
                                + " order by a.lesson.date")
                        .setParameter("student", student)
                        .list();
    }

    /**
     * Queries current time window from the database.
     * Uses the underlying hibernate session to get the time window,
     * that started before the current time and ends after the current
     * time (that is, a time window that is taking place right now)
     * and returns it.
     */
    // TODO change to queryTimeWindow(Time);
    // TODO import java.sql.Time?
    public TimeWindow queryCurrentTimeWindow() {
        java.util.Date today = new java.util.Date();
        java.sql.Time currentTime = new java.sql.Time(today.getTime());

        return
                (TimeWindow) session.createQuery
                        ("from TimeWindow as tw"
                                + " where tw.startTime <= :currentTime"
                                + " and tw.endTime >= :currentTime")
                        .setParameter("currentTime", currentTime)
                        .uniqueResult();
    }

    /**
     * Queries lesson, that took place at given time from the database.
     * Uses the underlying hibernate session to get the lesson,
     * that took place on date on weekTerm and returns it.
     *
     * @param date     Represents when the queried lesson took place.
     * @param weekTerm Represents when the queried lesson took place.
     */
    public Lesson queryLesson(final Date date, final WeekTerm weekTerm) {
        return
                (Lesson) session.createQuery
                        ("from Lesson as l"
                                + " where l.date=:date"
                                + " and l.weekTerm=:weekTerm")
                        .setParameter("date", date)
                        .setParameter("weekTerm", weekTerm)
                        .uniqueResult();
    }

    /**
     * Queries attendance of given student on given lesson from the database.
     * Uses the underlying hibernate session to get the attendance
     * corresponding to student and lesson and returns it.
     *
     * @param student Is the student, who's attendance is being queried.
     * @param lesson  Is the lesson, attendance on which is being queried.
     */
    public Attendance queryAttendance(final Student student, final Lesson lesson) {
        return
                (Attendance) session.createQuery
                        ("from Attendance as a"
                                + " where a.lesson=:lesson"
                                + " and a.student=:student")
                        .setParameter("lesson", lesson)
                        .setParameter("student", student)
                        .uniqueResult();
    }

    /**
     * Queries classSubject of given class, subject and teacher from the database.
     * Uses the underlying hibernate session to get the classSubject
     * corresponding to teachingClass, subject and teacher.
     *
     * @param teachingClass Is the class, classSubject of which is queried.
     * @param subject       Is the subject, classSubject of which is queried.
     * @param teacher       Is the teacher, who teaches the classSubject being queried.
     */
    public ClassSubject queryClassSubject
    (final TeachingClass teachingClass, final Subject subject, final Teacher teacher) {

        return (ClassSubject) session.createQuery
                ("from ClassSubject as cs"
                        + " where cs.teachingClass=:teachingClass"
                        + " and cs.subject=:subject"
                        + " and cs.teacher=:teacher")
                .setParameter("teachingClass", teachingClass)
                .setParameter("subject", subject)
                .setParameter("teacher", teacher)
                .uniqueResult();

    }

    // TODO it's probably not guarded against, but teacher shouldn't
    // have multiple lessons at the same time, so we should be able to
    // get rid of this teachingClass parameter... Or, actually, we
    // could have 2 methods: one querying the weekterm by teacher and
    // the other - by class.

    /**
     * Queries week term of given teacher and class on given time from the database.
     * Uses the underlying hibernate session to get the week term
     * teacher has with teachingClass during timeWindow on day.
     *
     * @param day           Is the day, on which the queried week term takes place.
     * @param timeWindow    Is the time window, during which the queried week term takes place.
     * @param teacher       Is the teacher, who leads lessons on the queried week term.
     * @param teachingClass Is the class, that has lessons on the queriede week term.
     */
    public WeekTerm queryWeekTerm(final WeekTerm.Day day, final TimeWindow timeWindow,
                                  final Teacher teacher, final TeachingClass teachingClass) {
        return (WeekTerm) session.createQuery
                ("from WeekTerm as wt"
                        + " where wt.day=:day"
                        + " and wt.timeWindow=:timeWindow"
                        + " and wt.classSubject.teacher=:teacher"
                        + " and wt.classSubject.teachingClass=:teachingClass")
                .setParameter("day", day)
                .setParameter("timeWindow", timeWindow)
                .setParameter("teacher", teacher)
                .setParameter("teachingClass", teachingClass)
                .uniqueResult();
    }

    /**
     * Queries number of given kind of attendances of given student from the database.
     * Uses the underlying hibernate session to get the attendances
     * of student with value equal to attendanceValue
     * and returns their count.
     *
     * @param student         Is the student, who's attendances are queried.
     * @param attendanceValue Represents what kind of attendances are being queried (i.e. presences or justified unpresences).
     */
    public Long queryPresenceCount(final Student student, final Attendance.AttendanceValue attendanceValue) {
        Long result = (Long) session.createQuery
                ("select count(a) from Attendance as a"
                        + " where a.student=:student"
                        + " and a.attendanceValue=:attendanceValue")
                .setParameter("student", student)
                .setParameter("attendanceValue", attendanceValue)
                .uniqueResult();

        return result == null ? 0 : result;
    }

    /**
     * Queries given kind of attendances of given student from the database.
     * Uses the underlying hibernate session to get the attendances
     * of student with value equal to attendanceValue
     * and returns them as a list.
     *
     * @param student         Is the student, who's attendances are queried.
     * @param attendanceValue Represents what kind of attendances are being queried (i.e. presences or justified unpresences).
     */
    public List<Attendance> queryPresence(final Student student, final Attendance.AttendanceValue attendanceValue) {
        return session.createQuery
                ("from Attendance as a"
                        + " where a.student=:student"
                        + " and a.attendanceValue=:attendanceValue"
                        + " order by a.lesson.date")
                .setParameter("student", student)
                .setParameter("attendanceValue", attendanceValue)
                .list();
    }

    /**
     * Queries number of given kind of attendances of given student in the given time period from the database.
     * Uses the underlying hibernate session to get the attendances
     * of student with value equal to attendanceValue.
     * Only attendances from the time period specified by from and to
     * are takrn into accout.
     * The attendances are returned as a list.
     *
     * @param student         Is the student, who's attendances are queried.
     * @param attendanceValue Represents what kind of attendances are being queried (i.e. presences or justified unpresences).
     * @param from            Represents the start of the time period in which the attendances are being queired.
     * @param to              Represents the end of the time period in which the attendances are being queired.
     */
    public List<Attendance> queryPresenceWithDate(final Student student, final Attendance.AttendanceValue attendanceValue, final Date from, final Date to) {
        return session.createQuery
                ("from Attendance as a"
                        + " where a.student=:student"
                        + " and a.attendanceValue=:attendanceValue"
                        + " and a.lesson.date between :from and :to"
                        + " order by a.lesson.date")
                .setParameter("student", student)
                .setParameter("attendanceValue", attendanceValue)
                .setParameter("from", from)
                .setParameter("to", to)
                .list();
    }


    /**
     * Persists given object in the database.
     * Uses underlying session to tie object with hibernate session
     * and add it to the database.
     *
     * @param object Is the object to persist.
     */
    public void persistObject(final Object object) {
        Transaction tx = session.beginTransaction();
        session.persist(object);
        tx.commit();
    }

    /**
     * Persists all objects in given list in the database.
     * Uses underlying session to tie list's members with hibernate
     * session and add them to the database.
     *
     * @param list Contains the objects to persist.
     */
    public void persistObjects(final List list) {
        Transaction tx = session.beginTransaction();
        for (Object object : list)
            session.persist(object);
        tx.commit();
    }

    /**
     * Updates given object in the database.
     * Uses underlying session to save the new state of object in
     * the database.
     *
     * @param object Is the modified object, old state of which is present in the databse.
     */
    public void updateObject(final Object object) {
        Transaction tx = session.beginTransaction();
        session.update(object);
        tx.commit();
    }

    /**
     * Deletes given object from the database.
     * Uses underlying session to remove object from the database.
     *
     * @param object Is the object to remove.
     */
    public void deleteObject(final Object object) {
        Transaction tx = session.beginTransaction();
        session.delete(object);
        tx.commit();
    }
}
