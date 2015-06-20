package com.dgmltn.pimatic.network;

import java.util.List;

import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.LoginResponse;
import com.dgmltn.pimatic.model.Message;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by doug on 6/19/15.
 */
public interface PimaticService {

	@GET("/api/database/messages")
	void getMessages(
		Callback<List<Message>> callback);

	@FormUrlEncoded
	@POST("/login")
	void login(
		@Field("username") String username,
		@Field("password") String password,
		Callback<LoginResponse> callback);

	@FormUrlEncoded
	@POST("/login")
	LoginResponse loginSynchronous(
		@Field("username") String username,
		@Field("password") String password);

	@GET("/api/device/{deviceId}/{actionName}")
	void callDeviceAction(
		@Path("deviceId") String deviceId,
		@Path("actionName") String action,
		Callback<ActionResponse> callback);

}