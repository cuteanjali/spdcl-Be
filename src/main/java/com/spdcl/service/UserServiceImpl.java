package com.spdcl.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spdcl.constants.ServiceConstants;
import com.spdcl.entity.TenantEntity;
import com.spdcl.entity.UserEntity;
import com.spdcl.model.AdminUserModel;
import com.spdcl.model.AdminUserResponseModel;
import com.spdcl.model.Response;
import com.spdcl.repository.AdminUserRepository;
import com.spdcl.repository.TenantRepository;
import com.spdcl.utils.ServiceException;


@Service("userService")
public class UserServiceImpl implements UserService{


 
    
    @Autowired
	private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
	private AdminUserRepository adminUserRepository;
    
    @Autowired
   	private TenantRepository tenantRepository;
    
    TenantEntity tenantCode = null;

	@Override
	public Response saveAdminUser(AdminUserModel adminUserModel, String tenantCode) throws ServiceException {
		Response response = new Response();
		TenantEntity enityTenant = tenantRepository.findByTenantCode(tenantCode);
		UserEntity entity = adminUserRepository.findByUserNameAndStatusAndTenantEntity(adminUserModel.getEmail().trim(),ServiceConstants.STATUS_ACTIVE, enityTenant);
		if (entity != null && entity.getUserName().equals(adminUserModel.getEmail().trim())) {
			response.setStatus(ServiceConstants.STATUS_FAILED);
			response.setStatusCode(404);
			response.setMessage("Username already exists!");
		}else{
			UserEntity adminUserEntity = new UserEntity();
			adminUserEntity.setStatus(ServiceConstants.STATUS_ACTIVE);
			
			adminUserEntity.setTenantEntity(enityTenant);
			adminUserEntity.setCreatedDate(new Date());
			BeanUtils.copyProperties(adminUserModel, adminUserEntity);
			/** password BCryptPasswordEncoder*/
			String encodePass = passwordEncoder.encode(adminUserModel.getPassword().trim());
			adminUserEntity.setPassword(encodePass);
			UserEntity adminUserEntityObj = adminUserRepository.save(adminUserEntity);
			if (adminUserEntityObj != null) {
				response.setStatus(ServiceConstants.STATUS_SUCCESS);
				response.setStatusCode(200);
				response.setMessage("saved successfully!");
			}else {
				response.setStatus(ServiceConstants.STATUS_FAILED);
				response.setStatusCode(404);
				response.setMessage("saved not successfully!");
			}
		}
	return response;
	}

	@Override
	public Response adminLogin(AdminUserModel adminUserModel, String tenantCode) throws ServiceException {
		
		Response response = new Response();
		TenantEntity enityTenant = tenantRepository.findByTenantCode(tenantCode);
		this.tenantCode = enityTenant;
		if (adminUserModel != null && adminUserModel.getEmail() != null) {
			UserEntity entity = adminUserRepository.findByUserNameAndTenantEntity(adminUserModel.getEmail().trim(), enityTenant).orElse(null);
			if (entity != null && entity.getTenantEntity().getId().equals(enityTenant.getId())) {
			
				if (entity.getStatus().equals(ServiceConstants.STATUS_ACTIVE)) {
					if (entity.getPassword() != null && passwordEncoder.matches(adminUserModel.getPassword().trim(), entity.getPassword())) {
						response.setMessage("Admin User Logined");
						response.setStatusCode(200);
						response.setStatus(ServiceConstants.STATUS_SUCCESS);
					}else {
						response.setMessage("Password In-Valid");
						response.setStatusCode(404);
						response.setStatus(ServiceConstants.STATUS_FAILED);
					}
				}else {
					response.setMessage("Username Id not Active please contact with admin.");
					response.setStatusCode(400);
					response.setStatus(ServiceConstants.STATUS_FAILED);
				}
				
			}else {
				response.setMessage("Username not exists");
				response.setStatusCode(404);
				response.setStatus(ServiceConstants.STATUS_FAILED);
			}
		}
		return response;
	}

	@Override
	public AdminUserResponseModel findByUserNameAndStatus(String userName, String status, String tenantCode)
			throws ServiceException {
		TenantEntity enityTenant = tenantRepository.findByTenantCode(tenantCode);
			UserEntity entity = adminUserRepository.findByUserNameAndStatusAndTenantEntity(userName,ServiceConstants.STATUS_ACTIVE, enityTenant);
			AdminUserResponseModel model = new AdminUserResponseModel();
			if (entity != null) {
				BeanUtils.copyProperties(entity, model);
			}
			
		return model;
	}

	@Override
	public TenantEntity getTenantCode() {
	
		return this.tenantCode;
	}
}
