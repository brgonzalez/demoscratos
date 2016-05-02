package com.itcr.demoscratos.api;

import java.io.IOException;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcr.demoscratos.db.DataBaseController;
import com.itcr.demoscratos.models.Api;
import com.itcr.demoscratos.models.Forum;
import com.itcr.demoscratos.models.FullTopic;
import com.itcr.demoscratos.models.Ring;
import com.itcr.demoscratos.models.Topic;
import com.itcr.demoscratos.models.User;

public final class RequestController {
	
	private ApacheHttpClient client; 
	private ObjectMapper mapper;
	private DataBaseController database;
	private User currentUser;
	
	private static RequestController instance = null;
	
	public RequestController() {
		setClient(new ApacheHttpClient(Resource.PATH.getUrl()));
		setMapper(new ObjectMapper());
		setDataBase(new DataBaseController());}
	 
	public static synchronized RequestController getInstance() {
		if (instance == null) {
			instance = new RequestController();
	    }
		return instance;
	}
	
	public User getCurrentUser(){
		return currentUser;
	}
	
	public void signIn(String email, String password) {
		String json = "{ \"email\": \""+ email +"\", \"password\": \""+ password +"\" }";
		client.postHttpRequest(Resource.SINGIN.getUrl(), json);
		client.setToken();
		if (isLoggedIn()) { setCurrentUser(getUserByEmail(email)); } }
	
	public void signUp(String email, String firstName, String lastName, String password) {
		String json = "{ \"email\": \""+email+"\", \"firstName\":\""+firstName+"\", \"lastName\":\""+lastName+"\", \"password\":\""+password+"\", \"re_password\":\""+password+"\" }";
		client.postHttpRequest(Resource.SINGUP.getUrl(), json); }
	
	public void signOut() {
		client.removeToken();
		setCurrentUser(null); } 
	
	public boolean isLoggedIn() {
		return client.tokenExists(); }
	
	public Api getApi() {
		client.getHttpRequest(Resource.API.getUrl()); Api api = null;
		try { api = mapper.readValue(client.getOutput(), Api.class); }
		catch (JsonParseException e) { e.printStackTrace(); }
		catch (JsonMappingException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return api;	}
	
	public ArrayList<Forum> getForums() {
		ArrayList<Forum> forums = new ArrayList<Forum>();
		client.getHttpRequest(Resource.FORUMS.getUrl());
		JSONArray array = new JSONArray(client.getOutput());
		JSONObject json;
		for (short index = 0; index < array.length(); index++) {
			json = array.getJSONObject(index);
			forums.add(new Forum(json)); }
		return forums; }
	
	public ArrayList<Topic> getTopics(String idForum) {
		ArrayList<Topic> topics = new ArrayList<Topic>();
		client.getHttpRequest(Resource.TOPICS.getUrl() + idForum);
		JSONArray array = new JSONArray(client.getOutput());
		JSONObject json;
		for (short index = 0; index < array.length(); index++) {
			json = array.getJSONObject(index);
			topics.add(new Topic(json)); }
		return topics; }
	
	public FullTopic getFullTopic(String idTopic) {
		client.getHttpRequest(Resource.TOPIC.getUrl() + idTopic);
		JSONObject json = new JSONObject(client.getOutput());
		FullTopic fullTopic = new FullTopic(json);
		return fullTopic; }
	
	public User getUserByEmail(String email) {
		client.getHttpRequest(Resource.SEARCH_USER.getUrl() + email);
		JSONArray array = new JSONArray(client.getOutput());
		User user = null;
		if (array.length() == 1) { user = new User(array.getJSONObject(0), email); }
		return user; }
			
	public ArrayList<User> getRing() {
		ArrayList<User> users = new ArrayList<User>(); 
		Ring ring = database.selectRing(currentUser.getId());
		if (ring != null) {	
			User member1 = getUserByEmail(ring.getMember1());
			User member2 = getUserByEmail(ring.getMember2());
			User member3 = getUserByEmail(ring.getMember3());
			users.add(member1); users.add(member2); users.add(member3);	}
		else {
			System.err.println("El usuario actual aún no ha definido un anillo de confianza.");	}
		return users; }
		
	public void postRing(String email1, String email2, String email3) {
		User member1 = getUserByEmail(email1);
		User member2 = getUserByEmail(email2);
		User member3 = getUserByEmail(email3);
		if (checkRing(member1, member2, member3)) {
			database.updateRing(currentUser.getId(), member1.getEmail(), member2.getEmail(), member3.getEmail()); } }
	
	public void postTopic(String idForum, String title, String tag, String closingAt, boolean votable, String source, String content) {
		String json = "{ \"forum\": \""+idForum+"\", \"mediaTitle\": \""+title+"\", \"source\": \""+source+"\", \"tag\": { \"name\": \""+tag+"\" }, \"closingAt\":\""+closingAt+"\", \"votable\": "+votable+", \"clauses\": [ { \"markup\": \""+content+"\" } ] }";
		client.postHttpRequest(Resource.TOPIC_CREATE.getUrl(), json); }
	
	public void postForum(String name, String title, String summary) {
		String json = "{ \"name\":\""+name+"\", \"title\":\""+title+"\", \"summary\":\""+summary+"\" }";
		client.postHttpRequest(Resource.FORUM.getUrl(), json); }
	
	public void postPassword(String currentPassword, String newPassword) {
		String json = "{ \"current_password\":\""+currentPassword+"\", \"password\":\""+newPassword+"\" }";
		client.postHttpRequest(Resource.PASSWORD.getUrl(), json); }
	
	public void postProfile(String firstName, String lastName, String email, String pictureUrl) {
		String json = "{ \"firstName\":\""+firstName+"\", \"lastName\":\""+lastName+"\", \"profilePictureUrl\":\""+pictureUrl+"\" }";
		client.postHttpRequest(Resource.PROFILE.getUrl(), json);
		currentUser = getUserByEmail(email);}
	
	public void postNotifications(boolean replies, boolean newTopic) {
		String json = "{ \"replies\": "+replies+", \"new-topic\": "+newTopic+" }";
		client.postHttpRequest(Resource.PASSWORD.getUrl(), json); }
	
	public void deleteForum(String idForum) {
		client.deleteHttpRequest(Resource.FORUM.getUrl() + idForum); }
	
	public void deleteTopic(String idTopic) {
		client.deleteHttpRequest(Resource.TOPIC.getUrl() + idTopic); }
	
	public void deleteRing() {
		database.deleteRing(currentUser.getId()); }
		
	private boolean checkRing(User member1, User member2, User member3) {
		if (member1 != null && member2 != null && member3 != null) {
			if (!member1.equals(member2) && !member1.equals(member3) && !member2.equals(member3)) {
				if (!currentUser.equals(member1) && !currentUser.equals(member2) && !currentUser.equals(member3)) {
					return true; }
				else {
					System.err.println("No es posible agregarse a usted mismo al anillo de confianza");
					return false; }	}	
			else {
				System.err.println("Todos los correos deben ser distintos");
				return false; } }
		else {
			System.err.println("Alguno de los correos ingresados no existe");
			return false; } }
	
	private void setClient(ApacheHttpClient client) {
		this.client = client; }

	private void setMapper(ObjectMapper mapper) {
		this.mapper = mapper; }

	private void setDataBase(DataBaseController database) {
		this.database = database; }
	
	private void setCurrentUser(User currentUser) {
		this.currentUser = currentUser; } }