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

import com.itcr.demoscratos.api.RequestController;
import com.itcr.demoscratos.models.Topic;
import com.itcr.demoscratos.models.User;

@Controller
public class TopicsController {
		
	private Messages messages = new Messages();
	private static final Logger logger = LoggerFactory.getLogger(ForumsController.class);
	private RequestController request = RequestController.getInstance();

	
	@RequestMapping(value = "forum/{key} " , method = RequestMethod.GET)
	public String displayTopiscForum(Locale locale, Model model,@PathVariable(value="key") String key) {
		if(!request.isLoggedIn()){
			logger.info(messages.userLoggedIn(), locale);
			return "redirect:/login";
		}
		User user = request.getCurrentUser();
		model.addAttribute("user", user );
		ArrayList<Topic> topics = request.getTopics(key);
		if(topics.size() > 0){
			model.addAttribute("topics",topics);
			model.addAttribute("idForum",key);
		}
		System.out.println(topics.get(0));

		logger.info(messages.getForum(key), locale);
		return "topics";
	}
	
	@RequestMapping(value = "/topics", method = RequestMethod.GET)
	public String topics(Locale locale, Model model) {
		if(!request.isLoggedIn()){
			logger.info(messages.userLoggedIn(), locale);
			return "redirect:/login";
		}
		User user = request.getCurrentUser();
		model.addAttribute("user", user );
		logger.info(messages.getTopics(), locale);
		return "topics";
	}
	

	
	@RequestMapping(value = "forum/{idForum}/topic/{idTopic}" , method = RequestMethod.GET)
	public String showTopic(Locale locale, Model model,
			@PathVariable(value="idForum") String idForum,
			@PathVariable(value="idTopic") String idTopic) {
		logger.info("forum = "+ idForum +", topic = " + idTopic, locale);
		if(!request.isLoggedIn()){
			logger.info(messages.userLoggedIn(), locale);
			return "redirect:/login";
		}
		User user = request.getCurrentUser();
		model.addAttribute("user", user );
		model.addAttribute("idForum", idForum);
		Topic topic = request.getFullTopic(idTopic);
		model.addAttribute("topic", topic);

		if(true){
			model.addAttribute("simpleVote", "block");
			model.addAttribute("multiVote", "none");

		}
		else{

		}
		return "privateTopic";
	}
	@RequestMapping(value = "/votePositive" , method = RequestMethod.GET)
	public String votePositive(Locale locale, Model model){
		logger.info("voto",locale);
		return "privateTopic";
	}
	
	@RequestMapping(value = "forum/{idForum}/topic/new" , method = RequestMethod.GET)
	public String newTopic(Locale locale, Model model,@PathVariable(value="idForum") String idForum) {
		//model.addAttribute("topics", topics);
		return "new-topic";
	}
}