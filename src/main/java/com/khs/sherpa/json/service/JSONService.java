package com.khs.sherpa.json.service;

/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.servlet.SherpaServlet;
import static com.khs.sherpa.util.Defaults.*;

public class JSONService {

	Logger LOG = Logger.getLogger(SherpaServlet.class.getName());

	class Result {

		private String code;

		private String message;

		Result(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	SessionTokenService tokenService = null;

	UserService userService = null;
	
	
	ActivityService activityService = null;
	

	// session timeout in milliseconds, zero indicates no timeout
	long sessionTimeout = SESSION_TIMEOUT;

	public SessionToken authenticate(@Param(name = "userid") String userid, @Param(name = "password") String password) {

		SessionToken token = null;
		try {
			userService.authenticate(userid, password);
			String tokenId = tokenService.newToken(userid);
			token = new SessionToken();
			token.setToken(tokenId);
			token.setTimeout(sessionTimeout);
			token.setActive(true);
			token.setUserid(userid);
			token.setLastActive(System.currentTimeMillis());
			log("authenticated", userid, "n/a");
			this.activityService.logActivity(token.getUserid(), "authenticated");
		} catch (AuthenticationException e) {
			// this.error(e, out);
			log("invalid authentication", userid, "n/a");
			this.activityService.logActivity("anynmous", "invalid authentication attempt");
		}

		return token;

	}

	public void xauthenticate(OutputStream out, String userid, String password) {
		try {
			userService.authenticate(userid, password);
			String tokenId = tokenService.newToken(userid);
			SessionToken token = new SessionToken();
			token.setToken(tokenId);
			token.setActive(true);
			token.setUserid(userid);
			map(out, token);
			log("authenticated", userid, "n/a");
		} catch (AuthenticationException e) {
			this.error(e, out);
			log("invalid authentication", userid, "n/a");
		}

	}

	public void error(Exception ex, OutputStream out) {
		String error = ex.getMessage();
		StackTraceElement[] stack = ex.getStackTrace();
		for (StackTraceElement ste : stack) {
			error += "\n" + ste.toString();
		}
		if (ex.getCause() != null) {
			error += "\n *** Exception Cause ***";
			stack = ex.getCause().getStackTrace();
			for (StackTraceElement ste : stack) {
				error += "\n" + ste.toString();
			}
		}

		map(out, new Result("ERROR", error));
	}

	public void error(String msg, OutputStream out) {
		map(out, new Result("ERROR", msg));
	}

	public void map(OutputStream out, Object object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(out, object);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void message(String message, OutputStream out) {
		map(out, new Result("SUCCESS", message));
	}

	public void validation(String message, OutputStream out) {
		map(out, new Result("VALIDATION", message));
	}

	public SessionStatus validToken(String token, String userid) {
		return tokenService.isActive(userid, token);
	}

	private void log(String action, String email, String token) {
		LOG.info(String.format("Executed - %s,%s,%s ", action, email, token));
	}

	public SessionTokenService getTokenService() {
		return tokenService;
	}

	public void setTokenService(SessionTokenService tokenService) {
		this.tokenService = tokenService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public ActivityService getActivityService() {
		return activityService;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}


}
