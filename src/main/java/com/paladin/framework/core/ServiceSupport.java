package com.paladin.framework.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paladin.framework.common.BaseModel;
import com.paladin.framework.common.CommonCondition;
import com.paladin.framework.common.CommonConditions;
import com.paladin.framework.common.GeneralCriteriaBuilder;
import com.paladin.framework.common.OffsetPage;
import com.paladin.framework.common.OrderType;
import com.paladin.framework.common.QueryOrderBy;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.common.UnDelete;
import com.paladin.framework.common.GeneralCriteriaBuilder.Condition;
import com.paladin.framework.mybatis.CustomMapper;
import com.paladin.framework.utils.ParseUtil;
import com.paladin.framework.utils.reflect.ReflectUtil;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.OrderBy;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

/**
 * <h2>业务支持类</h2>
 * <p>
 * 提供一些简单业务方法
 * </p>
 * <p>
 * 通过{@link com.netmatch.core.conatiner.ServiceSupportConatiner}自动注册sqlMapper
 * </p>
 * 
 * @author TontoZhou
 *
 * @param <Model>
 *            实体类
 */
public abstract class ServiceSupport<Model> {

	private final static Logger logger = LoggerFactory.getLogger(ServiceSupport.class);

	private Class<Model> modelType; // 业务对应类

	@SuppressWarnings("unchecked")
	public ServiceSupport() {
		Class<?> clazz = ReflectUtil.getSuperClassArgument(this.getClass(), ServiceSupport.class, 0);
		if (clazz == null) {
			logger.warn("[" + this.getClass().getName() + "]的实现类没有明确定义[" + ServiceSupport.class.getName() + "]的泛型，无法为其注册SqlMapper");
		}
		modelType = (Class<Model>) clazz;
	}

	// -------------------------- 初始化配置 --------------------------

	List<Condition> commonConditions = new ArrayList<>(); // 通用查询条件
	Example commonExample;
	boolean hasCommonCondition = false; // 是否有通用查询条件

	boolean isUnDelete = false; // 是否不删除，进行逻辑删除
	boolean isBaseModel = false; // 是否基于基础模型

	/*
	 * 动态Example
	 */
	boolean hasDynamicCondition = false;

	/*
	 * 排序
	 */
	String[] orderByProperties;
	OrderType[] orderByTypes;
	boolean hasOrderBy = false;
	boolean isAllwaysOrderFirst = false;

	/**
	 * 初始化
	 */
	public void init() {

		if (BaseModel.class.isAssignableFrom(modelType)) {
			isBaseModel = true;
		}

		// 是否逻辑删除数据模型
		if (UnDelete.class.isAssignableFrom(modelType)) {
			isUnDelete = true;
		}

		// 通用查询条件

		Class<?> type = this.getClass();
		CommonCondition commonCondtion = type.getAnnotation(CommonCondition.class);
		CommonConditions commonCondtions = type.getAnnotation(CommonConditions.class);

		if (commonCondtion != null) {
			commonConditions.add(createCondition(commonCondtion));
		}

		if (commonCondtions != null) {
			for (CommonCondition cc : commonCondtions.value()) {
				commonConditions.add(createCondition(cc));
			}
		}

		// 如果是逻辑删除模型，则所有查询中需要过滤删除数据
		if (isUnDelete) {
			commonConditions.add(new Condition(UnDelete.COLUMN_FIELD_IS_DELETE, QueryType.EQUAL, 0));
		}

		if (commonConditions.size() > 0) {
			hasCommonCondition = true;
			commonExample = new Example(modelType);
			GeneralCriteriaBuilder.buildAnd(commonExample, commonConditions);
		}

		/*
		 * 初始化通用排序
		 */
		QueryOrderBy orderBy = type.getAnnotation(QueryOrderBy.class);
		if (orderBy != null) {

			isAllwaysOrderFirst = orderBy.orderByFirst();

			int size = Math.min(orderBy.property().length, orderBy.type().length);
			if (size > 0) {

				orderByProperties = new String[size];
				orderByTypes = new OrderType[size];

				for (int i = 0; i < size; i++) {
					orderByProperties[i] = orderBy.property()[i];
					orderByTypes[i] = orderBy.type()[i];
				}

				hasOrderBy = true;
			}
		}

		/*
		 * 如果没有排序规则，但是是基础对象则默认倒叙创建时间
		 */
		if (!hasOrderBy && isBaseModel) {
			orderByProperties = new String[] { BaseModel.COLUMN_FIELD_CREATE_TIME };
			orderByTypes = new OrderType[] { OrderType.DESC };
			hasOrderBy = true;
		}

		if (hasCommonCondition) {
			buildOrderBy(commonExample);
		}

	}

	/**
	 * 创建通用查询条件
	 * 
	 * @param commonCondtion
	 * @return
	 */
	private Condition createCondition(CommonCondition commonCondtion) {

		String v1 = commonCondtion.value();
		Class<?> type = commonCondtion.valueType();
		String name = commonCondtion.name();
		QueryType queryType = commonCondtion.type();

		Object o1 = null;

		if (!"".equals(v1)) {
			// IN 查询，value值应该为逗号分隔的字符串，例如 1,2,3,4
			if (queryType == QueryType.IN) {
				String[] vs = v1.split(",");
				ArrayList<Object> list = new ArrayList<>(vs.length);
				for (String v : vs) {
					list.add(ParseUtil.parseString(v, type));
				}
				o1 = list;
			} else {
				o1 = ParseUtil.parseString(v1, type);
			}
		}

		return new Condition(name, queryType, o1);
	}

	private Method[] pkGetMethods; // 主键对应get方法
	private Object lock = new Object();

	/**
	 * 判断是否存在主键
	 * 
	 * @param model
	 * @return
	 */
	private boolean judgeHasPKValue(Model model) {

		if (model == null)
			return false;

		Class<?> type = model.getClass();

		if (pkGetMethods == null) {
			synchronized (lock) {
				if (pkGetMethods == null) {
					// 判断是否存在主键
					Set<EntityColumn> pkColumns = EntityHelper.getEntityTable(modelType).getEntityClassPKColumns();
					if (pkColumns == null || pkColumns.size() == 0) {
						pkGetMethods = new Method[0];
					} else {
						pkGetMethods = new Method[pkColumns.size()];
						int i = 0;
						for (EntityColumn column : pkColumns) {
							String property = column.getProperty();
							pkGetMethods[i++] = ReflectUtil.getGetMethod(property, type);
						}
					}
				}
			}
		}

		if (pkGetMethods.length == 0)
			return false;

		for (Method method : pkGetMethods) {
			try {
				Object value = method.invoke(model);
				if (value == null)
					return false;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

			}
		}

		return true;
	}

	// -----------------------------------------------------
	// 通用业务方法
	// -----------------------------------------------------

	private CustomMapper<Model> cusomMapper;

	public CustomMapper<Model> getSqlMapper() {
		return cusomMapper;
	}

	public void setSqlMapper(CustomMapper<Model> cusomMapper) {
		this.cusomMapper = cusomMapper;
	}

	public Model get(Object pk) {
		return getSqlMapper().selectByPrimaryKey(pk);
	}

	// -----------------------------------------------------
	// 查询，包含权限判断
	// -----------------------------------------------------

	/**
	 * 获取一个干净的当前Example
	 * 
	 * @return
	 */
	private Example getClearCurrentExample() {
		return GeneralCriteriaBuilder.getClearCurrentExample(modelType);
	}

	/**
	 * 构建排序
	 * 
	 * @param example
	 */
	private Example buildOrderBy(Example example) {

		if (hasOrderBy) {

			if (example == null) {
				example = getClearCurrentExample();
			}

			String orderByClause = null;
			boolean hasFirst = false;

			if (isAllwaysOrderFirst) {
				orderByClause = example.getOrderByClause();
				if (orderByClause != null && orderByClause.length() > 0) {
					example.setOrderByClause("");
					hasFirst = true;
				}
			}

			for (int i = 0; i < orderByProperties.length; i++) {
				OrderBy orderBy = example.orderBy(orderByProperties[i]);
				if (orderByTypes[i] == OrderType.DESC) {
					orderBy.desc();
				} else {
					orderBy.asc();
				}
			}

			if (hasFirst) {
				example.setOrderByClause(orderByClause + "," + example.getOrderByClause());
			}
		}

		return example;
	}

	/**
	 * 构建动态查询条件
	 * 
	 * @param example
	 * @return
	 */
	private Example buildDynamicCondition(Example example) {
		return example;
	}

	/**
	 * 查找所有结果
	 * 
	 * @return
	 */
	public List<Model> findAll() {
		return findAll(false);
	}

	/**
	 * 查找所有结果
	 * 
	 * @param simple
	 *            是否简单模式
	 * @return
	 */
	public List<Model> findAll(boolean simple) {

		if (simple) {
			return getSqlMapper().selectAll();
		}

		Example example = null;

		if (hasCommonCondition) {
			if (hasDynamicCondition) {
				example = GeneralCriteriaBuilder.buildAnd(modelType, commonConditions);
				example = buildDynamicCondition(example);
			} else {
				// commonExample 已经排序过
				return getSqlMapper().selectByExample(commonExample);
			}
		} else if (hasDynamicCondition) {
			example = buildDynamicCondition(null);
		}

		example = buildOrderBy(example);

		if (example == null) {
			return getSqlMapper().selectAll();
		} else {
			return getSqlMapper().selectByExample(example);
		}
	}

	/**
	 * 分页查找
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	public Page<Model> findPage(int offset, int limit) {
		return findPage(offset, limit, false);
	}

	/**
	 * 分页查找
	 * 
	 * @param offset
	 * @param limit
	 * @param simple
	 *            是否简单模式
	 * @return
	 */
	public Page<Model> findPage(int offset, int limit, boolean simple) {

		Page<Model> page = PageHelper.offsetPage(offset, limit);
		try {
			List<Model> result = findAll(simple);
			if (result == null || result.size() == 0) {
				page.setTotal(0L);
			}
			return page;
		} finally {
			PageHelper.clearPage();
		}
	}

	/**
	 * 条件过滤查询(非简单模式)
	 * 
	 * @param searchParam
	 * @return
	 */
	public List<Model> searchAll(Object searchParam) {
		return searchAll(searchParam, false);
	}

	/**
	 * 条件过滤查询（简单模式下将不会考虑通用条件和动态条件）
	 * 
	 * @param searchParam
	 *            <ul>
	 *            <li>{@link Condition}单个条件或者它的数组和集合</li>
	 *            <li>基于注解和{@link GeneralCriteriaBuilder}的查询类实例</li>
	 *            </ul>
	 * @param simple
	 *            是否简单模式
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Model> searchAll(Object searchParam, boolean simple) {

		Example example = null;

		if (searchParam instanceof Example) {

			example = (Example) searchParam;

		} else if (searchParam instanceof Condition) {

			Condition condition = (Condition) searchParam;
			example = GeneralCriteriaBuilder.buildAnd(modelType, condition);

		} else {

			Class<?> clazz = searchParam.getClass();

			if (List.class.isAssignableFrom(clazz)) {
				List<Condition> list = (List<Condition>) searchParam;
				/*
				 * 对于list，array暂时只处理Condition情况，对于其他多条件传入日后补充
				 */
				example = GeneralCriteriaBuilder.buildAnd(modelType, list);
			} else if (clazz.isArray()) {
				Condition[] array = (Condition[]) searchParam;
				example = GeneralCriteriaBuilder.buildAnd(modelType, Arrays.asList(array));
			} else {
				example = GeneralCriteriaBuilder.buildQuery(modelType, searchParam);
			}
		}

		// 如果是简单模式，则不考虑通用条件和动态条件
		if (!simple) {

			if (hasCommonCondition) {

				if (example != null) {
					example = GeneralCriteriaBuilder.buildAnd(example, commonConditions);
				}

				if (hasDynamicCondition) {
					example = buildDynamicCondition(example);
				} else {
					if (example == null) {
						return getSqlMapper().selectByExample(commonExample);
					}
				}
			} else if (hasDynamicCondition) {
				example = buildDynamicCondition(example);
			}

			example = buildOrderBy(example);
		}

		if (example == null) {
			return getSqlMapper().selectAll();
		} else {
			return getSqlMapper().selectByExample(example);
		}
	}

	/**
	 * 条件过滤分页查询（非简单模式）
	 * 
	 * @param searchParam
	 * @return
	 */
	public Page<Model> searchPage(Object searchParam) {
		return searchPage(searchParam, false);
	}

	/**
	 * 条件过滤分页查询（简单模式下将不会考虑通用条件和动态条件）
	 * 
	 * @param searchParam
	 * @param simple
	 *            是否简单模式
	 * @return
	 */
	public Page<Model> searchPage(Object searchParam, boolean simple) {
		if (searchParam == null)
			return searchPage(searchParam, 0, OffsetPage.DEFAULT_LIMIT, simple);

		if (searchParam instanceof OffsetPage) {
			OffsetPage page = (OffsetPage) searchParam;
			return searchPage(searchParam, page.getOffset(), page.getLimit(), simple);
		} else {
			return searchPage(searchParam, 0, OffsetPage.DEFAULT_LIMIT, simple);
		}
	}

	/**
	 * 条件过滤分页查询（非简单模式）
	 * 
	 * @param searchParam
	 * @param offset
	 * @param limit
	 * @return
	 */
	public Page<Model> searchPage(Object searchParam, int offset, int limit) {
		return searchPage(searchParam, offset, limit, false);
	}

	/**
	 * 条件过滤分页查询（简单模式下将不会考虑通用条件和动态条件）
	 * 
	 * @param searchParam
	 * @param offset
	 * @param limit
	 * @param simple
	 *            是否简单模式
	 * @return
	 */
	public Page<Model> searchPage(Object searchParam, int offset, int limit, boolean simple) {
		Page<Model> page = PageHelper.offsetPage(offset, limit);
		try {
			List<Model> result = searchAll(searchParam, simple);
			if (result == null || result.size() == 0) {
				page.setTotal(0L);
			}
			return page;
		} finally {
			PageHelper.clearPage();
		}
	}

	// -----------------------------------------------------
	// 修改保存删除
	// -----------------------------------------------------

	public int save(Model model) {
		saveModelWrap(model);
		return getSqlMapper().insert(model);
	}

	public int saveOrUpdate(Model model) {
		if (judgeHasPKValue(model)) {
			return updateSelective(model);
		} else {
			return save(model);
		}
	}

	public int updateSelective(Model model) {
		updateModelWrap(model);
		return getSqlMapper().updateByPrimaryKeySelective(model);
	}

	public int removeByPrimaryKey(Object pk) {
		if (isUnDelete) {
			Model model = getSqlMapper().selectByPrimaryKey(pk);
			if (model != null) {
				((UnDelete) model).setIsDelete(1);
				updateModelWrap(model);
				return getSqlMapper().updateByPrimaryKey(model);
			}
			return 0;
		}

		return getSqlMapper().deleteByPrimaryKey(pk);
	}

	/**
	 * 更新操作前需要对数据包裹，例如设置更新操作人与操作时间
	 * 
	 * @param model
	 */
	public void updateModelWrap(Model model) {
		if (model instanceof BaseModel) {
			Date now = new Date();
			UserSession userSession = UserSession.getCurrentUserSession();
			String uid = userSession == null ? "" : userSession.getUserId();
			BaseModel baseModel = (BaseModel) model;
			baseModel.setUpdateTime(now);
			baseModel.setUpdateUserId(uid);
		}
	}

	/**
	 * 保存操作前需要对数据包裹，例如设置创建操作人与操作时间
	 * 
	 * @param model
	 */
	public void saveModelWrap(Model model) {
		if (model instanceof BaseModel) {
			Date now = new Date();
			UserSession userSession = UserSession.getCurrentUserSession();
			String uid = userSession == null ? "" : userSession.getUserId();
			BaseModel baseModel = (BaseModel) model;
			baseModel.setCreateTime(now);
			baseModel.setCreateUserId(uid);
			baseModel.setUpdateTime(now);
			baseModel.setUpdateUserId(uid);
		}
	}

	// -----------------------------------------------------
	//
	// -----------------------------------------------------

	@SuppressWarnings("rawtypes")
	public Page getEmptyPage(OffsetPage offsetPage) {
		Page page = new Page(offsetPage.getOffset(), offsetPage.getLimit());
		page.setTotal(0L);
		return page;
	}

}
