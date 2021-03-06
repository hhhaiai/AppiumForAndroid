package com.ufenqi.base;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 该类用于连接数据库和执行sql
 * @author 杨少佳
 * 2015-11-11
 *
 */
public class JDBCUtils
{
	//加载配置文件
	private static final String drivername;
	private static final String dburl;
	private static final String dbusername;
	private static final String dbpassword;
	
	static
	{
		InputStream inStream = null;
		try
		{
			inStream = JDBCUtils.class.getResourceAsStream("/config/mysql.properties");
			Properties prop = new Properties();
			prop.load(inStream);
			drivername = prop.getProperty("drivername");
			dburl = prop.getProperty("dburl");
			dbusername = prop.getProperty("dbusername");
			dbpassword = prop.getProperty("dbpassword");
		}
		catch(IOException e)
		{
			throw new  RuntimeException("加载数据库配置文件失败",e);
		}
		finally
		{
			if(inStream!=null)
			{
				try
				{
					inStream.close();
				} catch (IOException e)
				{
					
				}
			}
		}
		
		
		//加载JDBC驱动
		try
		{
			Class.forName(drivername);
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException("加载JDBC驱动失败",e);
		}
		
	}
/*----------------------------------------------------------------------------------------------------*/	
	
	//创建数据库链接
	public static Connection creatConnection() throws SQLException
	{
		return DriverManager.getConnection(dburl,dbusername,dbpassword);
	}
	
/*---------------------------------------------------------------------------------------------------*/	
	//执行update，insert，update，delete语句
	public static int executeUpdate(Connection conn,String sql,Object...parameters) throws SQLException
	{
		PreparedStatement stmt = null;
		try
		{
			stmt = conn.prepareStatement(sql);
			
			//占位符参数设置
			for(int i=0;i<parameters.length;i++)
			{
				stmt.setObject(i+1, parameters[i]);
			}
			return stmt.executeUpdate();
		}
		finally
		{
			
			closeStatement(stmt);
		}
	}
/*----------------------------------------------------------------------*/
	public static int executeUpdate(String sql,Object...parameters) throws SQLException 
	{
		Connection conn =null;
		try
		{
			conn = creatConnection();
			return executeUpdate(conn, sql, parameters);
		}
		finally
		{
			closeConnection(conn);
		}
	}
	
	
/*----------------------------------------------------------------------------*/	
	//执行查询语句
	public static ResultSet executeQuery(Connection conn,String sql,Object...parameters) throws SQLException
	{
		
		 PreparedStatement stmt = conn.prepareStatement(sql);
		 for(int i=0;i<parameters.length;i++)
		 {
			 stmt.setObject(i+1, parameters[i]);
		 }
		 return stmt.executeQuery();
		
	}
/*-------------------------------------------------------------------*/

	public static ResultSet executeQuery(String sql,Object...parameters) throws SQLException
	{
		Connection conn = creatConnection();
		return executeQuery(conn, sql, parameters);
		
	}
	
	
	
	
/*------------------------------------------------------------------------------------------------------*/	
	//各种关闭方法
	
	//1.关闭ResultSet
	public static void closeResultSet(ResultSet rs)
	{
		try
		{
			rs.close();
		} catch (SQLException e)
		{
			
		}
	}
	
	//2.关闭PreparedStatement
	public static void closeStatement(Statement stmt)
	{
		if(stmt!=null)
		{
			try
			{
				stmt.close();
			} catch (SQLException e)
			{
				
			}
		}
	}
	
	//3.关闭Connection
	public static void closeConnection(Connection conn)
	{
		if(conn!=null)
		{
			try
			{
				conn.close();
			} catch (SQLException e)
			{
				
			}
		}
	}
	
	//4.通过ResultSet来关闭statement
	public static void closeResultSetAndStatement(ResultSet rs)
	{
		closeResultSet(rs);
		try
		{
			closeStatement(rs.getStatement());
		} catch (SQLException e)
		{
			
		}
	}
	
	//5.通过关闭ResultSet来关闭所有
	public static void closeAll(ResultSet rs)
	{
		Statement stmt = null;
		Connection conn = null;
		try
		{
			stmt = rs.getStatement();
			conn = stmt.getConnection();
			
		}
		catch(SQLException e)
		{
			
		}
		finally
		{
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	//关闭IO
	public static void closeMethod(Closeable closeable)
	{
		if(closeable!=null)
		{
			try
			{
				closeable.close();
			}
			catch(IOException e)
			{
				//
			}
		}
	}
}
