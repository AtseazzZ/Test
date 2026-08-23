package com.atsea.dao;

import com.atsea.entity.Course;
import com.atsea.util.DBUtil;

import java.sql.*;

public class CourseDAO {

    public void addCourse(Course c){

        String sql="insert into course values(?,?,?,?)";

        try(
                Connection conn=DBUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement(sql)
        ){

            ps.setString(1,c.getCno());
            ps.setString(2,c.getCname());
            ps.setInt(3,c.getCredit());
            ps.setString(4,c.getTeacher());

            ps.executeUpdate();

            System.out.println("课程添加成功");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void findAll(){

        String sql="select * from course";

        try(
                Connection conn=DBUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement(sql);
                ResultSet rs=ps.executeQuery()
        ){

            while(rs.next()){

                System.out.println(
                        rs.getString("cno")+" "
                                +rs.getString("cname")+" "
                                +rs.getInt("credit")+" "
                                +rs.getString("teacher")
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}