package com.spdcl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spdcl.constants.ServiceConstants;
import com.spdcl.email.dto.ApplicationSubmissionDTO;
import com.spdcl.entity.RoleAssignEntity;
import com.spdcl.entity.RoleEntity;
import com.spdcl.entity.TenantEntity;
import com.spdcl.entity.UserEntity;
import com.spdcl.executor.EmailExecutor;
import com.spdcl.model.AdminUserModel;
import com.spdcl.model.AdminUserResponseModel;
import com.spdcl.model.Response;
import com.spdcl.repository.AdminUserRepository;
import com.spdcl.repository.RoleAssignRepository;
import com.spdcl.repository.RoleRepository;
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
    @Autowired
   	private RoleRepository roleRepository;
    @Autowired
   	private RoleAssignRepository roleAssignRepository;
    
    TenantEntity tenantCode = null;

	@Override
	public Response saveAdminUser(AdminUserModel adminUserModel) throws ServiceException {
		Response response = new Response();
		TenantEntity enityTenant = tenantRepository.findByTenantCode(adminUserModel.getTenantCode());
		UserEntity entity = adminUserRepository.findByUserNameAndTenantEntity(adminUserModel.getEmail(), enityTenant);
		RoleEntity roleEntity = null;
		ApplicationSubmissionDTO submissionDTO = null;
		if(adminUserModel.getRoles() != null && adminUserModel.getRoles().size()>0)
			roleEntity= roleRepository.findById(adminUserModel.getRoles().get(0).getId()).orElse(null);
		
		List<RoleAssignEntity> roleAss= new ArrayList<RoleAssignEntity>();
		RoleAssignEntity assignEntity =  new RoleAssignEntity();
		
		if (entity != null && entity.getUserName().equals(adminUserModel.getEmail().trim())) {
			
		  if(entity.getStatus().equalsIgnoreCase("Inactive")) {
			response.setStatus(ServiceConstants.STATUS_FAILED);
			response.setStatusCode(404);
			response.setMessage("Username Inactive!");
		}else if(entity.getStatus().equalsIgnoreCase("Active")){
			response.setStatus(ServiceConstants.STATUS_FAILED);
			response.setStatusCode(404);
			response.setMessage("Username already exists!");
			if (entity != null && entity.getUserName().equals(adminUserModel.getEmail())) {
				response.setStatus(ServiceConstants.STATUS_FAILED_FOR_USED);
			}else {
			UserEntity entity1 = adminUserRepository.findById(adminUserModel.getId()).get();
			if(adminUserModel.getId() != null && entity1 != null) {
				entity1.setModifiedDate(new Date());
				entity1.setFirstName(adminUserModel.getFirstName());
				entity1.setLastName(adminUserModel.getLastName());
				entity1.setUserName(adminUserModel.getEmail());
				entity1.setStatus(adminUserModel.getStatus());
				if(roleEntity != null) {
				List<RoleAssignEntity> listAss = roleAssignRepository.findByRoleEntityAndUserEntity(roleEntity,entity1);
				if(listAss != null && listAss.size()>0) {
					roleAssignRepository.deleteAll(listAss);
					
				}
				assignEntity.setRoleEntity(roleEntity);
				assignEntity.setUserEntity(entity1);
				roleAss.add(assignEntity);
				entity1.setRoleAssignEntities(roleAss);
				}
				adminUserRepository.save(entity1);
				response.setStatus(ServiceConstants.STATUS_SUCCESS);
				response.setStatusCode(200);
				response.setMessage("Updated successfully!");
			}
			}
		}
		  response.setStatus(ServiceConstants.STATUS_FAILED_FOR_USED);
		} else{
			
			
			UserEntity adminUserEntity = new UserEntity();
			
			adminUserEntity.setStatus("Active");
			adminUserEntity.setTenantEntity(enityTenant);
			adminUserEntity.setCreatedDate(new Date());
			adminUserEntity.setFirstName(adminUserModel.getFirstName());
			adminUserEntity.setLastName(adminUserModel.getLastName());
			adminUserEntity.setUserName(adminUserModel.getEmail());
			String generatedString = RandomStringUtils.randomAlphanumeric(10);
			/** password BCryptPasswordEncoder*/
			String encodePass = passwordEncoder.encode(generatedString);
			adminUserEntity.setPassword(encodePass);
			
			if(roleEntity != null) {
			assignEntity.setRoleEntity(roleEntity);
			assignEntity.setUserEntity(adminUserEntity);
			roleAss.add(assignEntity);
			adminUserEntity.setRoleAssignEntities(roleAss);
			}
			UserEntity adminUserEntityObj = adminUserRepository.save(adminUserEntity);
			if (adminUserEntityObj != null) {
				
				submissionDTO = new ApplicationSubmissionDTO();
				submissionDTO.setApplicantName(adminUserEntityObj.getFirstName() + " "+adminUserEntityObj.getLastName());
				submissionDTO.setAppUrl("");
				submissionDTO.setReciepients(new ArrayList<>(Arrays.asList(adminUserEntityObj.getUserName())));
				//submissionDTO.setCc(rentalEmailBean.getCcList());
				submissionDTO.setErpName(adminUserEntityObj.getTenantEntity().getTenantName());
				submissionDTO.setUserId(adminUserEntityObj.getUserName());
				submissionDTO.setNewPassword(generatedString);
				submissionDTO.setSubjectMessage(adminUserEntityObj.getTenantEntity().getTenantName());
				submissionDTO.setBodyMessage("");
				Timer timer = new Timer(true);
				TimerTask timerTask = new EmailExecutor(submissionDTO, ServiceConstants.USER_ID_CREATION);
				timer.schedule(timerTask, 1000);
				
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
			UserEntity entity = adminUserRepository.findByUserNameAndTenantEntity(adminUserModel.getEmail().trim(), enityTenant);
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
				if(entity.getRoleAssignEntities() != null && entity.getRoleAssignEntities().size()>0) {
					model.setRole(entity.getRoleAssignEntities().get(0).getRoleEntity().getName());
				}
			}
		return model;
	}

	@Override
	public TenantEntity getTenantCode() {
	
		return this.tenantCode;
	}
}
