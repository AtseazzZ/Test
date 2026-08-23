package com.atsea.dao;

import com.atsea.entity.Student;
import com.atsea.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDAO {

    // 添加学生
    public void addStudent(Student s) {

        String sql =
                "insert into student values(?,?,?,?,?)";

        try (
                Connection conn =
                        DBUtil.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, s.getSno());
            ps.setString(2, s.getSname());
            ps.setString(3, s.getSex());
            ps.setInt(4, s.getAge());
            ps.setString(5, s.getClassName());

            ps.executeUpdate();

            System.out.println("添加成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查询全部学生
    public void findAll() {

        String sql =
                "select * from student";

        try (
                Connection conn =
                        DBUtil.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                System.out.println(
                        rs.getString("sno")
                                + "\t"
                                + rs.getString("sname")
                                + "\t"
                                + rs.getString("sex")
                                + "\t"
                                + rs.getInt("age")
                                + "\t"
                                + rs.getString("class_name")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除学生
    public void deleteStudent(String sno) {

        String sql =
                "delete from student where sno=?";

        try (
                Connection conn =
                        DBUtil.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, sno);

            ps.executeUpdate();

            System.out.println("删除成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 修改学生年龄
    public void updateAge(String sno, int age) {

        String sql =
                "update student set age=? where sno=?";

        try (
                Connection conn =
                        DBUtil.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1, age);
            ps.setString(2, sno);

            ps.executeUpdate();

            System.out.println("修改成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}