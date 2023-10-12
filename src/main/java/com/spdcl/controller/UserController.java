package com.spdcl.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spdcl.constants.ServiceConstants;
import com.spdcl.email.dto.ApplicationSubmissionDTO;
import com.spdcl.email.dto.DisconnectionRequest;
import com.spdcl.email.dto.JwtResponse;
import com.spdcl.email.dto.RefreshTokenRequest;
import com.spdcl.entity.DisconnectionEntity;
import com.spdcl.entity.DisconnectionSessionTariffEntity;
import com.spdcl.entity.RefreshToken;
import com.spdcl.entity.RoleEntity;
import com.spdcl.entity.SessionTariffEntity;
import com.spdcl.entity.TenantEntity;
import com.spdcl.entity.UserEntity;
import com.spdcl.executor.EmailExecutor;
import com.spdcl.model.AdminUserModel;
import com.spdcl.model.AdminUserResponseModel;
import com.spdcl.model.DisconnectionRequestData;
import com.spdcl.model.ForgotRequestModel;
import com.spdcl.model.PaginationRequestBean;
import com.spdcl.model.Response;
import com.spdcl.model.RoleModel;
import com.spdcl.model.SessionTariffModel;
import com.spdcl.model.SessionTariffRequest;
import com.spdcl.model.UpdateProfileModel;
import com.spdcl.model.UpdateRequestModel;
import com.spdcl.repository.AdminUserRepository;
import com.spdcl.repository.DisconnectionRepository;
import com.spdcl.repository.DisconnectionSessionTariffRepository;
import com.spdcl.repository.RoleRepository;
import com.spdcl.repository.SessionTariffRepository;
import com.spdcl.repository.TenantRepository;
import com.spdcl.service.DisconnectionService;
import com.spdcl.service.RefreshTokenService;
import com.spdcl.service.UserService;
import com.spdcl.utils.IJwtTokenProviderService;
import com.spdcl.utils.ServiceException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/v1")
@Api(tags = { "User" })
public class UserController {

	Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private IJwtTokenProviderService jwtTokenProviderService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserService userService;

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private SessionTariffRepository sessionTariffRepository;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private DisconnectionService disconnectionService;

	@Autowired
	private DisconnectionRepository disconnectionRepository;
	@Autowired
	private DisconnectionSessionTariffRepository disconnectionSessionTariffRepository;

	@Autowired
	private AdminUserRepository adminUserRepository;

	@ApiOperation(value = "User Login for application", response = Response.class)
	@PostMapping(path = "/refreshToken")
	public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return refreshTokenService.findByToken(refreshTokenRequest.getToken())
				.map(refreshTokenService::verifyExpiration).map(RefreshToken::getUserEntity).map(userInfo -> {
					String accessToken = jwtTokenProviderService.generateToken(userInfo.getUserName());
					return JwtResponse.builder().accessToken("Bearer " + accessToken)
							.token(refreshTokenRequest.getToken()).build();
				}).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
	}

	/**
	 * @param adminUserModel
	 * @param tenantCode
	 * @return
	 */
	@ApiOperation(value = "user save for application", response = Response.class)
	@RequestMapping(path = "/saveUser", method = RequestMethod.POST)
	public Response saveUser(@RequestBody AdminUserModel adminUserModel) {
		Response response = new Response();
		try {
			response = userService.saveAdminUser(adminUserModel);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		}
		return response;
	}

	@ApiOperation(value = "User Login for application", response = Response.class)
	@PostMapping(path = "/login/{tenantCode}")
	public Response login(@RequestBody AdminUserModel authRequest, @PathVariable String tenantCode) throws Exception {
		AdminUserResponseModel model = new AdminUserResponseModel();
		Response response = null;
		try {
			response = userService.adminLogin(authRequest, tenantCode);
			if (response != null && response.getStatusCode() == 200) {

				Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
				if (authentication.isAuthenticated()) {
					RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail(),
							tenantCode);
					model.setAccessToken("Bearer " + jwtTokenProviderService.generateToken(authRequest.getEmail()));
					model.setToken(refreshToken.getToken());
					model.setUserName(authRequest.getEmail());
					AdminUserResponseModel modelObj = userService.findByUserNameAndStatus(authRequest.getEmail(),
							"Active", tenantCode);
					model.setId(modelObj.getId());
					model.setTenantCode(tenantCode);
					model.setFirstName(modelObj.getFirstName());
					model.setLastName(modelObj.getLastName());
					model.setRole(modelObj.getRole());
				} else {
					throw new UsernameNotFoundException("invalid user request !");
				}

//				authenticationManager.authenticate(
//						new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
//						);
//				model.setAccessToken("Bearer "+jwtTokenProviderService.generateToken(authRequest.getUserName()));
//				
//				model.setToken("Bearer "+jwtTokenProviderService.generateToken(authRequest.getUserName()));
//				
//				model.setUserName(authRequest.getUserName());
//				AdminUserResponseModel modelObj = userService.findByUserNameAndStatus(authRequest.getUserName(), "Active", tenantCode);
//				model.setId(modelObj.getId());
//				model.setTenantCode(tenantCode);

				response.setData(model);
			} else {
				response.setData(model);
			}

		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

		return response;
	}

	@ApiOperation(value = "save tenant for application", response = Response.class)
	@RequestMapping(path = "/saveTenant", method = RequestMethod.POST)
	public Response saveTenant(@RequestBody TenantEntity tenantEntity) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.save(tenantEntity);
		response.setData(tenantEntity1);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);
		return response;
	}

	@ApiOperation(value = "save role for application", response = Response.class)
	@RequestMapping(path = "/saveRole", method = RequestMethod.POST)
	public Response saveRole(@RequestBody RoleModel reModel) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(reModel.getTenantCode());
		RoleEntity tenantEntity2q = null;
		if (reModel != null && reModel.getId() != null) {
			List<RoleEntity> tenantEntityList = roleRepository.findByTenantEntityAndName(tenantEntity1,
					reModel.getName().trim());
			if (tenantEntityList != null && tenantEntityList.size() > 0) {
				response.setStatus(ServiceConstants.STATUS_FAILED_FOR_USED);
			} else {
				RoleEntity tenantEntity2 = roleRepository.findById(reModel.getId()).orElse(null);
				tenantEntity2.setModifiedDate(new Date());
				tenantEntity2.setName(reModel.getName());
				tenantEntity2.setDescName(reModel.getDesc());
				tenantEntity2q = roleRepository.save(tenantEntity2);
				response.setData(tenantEntity2q);
				response.setStatus(ServiceConstants.STATUS_SUCCESS);
			}

		} else {
			List<RoleEntity> tenantEntityList = roleRepository.findByTenantEntityAndName(tenantEntity1,
					reModel.getName().trim());
			if (tenantEntityList != null && tenantEntityList.size() > 0) {
				response.setStatus(ServiceConstants.STATUS_FAILED_FOR_USED);
			} else {
				RoleEntity tenantEntity2 = new RoleEntity();
				tenantEntity2.setModifiedDate(new Date());
				tenantEntity2.setName(reModel.getName());
				tenantEntity2.setDescName(reModel.getDesc());
				tenantEntity2.setTenantEntity(tenantEntity1);
				tenantEntity2q = roleRepository.save(tenantEntity2);
			}
			response.setData(tenantEntity2q);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}

		return response;
	}

	@ApiOperation(value = "get all Roles for application", response = Response.class)
	@GetMapping(path = "/getAllRoles/{tenantCode}")
	public Response getAllRoles(@PathVariable String tenantCode) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
		List<RoleEntity> tenantEntity2 = roleRepository.findByTenantEntity(tenantEntity1);
		List<RoleModel> sessionTariffModels = new ArrayList<RoleModel>();
		if (tenantEntity2 != null && tenantEntity2.size() > 0) {
			tenantEntity2.forEach(str -> {
				RoleModel sessionTariffModel = new RoleModel();
				sessionTariffModel.setId(str.getId());
				sessionTariffModel.setName(str.getName());
				sessionTariffModel.setDesc(str.getDescName());
				sessionTariffModel.setTenantCode(str.getTenantEntity().getTenantCode());
				sessionTariffModels.add(sessionTariffModel);
			});
		}
		response.setData(sessionTariffModels);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);

		return response;
	}

	@ApiOperation(value = "get all Users for application", response = Response.class)
	@GetMapping(path = "/getAllUsers/{tenantCode}")
	public Response getAllUsers(@PathVariable String tenantCode) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
		List<AdminUserResponseModel> listArr = new ArrayList<AdminUserResponseModel>();
		List<UserEntity> list = adminUserRepository.findByTenantEntity(tenantEntity1);
		if (list.size() > 0) {
			for (UserEntity userEntity : list) {

				AdminUserResponseModel model = new AdminUserResponseModel();
				model.setId(userEntity.getId());
				model.setTenantCode(tenantCode);
				model.setFirstName(userEntity.getFirstName());
				model.setLastName(userEntity.getLastName());
				model.setStatus(userEntity.getStatus());
				model.setName(userEntity.getFirstName() + " " + userEntity.getLastName());
				model.setUserName(userEntity.getUserName());
				if (userEntity.getRoleAssignEntities() != null && userEntity.getRoleAssignEntities().size() > 0) {
					model.setRoleId(userEntity.getRoleAssignEntities().get(0).getRoleEntity().getId());
					model.setRole(userEntity.getRoleAssignEntities().get(0).getRoleEntity().getName());
				}

				listArr.add(model);
			}
		}
		response.setData(listArr);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);

		return response;
	}

	@ApiOperation(value = "save SessionTariff for application", response = Response.class)
	@RequestMapping(path = "/saveSessionTariff", method = RequestMethod.POST)
	public Response saveSessionTariff(@RequestBody SessionTariffModel sessionTariffModel) {
		Response response = new Response();
		if (sessionTariffModel != null && sessionTariffModel.getId() != null) {
			SessionTariffEntity tenantEntity2 = sessionTariffRepository.findById(sessionTariffModel.getId())
					.orElse(null);
			tenantEntity2.setModifiedDate(new Date());
			tenantEntity2.setTariffType(sessionTariffModel.getTariffType());
			tenantEntity2.setSession(sessionTariffModel.getSession());
			tenantEntity2.setStatus(sessionTariffModel.getStatus());
			tenantEntity2.setTariffValue(sessionTariffModel.getTariffValue());
			tenantEntity2.setAppAmnt(sessionTariffModel.getAppAmnt());
			tenantEntity2.setPhaseType(sessionTariffModel.getPhaseType());
			tenantEntity2.setMeterRemovingAmnt(sessionTariffModel.getMeterRemovingAmnt());
			tenantEntity2.setDisconnectionAmnt(sessionTariffModel.getDisconnectionAmnt());
			SessionTariffEntity tenantEntity2q = sessionTariffRepository.save(tenantEntity2);
			response.setData(tenantEntity2q);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);

		} else {
			SessionTariffEntity sessionTariffEntity = new SessionTariffEntity();
			sessionTariffEntity.setPhaseType(sessionTariffModel.getPhaseType());
			sessionTariffEntity.setAppAmnt(sessionTariffModel.getAppAmnt());
			sessionTariffEntity.setMeterRemovingAmnt(sessionTariffModel.getMeterRemovingAmnt());
			sessionTariffEntity.setDisconnectionAmnt(sessionTariffModel.getDisconnectionAmnt());
			sessionTariffEntity.setSession(sessionTariffModel.getSession());
			sessionTariffEntity.setStatus(sessionTariffModel.getStatus());
			sessionTariffEntity.setTariffType(sessionTariffModel.getTariffType());
			sessionTariffEntity.setCreatedDate(new Date());
			sessionTariffEntity.setTariffValue(sessionTariffModel.getTariffValue());
			TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(sessionTariffModel.getTenantCode());
			sessionTariffEntity.setTenantEntity(tenantEntity1);
			SessionTariffEntity tenantEntity2 = sessionTariffRepository.save(sessionTariffEntity);
			response.setData(tenantEntity2);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}

		return response;
	}

	@ApiOperation(value = "get all SessionTariff for application", response = Response.class)
	@GetMapping(path = "/getAllSessionTariff/{tenantCode}")
	public Response getAllSessionTariff(@PathVariable String tenantCode) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
		List<SessionTariffEntity> tenantEntity2 = sessionTariffRepository.findByTenantEntity(tenantEntity1);
		List<SessionTariffModel> sessionTariffModels = new ArrayList<SessionTariffModel>();
		if (tenantEntity2 != null && tenantEntity2.size() > 0) {
			tenantEntity2.forEach(str -> {
				SessionTariffModel sessionTariffModel = new SessionTariffModel();
				sessionTariffModel.setId(str.getId());
				sessionTariffModel.setTariffType(str.getTariffType());
				sessionTariffModel.setSession(str.getSession());
				sessionTariffModel.setStatus(str.getStatus());
				sessionTariffModel.setTariffValue(str.getTariffValue());
				sessionTariffModel.setAppAmnt(str.getAppAmnt());
				sessionTariffModel.setPhaseType(str.getPhaseType());
				sessionTariffModel.setDisconnectionAmnt(str.getDisconnectionAmnt());
				sessionTariffModel.setMeterRemovingAmnt(str.getMeterRemovingAmnt());
				sessionTariffModel.setTenantCode(str.getTenantEntity().getTenantCode());
				sessionTariffModels.add(sessionTariffModel);
			});
		}
		response.setData(sessionTariffModels);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);

		return response;
	}

	@ApiOperation(value = "validated SessionTariff for application", response = Response.class)
	@GetMapping(path = "/validateSessionTariff/{tenantCode}/{type}/{phaseType}/{session}")
	public Response validateSessionTariff(@PathVariable String tenantCode, @PathVariable String type,
			@PathVariable String phaseType, @PathVariable String session) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
		List<String> sess = new ArrayList<String>();
		sess.add(session);
		List<SessionTariffEntity> tenantEntity2 = sessionTariffRepository
				.findByTenantEntityAndTariffTypeAndPhaseTypeAndSessionIn(tenantEntity1, type, phaseType,sess);
		if (tenantEntity2 != null && tenantEntity2.size() > 0) {
			List<DisconnectionSessionTariffEntity> entity = disconnectionSessionTariffRepository
					.findBySessionTariffEntityIn(tenantEntity2);
			if (entity != null && entity.size() > 0) {
				response.setStatus(ServiceConstants.STATUS_FAILED_FOR_USED);
			} else {
				response.setStatus(ServiceConstants.STATUS_FAILED);
			}
		} else {
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}

		return response;
	}

	@ApiOperation(value = "delete Session Tariff for application", response = Response.class)
	@RequestMapping(path = "/deleteSessionTariff/{tenantCode}/{id}", method = RequestMethod.POST)
	public Response deleteSessionTariff(@PathVariable String tenantCode, @PathVariable UUID id) {
		Response response = new Response();
		SessionTariffEntity tenantEntity2 = sessionTariffRepository.findById(id).get();
		if (tenantEntity2 != null) {
			List<SessionTariffEntity> list = new ArrayList<SessionTariffEntity>();
			list.add(tenantEntity2);
			List<DisconnectionSessionTariffEntity> entity = disconnectionSessionTariffRepository
					.findBySessionTariffEntityIn(list);
			if (entity != null && entity.size() > 0) {
				response.setStatus(ServiceConstants.STATUS_FAILED_FOR_USED);
			} else {
				sessionTariffRepository.delete(tenantEntity2);
				response.setStatus(ServiceConstants.STATUS_SUCCESS);
			}
		} else {
			response.setStatus(ServiceConstants.STATUS_FAILED);
		}

		return response;
	}

	@ApiOperation(value = "get all SessionTariff for application", response = Response.class)
	@PostMapping(path = "/getSessionTariffByTariffType")
	public Response getSessionTariffByTariffType(@RequestBody SessionTariffRequest request) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(request.getTenantCode());
		List<SessionTariffEntity> tenantEntity2 = sessionTariffRepository
				.findByTenantEntityAndTariffTypeAndPhaseTypeAndSessionIn(tenantEntity1, request.getType(),request.getPhaseType(), request.getSessions());
		List<SessionTariffModel> sessionTariffModels = new ArrayList<SessionTariffModel>();
		if (tenantEntity2 != null && tenantEntity2.size() > 0) {
			tenantEntity2.forEach(str -> {
				SessionTariffModel sessionTariffModel = new SessionTariffModel();
				sessionTariffModel.setId(str.getId());
				sessionTariffModel.setPhaseType(str.getPhaseType());
				sessionTariffModel.setTariffType(str.getTariffType());
				sessionTariffModel.setSession(str.getSession());
				sessionTariffModel.setStatus(str.getStatus());
				sessionTariffModel.setTariffValue(str.getTariffValue());
				sessionTariffModel.setAppAmnt(str.getAppAmnt());
				sessionTariffModel.setDisconnectionAmnt(str.getDisconnectionAmnt());
				sessionTariffModel.setMeterRemovingAmnt(str.getMeterRemovingAmnt());
				sessionTariffModel.setTenantCode(str.getTenantEntity().getTenantCode());
				sessionTariffModels.add(sessionTariffModel);
			});
		}
		response.setData(sessionTariffModels);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);

		return response;
	}

	@ApiOperation(value = "save Disconnection for application", response = Response.class)
	@PostMapping(path = "/saveDisconnection")
	public Response saveDisconnection(@RequestBody DisconnectionRequest request) {
		Response response = new Response();
		TenantEntity tenantEntity = tenantRepository.findByTenantCode(request.getTenantCode());
		DisconnectionEntity entity = disconnectionRepository.findById(request.getId()).orElse(null);
		List<DisconnectionEntity> entityConsumer = disconnectionRepository.findByConsumer(request.getConsumerNo());
		DisconnectionEntity consumer = entityConsumer.size()>0?entityConsumer.get(0):null;
		List<DisconnectionSessionTariffEntity> disconnectionSessionTariffEntities = new ArrayList<DisconnectionSessionTariffEntity>();
		List<SessionTariffEntity> sessionTariffEntity = sessionTariffRepository
				.findByTenantEntityAndTariffTypeAndPhaseTypeAndSessionIn(tenantEntity, request.getTariffType(),request.getPhaseType(),
						request.getSession());
		double totalFixedAmnt = 0;
		int noticePeriod = 30;
		int noDays = 0;
		int diffTimeDay = 0;
		String msg = null;
		String ruleDisc = null;
		double totalAmt = 0;
		double totalFinalPay = 0;
		
		double applicationPay = 0;
		double disconnectionPay = 0;
		double meterRemovingPay = 0;
		
		if (request.getId() != null && entity != null) {

			
				
			
			entity.setModifiedDate(new Date());
			entity.setTenantEntity(tenantEntity);
			entity.setName(request.getName());
			entity.setConsumerNo(request.getConsumerNo());
			entity.setReadingNo(request.getReadingNo());
			entity.setMeter(request.getMeter());
			entity.setDateConnection(request.getDateConnection());
			entity.setDateDisconnection(request.getDateDisconnection());
			entity.setDateLastBill(request.getDateLastBill());
			entity.setLoadBal(request.getLoadBal());
			entity.setPayAmnt(request.getPayAmnt());
			entity.setSecurityAmnt(request.getSecurityAmnt());
			entity.setDuesAmnt(request.getDuesAmnt());
			entity.setNoOfDays(request.getNoOfDays());
			entity.setAppApplicable(request.isAppApplicable());
			entity.setDisconnectionApplicable(request.isDisconnectionApplicable());
			entity.setMeterRemovingApplicable(request.isMeterRemovingApplicable());
			if (entity.getDisconnectionSessionTariffEntities() != null
					&& entity.getDisconnectionSessionTariffEntities().size() > 0) {
				disconnectionSessionTariffRepository.deleteAll(entity.getDisconnectionSessionTariffEntities());
				// disconnectionSessionTariffRepository.flush();
			}
			if (sessionTariffEntity != null && sessionTariffEntity.size() > 0) {
				for (SessionTariffEntity sessionTariffEntity2 : sessionTariffEntity) {
					System.out.println("=sessionTariffEntity2==" + sessionTariffEntity2.getId());
					DisconnectionSessionTariffEntity entity1 = new DisconnectionSessionTariffEntity();
					entity1.setDisconnectionEntity(entity);
					entity1.setModifiedDate(new Date());
					entity1.setSessionTariffEntity(sessionTariffEntity2);
					disconnectionSessionTariffEntities.add(entity1);
					totalFixedAmnt = sessionTariffEntity2.getTariffValue();
				}
			}
			entity.setDisconnectionSessionTariffEntities(disconnectionSessionTariffEntities);

			if (request.isAppApplicable()) {
				if (entity.getDisconnectionSessionTariffEntities() != null && entity.getDisconnectionSessionTariffEntities().size() > 0) {
					applicationPay = entity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getAppAmnt();
				}

			} else if (entity.isDisconnectionApplicable()) {
				if (entity.getDisconnectionSessionTariffEntities() != null && entity.getDisconnectionSessionTariffEntities().size() > 0) {
					disconnectionPay = entity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getDisconnectionAmnt();
				}
			} else if (entity.isMeterRemovingApplicable()) {
				if (entity.getDisconnectionSessionTariffEntities() != null && entity.getDisconnectionSessionTariffEntities().size() > 0) {
					meterRemovingPay = entity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getMeterRemovingAmnt();
				}
			}
			
			Date dateOfCon = entity.getDateConnection();
			Date dateOfDisCon = entity.getDateDisconnection();
			Date dateOfLastBill = entity.getDateLastBill();
			long diff = dateOfDisCon.getTime() - dateOfCon.getTime();
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			if (diffDays < 365) {

//				 if (request.getDateLastBill().getTime() < request.getDateDisconnection().getTime()) {
//				 }
			} else {

				if (dateOfDisCon.getTime() > dateOfLastBill.getTime()) {
					ruleDisc = "Last Bill Date To Disconnection Date + one month notice period";
					long diffTime = dateOfDisCon.getTime() - dateOfLastBill.getTime();
					diffTimeDay = (int) (diffTime / (24 * 60 * 60 * 1000));
					noDays = (diffTimeDay + noticePeriod);
					totalAmt = noDays * (totalFixedAmnt / 30) * request.getLoadBal();
					//totalFinalPay = (totalAmt + request.getDuesAmnt()) - request.getSecurityAmnt();
					if(applicationPay > 0 || disconnectionPay > 0 || meterRemovingPay>0) {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
						totalFinalPay = totalFinalPay + applicationPay + disconnectionPay + meterRemovingPay;
					}else {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
					}
					if (request.getSecurityAmnt() > 0) {
						msg = "Note : after security adjustment";
					} else {
						msg = "Note : final payable amount against PLD";
					}
				} else if (dateOfLastBill.getTime() > dateOfDisCon.getTime()) {
					ruleDisc = "Last Bill Date  to 30 - (last bill date - disconnection date) ";
					Calendar call22 = Calendar.getInstance();
					call22.setTime(dateOfDisCon);
					call22.add(Calendar.DATE, +30);
					
					long diffTime =  call22.getTime().getTime() - dateOfLastBill.getTime();
					diffTimeDay = (int) (diffTime / (24 * 60 * 60 * 1000));
					if (diffTimeDay<=0) {
						diffTimeDay = 0;
					}
					noDays = (diffTimeDay);
					totalAmt = noDays * (totalFixedAmnt / 30) * request.getLoadBal();
					//totalFinalPay = (totalAmt + request.getDuesAmnt()) - request.getSecurityAmnt();
					if(applicationPay > 0 || disconnectionPay > 0 || meterRemovingPay>0) {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
						totalFinalPay = totalFinalPay + applicationPay + disconnectionPay + meterRemovingPay;
					}else {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
					}
					if (request.getSecurityAmnt() > 0) {
						msg = "Note : after security adjustment";
					} else {
						msg = "Note : final payable amount against PLD";
					}
				}
			}
			entity.setNoOfDays(noDays);
			entity.setPayAmnt(totalFinalPay);
			disconnectionRepository.save(entity);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		
		} else {
			if (consumer ==null) {
			entity = new DisconnectionEntity();
			entity.setAppApplicable(request.isAppApplicable());
			entity.setDisconnectionApplicable(request.isDisconnectionApplicable());
			entity.setMeterRemovingApplicable(request.isMeterRemovingApplicable());
			entity.setCreatedDate(new Date());
			entity.setTenantEntity(tenantEntity);
			entity.setName(request.getName());
			entity.setConsumerNo(request.getConsumerNo());
			entity.setReadingNo(request.getReadingNo());
			entity.setMeter(request.getMeter());
			entity.setSecurityAmnt(request.getSecurityAmnt());
			entity.setDateConnection(request.getDateConnection());
			entity.setDateDisconnection(request.getDateDisconnection());
			entity.setDateLastBill(request.getDateLastBill());
			entity.setLoadBal(request.getLoadBal());
			entity.setPayAmnt(request.getPayAmnt());
			entity.setDuesAmnt(request.getDuesAmnt());
			if (sessionTariffEntity != null && sessionTariffEntity.size() > 0) {
				for (SessionTariffEntity sessionTariffEntity2 : sessionTariffEntity) {
					DisconnectionSessionTariffEntity entity1 = new DisconnectionSessionTariffEntity();
					entity1.setDisconnectionEntity(entity);
					entity1.setCreatedDate(new Date());
					entity1.setSessionTariffEntity(sessionTariffEntity2);
					disconnectionSessionTariffEntities.add(entity1);
					totalFixedAmnt = sessionTariffEntity2.getTariffValue();
				}
			}
			
			
			entity.setDisconnectionSessionTariffEntities(disconnectionSessionTariffEntities);
			
			if (request.isAppApplicable()) {
				if (entity.getDisconnectionSessionTariffEntities() != null && entity.getDisconnectionSessionTariffEntities().size() > 0) {
					applicationPay = entity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getAppAmnt();
				}

			} else if (entity.isDisconnectionApplicable()) {
				if (entity.getDisconnectionSessionTariffEntities() != null && entity.getDisconnectionSessionTariffEntities().size() > 0) {
					disconnectionPay = entity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getDisconnectionAmnt();
				}
			} else if (entity.isMeterRemovingApplicable()) {
				if (entity.getDisconnectionSessionTariffEntities() != null && entity.getDisconnectionSessionTariffEntities().size() > 0) {
					meterRemovingPay = entity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getMeterRemovingAmnt();
				}
			}
			
			Date dateOfCon = entity.getDateConnection();
			Date dateOfDisCon = entity.getDateDisconnection();
			Date dateOfLastBill = entity.getDateLastBill();
			long diff = dateOfDisCon.getTime() - dateOfCon.getTime();
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			if (diffDays < 365) {
				System.out.println("=======diffDays>365===========");
//				 if (request.getDateLastBill().getTime() < request.getDateDisconnection().getTime()) {
//				 }
			} else {

				if (dateOfDisCon.getTime() > dateOfLastBill.getTime()) {
					ruleDisc = "Last Bill Date To Disconnection Date + one month notice period";
					long diffTime = dateOfDisCon.getTime() - dateOfLastBill.getTime();
					diffTimeDay = (int) (diffTime / (24 * 60 * 60 * 1000));
					noDays = (diffTimeDay + noticePeriod);
					totalAmt = noDays * (totalFixedAmnt / 30) * entity.getLoadBal();
					
					if(applicationPay > 0 || disconnectionPay > 0 || meterRemovingPay>0) {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
						totalFinalPay = totalFinalPay + applicationPay + disconnectionPay + meterRemovingPay;
					}else {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
					}
					//totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
					if (entity.getSecurityAmnt() > 0) {
						msg = "Note : after security adjustment";
					} else {
						msg = "Note : final payable amount against PLD";
					}
				} else if (dateOfLastBill.getTime() > dateOfDisCon.getTime()) {
					ruleDisc = "Last Bill Date  to 30 - (last bill date - disconnection date) ";
					Calendar call22 = Calendar.getInstance();
					call22.setTime(dateOfDisCon);
					call22.add(Calendar.DATE, +30);
					
					long diffTime =  call22.getTime().getTime() - dateOfLastBill.getTime();
					
					diffTimeDay = (int) (diffTime / (24 * 60 * 60 * 1000));
					if (diffTimeDay<=0) {
						diffTimeDay = 0;
					}
					noDays = (diffTimeDay);
					totalAmt = noDays * (totalFixedAmnt / 30) * entity.getLoadBal();
					totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
					if(applicationPay > 0 || disconnectionPay > 0 || meterRemovingPay>0) {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
						totalFinalPay = totalFinalPay + applicationPay + disconnectionPay + meterRemovingPay;
					}else {
						totalFinalPay = (totalAmt + entity.getDuesAmnt()) - entity.getSecurityAmnt();
					}
					if (entity.getSecurityAmnt() > 0) {
						msg = "Note : after security adjustment";
					} else {
						msg = "Note : final payable amount against PLD";
					}
				}
			}
			entity.setPayAmnt(totalFinalPay);
			entity.setNoOfDays(noDays);
			entity.setPayAmnt(totalAmt);
			disconnectionRepository.save(entity);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
			}else {
				response.setStatus(ServiceConstants.STATUS_FAILED_FOR_USED);
				response.setMessage("Consumer Number already Exist!");
			}
		}
		
		return response;
	}

	@ApiOperation(value = "get all Disconnection for application", response = Response.class)
	@GetMapping(path = "/getAllDisconnection/{tenantCode}")
	public Response getAllDisconnection(@PathVariable String tenantCode) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
		List<DisconnectionEntity> disconnectionEntities = disconnectionRepository.findAll();
		List<DisconnectionRequestData> sessionTariffModels = new ArrayList<DisconnectionRequestData>();

		if (disconnectionEntities != null && disconnectionEntities.size() > 0) {
			disconnectionEntities.forEach(str -> {
				List<String> sessions = new ArrayList<String>();
				List<String> fixedAmts = new ArrayList<String>();
				if (str.getTenantEntity().getTenantCode().equals(tenantEntity1.getTenantCode())) {

					DisconnectionRequestData sessionTariffModel = new DisconnectionRequestData();
					sessionTariffModel.setName(str.getName());
					sessionTariffModel.setId(str.getId());
					sessionTariffModel.setMeter(str.getMeter());
					sessionTariffModel.setConsumerNo(str.getConsumerNo());
					sessionTariffModel.setReadingNo(str.getReadingNo());
					sessionTariffModel.setDateConnection(str.getDateConnection());
					sessionTariffModel.setDateDisconnection(str.getDateDisconnection());
					sessionTariffModel.setDateLastBill(str.getDateLastBill());
					sessionTariffModel.setLoadBal(str.getLoadBal());
					sessionTariffModel.setNoOfDays(str.getNoOfDays());
					sessionTariffModel.setSecurityAmt(str.getSecurityAmnt());
					sessionTariffModel.setPayAmnt(Math.round(str.getPayAmnt()));
					sessionTariffModel.setDuesAmnt(str.getDuesAmnt());
					sessionTariffModel.setCreatedDate(str.getCreatedDate());
					sessionTariffModel.setAppApplicable(str.isAppApplicable());
					sessionTariffModel.setMeterRemovingApplicable(str.isMeterRemovingApplicable());
					sessionTariffModel.setDisconnectionApplicable(str.isDisconnectionApplicable());
					
					sessionTariffModel.setTenantCode(str.getTenantEntity().getTenantCode());
					if (str.getDisconnectionSessionTariffEntities() != null) {
						str.getDisconnectionSessionTariffEntities().forEach(se -> {
							sessionTariffModel.setPhaseType(se.getSessionTariffEntity().getPhaseType());
							sessions.add(se.getSessionTariffEntity().getSession());
							sessionTariffModel.setTariffType(se.getSessionTariffEntity().getTariffType());
							sessionTariffModel.setAppAmnt(se.getSessionTariffEntity().getAppAmnt() + "");
							sessionTariffModel
									.setMeterRemovingAmnt(se.getSessionTariffEntity().getMeterRemovingAmnt() + "");
							sessionTariffModel
									.setDisconnectionAmnt(se.getSessionTariffEntity().getDisconnectionAmnt() + "");
							fixedAmts.add(se.getSessionTariffEntity().getTariffValue() + "");
						});
					}
					sessionTariffModel.setTariffValue(fixedAmts + "");
					sessionTariffModel.setSession(sessions);
					
					sessionTariffModels.add(sessionTariffModel);
				}
			});
		}
		Collections.sort(sessionTariffModels, Comparator.comparing(DisconnectionRequestData::getCreatedDate).reversed()); 
		response.setData(sessionTariffModels);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);
		return response;
	}

	@ApiOperation(value = "get all Disconnection for application", response = Response.class)
	@PostMapping(path = "/getAllDisconnectionPagination/{tenantCode}")
	public Response getAllDisconnectionPagination(@RequestBody PaginationRequestBean paginationRequestBean,@PathVariable String tenantCode) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
		Page<DisconnectionEntity> disconnectionEntities = null;
		List<DisconnectionRequestData> sessionTariffModels = new ArrayList<DisconnectionRequestData>();
		Sort sort = paginationRequestBean.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(paginationRequestBean.getSortBy()).ascending()
				: Sort.by(paginationRequestBean.getSortBy()).descending();
		Pageable pageable = PageRequest.of(paginationRequestBean.getPageNo(), paginationRequestBean.getPageSize(), sort);
		if (paginationRequestBean.getSearchText() != null && !paginationRequestBean.getSearchText().isEmpty()) {
			disconnectionEntities = disconnectionRepository.fullTextSearch(paginationRequestBean.getSearchText(),pageable);
		} else {
			disconnectionEntities = disconnectionRepository.findAll(pageable);
		}
		if (disconnectionEntities != null) {
			disconnectionEntities.forEach(str -> {
				List<String> sessions = new ArrayList<String>();
				List<String> fixedAmts = new ArrayList<String>();
				if (str.getTenantEntity().getTenantCode().equals(tenantEntity1.getTenantCode())) {

					DisconnectionRequestData sessionTariffModel = new DisconnectionRequestData();
					sessionTariffModel.setName(str.getName());
					sessionTariffModel.setId(str.getId());
					sessionTariffModel.setMeter(str.getMeter());
					sessionTariffModel.setConsumerNo(str.getConsumerNo());
					sessionTariffModel.setReadingNo(str.getReadingNo());
					sessionTariffModel.setDateConnection(str.getDateConnection());
					sessionTariffModel.setDateDisconnection(str.getDateDisconnection());
					sessionTariffModel.setDateLastBill(str.getDateLastBill());
					sessionTariffModel.setLoadBal(str.getLoadBal());
					sessionTariffModel.setNoOfDays(str.getNoOfDays());
					sessionTariffModel.setSecurityAmt(str.getSecurityAmnt());
					sessionTariffModel.setPayAmnt(Math.round(str.getPayAmnt()));
					sessionTariffModel.setDuesAmnt(str.getDuesAmnt());
					sessionTariffModel.setCreatedDate(str.getCreatedDate());
					sessionTariffModel.setAppApplicable(str.isAppApplicable());
					sessionTariffModel.setMeterRemovingApplicable(str.isMeterRemovingApplicable());
					sessionTariffModel.setDisconnectionApplicable(str.isDisconnectionApplicable());
					
					sessionTariffModel.setTenantCode(str.getTenantEntity().getTenantCode());
					if (str.getDisconnectionSessionTariffEntities() != null) {
						str.getDisconnectionSessionTariffEntities().forEach(se -> {
							sessionTariffModel.setPhaseType(se.getSessionTariffEntity().getPhaseType());
							sessions.add(se.getSessionTariffEntity().getSession());
							sessionTariffModel.setTariffType(se.getSessionTariffEntity().getTariffType());
							sessionTariffModel.setAppAmnt(se.getSessionTariffEntity().getAppAmnt() + "");
							sessionTariffModel
									.setMeterRemovingAmnt(se.getSessionTariffEntity().getMeterRemovingAmnt() + "");
							sessionTariffModel
									.setDisconnectionAmnt(se.getSessionTariffEntity().getDisconnectionAmnt() + "");
							fixedAmts.add(se.getSessionTariffEntity().getTariffValue() + "");
						});
					}
					sessionTariffModel.setTariffValue(fixedAmts + "");
					sessionTariffModel.setSession(sessions);
					
					sessionTariffModels.add(sessionTariffModel);
				}
			});
		}
		//Collections.sort(sessionTariffModels, Comparator.comparing(DisconnectionRequestData::getCreatedDate).reversed()); 
		response.setData(sessionTariffModels);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);
		return response;
	}
	
	@ApiOperation(value = "get all Disconnection for application", response = Response.class)
	@GetMapping(path = "/getSearchDisconnection/{tenantCode}/{key}")
	public Response getSearchDisconnection(@PathVariable String tenantCode, @PathVariable String key) {
		Response response = new Response();
		TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
		List<SessionTariffEntity> tenantEntity2 = sessionTariffRepository.findByTenantEntity(tenantEntity1);
		
		List<DisconnectionEntity> disconnectionEntities = disconnectionRepository.getAllRecords(key);
		List<DisconnectionRequestData> sessionTariffModels = new ArrayList<DisconnectionRequestData>();

		if (disconnectionEntities != null && disconnectionEntities.size() > 0) {
			disconnectionEntities.forEach(str -> {
				List<String> sessions = new ArrayList<String>();
				List<String> fixedAmts = new ArrayList<String>();
				if (str.getTenantEntity().getTenantCode().equals(tenantEntity1.getTenantCode())) {

					DisconnectionRequestData sessionTariffModel = new DisconnectionRequestData();
					sessionTariffModel.setName(str.getName());
					sessionTariffModel.setId(str.getId());
					sessionTariffModel.setMeter(str.getMeter());
					sessionTariffModel.setConsumerNo(str.getConsumerNo());
					sessionTariffModel.setReadingNo(str.getReadingNo());
					sessionTariffModel.setAppApplicable(str.isAppApplicable());
					sessionTariffModel.setMeterRemovingApplicable(str.isMeterRemovingApplicable());
					sessionTariffModel.setDisconnectionApplicable(str.isDisconnectionApplicable());
					sessionTariffModel.setDateConnection(str.getDateConnection());
					sessionTariffModel.setDateDisconnection(str.getDateDisconnection());
					sessionTariffModel.setDateLastBill(str.getDateLastBill());
					sessionTariffModel.setLoadBal(str.getLoadBal());
					sessionTariffModel.setNoOfDays(str.getNoOfDays());
					sessionTariffModel.setSecurityAmt(str.getSecurityAmnt());
					sessionTariffModel.setPayAmnt(Math.round(str.getPayAmnt()));
					sessionTariffModel.setDuesAmnt(str.getDuesAmnt());
					sessionTariffModel.setTenantCode(str.getTenantEntity().getTenantCode());
					if (str.getDisconnectionSessionTariffEntities() != null) {
						str.getDisconnectionSessionTariffEntities().forEach(se -> {
							sessionTariffModel.setPhaseType(se.getSessionTariffEntity().getPhaseType());
							sessions.add(se.getSessionTariffEntity().getSession());
							sessionTariffModel.setTariffType(se.getSessionTariffEntity().getTariffType());
							sessionTariffModel.setAppAmnt(se.getSessionTariffEntity().getAppAmnt() + "");
							sessionTariffModel
									.setMeterRemovingAmnt(se.getSessionTariffEntity().getMeterRemovingAmnt() + "");
							sessionTariffModel
									.setDisconnectionAmnt(se.getSessionTariffEntity().getDisconnectionAmnt() + "");
							fixedAmts.add(se.getSessionTariffEntity().getTariffValue() + "");
						});
					}
					sessionTariffModel.setTariffValue(fixedAmts + "");
					sessionTariffModel.setSession(sessions);
					sessionTariffModels.add(sessionTariffModel);
				}
			});
		}
		response.setData(sessionTariffModels);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);
		return response;
	}
	
	@ApiOperation(value = "delete Disconnection for application", response = Response.class)
	@RequestMapping(path = "/deleteDisconnection/{tenantCode}/{id}", method = RequestMethod.POST)
	public Response deleteDisconnection(@PathVariable String tenantCode, @PathVariable UUID id) {
		Response response = new Response();
		DisconnectionEntity disconnectionEntity = disconnectionRepository.findById(id).get();
		if (disconnectionEntity != null && disconnectionEntity.getTenantEntity().getTenantCode().equals(tenantCode)) {
			disconnectionRepository.delete(disconnectionEntity);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}
		return response;
	}

	// @ApiOperation(value = "download Disconnection for application")
	@GetMapping(value = "/downloadDisconnection/{tenantCode}/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> downloadDisconnection(@PathVariable String tenantCode,
			@PathVariable UUID id) {
		InputStreamResource resource = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);

		try {
			resource = disconnectionService.routeDownload(tenantCode, id);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (resource != null) {
			return ResponseEntity.ok().headers(headers).body(resource);
		} else {
			return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	@ApiOperation(value = "get Disconnection for application", response = Response.class)
	@GetMapping(path = "/getDisconnectionById/{tenantCode}/{id}")
	public Response getDisconnectionById(@PathVariable String tenantCode, @PathVariable UUID id) {
		Response response = new Response();
		DisconnectionEntity disconnectionEntity = disconnectionRepository.findById(id).get();
		if (disconnectionEntity != null && disconnectionEntity.getTenantEntity().getTenantCode().equals(tenantCode)) {
			DisconnectionRequestData authRequest = new DisconnectionRequestData();
			authRequest.setName(disconnectionEntity.getName());
			authRequest.setConsumerNo(disconnectionEntity.getConsumerNo());
			response.setData(authRequest);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}
		return response;
	}
	
	@ApiOperation(value = "get Disconnection for application", response = Response.class)
	@GetMapping(path = "/viewDisconnectionById/{tenantCode}/{id}")
	public Response viewDisconnectionById(@PathVariable String tenantCode, @PathVariable UUID id) {
		Response response = new Response();
		Map<String, Object> disconnectionEntity = disconnectionService.viewDisconnection(tenantCode,id);
		if (disconnectionEntity != null) {
			response.setData(disconnectionEntity);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}
		return response;
	}
	
	@ApiOperation(value = "User forgot pass for application", response = Response.class)
	@PostMapping(path = "/forgotPass/{tenantCode}")
	public Response forgotPass(@RequestBody ForgotRequestModel authRequest, @PathVariable String tenantCode) throws Exception {
		Response response = new Response();
		ApplicationSubmissionDTO submissionDTO = null;
		if(authRequest != null) {
			TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
			UserEntity entity = adminUserRepository.findByUserNameAndTenantEntity(authRequest.getEmail().trim(), tenantEntity1);
			if(entity != null) {
		    if(entity.getStatus().equals("Active")) {
		    	
		    	String generatedString = RandomStringUtils.randomAlphanumeric(10);
				/** password BCryptPasswordEncoder*/
				String encodePass = passwordEncoder.encode(generatedString);
		    	
		    	submissionDTO = new ApplicationSubmissionDTO();
				submissionDTO.setApplicantName(entity.getFirstName() + " "+entity.getLastName());
				
				submissionDTO.setAppUrl("");
				submissionDTO.setReciepients(new ArrayList<>(Arrays.asList(entity.getUserName())));
				//submissionDTO.setCc(rentalEmailBean.getCcList());
				submissionDTO.setErpName(entity.getTenantEntity().getTenantName());
				submissionDTO.setUserId(entity.getUserName());
				submissionDTO.setNewPassword(generatedString);
				submissionDTO.setSubjectMessage(entity.getTenantEntity().getTenantName());
				submissionDTO.setBodyMessage("");
				Timer timer = new Timer(true);
				TimerTask timerTask = new EmailExecutor(submissionDTO, ServiceConstants.USER_PASSWORD_RESET);
				timer.schedule(timerTask, 1000);
				entity.setPassword(encodePass);
				adminUserRepository.save(entity);
				response.setStatus(ServiceConstants.STATUS_SUCCESS);
				response.setMessage("Password has been send in register email Id!");
			}else {
				response.setStatus(ServiceConstants.STATUS_FAILED);
				response.setMessage("Email Id is Inactive!");
			}
			
		}else {
			response.setStatus(ServiceConstants.STATUS_FAILED);
			response.setMessage("Email Id not exist!");
		}
		}
		return response;
	}

	@ApiOperation(value = "User update pass for application", response = Response.class)
	@PostMapping(path = "/updatePass/{tenantCode}")
	public Response updatePass(@RequestBody UpdateRequestModel authRequest, @PathVariable String tenantCode)
			throws Exception {
		Response response = new Response();
		if (authRequest != null) {
			TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
			UserEntity entity = adminUserRepository.findById(authRequest.getId()).orElse(null);
			if (entity != null && entity.getTenantEntity().getTenantCode().equals(tenantEntity1.getTenantCode())) {
				String encodePass = passwordEncoder.encode(authRequest.getNewPass().trim());
				entity.setPassword(encodePass);
				UserEntity entity11 =  adminUserRepository.save(entity);
				if(entity11 != null ) {
					response.setStatus(ServiceConstants.STATUS_SUCCESS);
					response.setMessage("Password updated successfully!");
				}else {
					response.setStatus(ServiceConstants.STATUS_FAILED);
					response.setMessage("Password updated not successfully!");
				}
			}
		}
		return response;
	}
	
	@ApiOperation(value = "User update pass for application", response = Response.class)
	@PostMapping(path = "/updateProfile/{tenantCode}")
	public Response updateProfile(@RequestBody UpdateProfileModel authRequest, @PathVariable String tenantCode)
			throws Exception {
		Response response = new Response();
		if (authRequest != null) {
			TenantEntity tenantEntity1 = tenantRepository.findByTenantCode(tenantCode);
			UserEntity entity = adminUserRepository.findById(authRequest.getId()).orElse(null);
			if (entity != null && entity.getTenantEntity().getTenantCode().equals(tenantEntity1.getTenantCode())) {
				entity.setLastName(authRequest.getLastName());
				entity.setFirstName(authRequest.getFirstName());
				UserEntity entity11 =  adminUserRepository.save(entity);
				if(entity11 != null ) {
					response.setStatus(ServiceConstants.STATUS_SUCCESS);
					response.setMessage("Profile updated successfully!");
				}else {
					response.setStatus(ServiceConstants.STATUS_FAILED);
					response.setMessage("Profile updated not successfully!");
				}
			}
		}
		return response;
	}
}
