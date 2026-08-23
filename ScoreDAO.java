package com.atsea.dao;

import com.atsea.entity.Score;
import com.atsea.util.DBUtil;

import java.sql.*;

public class ScoreDAO {

    public void addScore(Score score){

        String sql=
                "insert into score(sno,cno,grade) values(?,?,?)";

        try(
                Connection conn=DBUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement(sql)
        ){

            ps.setString(1,score.getSno());
            ps.setString(2,score.getCno());
            ps.setDouble(3,score.getGrade());

            ps.executeUpdate();

            System.out.println("成绩录入成功");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void queryScore(){

        String sql=
                """
                SELECT
                s.sname,
                c.cname,
                sc.grade
                FROM score sc
                JOIN student s
                ON s.sno=sc.sno
                JOIN course c
                ON c.cno=sc.cno
                """;

        try(
                Connection conn=DBUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement(sql);
                ResultSet rs=ps.executeQuery()
        ){

            while(rs.next()){

                System.out.println(
                        rs.getString("sname")
                                +" "
                                +rs.getString("cname")
                                +" "
                                +rs.getDouble("grade")
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void avgScore(){

        String sql=
                "SELECT sno,AVG(grade) avg_grade FROM score GROUP BY sno";

        try(
                Connection conn=DBUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement(sql);
                ResultSet rs=ps.executeQuery()
        ){

            while(rs.next()){

                System.out.println(
                        rs.getString("sno")
                                +" 平均分:"
                                +rs.getDouble("avg_grade")
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}