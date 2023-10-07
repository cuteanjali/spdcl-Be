package com.spdcl.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserEntity extends BaseEntity{
	@Column(name = "userName", nullable = false, length = 100)
	private String userName;
	@Column(name = "password", nullable = false, length = 250)
	private String password;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tenant_id")
	private TenantEntity tenantEntity;

	private String firstName;
	private String lastName;
	
	@OneToMany(mappedBy="userEntity",cascade=CascadeType.ALL, orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<RoleAssignEntity> roleAssignEntities = new ArrayList<RoleAssignEntity>();
	
}
