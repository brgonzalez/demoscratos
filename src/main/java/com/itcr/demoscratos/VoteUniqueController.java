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
import com.itcr.demoscratos.models.FullTopic;
import com.itcr.demoscratos.models.Option;
import com.itcr.demoscratos.models.User;
import com.itcr.demoscratos.models.VisibleVote;
import com.itcr.demoscratos.services.MessagesService;
import com.itcr.demoscratos.services.DateService;

@Controller
public class VoteUniqueController {
		
	private MessagesService messages = new MessagesService();
	private static final Logger logger = LoggerFactory.getLogger(VoteUniqueController.class);
	private RequestController request = RequestController.getInstance();


	@RequestMapping(value = "forum/{idForum}/topic/{idTopic}/unique" , method = RequestMethod.GET)
	public String showTopicUnique(Locale locale, Model model,
			@PathVariable(value="idForum") String idForum,
			@PathVariable(value="idTopic") String idTopic) {
		if(!request.isLoggedIn()){
			logger.info(messages.userLoggedIn(), locale);
			return "redirect:/login";
		}
		FullTopic topic = request.getFullTopic(idTopic);
		User user = request.getCurrentUser();
		model.addAttribute("question", topic.getQuestion());
		model.addAttribute("options", topic.getOptions());
		model.addAttribute("user", user );
		model.addAttribute("idForum", idForum);
		model.addAttribute("topic", topic);
		
		//votos cedidos
		model.addAttribute("givenVotes", topic.getGivenVotes());
		DateService serviceDate = new DateService();
		boolean isClosed = serviceDate.isClose((String) topic.getClosingAt());
		
		if(isClosed){
			return "redirect:/forum/{idForum}/topic/{idTopic}/report";
		}
		else{
			model.addAttribute("close", serviceDate.getCloseDate((String) topic.getClosingAt()));
		}

		if(topic.isSecret()){
			model.addAttribute("isSecret", "none" );
			model.addAttribute("modality", "Privado" );
		}
		else{
			ArrayList<VisibleVote> votes = topic.getVisibleVotes();
			model.addAttribute("votes", votes);
			model.addAttribute("modality", "Semipúblico" );
		}

		boolean isVoted = false;
		for(Option o : topic.getOptions()){
			if (topic.userAlreadyVoted(o.getId())){
				isVoted = true;
				break;
			}
		}
		
		//view rings
		if(!request.doesGivenVoteExist(idTopic) && !isVoted ){
			if(request.getRing().size() > 0){
				model.addAttribute("hasRing", "selection-ring");
				model.addAttribute("members", request.getRing());
			}
			else{
				model.addAttribute("hasRing", "no-ring");
			}
		}else{
			model.addAttribute("hasRing", "voteGiven");
		}
		
		if(isVoted || request.doesGivenVoteExist(idTopic)){
			if(isVoted){
				model.addAttribute("messageNoVote", messages.isVoted());
			}
			else{
				model.addAttribute("messageNoVote", messages.isGiven());
			}
			model.addAttribute("voted", "block");
			model.addAttribute("displayVote", "none");

		}else{
			model.addAttribute("voted", "none");
			model.addAttribute("displayVote", "block");
		}
		
		return "topic-unique";
	}
	
	@RequestMapping(value = "forum/{idForum}/topic/{idTopic}/unique" , method = RequestMethod.POST)
	public String postVoteUnique(Locale locale, Model model,
			@PathVariable(value="idForum") String idForum,
			@RequestParam(value="idOption",defaultValue = "null") String idOption,
			@PathVariable(value="idTopic") String idTopic) {
		if(!request.isLoggedIn()){
			logger.info(messages.userLoggedIn(), locale);
			return "redirect:/login";
		}
		FullTopic topic = request.getFullTopic(idTopic);
		User user = request.getCurrentUser();
		model.addAttribute("question", topic.getQuestion());
		model.addAttribute("options", topic.getOptions());
		model.addAttribute("user", user );
		model.addAttribute("idForum", idForum);
		model.addAttribute("topic", topic);
		
		//votos cedidos
		model.addAttribute("givenVotes", topic.getGivenVotes());
		DateService serviceDate = new DateService();
		boolean isClosed = serviceDate.isClose((String) topic.getClosingAt());
		
		if(idOption.equals("null")){
			return "redirect:/forum/"+idForum+"/topic/"+idTopic+"/unique";
		}
		
		if(isClosed){
			return "redirect:/forum/{idForum}/topic/{idTopic}/report";
		}
		else{
			model.addAttribute("close", serviceDate.getCloseDate((String) topic.getClosingAt()));
		}

		if(topic.isSecret()){
			model.addAttribute("isSecret", "none" );
			model.addAttribute("modality", "Privado" );
		}
		else{
			ArrayList<VisibleVote> votes = topic.getVisibleVotes();
			model.addAttribute("votes", votes);
			model.addAttribute("modality", "Semipúblico" );
		}

		boolean isVoted = false;
		for(Option o : topic.getOptions()){
			if (topic.userAlreadyVoted(o.getId())){
				isVoted = true;
				break;
			}
		}
		//Verify if the user does not check a option
		
		//view rings
		if(!request.doesGivenVoteExist(idTopic) && !isVoted ){
			
			request.postUniqueVote(idTopic, Integer.parseInt(idOption));
			isVoted = true;
			model.addAttribute("hasRing", "voteGiven");

			if(request.getRing().size() > 0){
				model.addAttribute("hasRing", "selection-ring");
				model.addAttribute("members", request.getRing());
			}
			else{
				model.addAttribute("hasRing", "no-ring");
			}
		}else{
			model.addAttribute("hasRing", "voteGiven");
		}
		
		if(isVoted || request.doesGivenVoteExist(idTopic)){
			if(isVoted){
				model.addAttribute("messageNoVote", messages.isVoted());
			}
			else{
				model.addAttribute("messageNoVote", messages.isGiven());
			}
			model.addAttribute("voted", "block");
			model.addAttribute("displayVote", "none");

		}else{
			model.addAttribute("voted", "none");
			model.addAttribute("displayVote", "block");
		}
		return "topic-unique";
	}
	
	@RequestMapping(value = "forum/{idForum}/topic/{idTopic}/unique" ,params="memberRing", method = RequestMethod.POST)
	public String giveSimpleVote(Locale locale, Model model,
			@PathVariable(value="idForum") String idForum,
			@RequestParam(value="memberRing") String memberRing,
			@PathVariable(value="idTopic") String idTopic) {
		request.postGiveVote(idTopic, memberRing);
			
		return "redirect:/forum/"+idForum+"/topic/"+idTopic+"/unique";
	}
	@RequestMapping(value = "forum/{idForum}/topic/{idTopic}/unique/givenVote/{idGivenVote}" ,params="idOption", method = RequestMethod.POST)
	public String voteGivenVote(Locale locale, Model model,
			@PathVariable(value="idForum") String idForum,
			@RequestParam(value="idOption") String idOption,
			@PathVariable(value="idTopic") String idTopic,
			@PathVariable(value="idGivenVote") String idGivenVote) {
		request.postGivenVote(Integer.parseInt(idGivenVote), Integer.parseInt(idOption));
			
		return "redirect:/forum/"+idForum+"/topic/"+idTopic+"/unique";
	}
}