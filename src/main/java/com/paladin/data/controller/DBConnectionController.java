package com.paladin.data.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paladin.common.service.cache.SysVisitCacheService;
import com.paladin.data.controller.dto.ColumnDTO;
import com.paladin.data.controller.dto.GenerateTableOptionDTO;
import com.paladin.data.database.DataBaseSource;
import com.paladin.data.database.model.Column;
import com.paladin.data.database.model.DataBase;
import com.paladin.data.database.model.Table;
import com.paladin.data.generate.GenerateTableOption;
import com.paladin.data.generate.GenerateType;
import com.paladin.data.model.DBConnection;
import com.paladin.data.service.DBConnectionService;
import com.paladin.framework.common.OffsetPage;
import com.paladin.framework.common.PageResult;
import com.paladin.framework.core.ControllerSupport;
import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.utils.convert.BeanCopyUtil;
import com.paladin.framework.web.response.CommonResponse;

@Controller
@RequestMapping("/data/connection")
public class DBConnectionController extends ControllerSupport {

	@Autowired
	DBConnectionService connectionService;

	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request) {
		return "/data/connection/index";
	}

	@RequestMapping("/search")
	@ResponseBody
	public Object searchAll(OffsetPage query) {
		return CommonResponse.getSuccessResponse(new PageResult(connectionService.searchPage(query)));
	}

	@RequestMapping("/view")
	public String view(@RequestParam String name, Model model) {
		DBConnection connection = connectionService.get(name);

		if (connection == null) {
			connection = new DBConnection();
		}

		model.addAttribute("connection", connection);
		return "data/connection/view";
	}

	@RequestMapping("/add/input")
	public String addInput(Model model) {
		model.addAttribute("connection", new DBConnection());
		return "data/connection/form";
	}

	@RequestMapping("/edit/input")
	public String editInput(@RequestParam String name, Model model) {
		DBConnection connection = connectionService.get(name);

		if (connection == null) {
			connection = new DBConnection();
		}

		model.addAttribute("connection", connection);
		return "data/connection/form";
	}

	@RequestMapping("/save")
	@ResponseBody
	public Object save(@Valid DBConnection connection, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return validErrorHandler(bindingResult);
		}
		return CommonResponse.getResponse(connectionService.saveOrUpdate(connection));
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Object delete(@RequestParam String name) {
		return CommonResponse.getResponse(connectionService.removeByPrimaryKey(name), "删除失败");
	}

	@RequestMapping("/connect")
	public String connect(DBConnection connection, Model model) {
		connectionService.connect(connection);
		try {
			return "redirect:/data/connection/db/index?dbName=" + URLEncoder.encode(connection.getName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException("非法数据库名称：" + connection.getName());
		}
	}

	@RequestMapping("/db/index")
	public String dbIndex(@RequestParam String dbName, Model model) {
		model.addAttribute("dbName", dbName);
		return "data/connection/db_index";
	}

	@RequestMapping("/db/table")
	@ResponseBody
	public Object tableList(@RequestParam String dbName) {

		Table[] tables = connectionService.getDBTables(dbName);
		String[] tableNames = new String[tables.length];
		for (int i = 0; i < tables.length; i++) {
			tableNames[i] = tables[i].getName();
		}

		return CommonResponse.getSuccessResponse(tableNames);
	}

	@RequestMapping("/db/column")
	@ResponseBody
	public Object tableList(@RequestParam String dbName, @RequestParam String tableName) {

		Column[] columns = connectionService.getDBTableColumns(dbName, tableName);
		List<ColumnDTO> columnVOs = BeanCopyUtil.simpleCopyList(Arrays.asList(columns), ColumnDTO.class);

		return CommonResponse.getSuccessResponse(columnVOs);
	}

	@RequestMapping("/db/build/input")
	public Object toBuild(@RequestParam String dbName, @RequestParam String tableName, HttpServletRequest request, Model model) {

		model.addAttribute("dbName", dbName);
		model.addAttribute("tableName", tableName);
		model.addAttribute("projectPath", visitCacheService.getCache(request, CACHE_PROJECT_PATH));

		return "data/connection/build";
	}

	@RequestMapping("/db/build")
	@ResponseBody
	public Object build(@RequestBody GenerateTableOptionDTO option) {

		String dbName = option.getDbName();
		String tableName = option.getTableName();
		
		DataBaseSource dataBaseSource = connectionService.getDataBaseSource(dbName);
		
		if(dataBaseSource == null) {
			throw new BusinessException("不存在数据库：" + dbName);
		}
		
		DataBase dataBase = dataBaseSource.getDataBase(false);
		Table table = dataBase.getChild(tableName);
		
		if(table == null) {
			throw new BusinessException("不存在表：" + tableName);
		}
		
		GenerateTableOption tableOption = new GenerateTableOption(table, dataBaseSource.getDataBaseConfig().getType());
		
		BeanCopyUtil.simpleCopy(option, tableOption);
		
		HashMap<String,String> contentMap = new HashMap<>();
		contentMap.put("model", connectionService.buildJavaClass(tableOption, GenerateType.MODEL));
		contentMap.put("mapper", connectionService.buildJavaClass(tableOption, GenerateType.MAPPER));
		contentMap.put("service", connectionService.buildJavaClass(tableOption, GenerateType.SERVICE));
		
		return CommonResponse.getSuccessResponse(null, contentMap);
	}
	
	@Autowired
	SysVisitCacheService visitCacheService;
	
	private final static String CACHE_PROJECT_PATH = "data_project_path";
	
	@RequestMapping("/db/build/boot")
	@ResponseBody
	public Object buildBoot(HttpServletRequest request, @RequestBody GenerateTableOptionDTO option) {

		String dbName = option.getDbName();
		String tableName = option.getTableName();
		
		DataBaseSource dataBaseSource = connectionService.getDataBaseSource(dbName);
		
		if(dataBaseSource == null) {
			throw new BusinessException("不存在数据库：" + dbName);
		}
		
		DataBase dataBase = dataBaseSource.getDataBase(false);
		Table table = dataBase.getChild(tableName);
		
		if(table == null) {
			throw new BusinessException("不存在表：" + tableName);
		}
		
		GenerateTableOption tableOption = new GenerateTableOption(table, dataBaseSource.getDataBaseConfig().getType());
		
		BeanCopyUtil.simpleCopy(option, tableOption);
		
		String modelContent = connectionService.buildJavaClass(tableOption, GenerateType.MODEL);
		String mapperContent = connectionService.buildJavaClass(tableOption, GenerateType.MAPPER);
		String serviceContent = connectionService.buildJavaClass(tableOption, GenerateType.SERVICE);
		String controllerContent = connectionService.buildJavaClass(tableOption, GenerateType.CONTROLLER);

		
		String projectPath = option.getProjectPath();
		
		if(projectPath == null || projectPath.length() == 0) {
			throw new BusinessException("项目路径不能为空" );
		}
		
		connectionService.buildBootProjectFile(tableOption, GenerateType.MODEL, projectPath, modelContent);
		connectionService.buildBootProjectFile(tableOption, GenerateType.MAPPER, projectPath, mapperContent);
		connectionService.buildBootProjectFile(tableOption, GenerateType.SERVICE, projectPath, serviceContent);
		connectionService.buildBootProjectFile(tableOption, GenerateType.CONTROLLER, projectPath, controllerContent);
	
		connectionService.buildBootProjectFile(tableOption, GenerateType.SQLMAPPER, projectPath, "");
		connectionService.buildBootProjectFile(tableOption, GenerateType.PAGE_INDEX, projectPath, "");
		connectionService.buildBootProjectFile(tableOption, GenerateType.PAGE_VIEW, projectPath, "");
		connectionService.buildBootProjectFile(tableOption, GenerateType.PAGE_EDIT, projectPath, "");
		
		visitCacheService.putCache(request, CACHE_PROJECT_PATH, projectPath);
		
		return CommonResponse.getSuccessResponse();
	}
	
}
