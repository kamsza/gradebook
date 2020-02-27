package pl.edu.agh.jdziennik;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.jdziennik.model.*;
import pl.edu.agh.jdziennik.model.dbaccess.DBAccessor;

import java.sql.Date;
import java.sql.Time;

import static org.junit.Assert.*;

public class DBAccessorTest {
    private DBAccessor accessor;

    @Before
    public void before() {
        SessionFactory sessionFactory = new Configuration().configure("hibernate/test_hibernate.cfg.xml").buildSessionFactory();
        accessor = new DBAccessor(sessionFactory.openSession());
    }

    @After
    public void after() {
        accessor.close();
    }

    @Test
    public void emptyQueriesTest() {
        assertTrue(accessor.queryAllClasses().size() == 0);
        assertTrue(accessor.queryAllTeachers().size() == 0);
        assertTrue(accessor.queryAllParents().size() == 0);
        assertTrue(accessor.queryAllStudents().size() == 0);
        assertTrue(accessor.queryAllSubjects().size() == 0);
        assertTrue(accessor.queryAllClassrooms().size() == 0);
        assertTrue(accessor.queryAllTimeWindows().size() == 0);
    }

    private Teacher teacher;

    private TeachingClass teachingClass;

    private Parent parent;

    private Student student;

    private Subject subject;

    private Classroom classroom;

    private ClassSubject classSubject;

    private TimeWindow nowWindow;

    private WeekTerm weekTerm;

    private Date date;
    private Lesson lesson;

    private GradeType gradeType;

    private Grade grade;

    private Assignment assignment;

    private Attendance attendance;

    private void addTestObjects() {
        teacher = new Teacher("Anna", "Nowak");

        teachingClass = new TeachingClass("3A", teacher);

        parent = new Parent("Anna", "Kowalska", "123456789");

        student = new Student(teachingClass, "Damian", "Kowalski", "98081687654", parent);

        subject = new Subject("Math");

        classroom = new Classroom("202A");

        classSubject = new ClassSubject(teachingClass, subject, teacher);

        Time now = new Time(System.currentTimeMillis());
        nowWindow = new TimeWindow(1, now, now);
        
        weekTerm = new WeekTerm(WeekTerm.Day.CZWARTEK, nowWindow, classSubject, classroom);

        date = new Date(System.currentTimeMillis());
        lesson =  new Lesson(weekTerm, "Dzielenie wielomianów", date);

        gradeType = new GradeType(teacher, "Aktywność pozalekcyjna", 5);

        grade = new Grade(student, classSubject, Grade.GradeValue.BARDZO_DOBRY, gradeType, "");

        assignment = new Assignment(Assignment.AssignmentType.SPRAWDZIAN,
                                    new Date(System.currentTimeMillis()), "", weekTerm);

        attendance = new Attendance(lesson, student, Attendance.AttendanceValue.SPOZNIENIE);
        
        accessor.persistObject(teacher);
        accessor.persistObject(teachingClass);
        accessor.persistObject(parent);
        accessor.persistObject(student);
        accessor.persistObject(subject);
        accessor.persistObject(classroom);
        accessor.persistObject(classSubject);
        accessor.persistObject(nowWindow);
        accessor.persistObject(weekTerm);
        accessor.persistObject(lesson);
        accessor.persistObject(gradeType);
        accessor.persistObject(grade);
        accessor.persistObject(assignment);
        accessor.persistObject(attendance);
    }
    
    @Test
    public void newObjectsTest() {
        addTestObjects();

        assertTrue(teacher.getId() > 0);
        assertTrue(student.getId() > 0);
        assertTrue(parent.getId() > 0);
        assertTrue(assignment.getId() > 0);
        assertTrue(classSubject.getId().getSubject() != null);
        assertTrue(classSubject.getId().getTeachingClass() != null);
        assertTrue(weekTerm.getId() > 0);
        assertTrue(grade.getId() > 0);
        assertTrue(attendance.getId() > 0);

        assertEquals(accessor.queryAllClasses().get(0), teachingClass);
        assertEquals(accessor.queryAllTeachers().get(0), teacher);
        assertEquals(accessor.queryAllParents().get(0), parent);
        assertEquals(accessor.queryAllStudents().get(0), student);
        assertEquals(accessor.queryAllSubjects().get(0), subject);
        assertEquals(accessor.queryAllClassrooms().get(0), classroom);
        // some grade types are added automatically with a teacher, so
        // we just check if the explicitly created one is among them
        assertTrue(accessor.queryGradeTypes(teacher).contains(gradeType));
        assertEquals(accessor.queryAllTimeWindows().get(0), nowWindow);
    }

    @Test
    public void queriesTest() {
        addTestObjects();

        Teacher tmpTeacher;
        TeachingClass tmpClass;
        Student tmpStudent;
        WeekTerm tmpWeekTerm;
        Subject tmpSubject;
        
        assertEquals(accessor.queryClassesTutoredByTeacher(teacher).get(0), teachingClass);
        accessor.persistObject(tmpTeacher = new Teacher("Marian", "Bubak"));
        assertTrue(accessor.queryClassesTutoredByTeacher(tmpTeacher).size() == 0);

        assertEquals(accessor.queryClassStudents(teachingClass).get(0), student);
        accessor.persistObject(tmpClass = new TeachingClass("6j", teacher));
        assertTrue(accessor.queryClassStudents(tmpClass).size() == 0);

        assertEquals(accessor.queryParentOfStudent(student), parent);
        
        assertEquals(accessor.queryClassOfStudent(student), teachingClass);

        // simple test for queryAverageGradeOfStudent() for now;
        // change queryAverageGradeOfStudent() to take weights into account
        // then add test with more grades for it
        assertEquals(accessor.queryAverageGradeOfStudent(student, subject), Float.valueOf(5.0f));

        assertEquals(accessor.querySubjectsOfTeacherAndClass(teacher, teachingClass).get(0), subject);
        accessor.persistObject(tmpTeacher = new Teacher("Anna", "Zygmunt"));
        assertTrue(accessor.querySubjectsOfTeacherAndClass(tmpTeacher, teachingClass).size() == 0);

        assertEquals(accessor.queryClassesOfTeacher(teacher).get(0), teachingClass);
        accessor.persistObject(tmpTeacher = new Teacher("Lech", "Siwik"));
        assertTrue(accessor.queryClassesOfTeacher(tmpTeacher).size() == 0);
        
        assertEquals(accessor.queryClassSubjectsOfTeacher(teacher).get(0), classSubject);
        accessor.persistObject(tmpTeacher = new Teacher("Artur", "Fortuna"));
        assertTrue(accessor.queryClassSubjectsOfTeacher(tmpTeacher).size() == 0);
        
        assertEquals(accessor.queryClassSubjectsOfClass(teachingClass).get(0), classSubject);
        accessor.persistObject(tmpClass = new TeachingClass("7f", teacher));
        assertTrue(accessor.queryClassSubjectsOfClass(tmpClass).size() == 0);

        assertEquals(accessor.queryStudentGrades(student, subject).get(0), grade);
        accessor.persistObject(tmpStudent = new Student(teachingClass, "Kateusz", "gosciu Mubicki", "00011199228", parent));
        assertTrue(accessor.queryStudentGrades(tmpStudent, subject).size() == 0);

        assertEquals(accessor.queryStudentAttendance(student).get(0), attendance);
        accessor.persistObject(tmpStudent = new Student(teachingClass, "Czakub", "Jajka", "12323434545", parent));
        assertTrue(accessor.queryStudentAttendance(tmpStudent).size() == 0);
        
        assertEquals(accessor.queryLesson(date, weekTerm), lesson);
        accessor.persistObject(tmpWeekTerm = new WeekTerm(WeekTerm.Day.WTOREK, nowWindow, classSubject, classroom));
        assertTrue(accessor.queryLesson(date, tmpWeekTerm) == null);

        assertEquals(accessor.queryAttendance(student, lesson), attendance);
        accessor.persistObject(tmpStudent = new Student(teachingClass, "Kiotr", "Potara", "16273849506", parent));
        assertTrue(accessor.queryAttendance(tmpStudent, lesson) == null);

        assertEquals(accessor.queryClassSubject(teachingClass, subject, teacher), classSubject);
        accessor.persistObject(tmpSubject = new Subject("lepienie pierogow"));
        assertTrue(accessor.queryClassSubject(teachingClass, tmpSubject, teacher) == null);
        
        assertEquals(accessor.queryWeekTerm(WeekTerm.Day.CZWARTEK, nowWindow, teacher, teachingClass), weekTerm);
        assertTrue(accessor.queryWeekTerm(WeekTerm.Day.PONIEDZIALEK, nowWindow, teacher, teachingClass) == null);

        assertEquals(accessor.queryPresenceCount(student, Attendance.AttendanceValue.SPOZNIENIE), Long.valueOf(1));
        assertEquals(accessor.queryPresenceCount(student, Attendance.AttendanceValue.NIEOBECNOSC), Long.valueOf(0));

        assertEquals(accessor.queryPresenceWithDate(student, Attendance.AttendanceValue.SPOZNIENIE, date, date).get(0), attendance);
        assertTrue(accessor.queryPresenceWithDate(student, Attendance.AttendanceValue.SPOZNIENIE,
                                                  new Date(System.currentTimeMillis() + 1000000000),
                                                  new Date(System.currentTimeMillis() + 2000000000)).size() == 0);
    }

    @Test
    public void updateTest() {
        addTestObjects();

        attendance.setAttendanceValue(Attendance.AttendanceValue.NIEOBECNOSC);
        accessor.updateObject(attendance);

        assertEquals(accessor.queryPresenceCount(student, Attendance.AttendanceValue.SPOZNIENIE), Long.valueOf(0));
        assertEquals(accessor.queryPresenceCount(student, Attendance.AttendanceValue.NIEOBECNOSC), Long.valueOf(1));
    }

    public void deleteTest() {
        addTestObjects();

        accessor.deleteObject(teacher);
        assertTrue(accessor.queryAllTeachers().size() == 0);
        assertTrue(accessor.queryAllClasses().size() == 0);
        assertFalse(accessor.queryAllParents().size() == 0);
        accessor.deleteObject(parent);
        assertTrue(accessor.queryAllParents().size() == 0);
    }
}
