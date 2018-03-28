package com.paladin.framework.mybatis;

import static tk.mybatis.mapper.util.MsUtil.getMapperClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import javax.persistence.Column;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import com.paladin.framework.utils.reflect.ReflectUtil;

import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;

/**
 * 
 * 连接查询实现类
 * <p>暂时只实现了两表连接，如果要多表，或常量表连接等实现，待日后扩展</p>
 * 
 * <p>修改思路：按照EntityColumn，EntityTable思路构造字段-列关系类</p>
 * 
 * @author TontoZhou
 * @since 2018年3月23日
 */
public class JoinSelectProvider extends MapperTemplate {

	public JoinSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
		super(mapperClass, mapperHelper);
	}

	public String selectJoinAll(MappedStatement ms) {

		final Class<?> entityClass = getEntityClass(ms);

		JoinConfig joinConfig = getJoinConfig(entityClass);

		EntityExtendTable extendTable = getEntityExtendTable(joinConfig, entityClass);

		// 修改返回值类型为实体类型
		setResultType(ms, entityClass, extendTable);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		
		for(EntityColumnWrap columnWrap : extendTable.baseColumns)
		{
			sql.append("t.").append(columnWrap.entityColumn.getColumn()).append(",");
		}
		
		for(EntityColumnWrap columnWrap : extendTable.joinColumns)
		{
			sql.append("j.").append(columnWrap.entityColumn.getColumn()).append(",");
		}
		
		sql.deleteCharAt(sql.length() - 1);
		
		sql.append(" FROM ")
			.append(extendTable.baseTable).append(" t, ")
			.append(extendTable.joinTable).append(" j");
		
		
		EntityTable joinEntityTable = EntityHelper.getEntityTable(joinConfig.joinType);
		
		Set<EntityColumn> keyColumns = joinEntityTable.getEntityClassPKColumns();
		if(keyColumns.isEmpty()) {
			throw new RuntimeException("暂时不支持多列主键");
		}
		
		sql.append(" WHERE j.").append(keyColumns.iterator().next().getColumn()).append("=t.").append(extendTable.joinColumnName);	
		sql.append(SqlHelper.orderByDefault(joinConfig.baseType));
		return sql.toString();
	}

	/**
	 * 获取连接列
	 * 
	 * @param joinType
	 * @param entityType
	 * @return
	 */
	private EntityExtendTable getEntityExtendTable(JoinConfig joinConfig, Class<?> entityType) {

		Class<?> joinType = joinConfig.joinType;
		Class<?> baseType = joinConfig.baseType;
		
		EntityExtendTable entityExtendTable = new EntityExtendTable();
		
		Set<EntityColumnWrap> joinEntityColumns = new HashSet<>();
		Set<EntityColumnWrap> baseEntityColumns = new HashSet<>();

		EntityHelper.initEntityNameMap(joinType, mapperHelper.getConfig());
		EntityHelper.initEntityNameMap(baseType, mapperHelper.getConfig());

		Set<EntityColumn> joinColumnList = EntityHelper.getColumns(joinType);
		Set<EntityColumn> baseColumnList = EntityHelper.getColumns(baseType);

		HashMap<String, EntityColumn> joinColumnMap = new HashMap<>();
		HashMap<String, EntityColumn> baseColumnMap = new HashMap<>();

		for (EntityColumn column : joinColumnList) {
			joinColumnMap.put(column.getColumn(), column);
		}

		for (EntityColumn column : baseColumnList) {
			baseColumnMap.put(column.getColumn(), column);
		}

		Style style = mapperHelper.getConfig().getStyle();

		String joinTable = StringUtil.convertByStyle(joinType.getSimpleName(), style);
		String baseTable = StringUtil.convertByStyle(baseType.getSimpleName(), style);

		boolean isExtends = baseType.isAssignableFrom(entityType);

		while (true) {

			if (isExtends && entityType == baseType) {
				// 如果实体类继承基础表实体类，则除基础表实体类外的字段默认都是连接表（大多数情况应该如此）
				break;
			}

			if (entityType == Object.class) {
				break;
			}

			Field[] fields = entityType.getDeclaredFields();
			for (Field field : fields) {

				String joinColumnName = null;
				String baseColumnName = null;

				Column column = field.getAnnotation(Column.class);

				if (column == null) {
					if (isExtends) {
						joinColumnName = StringUtil.convertByStyle(field.getName(), style);
					} else {
						throw new RuntimeException("对于不是继承基础表类进行连接扩展的类中字段必须标明@Column，并标明table()");
					}
				} else {

					if (isExtends) {
						String table = column.table();
						if ("".equals(table) || joinTable.equals(table)) {
							joinColumnName = column.name();
							if ("".equals(joinColumnName)) {
								joinColumnName = StringUtil.convertByStyle(field.getName(), style);
							}
						} else if (baseTable.equals(table)) {
							// 不处理
						} else {
							throw new RuntimeException("不存在表[" + table + "]，该table()值应该为[" + joinTable + "]或者[" + baseTable + "]");
						}

					} else {

						String table = column.table();

						String columnName = column.name();
						if ("".equals(columnName)) {
							columnName = StringUtil.convertByStyle(field.getName(), style);
						}

						if (joinTable.equals(table)) {
							joinColumnName = columnName;
						} else if (baseTable.equals(table)) {
							baseColumnName = columnName;
						} else {
							throw new RuntimeException("不存在表[" + table + "]，该table()值应该为[" + joinTable + "]或者[" + baseTable + "]");
						}
					}
				}

				if (joinColumnName != null) {
					EntityColumn entityColumn = joinColumnMap.get(joinColumnName);
					if (entityColumn != null) {
						joinEntityColumns.add(new EntityColumnWrap(field.getName(), entityColumn));
					}
				}

				if (baseColumnName != null) {
					EntityColumn entityColumn = baseColumnMap.get(baseColumnName);
					if (entityColumn != null) {
						baseEntityColumns.add(new EntityColumnWrap(field.getName(), entityColumn));
					}
				}

				entityType = entityType.getSuperclass();
			}

			if (isExtends) {
				for (EntityColumn column : baseColumnList) {
					baseEntityColumns.add(new EntityColumnWrap(column));
				}
			}
			
			entityExtendTable.baseColumns = baseEntityColumns;
			entityExtendTable.joinColumns = joinEntityColumns;
			
			entityExtendTable.joinTable = joinTable;
			entityExtendTable.baseTable = baseTable;
			
			entityExtendTable.joinColumnName = StringUtil.convertByStyle(joinConfig.joinField, style);
		}

		return entityExtendTable;
	}

	/**
	 * 基于{@link EntityTable}
	 * @param ms
	 * @param entityClass
	 * @param extendTable
	 */
	protected void setResultType(MappedStatement ms, Class<?> entityClass, EntityExtendTable extendTable) {
		
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        Configuration configuration= ms.getConfiguration();
        
        
        List<EntityColumnWrap> entityClassColumns = new ArrayList<>(extendTable.baseColumns);
        entityClassColumns.addAll(extendTable.joinColumns);
               
        List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
        for (EntityColumnWrap entityColumnWrap : entityClassColumns) {
        	EntityColumn entityColumn = entityColumnWrap.entityColumn;       	
            String column = entityColumn.getColumn();
            //去掉可能存在的分隔符
            Matcher matcher = EntityTable.DELIMITER.matcher(column);
            if(matcher.find()){
                column = matcher.group(1);
            }
            ResultMapping.Builder builder = new ResultMapping.Builder(configuration, entityColumnWrap.property, column, entityColumn.getJavaType());
            if (entityColumn.getJdbcType() != null) {
                builder.jdbcType(entityColumn.getJdbcType());
            }
            if (entityColumn.getTypeHandler() != null) {
                try {
                    builder.typeHandler(getInstance(entityColumn.getJavaType(),entityColumn.getTypeHandler()));
                } catch (Exception e) {
                    throw new MapperException(e);
                }
            }
            List<ResultFlag> flags = new ArrayList<ResultFlag>();
            if (entityColumn.isId()) {
                flags.add(ResultFlag.ID);
            }
            builder.flags(flags);
            resultMappings.add(builder.build());
        }
        
        ResultMap.Builder builder = new ResultMap.Builder(configuration, "BaseMapperResultMap", entityClass, resultMappings, true);        
        resultMaps.add(builder.build()) ;  
        
        MetaObject metaObject = SystemMetaObject.forObject(ms);
        metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps));
    }
	
	/**
     * 实例化TypeHandler
     * @param javaTypeClass
     * @param typeHandlerClass
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
          try {
            Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
            return (TypeHandler<T>) c.newInstance(javaTypeClass);
          } catch (NoSuchMethodException ignored) {
            // ignored
          } catch (Exception e) {
            throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
          }
        }
        try {
          Constructor<?> c = typeHandlerClass.getConstructor();
          return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
          throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
      }
	
	/**
	 * 重写获取实体类型方法，因为是连接查询表，不存在与实体类对应的表，所以不初始化EntityTable
	 */
	public Class<?> getEntityClass(MappedStatement ms) {
		String msId = ms.getId();
		if (entityClassMap.containsKey(msId)) {
			return entityClassMap.get(msId);
		} else {
			Class<?> mapperClass = getMapperClass(msId);
			Class<?> joinEntityType = ReflectUtil.getSuperClassArgument(mapperClass, JoinMapper.class, 0);
			entityClassMap.put(msId, joinEntityType);
			return joinEntityType;
		}
	}

	/**
	 * 获取连接配置
	 * 
	 * @param entityClass
	 * @return
	 */
	private JoinConfig getJoinConfig(Class<?> entityClass) {

		JoinConfig config = joinConfigMap.get(entityClass);
		if (config == null) {
			synchronized (JoinSelectProvider.class) {
				config = joinConfigMap.get(entityClass);
				if (config == null) {

					SimpleJoin simpleJoin = entityClass.getAnnotation(SimpleJoin.class);
					if (simpleJoin == null) {
						throw new RuntimeException("连接查询实体类必须有SimpleJoin注解进行相关配置");
					}

					config = new JoinConfig();
					config.baseType = simpleJoin.baseType();
					config.joinType = simpleJoin.joinType();
					config.joinField = simpleJoin.joinField();

					joinConfigMap.put(entityClass, config);
				}
			}
		}

		return config;
	}

	private static HashMap<Class<?>, JoinConfig> joinConfigMap = new HashMap<>();

	private static class JoinConfig {

		Class<?> baseType;
		Class<?> joinType;

		String joinField;

	}

	private static class EntityColumnWrap {

		EntityColumn entityColumn;

		@SuppressWarnings("unused")
		String property;

		EntityColumnWrap(String property, EntityColumn entityColumn) {
			this.property = property;
			this.entityColumn = entityColumn;
		}

		EntityColumnWrap(EntityColumn entityColumn) {
			this.property = entityColumn.getProperty();
			this.entityColumn = entityColumn;
		}
	}

	private static class EntityExtendTable {
		
		Set<EntityColumnWrap> joinColumns;
		Set<EntityColumnWrap> baseColumns;
		
		String joinTable;
		String baseTable;
		
		String joinColumnName;
	}
	
}
