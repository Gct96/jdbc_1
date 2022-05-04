package com.atguigu.preparedstatement.crud;

import com.atguigu.bean.Customer;
import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 针对于不同的表的通用的查询操作，返回表中的一条记录
 * @author keyboardhero
 * @create 2022-04-24 10:41
 */
public class PreparedStatementQueryTest {
    @Test
    public void testGetForList(){
        String sql="select id,name,email from customers where id<?";
        List<Customer> list=getForList(Customer.class,sql,12);
        list.forEach(System.out::println); //java8新特性
    }
    public <T> List<T> getForList(Class<T> clazz,String sql,Object...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据：ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数
            int columnCount=rsmd.getColumnCount();
            //创建集合对象
            ArrayList<T> list=new ArrayList<T>();
            while(rs.next()){
                T t = clazz.newInstance();
                for(int i=0;i<columnCount;i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的列名
                    String columnName = rsmd.getColumnName(i + 1);

                    //给cust对象指定的columnName属性，赋值为columValue:通过反射
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
        return null;
    }


    @Test
    public void testGetInstance(){
        String sql="select id,name,email from customers where id=?";
        Customer customer = getInstance(Customer.class, sql,12);
        System.out.println(customer);
    }
    public <T> T getInstance(Class<T> clazz,String sql,Object...args){ //泛型方法
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据：ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数
            int columnCount=rsmd.getColumnCount();

            if(rs.next()){
                T t = clazz.newInstance();
                for(int i=0;i<columnCount;i++){
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的列名
                    String columnName=rsmd.getColumnName(i+1);

                    //给cust对象指定的columnName属性，赋值为columValue:通过反射
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
        return null;
    }
}
