package com.spdcl.service;

import com.spdcl.entity.TenantEntity;
import com.spdcl.model.AdminUserModel;
import com.spdcl.model.AdminUserResponseModel;
import com.spdcl.model.Response;
import com.spdcl.utils.ServiceException;


public interface UserService {

	public TenantEntity getTenantCode();

	/**
	 * 
	 * @param adminUserModel
	 * @param tenantCode
	 * @return
	 * @throws ServiceException
	 */
	public Response saveAdminUser(AdminUserModel adminUserModel, String tenantCode) throws ServiceException;
	
	/**
	 * @param adminUserModel
	 * @param tenantCode
	 * @return
	 * @throws ServiceException
	 */
	public Response adminLogin(AdminUserModel adminUserModel, String tenantCode) throws ServiceException;
	
	/**
	 * @param email
	 * @param status
	 * @return
	 * @throws ServiceException
	 */
	public AdminUserResponseModel findByUserNameAndStatus(String userName, String status, String tenantCode)throws ServiceException;
}
