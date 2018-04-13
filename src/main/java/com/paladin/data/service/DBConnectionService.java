package com.paladin.data.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;
import com.paladin.data.database.CommonDataBase;
import com.paladin.data.database.DataBaseConfig;
import com.paladin.data.database.DataBaseSource;
import com.paladin.data.database.DataBaseType;
import com.paladin.data.database.model.Column;
import com.paladin.data.database.model.DataBase;
import com.paladin.data.database.model.Table;
import com.paladin.data.generate.GenerateTableOption;
import com.paladin.data.generate.GenerateType;
import com.paladin.data.generate.GenerateUtil;
import com.paladin.data.generate.GenerateControllerClassUtil;
import com.paladin.data.generate.GenerateMapperClassUtil;
import com.paladin.data.generate.GenerateModelClassUtil;
import com.paladin.data.generate.GenerateServiceClassUtil;
import com.paladin.data.model.DBConnection;
import com.paladin.framework.core.ServiceSupport;
import com.paladin.framework.exception.BusinessException;

@Service
public class DBConnectionService extends ServiceSupport<DBConnection> {

	private Map<String, DataBaseSource> dataBaseSourceMap = new HashMap<>();

	/**
	 * 连接数据库
	 * 
	 * @param connection
	 */
	public void connect(DBConnection connection) {

		String name = connection.getName();

		DataBaseSource source = dataBaseSourceMap.get(name);
		if (source == null) {
			synchronized (dataBaseSourceMap) {
				source = dataBaseSourceMap.get(name);
				if (source == null) {
					DataBaseConfig config = new DataBaseConfig();
					config.setName(connection.getName());
					config.setPassword(connection.getPassword());
					config.setType(DataBaseType.valueOf(connection.getType()));
					config.setUrl(connection.getUrl());
					config.setUsername(connection.getUserName());

					try {
						source = createDataBaseSource(config);
					} catch (Exception e) {
						throw new BusinessException(e.getMessage());
					}
					dataBaseSourceMap.put(name, source);
				}
			}
		}
	}

	private DataBaseSource createDataBaseSource(DataBaseConfig config) {

		return new CommonDataBase(config) {

			@Override
			protected DataSource createRealDataSource() {
				DataBaseType type = config.getType();

				if (type == null)
					throw new NullPointerException("Database Type Can't Be Null");

				DruidDataSource dataSource = new DruidDataSource();
				dataSource.setUrl(config.getUrl());
				dataSource.setPassword(config.getPassword());
				dataSource.setUsername(config.getUsername());
				dataSource.setName(config.getName());
				dataSource.setMaxWait(10000);

				return dataSource;
			}

			@Override
			protected boolean initialize() {
				try {
					((DruidDataSource) realDataSource).init();
				} catch (SQLException e) {
					throw new RuntimeException("数据源初始化异常", e);
				}
				return true;
			}

			@Override
			protected boolean destroy() {
				((DruidDataSource) realDataSource).close();
				return true;
			}
		};
	}

	public DataBaseSource getDataBaseSource(String dbName) {
		return dataBaseSourceMap.get(dbName);
	}

	public DataBase getDataBase(String dbName, boolean refresh) {
		DataBaseSource source = dataBaseSourceMap.get(dbName);

		if (source == null) {
			throw new BusinessException("不存在数据库：" + dbName);
		}

		DataBase database = source.getDataBase(refresh);
		return database;
	}

	public Table[] getDBTables(String dbName) {
		DataBase database = getDataBase(dbName, true);
		return database.getChildren();
	}

	public Column[] getDBTableColumns(String dbName, String tableName) {
		DataBase database = getDataBase(dbName, false);
		Table table = database.getChild(tableName);
		if (table == null) {
			throw new BusinessException("不存在表：" + tableName);
		}
		return table.getChildren();
	}

	/**
	 * 创建java class内容
	 * 
	 * @param tableOption
	 * @param generateType
	 * @return
	 */
	public String buildJavaClass(GenerateTableOption tableOption, GenerateType generateType) {

		if (generateType == GenerateType.MAPPER) {
			return GenerateMapperClassUtil.createClassContent(tableOption);
		} else if (generateType == GenerateType.MODEL) {
			return GenerateModelClassUtil.createClassContent(tableOption);
		} else if (generateType == GenerateType.SERVICE) {
			return GenerateServiceClassUtil.createClassContent(tableOption);
		} else if (generateType == GenerateType.CONTROLLER) {
			return GenerateControllerClassUtil.createClassContent(tableOption);
		}

		return null;
	}

	/**
	 * 创建spring boot项目文件
	 * 
	 * @param tableOption
	 * @param generateType
	 * @param content
	 */
	public void buildBootProjectFile(GenerateTableOption tableOption, GenerateType generateType, String projectPath, String content) {
		
		Path path = null;
		
		if (generateType == GenerateType.MAPPER || generateType == GenerateType.MODEL || generateType == GenerateType.SERVICE
				|| generateType == GenerateType.CONTROLLER) {
			// java class
			try {
				path = GenerateUtil.getBootProjectJavaPath(projectPath, tableOption, generateType);
			} catch (IOException e) {
				e.printStackTrace();
				throw new BusinessException("创建文件[" + generateType + "]失败");
			}
		} else {
			
			try {
				path = GenerateUtil.getBootProjectResourcesPath(projectPath, tableOption, generateType);
			} catch (IOException e) {
				e.printStackTrace();
				throw new BusinessException("创建文件[" + generateType + "]失败");
			}
		}
		
		try {
			Files.write(path,content.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException("创建文件[" + generateType + "]失败");
		}
		
	}

}
