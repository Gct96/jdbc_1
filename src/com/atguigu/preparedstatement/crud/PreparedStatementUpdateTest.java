package com.atguigu.preparedstatement.crud;

import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * @author keyboardhero
 * @create 2022-04-16 17:41
 * 使用PreparedStatementUpdateTest替换Statement，实现对数据表的增删改查操作
 * PreparedStatement通过预编译解决了SQL注入问题。
 */
public class PreparedStatementUpdateTest{
    @Test
    public void testCommonUpdate(){
//        String sql="delete from customers where id = ?";
//        update(sql,3);

        String sql="update `order` set order_name=? where order_id=?";
        update(sql,"DD","2");
    }
    //通用的增删改操作
    public void update(String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();
            //2.预编译
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for(int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResource(conn,ps);
        }
    }
    //修改customers表中的一条记录
    @Test
    public void testUpdate(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();
            //2.预编译
            String sql="update customers set name=? where id=?";
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            ps.setObject(1,"莫扎特");
            ps.setObject(2,18);
            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResource(conn,ps);
        }
    }
    //向customers表中添加一条记录
    @Test
    public void testInsert() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.读取配置文件中的4个基本信息
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
            Properties pros = new Properties();
            pros.load(is);

            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driver = pros.getProperty("driver");


            //2.获取连接
            conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);

            //3.预编译
            String sql = "insert into customers(name,email,birth)values(?,?,?)";
            ps = conn.prepareStatement(sql);
            //4.填充占位符
            ps.setString(1, "吴签");
            ps.setString(2, "wuqian@gmail.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("2021-01-01");
            ps.setDate(3, new Date(date.getTime()));
            //5.执行操作
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            //6.关闭资源
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
