package com.atguigu.preparedstatement.crud;

import com.atguigu.bean.Customer;
import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 针对Customer表的查询操作
 * @author keyboardhero
 * @create 2022-04-21 16:27
 */
public class CustomerForQuery {
    @Test
    public void testQueryForCustomers(){
        String sql="select id,name,birth,email from customers where id = ?";
        //填充占位符
        Customer customer = queryForCustomers(sql, 13);
        System.out.println(customer);
    }
    /**
     * 针对customers表的通用的查询操作
     */
    public Customer queryForCustomers(String sql,Object...args){
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
                Customer cust = new Customer();
                for(int i=0;i<columnCount;i++){
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的列名
                    String columnName=rsmd.getColumnName(i+1);

                    //给cust对象指定的columnName属性，赋值为columValue:通过反射
                    Field field = Customer.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(cust,columnValue);
                }
                return cust;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
        return null;
    }


    @Test
    public void testQuery1(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql="select id,name,email,birth from customers where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,1);
            //执行并返回结果集
            resultSet = ps.executeQuery();
            //处理结果集
            if(resultSet.next()){//判断结果集的下一条是否有数据
                //获取当前这条数据的各个字段值
                int id=resultSet.getInt(1);//第一个字段
                String name=resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeResource(conn,ps,resultSet);
        }
    }
}
