package com.paladin.example.model;

import java.sql.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.paladin.framework.common.UnDeleteModel;

public class OrgUser extends UnDeleteModel {

	@Id
	@GeneratedValue(generator = "UUID")
	private String id;

	private String orgUnitId;
	
	private String orgAgencyId;
	
	private String orgAssessTeamId;

	private String name;

	private String account;

	private Date recordCreateTime;

	private String sex;

	private String oeducation;

	private String nation;

	private String partisan;

	private Date birthday;

	private String jobDuties;

	private String jobRank;

	private Date startWorkTime;

	private Date comeUnitTime;

	private String resume;

	private String reward;

	private String punish;

	private Integer isAdmin;

	private Integer isAssessor;

	private String assessRole;

	private String assessUnitId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrgUnitId() {
		return orgUnitId;
	}

	public void setOrgUnitId(String orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getOeducation() {
		return oeducation;
	}

	public void setOeducation(String oeducation) {
		this.oeducation = oeducation;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getPartisan() {
		return partisan;
	}

	public void setPartisan(String partisan) {
		this.partisan = partisan;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getJobDuties() {
		return jobDuties;
	}

	public void setJobDuties(String jobDuties) {
		this.jobDuties = jobDuties;
	}

	public String getJobRank() {
		return jobRank;
	}

	public void setJobRank(String jobRank) {
		this.jobRank = jobRank;
	}

	public Date getStartWorkTime() {
		return startWorkTime;
	}

	public void setStartWorkTime(Date startWorkTime) {
		this.startWorkTime = startWorkTime;
	}

	public Date getComeUnitTime() {
		return comeUnitTime;
	}

	public void setComeUnitTime(Date comeUnitTime) {
		this.comeUnitTime = comeUnitTime;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getPunish() {
		return punish;
	}

	public void setPunish(String punish) {
		this.punish = punish;
	}

	public Integer getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Integer getIsAssessor() {
		return isAssessor;
	}

	public void setIsAssessor(Integer isAssessor) {
		this.isAssessor = isAssessor;
	}

	public Date getRecordCreateTime() {
		return recordCreateTime;
	}

	public void setRecordCreateTime(Date recordCreateTime) {
		this.recordCreateTime = recordCreateTime;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAssessUnitId() {
		return assessUnitId;
	}

	public void setAssessUnitId(String assessUnitId) {
		this.assessUnitId = assessUnitId;
	}

	public String getAssessRole() {
		return assessRole;
	}

	public void setAssessRole(String assessRole) {
		this.assessRole = assessRole;
	}

	public String getOrgAgencyId() {
		return orgAgencyId;
	}

	public void setOrgAgencyId(String orgAgencyId) {
		this.orgAgencyId = orgAgencyId;
	}

	public String getOrgAssessTeamId() {
		return orgAssessTeamId;
	}

	public void setOrgAssessTeamId(String orgAssessTeamId) {
		this.orgAssessTeamId = orgAssessTeamId;
	}

}