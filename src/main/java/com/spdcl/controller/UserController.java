package com.spdcl.controller;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spdcl.constants.ServiceConstants;
import com.spdcl.email.dto.DisconnectionRequest;
import com.spdcl.email.dto.JwtResponse;
import com.spdcl.email.dto.RefreshTokenRequest;
import com.spdcl.entity.RefreshToken;
import com.spdcl.entity.SessionTariffEntity;
import com.spdcl.entity.TenantEntity;
import com.spdcl.model.AdminUserModel;
import com.spdcl.model.AdminUserResponseModel;
import com.spdcl.model.Response;
import com.spdcl.model.SessionTariffModel;
import com.spdcl.model.SessionTariffRequest;
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
	
	@ApiOperation(value = "User Login for application", response = Response.class)
	@PostMapping(path = "/refreshToken")
	public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return refreshTokenService.findByToken(refreshTokenRequest.getToken())
				.map(refreshTokenService::verifyExpiration).map(RefreshToken::getUserEntity).map(userInfo -> {
					String accessToken = jwtTokenProviderService.generateToken(userInfo.getUserName());
					return JwtResponse.builder().accessToken("Bearer "+accessToken).token(refreshTokenRequest.getToken()).build();
				}).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
	}
	   
	/**
	 * @param adminUserModel
	 * @param tenantCode
	 * @return
	 */
	@ApiOperation(value = "user save for application", response = Response.class)
	@RequestMapping(path = "/saveUser/{tenantCode}", method = RequestMethod.POST)
	public Response saveUser(@RequestBody AdminUserModel adminUserModel, @PathVariable String tenantCode) {
		Response response = new Response();
		try {
			response  = userService.saveAdminUser(adminUserModel,tenantCode);
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
			response  = userService.adminLogin(authRequest, tenantCode);
			if (response != null && response.getStatusCode() == 200) {
				
				  Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
			        if (authentication.isAuthenticated()) {
			            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail(), tenantCode);
			            model.setAccessToken("Bearer "+jwtTokenProviderService.generateToken(authRequest.getEmail()));
			            model.setToken(refreshToken.getToken());
						model.setUserName(authRequest.getEmail());
						AdminUserResponseModel modelObj = userService.findByUserNameAndStatus(authRequest.getEmail(), "Active", tenantCode);
						model.setId(modelObj.getId());
						model.setTenantCode(tenantCode);
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
			}else {
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
		TenantEntity tenantEntity1  = tenantRepository.save(tenantEntity);
		response.setData(tenantEntity1);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);
		return response;
	}
	
	@ApiOperation(value = "save SessionTariff for application", response = Response.class)
	@RequestMapping(path = "/saveSessionTariff", method = RequestMethod.POST)
	public Response saveSessionTariff(@RequestBody SessionTariffModel sessionTariffModel) {
		Response response = new Response();
		if (sessionTariffModel != null && sessionTariffModel.getId() != null) {
			SessionTariffEntity tenantEntity2  = sessionTariffRepository.findById(sessionTariffModel.getId()).orElse(null);
			tenantEntity2.setModifiedDate(new Date());
			System.out.println("===="+sessionTariffModel.getTariffType());
			tenantEntity2.setTariffType(sessionTariffModel.getTariffType());
			tenantEntity2.setSession(sessionTariffModel.getSession());
			tenantEntity2.setStatus(sessionTariffModel.getStatus());
			tenantEntity2.setTariffValue(sessionTariffModel.getTariffValue());
			tenantEntity2.setAppAmnt(sessionTariffModel.getAppAmnt());
			tenantEntity2.setMeterRemovingAmnt(sessionTariffModel.getMeterRemovingAmnt());
			tenantEntity2.setDisconnectionAmnt(sessionTariffModel.getDisconnectionAmnt());
			SessionTariffEntity tenantEntity2q  = sessionTariffRepository.save(tenantEntity2);
			response.setData(tenantEntity2q);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
			
		}else {
			SessionTariffEntity sessionTariffEntity = new SessionTariffEntity();
			sessionTariffEntity.setAppAmnt(sessionTariffModel.getAppAmnt());
			sessionTariffEntity.setMeterRemovingAmnt(sessionTariffModel.getMeterRemovingAmnt());
			sessionTariffEntity.setDisconnectionAmnt(sessionTariffModel.getDisconnectionAmnt());
			sessionTariffEntity.setSession(sessionTariffModel.getSession());
			sessionTariffEntity.setStatus(sessionTariffModel.getStatus());
			sessionTariffEntity.setTariffType(sessionTariffModel.getTariffType());
			sessionTariffEntity.setCreatedDate(new Date());
			sessionTariffEntity.setTariffValue(sessionTariffModel.getTariffValue());
			TenantEntity tenantEntity1  = tenantRepository.findByTenantCode(sessionTariffModel.getTenantCode());
			sessionTariffEntity.setTenantEntity(tenantEntity1);
			SessionTariffEntity tenantEntity2  = sessionTariffRepository.save(sessionTariffEntity);
			response.setData(tenantEntity2);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}
	
		return response;
	}
	
	@ApiOperation(value = "get all SessionTariff for application", response = Response.class)
	@GetMapping(path = "/getAllSessionTariff/{tenantCode}")
	public Response getAllSessionTariff(@PathVariable String tenantCode) {
		Response response = new Response();
		TenantEntity tenantEntity1  = tenantRepository.findByTenantCode(tenantCode);
			List<SessionTariffEntity> tenantEntity2  = sessionTariffRepository.findByTenantEntity(tenantEntity1);
			List<SessionTariffModel> sessionTariffModels = new ArrayList<SessionTariffModel>();
			if(tenantEntity2!= null && tenantEntity2.size()>0) {
				tenantEntity2.forEach(str->{
					SessionTariffModel sessionTariffModel = new SessionTariffModel();
					sessionTariffModel.setId(str.getId());
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
	
	@ApiOperation(value = "delete Session Tariff for application", response = Response.class)
	@RequestMapping(path = "/deleteSessionTariff/{tenantCode}/{id}", method = RequestMethod.POST)
	public Response deleteSessionTariff(@PathVariable String tenantCode, @PathVariable UUID id) {
		Response response = new Response();
		SessionTariffEntity tenantEntity2  = sessionTariffRepository.findById(id).get();
		if(tenantEntity2!= null){
		sessionTariffRepository.delete(tenantEntity2);
		response.setStatus(ServiceConstants.STATUS_SUCCESS);
		}else {
			response.setStatus(ServiceConstants.STATUS_FAILED);
		}
		
		return response;
	}

	@ApiOperation(value = "get all SessionTariff for application", response = Response.class)
	@PostMapping(path = "/getSessionTariffByTariffType")
	public Response getSessionTariffByTariffType(@RequestBody SessionTariffRequest request) {
		Response response = new Response();
		TenantEntity tenantEntity1  = tenantRepository.findByTenantCode(request.getTenantCode());
			List<SessionTariffEntity> tenantEntity2  = sessionTariffRepository.findByTenantEntityAndTariffTypeAndSessionIn(tenantEntity1, request.getType(), request.getSessions());
			List<SessionTariffModel> sessionTariffModels = new ArrayList<SessionTariffModel>();
			if(tenantEntity2!= null && tenantEntity2.size()>0) {
				tenantEntity2.forEach(str->{
					SessionTariffModel sessionTariffModel = new SessionTariffModel();
					sessionTariffModel.setId(str.getId());
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
		TenantEntity tenantEntity  = tenantRepository.findByTenantCode(request.getTenantCode());
			
		if(request != null) {
			
			long diff = request.getDateDisconnection().getTime() - request.getDateConnection().getTime();
			
			 int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			 
			 if (diffDays>=365) {
				 
				 if (request.getDateLastBill().getTime() < request.getDateDisconnection().getTime()) {
					  
				 }
			 }else {
				 
				 
			 }
		}
		
		
		
		response.setStatus(ServiceConstants.STATUS_SUCCESS);
		
	
		return response;
	}
	
	@ApiOperation(value = "get all Disconnection for application", response = Response.class)
	@GetMapping(path = "/getAllDisconnection/{tenantCode}")
	public Response getAllDisconnection(@PathVariable String tenantCode) {
		Response response = new Response();
		TenantEntity tenantEntity1  = tenantRepository.findByTenantCode(tenantCode);
			List<SessionTariffEntity> tenantEntity2  = sessionTariffRepository.findByTenantEntity(tenantEntity1);
			List<DisconnectionRequest> sessionTariffModels = new ArrayList<DisconnectionRequest>();
			if(tenantEntity2!= null && tenantEntity2.size()>0) {
				tenantEntity2.forEach(str->{
					DisconnectionRequest sessionTariffModel = new DisconnectionRequest();
					sessionTariffModel.setName("Smith");
					sessionTariffModel.setId(str.getTenantEntity().getId()+"");
					sessionTariffModel.setMeter("190190");
					sessionTariffModel.setTenantCode(str.getTenantEntity().getTenantCode());
					sessionTariffModels.add(sessionTariffModel);
				});
			}
			response.setData(sessionTariffModels);
			response.setStatus(ServiceConstants.STATUS_SUCCESS);
		
	
		return response;
	}
	
	@ApiOperation(value = "delete Disconnection for application", response = Response.class)
	@RequestMapping(path = "/deleteDisconnection/{tenantCode}/{id}", method = RequestMethod.POST)
	public Response deleteDisconnection(@PathVariable String tenantCode, @PathVariable String id) {
		Response response = new Response();
		return response;
	}
	
	//@ApiOperation(value = "download Disconnection for application")
	@GetMapping(value = "/downloadDisconnection/{tenantCode}/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource>  downloadDisconnection(@PathVariable String tenantCode, @PathVariable UUID id) {
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
}
