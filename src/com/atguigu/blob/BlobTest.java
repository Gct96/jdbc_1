package com.atguigu.blob;

import com.atguigu.bean.Customer;
import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.io.*;
import java.sql.*;

/**
 * @author keyboardhero
 * @create 2022-04-25 10:50
 */
public class BlobTest {
    //向数据表cus中插入Blob类型的字段
    @Test
    public void testInsert() throws Exception{
        Connection conn = JDBCUtils.getConnection();
        String sql="insert into customer(name,email,birth,photo)values(?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setObject(1,"张宇豪");
        ps.setObject(2,"zhang@qq.com");
        ps.setObject(3,"1992-09-08");
        FileInputStream is = new FileInputStream(new File("xiangduilujing.jpg"));
        ps.setBlob(4,is);

        ps.execute();
        JDBCUtils.closeResource(conn,ps);
    }

    //查询数据表cus中Blob类型的字段
    public void testQuery() {
        Connection conn = null;
        PreparedStatement ps = null;
        InputStream is = null;
        FileOutputStream fos = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql="select id,name,email,birth,photo from customers where id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,21);
            rs = ps.executeQuery();
            if(rs.next()){
                int id=rs.getInt("id");
                String name=rs.getString("name");
                String email=rs.getString("email");
                Date birth=rs.getDate("birth");

                Customer cust = new Customer(id, name, email, birth);
                System.out.println(cust);
                //将Blob字段下载到本地，以文件形式保存
                Blob photo = rs.getBlob("photo");
                is = photo.getBinaryStream();
                fos = new FileOutputStream("xiangduilujing.jpg");
                byte[] buffer = new byte[1024];
                int len;
                while((len=is.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is!=null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(fos!=null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn,ps,rs);
        }
    }
}
