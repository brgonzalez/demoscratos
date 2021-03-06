package com.itcr.demoscratos;

import java.util.ArrayList;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.itcr.demoscratos.api.RequestController;
import com.itcr.demoscratos.models.Ring;
import com.itcr.demoscratos.models.User;
import com.itcr.demoscratos.services.MessagesService;


@Controller
public class RingController {
	
	private MessagesService messages = new MessagesService();
	private static final Logger logger = LoggerFactory.getLogger(ForumsController.class);
	private RequestController request =  RequestController.getInstance();

	@RequestMapping(value = "/settings-ring", method = RequestMethod.GET)
	public String settingsPerfil(Locale locale, Model model) {
		if(!request.isLoggedIn()){
			logger.info(messages.userLoggedIn(), locale);
			return "redirect:/login";
		}
		User user = request.getCurrentUser();
		model.addAttribute("user", user );
		ArrayList<User> members = request.getRing();
		if(members.size() > 0){
			model.addAttribute("member1", members.get(0));
			model.addAttribute("member2",  members.get(1) );
			model.addAttribute("member3",  members.get(2) );
			model.addAttribute("modify", "none" );
			model.addAttribute("noRing", "none" );
		}
		else{
			model.addAttribute("show", "none" );
			model.addAttribute("modify", "block" );
			model.addAttribute("noRing", "block" );
		}
		//mensajes
		model.addAttribute("noMember1", "none");
		model.addAttribute("noMember2", "none");
		model.addAttribute("noMember3", "none");
		model.addAttribute("success", "none");
		
		model.addAttribute("noRingCreated", "none");



		logger.info(messages.getRing(), locale);
		return "settings-ring";
	}
	
	@RequestMapping(value = "/settings-ring", method = RequestMethod.POST)
	public String modifyRing(Locale locale,Model model,
			@RequestParam("emailMember1") String emailMember1 ,
			@RequestParam("emailMember2") String emailMember2,
			@RequestParam("emailMember3") String emailMember3) {
		if(!request.isLoggedIn()){
			logger.info(messages.userLoggedIn(), locale);
			return "redirect:/login";
		}
		User user = request.getCurrentUser();
		model.addAttribute("user", user );
		
		boolean existEmails = true;
		
		//verificación de anillos
		if(request.getUserByEmail(emailMember1) == null){
			model.addAttribute("noMember1", "block");
			existEmails = false;
		}else{
			model.addAttribute("noMember1", "none");
		}
		
		if(request.getUserByEmail(emailMember2) == null){
			model.addAttribute("noMember2", "block");
			existEmails = false;
		}else{
			model.addAttribute("noMember2", "none");
		}
		if(request.getUserByEmail(emailMember3) == null){
			model.addAttribute("noMember3", "block");
			existEmails = false;
		}else{
			model.addAttribute("noMember3", "none");
		}
		
		
		
		if (existEmails){
			request.postRing(emailMember1, emailMember2, emailMember3);
			ArrayList<User>  rings = request.getRing();
			if(rings.size() > 0){
				model.addAttribute("success", "block");
				model.addAttribute("noRingCreated", "none");
			}else{
				model.addAttribute("noRingCreated", "block");
				model.addAttribute("success", "none");
			}
		}
		else{
			model.addAttribute("success", "none");
			model.addAttribute("noRingCreated", "none");
		}

		
		model.addAttribute("noRing", "none" );

		ArrayList<User> members = request.getRing();
		if(members.size() > 0){
			model.addAttribute("member1", members.get(0));
			model.addAttribute("member2",  members.get(1) );
			model.addAttribute("member3",  members.get(2) );
			model.addAttribute("modify", "none" );
			model.addAttribute("show", "block" );
		}
		else{
			model.addAttribute("modify", "block" );
			model.addAttribute("show", "none" );
		}
		logger.info(messages.updatedRing(), locale);
		return "settings-ring";
	}
}
	