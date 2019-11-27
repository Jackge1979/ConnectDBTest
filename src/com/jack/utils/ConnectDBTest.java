package com.jack.utils;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ConnectDBTest {

	static Connection connection = null;
	static String jdbcUrl = "";
	static String dbUser = "";
	static String dbPwd = "";
	static String driverClass = "";
	static String tableName = "";
	static String fieldName = "";

	public static void main(String[] args) throws Exception {
		System.out.println("\t\t数据库连接测试工具 v1.0 (author:Jack)");
		System.out.println("\t------------------------------------------------");
		System.out.println("\t|  支持的数据库类型：\t\t\t\t|");
		System.out.println("\t|\tMySQL、Oracle、PostgreSQL\t\t|");
		System.out.println("\t|\tDB2、edb、Teradata、DM\t\t\t|");
		System.out.println("\t|\tGBase、Kingbase、EsgynDB\t\t|");
		System.out.println("\t|\tTrafodion、UltraDB\t\t\t|");
		System.out.println("\t------------------------------------------------");
		System.out.println("请输入 JDBC 连接字符串并回车，\n例如：jdbc:oracle:thin:@127.0.0.1:1521/orcl：");
		Scanner scanner = new Scanner(System.in);
		jdbcUrl = scanner.nextLine();
		System.out.println("请输入数据库用户名并回车：");
		dbUser = scanner.nextLine();
		System.out.println("请输入数据库密码后回车：");
		dbPwd = scanner.nextLine();
		System.out.println("是否需要检测表的字段编码？请输入“Y/N”：");
		if("Y".equalsIgnoreCase(scanner.nextLine())) {
			System.out.println("请输入要检测的表名并回车：");
			tableName = scanner.nextLine();
			System.out.println("请输入要检测的（内容为中文）字段名并回车：");
			fieldName = scanner.nextLine();
		}
		
		scanner.close();
		System.out.print("正在连接数据库...\t\t");
		connect();
	}

	public static void connect() throws Exception {
		if (jdbcUrl.indexOf("jdbc:mysql") > -1) {
			try {
				driverClass = "com.mysql.cj.jdbc.Driver";
				Class.forName(driverClass);
			} catch (ClassNotFoundException ex) {
				driverClass = "com.mysql.jdbc.Driver";
				Class.forName(driverClass);
			}
		} else if (jdbcUrl.indexOf("jdbc:dm") > -1) {
			driverClass = "dm.jdbc.driver.DmDriver";
			Class.forName(driverClass);
		} else if (jdbcUrl.indexOf("jdbc:oracle") > -1) {
			driverClass = "oracle.jdbc.OracleDriver";
			Class.forName(driverClass);
		} else if (jdbcUrl.startsWith("jdbc:edb:")) {
			Class.forName("com.edb.Driver");
		} else if (jdbcUrl.startsWith("jdbc:teradata:")) {
			Class.forName("com.teradata.jdbc.TeraDriver");
		} else if (jdbcUrl.startsWith("jdbc:ultradb:")) {
			Class.forName("com.ultracloud.ultradb.jdbc.UltraDBDriver");
		} else if (jdbcUrl.startsWith("jdbc:gbase:")) {
			Class.forName("com.gbase.jdbc.Driver");
		} else if (jdbcUrl.startsWith("jdbc:postgresql:")) {
			Class.forName("org.postgresql.Driver");
		} else if (jdbcUrl.startsWith("jdbc:kingbase:")) {
			Class.forName("com.kingbase.Driver");
		} else if (jdbcUrl.startsWith("jdbc:db2:")) {
			Class.forName("com.ibm.db2.jdbc.net.DB2Driver");
		} else if (jdbcUrl.startsWith("jdbc:t4jdbc:")) {
			Class.forName("org.trafodion.jdbc.t4.T4Driver");
			Class.forName(driverClass);
		}
		connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPwd);
		if (connection != null) {
			System.out.println("数据库连接成功");
			if (tableName.length() > 0 && fieldName.length() > 0) {
				System.out.println("正在读取数据");
				select(tableName, fieldName);
			}
		} else {
			System.out.println("数据库连接失败");
		}
		if (connection != null)
			connection.close();
	}

	public static void select(String table,String fieldName) throws SQLException {

		String sql = "select "+fieldName+" from " + table;
		Statement stat = connection.createStatement();
		ResultSet rs = stat.executeQuery(sql);
		if (rs != null && rs.next()) {
			try {
				getEncod(rs.getString(1));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		stat.close();
	}

	public static void getEncod(String str) throws UnsupportedEncodingException {
		String sysencod = System.getProperty("file.encoding");
		System.out.println("系统默认编码：" + sysencod);
		System.out.println("字符实例：" + str);
		System.out.println("===============getbytes无参:===============");
		byte[] bytes = str.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(bytes[i] + " ");
		}
		System.out.println();
		System.out.println("字符实例转为操作系统默认编码：" + new String(bytes,sysencod));
		System.out.println("通过默认字符集，将字符数组解码为字符：" + new String(bytes));
		System.out.println("通过utf-8字符集，将字符数组解码为字符：" + new String(bytes, "utf-8"));
		System.out.println("通过gbk字符集，将字符数组解码为字符：" + new String(bytes, "gbk"));
		System.out.println("通过iso-8859-1字符集，将字符数组解码为字符：" + new String(bytes, "iso-8859-1"));

		System.out.println("===============getbytes(utf-8):===============");
		byte[] bytes2 = str.getBytes("utf-8");
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(bytes[i] + " ");
		}
		System.out.println();
		System.out.println("通过默认字符集，将字符数组解码为字符：" + new String(bytes2));
		System.out.println("通过utf-8字符集，将字符数组解码为字符：" + new String(bytes2, "utf-8"));
		System.out.println("通过gbk字符集，将字符数组解码为字符：" + new String(bytes2, "gbk"));
		System.out.println("通过iso-8859-1字符集，将字符数组解码为字符：" + new String(bytes2, "iso-8859-1"));
		System.out.println("===============getbytes(gbk):===============");
		byte[] bytes3 = str.getBytes("gbk");
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(bytes[i] + " ");
		}
		System.out.println();
		System.out.println("通过默认字符集，将字符数组解码为字符：" + new String(bytes3));
		System.out.println("通过utf-8字符集，将字符数组解码为字符：" + new String(bytes3, "utf-8"));
		System.out.println("通过gbk字符集，将字符数组解码为字符：" + new String(bytes3, "gbk"));
		System.out.println("通过iso-8859-1字符集，将字符数组解码为字符：" + new String(bytes3, "iso-8859-1"));
		System.out.println("===============getbytes(iso-8859-1):===============");
		byte[] bytes4 = str.getBytes("iso-8859-1");
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(bytes[i] + " ");
		}
		System.out.println();
		System.out.println("通过默认字符集，将字符数组解码为字符：" + new String(bytes4));
		System.out.println("通过utf-8字符集，将字符数组解码为字符：" + new String(bytes4, "utf-8"));
		System.out.println("通过gbk字符集，将字符数组解码为字符：" + new String(bytes4, "gbk"));
		System.out.println("通过iso-8859-1字符集，将字符数组解码为字符：" + new String(bytes4, "iso-8859-1"));
	}

	class Security {
		public void disopen() {

		}
	}
}
