package com.atguigu.preparedstatement.crud;

import com.atguigu.bean.Order;
import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 对Order表的查询操作
 * @author keyboardhero
 * @create 2022-04-21 20:00
 */
public class OrderForQuery {
    @Test
    public void testOrderForQuery(){
        String sql="select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = orderForQuery(sql,1);
        System.out.println(order);
    }
    /**
     * 通用的对Order表的查询操作
     */
    public Order orderForQuery(String sql, Object... args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            //执行 获取结果集
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                Order order = new Order();
                for (int i = 0; i < columnCount; i++) {
                    //获取每个列的列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的别名
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //给order对象指定的columnName属性，赋值为columValue:通过反射
                    Field field = Order.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(order,columnValue);
                }
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return null;
    }

    @Test
    public void testQuery1(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql="select order_id,order_name,order_date from `order` where order_id= ?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,1);

            rs = ps.executeQuery();
            if(rs.next()){
                int id = (int)rs.getObject(1);
                String name =(String)rs.getObject(2);
                Date date = (Date)rs.getObject(3);

                Order order = new Order(id, name, date);
                System.out.println(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
    }
}
