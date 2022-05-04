package com.atguigu.connection;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest {
    //方式一
    @Test
    public void testConnection1() throws SQLException{
        //获取Driver实现类对象
        Driver driver=new com.mysql.cj.jdbc.Driver();
        String url="jdbc:mysql://localhost:3306/test";
        //将用户名和密码封装在Properties中
        Properties info=new Properties();
        info.setProperty("user","root");
        info.setProperty("password","root");
        Connection conn=driver.connect(url,info);
        System.out.println(conn);
    }
    //方式二：对方式一的迭代，不出现第三方的api，使程序有更好的可移植性
    @Test
    public void testConnection2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        //1.获取Driver实现类对象，使用反射
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver=(Driver) clazz.getDeclaredConstructor().newInstance();
        //2.提供要连接的数据库
        String url="jdbc:mysql://localhost:3306/test";
        //3.提供连接需要的用户名和密码
        Properties info=new Properties();
        info.setProperty("user","root");
        info.setProperty("password","root");
        //4.获取连接
        Connection conn=driver.connect(url,info);
        System.out.println(conn);
    }
    //方式三：使用DriverManager替换Driver
    @Test
    public void testConnection3() throws SQLException {
        String url="jdbc:mysql://localhost:3306/test";
        String user="root";
        String password="root";
        //获取连接
        Connection conn=DriverManager.getConnection(url,user,password);
        System.out.println(conn);
    }
    //方式四最终版：将数据库连接的4个基本信息声明在配置文件中
    @Test
    public void  getConnection4() throws IOException, SQLException {
        //1.读取配置文件中的4个基本信息
        InputStream is=ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties pros = new Properties();
        pros.load(is);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driver = pros.getProperty("driver");


        //2.获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }
}
