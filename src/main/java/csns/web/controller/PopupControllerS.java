/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2016, Mahdiye Jamali (mjamali@calstatela.edu).
 * 
 * CSNS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * CSNS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with CSNS. If not, see http://www.gnu.org/licenses/agpl.html.
 */
package csns.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Department;
import csns.model.academics.Standing;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.StandingDao;
import csns.model.core.User;
import csns.model.core.dao.SubscriptionDao;
import csns.model.core.dao.UserDao;
import csns.model.forum.Forum;
import csns.model.forum.Post;
import csns.model.forum.dao.ForumDao;
import csns.model.forum.dao.PostDao;
import csns.model.news.News;
import csns.model.news.dao.NewsDao;
import csns.model.popup.Popup;
import csns.model.popup.dao.PopupDao;
import csns.security.SecurityUtils;
import csns.web.editor.CalendarPropertyEditor;
import csns.web.editor.StandingPropertyEditor;
import csns.web.validator.PopupValidator;

@Controller
@SessionAttributes("popup")
public class PopupControllerS {
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private NewsDao newsDao;

	@Autowired
	private PostDao postDao;
	
	@Autowired
	private ForumDao forumDao;

	@Autowired
	private SubscriptionDao subscriptionDao;

	@Autowired
	private PopupDao popupDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private PopupValidator popupValidator;

	@Autowired
	private StandingDao standingDao;

	@Autowired
	private WebApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(PopupControllerS.class);

	private List<String> roles = new ArrayList<String>();
	private Map<String, String> rolesMap = new HashMap<String, String>();

	@PostConstruct
	public void init() {
		List<String> roleDispValues = new ArrayList<String>();
		roles.add("DEPT_ROLE_ADMIN");
		roles.add("DEPT_ROLE_FACULTY");
		roles.add("DEPT_ROLE_INSTRUCTOR");
		roles.add("DEPT_ROLE_REVIEWER");
		roles.add("DEPT_ROLE_EVALUATOR");
		
		roleDispValues.add("Administraitors");
		roleDispValues.add("Faculty");
		roleDispValues.add("Instructors");
		roleDispValues.add("Program Reviewers");
		roleDispValues.add("External Rubric Evaluators");
		
		for(int i = 0; i<roles.size(); i++)
			rolesMap.put(roles.get(i), roleDispValues.get(i));
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Standing.class, (StandingPropertyEditor) context.getBean("standingPropertyEditor"));

		binder.registerCustomEditor(Calendar.class, new CalendarPropertyEditor("MM/dd/yyyy"));
	}

	@RequestMapping(value = "/department/{dept}/popup/post", method = RequestMethod.GET)
	public String post(@PathVariable String dept, ModelMap models) {

		List<Standing> standings = standingDao.getStandings();
		Department department = departmentDao.getDepartment(dept);
		
		Popup popup = new Popup();
		popup.setDepartment(department);

		models.put("popup", popup);
		models.put("roles", roles);
		models.put("rolesMap", rolesMap);
		models.put("standings", standings);
		models.put("dept", dept);
		return "popup/post";
	}

	@RequestMapping(value = "/department/{dept}/popup/post", method = RequestMethod.POST)
	public String post(@ModelAttribute Popup popup, @PathVariable String dept, @RequestParam Long targetUserId,
			@RequestParam Long removeUserId, @RequestParam(required = false) Long forumId, BindingResult result, 
			SessionStatus sessionStatus, ModelMap models) {
		User user = SecurityUtils.getUser();
		Department department = departmentDao.getDepartment(dept);

		//------------add individual users------------//
		if(targetUserId != null) {
			Set<User> users = popup.getIndividualTargetUsers();
			boolean add = true;
			for(User u : users) {
				if(u.getId().equals(targetUserId))
					add = false;
			}
			if(add)
				users.add(userDao.getUser(targetUserId));
			
			List<Standing> standings = standingDao.getStandings();

			popup.setDepartment(department);

			models.put("popup", popup);
			models.put("roles", roles);
			models.put("rolesMap", rolesMap);
			models.put("standings", standings);
			models.put("dept", dept);
			return "popup/post";
		}
		//---------------------------------------------//
		
		//------------remove individual users------------//
		if(removeUserId != null) {
			Set<User> users = popup.getIndividualTargetUsers();
			for(User u : users) {
				if(u.getId().equals(removeUserId)) {
					users.remove(u);
					break;
				}
			}
			
			List<Standing> standings = standingDao.getStandings();

			popup.setDepartment(department);

			models.put("popup", popup);
			models.put("roles", roles);
			models.put("rolesMap", rolesMap);
			models.put("standings", standings);
			models.put("dept", dept);
			return "popup/post";
		}
		//---------------------------------------------//
		
		popupValidator.validate(popup, result);
		if (result.hasErrors()) {
			List<Standing> standings = standingDao.getStandings();
			models.put("roles", roles);
			models.put("rolesMap", rolesMap);
			models.put("standings", standings);
			return "popup/post";
		}
		
		//----------handle target users--------//
		Set<User> targetUsers = popup.getTargetUsers();
		Set<String> roles = (popup.getTargetRoles() != null) ? popup.getTargetRoles() : new HashSet<String>();
		Set<Standing> standings = (popup.getTargetStandings() != null) ? popup.getTargetStandings() : new HashSet<Standing>();
		
		if(popup.getIndividualTargetUsers().size() > 0) {
			popup.getTargetUsers().addAll(popup.getIndividualTargetUsers());
		}
		
		for(String role : roles) {
			targetUsers.addAll(userDao.searchUsersByRole(dept, role));
		}
		
		for(Standing standing : standings) {
			targetUsers.addAll(userDao.searchUsersByStanding(dept, standing.getSymbol()));
		}
		//-------------------------------------//

		//-----handle set as news------//
		News news = new News();
		
		if (popup.isSetAsNews()) {
			news.setDepartment(department);
			Post post = news.getTopic().getFirstPost();
			
			post.setAuthor(user);
			post.setDate(new Date());
			post.setSubject(popup.getSubject());
			post.setContent(popup.getContent());
			
			Forum forum = forumDao.getForum(forumId);
			news.getTopic().setForum(forum);
			
			forum.incrementNumOfTopics();
			forum.incrementNumOfPosts();
			forum.setLastPost(post);

			news.setExpireDate(popup.getExpireDate());
			user.incrementNumOfForumPosts();

			popup.setNews(news);
			subscriptionDao.subscribe(popup.getNews().getTopic(), user);
		}
		//-----------------------------//

		popup.setDepartment(department);
		popup.setAuthor(user);
		popup.setCreateDate(new Date());
		popup = popupDao.savePopup(popup);

		logger.info(user.getUsername() + " posted popup " + popup.getId());

		sessionStatus.setComplete();
		return "redirect:/department/" + dept + "/popup/current";
	}

	@RequestMapping(value = "/department/{dept}/popup/edit", method = RequestMethod.GET)
	public String edit(@RequestParam Long id, ModelMap models) {

		List<Standing> standings = standingDao.getStandings();
		Popup popup = popupDao.getPopup(id);
		
		Calendar cal = popup.getPublishDate();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		models.put("formattedPublishDate", df.format(cal.getTime()));
		models.put("roles", roles);
		models.put("rolesMap", rolesMap);
		models.put("standings", standings);
		models.put("popup", popup);

		return "popup/edit";
	}

	@RequestMapping(value = "/department/{dept}/popup/edit", method = RequestMethod.POST)
	public String edit(@ModelAttribute Popup popup, @PathVariable String dept, @RequestParam Long targetUserId,
			@RequestParam Long removeUserId, @RequestParam(required = false) Long forumId, BindingResult result, 
			SessionStatus sessionStatus, ModelMap models) {
		User user = SecurityUtils.getUser();
		Department department = departmentDao.getDepartment(dept);
		Calendar now = Calendar.getInstance();
		
		//------------add individual users------------//
		if(targetUserId != null) {
			Set<User> users = popup.getIndividualTargetUsers();
			boolean add = true;
			for(User u : users) {
				if(u.getId().equals(targetUserId))
					add = false;
			}
			if(add)
				users.add(userDao.getUser(targetUserId));
			
			List<Standing> standings = standingDao.getStandings();

			popup.setDepartment(department);

			models.put("popup", popup);
			models.put("roles", roles);
			models.put("rolesMap", rolesMap);
			models.put("standings", standings);
			models.put("dept", dept);
			return "popup/edit";
		}
		//---------------------------------------------//
		
		//------------remove individual users------------//
		if(removeUserId != null) {
			Set<User> users = popup.getIndividualTargetUsers();
			for(User u : users) {
				if(u.getId().equals(removeUserId)){
					users.remove(u);
					break;
				}
			}
			Set<User> allUsers = popup.getTargetUsers();
			for(User u : allUsers) {
				if(u.getId().equals(removeUserId)){
					users.remove(u);
					break;
				}
			}
			
			List<Standing> standings = standingDao.getStandings();

			popup.setDepartment(department);

			models.put("popup", popup);
			models.put("roles", roles);
			models.put("rolesMap", rolesMap);
			models.put("standings", standings);
			models.put("dept", dept);
			return "popup/edit";
		}
		//---------------------------------------------//

		popupValidator.validate(popup, result);
		if (result.hasErrors()) {
			List<Standing> standings = standingDao.getStandings();
			Calendar cal = popup.getPublishDate();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			
			models.put("formattedPublishDate", df.format(cal.getTime()));
			models.put("roles", roles);
			models.put("rolesMap", rolesMap);
			models.put("standings", standings);
			return "popup/edit";
		}
		
		//----------handle target users--------//
		Set<User> targetUsers =	new HashSet<User>();
		popup.setTargetUsers(targetUsers);
		Set<String> roles = (popup.getTargetRoles() != null) ? popup.getTargetRoles() : new HashSet<String>();
		Set<Standing> standings = (popup.getTargetStandings() != null) ? popup.getTargetStandings() : new HashSet<Standing>();
		
		if(popup.getIndividualTargetUsers().size() > 0) {
			targetUsers.addAll(popup.getIndividualTargetUsers());
		}
		
		for(String role : roles) {
			targetUsers.addAll(userDao.searchUsersByRole(dept, role));
		}
		
		for(Standing standing : standings) {
			targetUsers.addAll(userDao.searchUsersByStanding(dept, standing.getSymbol()));
		}
		//-------------------------------------//
		
		//-----handle set as news------//
		if (popup.isSetAsNews() && popup.getNews() == null && popup.getPublishDate().after(now)) {
			News news = new News();
			news.setDepartment(department);

			Post post = news.getTopic().getFirstPost();
			
			post.setAuthor(user);
			post.setDate(new Date());
			post.setSubject(popup.getSubject());
			post.setContent(popup.getContent());
			
			Forum forum = forumDao.getForum(forumId);
			news.getTopic().setForum(forum);
			
			forum.incrementNumOfTopics();
			forum.incrementNumOfPosts();
			forum.setLastPost(post);

			news.setExpireDate(popup.getExpireDate());
			user.incrementNumOfForumPosts();

			popup.setNews(news);
			subscriptionDao.subscribe(popup.getNews().getTopic(), user);
			
		} else if (popup.isSetAsNews() && popup.getNews() != null && popup.getPublishDate().after(now)) {
			News news = popup.getNews();

			Post post = news.getTopic().getFirstPost();
			post.setEditedBy( user );
	        post.setEditDate( new Date() );
			post = postDao.savePost(post);

			if (!news.getTopic().getForum().getId().equals(forumId)) {
				Forum forum = forumDao.getForum(forumId);
				news.getTopic().setForum(forum);
				
				forum.incrementNumOfTopics();
				forum.incrementNumOfPosts();
				forum.setLastPost(post);

				news.setExpireDate(popup.getExpireDate());
				user.incrementNumOfForumPosts();

				subscriptionDao.subscribe(news.getTopic(), user);
			}
			popup.setNews(news);

		} else if (!popup.isSetAsNews() && popup.getNews() != null && popup.getPublishDate().after(now)) {
			News news = newsDao.getNews(popup.getNews().getId());
			news.setExpireDate(Calendar.getInstance());
			news = newsDao.saveNews(news);
			popup.setNews(null);
		}
		
		News news = popup.getNews();
		if(news != null) {
			news.setExpireDate(popup.getExpireDate());
			popup.setNews(news);
			news = newsDao.saveNews(news);
		}
		//-------------------------------//
		popup = popupDao.savePopup(popup);

		logger.info(user.getUsername() + " edited popup " + popup.getId());

		sessionStatus.setComplete();
		return "redirect:/department/" + dept + "/popup/current";
	}
	
	
	@RequestMapping(value = "/department/{dept}/popup/seenUsers", method = RequestMethod.GET)
	public String seenUsers(@RequestParam Long id, ModelMap models) {

		Popup popup = popupDao.getPopup(id);
		models.put("popup", popup);

		return "popup/seenUsers";
	}
	
	@RequestMapping(value = "/department/{dept}/popup/remainingUsers", method = RequestMethod.GET)
	public String remainingUsers(@RequestParam Long id, ModelMap models) {

		Popup popup = popupDao.getPopup(id);
		models.put("popup", popup);

		return "popup/remainingUsers";
	}

}
