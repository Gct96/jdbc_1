package com.atguigu.blob;

import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 使用PreparedStatement实现批量数据的操作
 * update\delete本身就具有批量操作的效果
 * 此时的批量操作主要指的是批量插入
 *
 * @author keyboardhero
 * @create 2022-04-27 20:52
 */
public class InsertTest {
    @Test
    public void testInsert1(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            String sql="insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1;i<=20000;i++){
                ps.setObject(1,"name_"+i);
                ps.execute();
            }
            long end = System.currentTimeMillis();
            System.out.println("spend time:"+(end-start)); //1025384
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }
    /**
     * 批量插入的方式二：
     * 1.addBatch()、executeBatch()、clearBatch()
     */
    @Test
    public void testInsert2(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            String sql="insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1;i<=20000;i++){
                ps.setObject(1,"name_"+i);
                //1."攒"sql
                ps.addBatch();
                if(i%500==0){
                    //2.执行
                    ps.executeBatch();
                    //3.清空batch
                    ps.clearBatch();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("spend time:"+(end-start)); //1025384
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }

    /**
     * 批量插入的方式三 设置连接不允许自动提交
     */
    @Test
    public void testInsert3() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            //设置不允许自动提交数据
            conn.setAutoCommit(false);
            String sql="insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1;i<=20000;i++){
                ps.setObject(1,"name_"+i);
                //1."攒"sql
                ps.addBatch();
                if(i%500==0){
                    //2.执行
                    ps.executeBatch();
                    //3.清空batch
                    ps.clearBatch();
                }
            }
            conn.commit();
            long end = System.currentTimeMillis();
            System.out.println("spend time:"+(end-start)); //1025384
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }
}
