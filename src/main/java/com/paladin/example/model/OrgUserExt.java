package com.paladin.example.model;

import com.paladin.framework.mybatis.SimpleJoin;

@SimpleJoin(baseType=OrgUser.class,joinType=OrgUnit.class,joinField="orgUnitId")
public class OrgUserExt extends OrgUser{

	private String unitName;

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
}
