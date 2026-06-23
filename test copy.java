package interfaceDemo;

public class test {
    public static void main(String[] args) {

        Student[] students = new Student[10];
        students[0] = new Student("张三", '男', 90);
        students[1] = new Student("李四", '女', 80);
        students[2] = new Student("王五", '男', 70);
        students[3] = new Student("赵六", '女', 60);
        students[4] = new Student("孙七", '男', 50);
        students[5] = new Student("周八", '女', 40);
        students[6] = new Student("吴九", '男', 30);
        students[7] = new Student("郑十", '女', 20);
        students[8] = new Student("王十一", '男', 10);
        students[9] = new Student("张十二", '女', 0);


        ClassDataInter c = new impl1();
        c.printAllStudentInfos();
        c.printAverageScore();
    }
}
