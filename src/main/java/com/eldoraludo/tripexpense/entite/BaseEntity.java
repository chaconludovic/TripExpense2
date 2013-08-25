package com.eldoraludo.tripexpense.entite;

import com.google.common.base.Preconditions;

public class BaseEntity {
	protected Integer id;

	public BaseEntity(Integer id) {
		this.id = id;
	}

	public BaseEntity() {

	}

	public Integer getId() {
		return id;
	}

	public void definirLId(Long insertId) {
		Preconditions.checkNotNull(insertId);
		this.id = Long.valueOf(insertId).intValue();

	}
}
