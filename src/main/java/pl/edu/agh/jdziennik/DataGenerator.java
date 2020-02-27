package pl.edu.agh.jdziennik;


import javafx.util.Pair;
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
import pl.edu.agh.jdziennik.model.dbaccess.DBAccessor;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Fills the database with example data.
 */
public class DataGenerator {

    DBAccessor accessor;

    /**
     * Creates a new DataGenerator.
     * It will use accessor to save objects to the database.
     *
     * @param accessor Is an accessor connected to the database, that is to be filled with data.
     */
    public DataGenerator(DBAccessor accessor) {
        this.accessor = accessor;
    }

    private static int createRandomIntBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    private static List<String> generateStringList(int size, String format, List<Pair<Integer, Integer>> boundaries) {
        List<String> res = new ArrayList();
        List<Integer> val;

        for (int i = 0; i < size; i++) {
            val = new ArrayList<>();

            for (Pair<Integer, Integer> boundary : boundaries)
                val.add(createRandomIntBetween(boundary.getKey(), boundary.getValue()));

            res.add(String.format(format, val.toArray()));
        }

        return res;
    }

    private static <T> T getRandomValue(List<T> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    /**
     * Fills the database with example data.
     * If the underlying database is not empty (currently only the
     * emptiness of the students table is checked), this method does
     * nothing. The amount of example data saved is relatively small
     * (currently only 3 classes of students).
     */
    public void generateData() {
        if (!accessor.queryAllStudents().isEmpty())
            return;

        List<Subject> subjectList = generateSubjectList();

        List<Classroom> classroomList = generateClassroomList(30);

        List<Teacher> teacherList = generateTeacherList();

        List<TeachingClass> teachingClassList = generateTeachingClassList(teacherList);

        List<ClassSubject> classSubjectList = generateClassSubjectList(120, teachingClassList, subjectList, teacherList);

        List<Parent> parentList = generateParentList(75);

        List<Student> studentList = generateStudentList(75, teachingClassList, parentList);

        List<TimeWindow> timeWindowList = generateTimeWindows();

        List<WeekTerm> weekTermList = generateWeekTermList(classSubjectList, classroomList, timeWindowList);

        List<Lesson> lessonList = generateLessonList(0, weekTermList);

        List<GradeType> gradeTypeList = generateGradeTypeList(teacherList);

        List<Grade> gradeList = generateGradeList(100, studentList, classSubjectList, gradeTypeList);

        saveList(teacherList);

        saveList(teachingClassList);

        saveList(parentList);

        saveList(studentList);

        saveList(subjectList);

        saveList(classroomList);

        saveList(classSubjectList);

        saveList(timeWindowList);

        saveList(weekTermList);

        saveList(lessonList);

        saveList(gradeTypeList);

        saveList(gradeList);
    }

    private void saveList(List objectsList) {
        accessor.persistObjects(objectsList);
    }

    private List<Classroom> generateClassroomList(int size) {
        List<String> classroomNumList = generateStringList(size, "%01d%02d", Arrays.asList(new Pair(0, 2), new Pair(0, 15)));
        Set<Classroom> classroomSet = new HashSet<>();

        for (String classroomNum : classroomNumList)
            classroomSet.add(new Classroom(classroomNum));

        return new ArrayList<>(classroomSet);
    }

    private List<ClassSubject> generateClassSubjectList(int size, List<TeachingClass> teachingClassList, List<Subject> subjectList, List<Teacher> teacherList) {
        Set<ClassSubject> classSubjectSet = new HashSet<>();

        for (int i = 0; i < size; i++) {
            TeachingClass teachingClass = getRandomValue(teachingClassList);
            Subject subject = getRandomValue(subjectList);
            Teacher teacher = getRandomValue(teacherList);

            classSubjectSet.add(new ClassSubject(teachingClass, subject, teacher));
        }

        return new ArrayList<>(classSubjectSet);
    }

    private List<Grade> generateGradeList(int size, List<Student> studentList, List<ClassSubject> classSubjectsList, List<GradeType> gradeTypeList) {
        List<Grade.GradeValue> gradeValueList = Arrays.asList(Grade.GradeValue.BARDZO_DOBRY, Grade.GradeValue.BARDZO_DOBRY_MINUS, Grade.GradeValue.DOBRY, Grade.GradeValue.DOBRY_MINUS, Grade.GradeValue.DOPUSZCZAJACY, Grade.GradeValue.DOSTATECZNY_PLUS);

        Set<Grade> gradeSet = new HashSet<>();

        for (int i = 0; i < size; i++) {
            gradeSet.add(new Grade(getRandomValue(studentList), getRandomValue(classSubjectsList), getRandomValue(gradeValueList), getRandomValue(gradeTypeList), ""));
        }

        return new ArrayList<>(gradeSet);
    }

    private List<GradeType> generateGradeTypeList(List<Teacher> teacherList) {
        return teacherList.get(0).getGradeTypes();
    }

    private List<Lesson> generateLessonList(int size, List<WeekTerm> weekTermList) {
        Set<Lesson> lessonSet = new HashSet<>();

        for (int i = 0; i < size; i++) {
            lessonSet.add(new Lesson(getRandomValue(weekTermList), "Thema " + i));
        }

        return new ArrayList<>(lessonSet);
    }

    private List<Parent> generateParentList(int size) {
        List<String> namesList = Arrays.asList("Zuzanna", "Julia", "Maja", "Hanna", "Lena", "Alicja", "Oliwia", "Natalia", "Anna", "Gabriela", "Jakub", "Szymon", "Mikolaj", "Adam", "Kacper", "Piotr", "Wiktror", "Karol", "Dawid", "Hubert");
        List<String> surnamesList = Arrays.asList("Nowak", "Wojcik", "Kowalczyk", "Mazur", "Krawczyk", "Zajac", "Krol", "Wieczorek", "Wrobel", "Dudek", "Sikora", "Baran", "Stepien", "Pawlak", "Duda", "Szewczyk", "Bak", "Lis", "Mazurek");
        List<String> phoneNumberList = generateStringList(10, "%03d %03d %03d", Arrays.asList(new Pair(0, 999), new Pair(0, 999), new Pair(0, 999)));

        Set<Parent> parentSet = new HashSet<>();
        for (int i = 0; i < size; i++) {
            String name = getRandomValue(namesList);
            String surname = getRandomValue(surnamesList);
            String phoneNum = getRandomValue(phoneNumberList);

            parentSet.add(new Parent(name, surname, phoneNum));
        }

        return new ArrayList<>(parentSet);
    }

    private List<Student> generateStudentList(int size, List<TeachingClass> teachingClassList, List<Parent> parentList) {
        List<String> namesList = Arrays.asList("Zuzanna", "Julia", "Maja", "Hanna", "Lena", "Alicja", "Oliwia", "Natalia", "Anna", "Gabriela", "Jakub", "Szymon", "Mikolaj", "Adam", "Kacper", "Piotr", "Wiktror", "Karol", "Dawid", "Hubert");
        List<String> ssnList = generateStringList(10, "%02d%02d%02d%05d", Arrays.asList(new Pair(04, 04), new Pair(1, 12), new Pair(1, 29), new Pair(0, 99999)));

        Set<Student> studentSet = new HashSet<>();

        for (int i = 0; i < size; i++) {
            Parent parent = getRandomValue(parentList);
            String name = getRandomValue(namesList);
            String surname = parent.getSurname();
            TeachingClass teachingClass = getRandomValue(teachingClassList);
            String SSN = getRandomValue(ssnList);

            studentSet.add(new Student(teachingClass, name, surname, SSN, parent));
        }

        return new ArrayList<>(studentSet);
    }

    private List<Subject> generateSubjectList() {
        return Arrays.asList(new Subject("matematyka"), new Subject("fizyka"), new Subject("plastyka"), new Subject("jezyk angielski"), new Subject("jezyk polski"), new Subject("geografia"), new Subject("chemia"), new Subject("informatyka"), new Subject("historia"), new Subject("wos"), new Subject("biologia"), new Subject("muzyka"), new Subject("jezyk niemiecki"));
    }

    private List<Teacher> generateTeacherList() {
        return Arrays.asList(new Teacher("Anna", "Nowak"));
    }

    private List<TeachingClass> generateTeachingClassList(List<Teacher> teacherList) {
        Set<TeachingClass> teachingClassSet = new HashSet<>();
        teachingClassSet.add(new TeachingClass("3A", getRandomValue(teacherList)));
        teachingClassSet.add(new TeachingClass("3B", getRandomValue(teacherList)));
        teachingClassSet.add(new TeachingClass("3C", getRandomValue(teacherList)));

        return new ArrayList<>(teachingClassSet);
    }

    private List<WeekTerm> generateWeekTermList(List<ClassSubject> classSubjectList, List<Classroom> classroomList, List<TimeWindow> timeWindowList) {
        List<WeekTerm.Day> daySet = Arrays.asList(WeekTerm.Day.PONIEDZIALEK, WeekTerm.Day.WTOREK, WeekTerm.Day.SRODA, WeekTerm.Day.CZWARTEK, WeekTerm.Day.PIATEK);

        Set<WeekTerm> weekTermSet = new HashSet<>();

        for (WeekTerm.Day d : daySet) {
            for (TimeWindow t : timeWindowList) {
                weekTermSet.add(new WeekTerm(d, t, getRandomValue(classSubjectList), getRandomValue(classroomList)));
            }
        }

        return new ArrayList<>(weekTermSet);
    }

    private List<TimeWindow> generateTimeWindows() {
        List<TimeWindow> timeWindowList = new ArrayList<>();
        timeWindowList.add(new TimeWindow(1, new Time(8, 0, 0)));
        timeWindowList.add(new TimeWindow(2, new Time(8, 50, 0)));
        timeWindowList.add(new TimeWindow(3, new Time(9, 45, 0)));
        timeWindowList.add(new TimeWindow(4, new Time(10, 45, 0)));
        timeWindowList.add(new TimeWindow(5, new Time(11, 40, 0)));
        timeWindowList.add(new TimeWindow(6, new Time(12, 30, 0)));
        timeWindowList.add(new TimeWindow(7, new Time(13, 25, 0)));
        timeWindowList.add(new TimeWindow(8, new Time(14, 15, 0)));
        timeWindowList.add(new TimeWindow(9, new Time(15, 10, 0)));
        timeWindowList.add(new TimeWindow(10, new Time(16, 00, 0)));
        return timeWindowList;
    }
}
