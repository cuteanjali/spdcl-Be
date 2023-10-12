package com.spdcl.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PaginationRequestBean {
	private String searchText;
	private int pageNo;
	private int pageSize;
	private String sortBy;
	private String sortDir;
	

}
