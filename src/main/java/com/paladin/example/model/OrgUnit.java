package com.paladin.example.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.paladin.framework.common.UnDeleteModel;

public class OrgUnit extends UnDeleteModel{

	public final static String UNIT_TYPE_DEPARTMENT = "A";
	public final static String UNIT_TYPE_AGENCY = "B";
	public final static String UNIT_TYPE_ASSESS_TEAM = "C";

	
	@Id
	@Column(name = "uid")
	@GeneratedValue(generator = "UUID")
	private String uid;
	
	private String unitName;

	private String unitDescription;

	private String parentUnitId;

	private String unitType;

	private String districtCode;
	
	private String contact;
	
	private String contactPhone;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitDescription() {
		return unitDescription;
	}

	public void setUnitDescription(String unitDescription) {
		this.unitDescription = unitDescription;
	}

	public String getParentUnitId() {
		return parentUnitId;
	}

	public void setParentUnitId(String parentUnitId) {
		this.parentUnitId = parentUnitId;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

}