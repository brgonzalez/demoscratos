package com.itcr.demoscratos;

import java.util.ArrayList;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.itcr.demoscratos.api.RequestController;
import com.itcr.demoscratos.models.Tag;
import com.itcr.demoscratos.models.Topic;
import com.itcr.demoscratos.models.User;
import com.itcr.demoscratos.services.MessagesService;
import com.itcr.demoscratos.services.DateService;

/*
 * 			Controlador para admin
 */

@Controller 
public class AdminController {
	
	private MessagesService messages = new MessagesService();
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	private RequestController request = RequestController.getInstance();
	
	@RequestMapping(value = "/admin" , method = RequestMethod.GET)
	public String admin(Locale locale, Model model) {	
		if(!request.isLoggedIn() || !request.getCurrentUser().isAdmin()){
			return "redirect:/admin/login";
		}
		User user = request.getCurrentUser();
		model.addAttribute("user", user);
		return "admin";
	}
	
	@RequestMapping(value = "/admin/forums " , method = RequestMethod.GET)
	public String displayTopiscForum(Locale locale, Model model,@PathVariable(value="idForum") String idForum) {
		
		if(!request.isLoggedIn() || !request.getCurrentUser().isAdmin()){
			return "redirect:/admin/login";
		}
		ArrayList<Topic> topics = request.getTopics(idForum);
		model.addAttribute("idForum",idForum);
		if(topics.size() > 0){
			for(Topic topic : topics){
				
				DateService serviceDate = new DateService();
				if(serviceDate.isClose((String) topic.getClosingAt())){
					topic.setClosingAt("Cerrado");
				}
				else{
					topic.setClosingAt(serviceDate.getCloseDate((String) topic.getClosingAt()));
				}
			}
			model.addAttribute("topics",topics);
		}
		return "admin-forums";
	}
	
	@RequestMapping(value = "/admin/tags" , method = RequestMethod.GET)
	public String getTags(Locale locale, Model model) {		
		if(!request.isLoggedIn() || !request.getCurrentUser().isAdmin()){
			return "redirect:/admin/login";
		}
		ArrayList<Tag> tags = request.getTags();
		model.addAttribute("tags", tags );
		return "admin-tags";
	}
	@RequestMapping(value = "/admin/tags" , method = RequestMethod.POST)
	public String postTag(Locale locale, Model model,@RequestParam("tagName") String tagName) {		
		if(!request.isLoggedIn() || !request.getCurrentUser().isAdmin()){
			return "redirect:/admin/login";
		}
		request.postTag(tagName);
		ArrayList<Tag> tags = request.getTags();
		model.addAttribute("tags", tags );
		return "admin-tags";
	}


}