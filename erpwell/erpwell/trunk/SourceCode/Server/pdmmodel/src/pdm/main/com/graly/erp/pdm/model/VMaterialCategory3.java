package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_PDM_MATERIAL_CATEGORY3")
public class VMaterialCategory3 extends ADBase {

	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_CATEGORY1")
	private String materialCategory1;
	
	@Column(name="MATERIAL_CATEGORY2")
	private String materialCategory2;
	
	@Column(name="MATERIAL_CATEGORY3")
	private String materialCategory3;

	public String getMaterialCategory1() {
		return materialCategory1;
	}

	public void setMaterialCategory1(String materialCategory1) {
		this.materialCategory1 = materialCategory1;
	}

	public String getMaterialCategory2() {
		return materialCategory2;
	}

	public void setMaterialCategory2(String materialCategory2) {
		this.materialCategory2 = materialCategory2;
	}

	public String getMaterialCategory3() {
		return materialCategory3;
	}

	public void setMaterialCategory3(String materialCategory3) {
		this.materialCategory3 = materialCategory3;
	}

}
