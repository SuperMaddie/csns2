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

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import csns.model.academics.Department;
import csns.model.academics.dao.DepartmentDao;
import csns.model.core.User;
import csns.model.popup.Popup;
import csns.model.popup.dao.PopupDao;
import csns.security.SecurityUtils;

@Controller
public class PopupController {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(PopupController.class);

	@Autowired
	private PopupDao popupDao;

	@Autowired
	private DepartmentDao departmentDao;

	List<Popup> popups;

	@RequestMapping(value = "/department/{dept}/popup/getpopups", method = RequestMethod.GET)
	protected void doGet(@PathVariable String dept, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * Receive a request from Ajax to send back a list of popups for the
		 * logged in user Check in popups and see if there is a popup the user
		 * hasn't seen, if there is add those to result from user_popups table
		 */
		User user = SecurityUtils.getUser();

		popups = popupDao.getPopups(user);

		//response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.write(popupsToJson(popups));
		out.flush();
		out.close();

	}

	private String jsonEscape(String str) {
		return str.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}

	private String popupsToJson(List<Popup> popups) {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		sb.append("{ \"count\" : \"" + popups.size() + "\",");
		sb.append(" \"popups\" : [");

		for (int i = 0; i < popups.size(); i++) {

			sb.append("{ \"id\" : \"" + popups.get(i).getId() + "\", \"subject\" : \"" + popups.get(i).getSubject()
					+ "\", \"content\" : \"" + popups.get(i).getContent() + "\", \"author\" : \"" + popups.get(i).getAuthor().getName() 
					+ "\", \"date\" : \"" + dateFormat.format(popups.get(i).getPublishDate().getTime()) + "\"}");
			if (i < popups.size() - 1)
				sb.append(", ");
		}
		sb.append("]");
		sb.append("}");

		return jsonEscape(sb.toString());
	}

	@RequestMapping(value = "/department/{dept}/popup/getpopups", method = RequestMethod.POST)
	protected void doPost(@PathVariable String dept, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		User user = SecurityUtils.getUser();
		List<Long> ids = new ArrayList<>();
		
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(request.getParameter("seenPopups"));
			JSONArray seenIds = jsonObj.getJSONArray("seenPopups");
			for(int i = 0; i<seenIds.length(); i++) {
				ids.add(Long.parseLong(seenIds.get(i).toString()));
				//System.out.println(user.getName() + " saw popup " + seenIds.get(i).toString());
				logger.info(user.getName() + " saw popup " + seenIds.get(i).toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//---------remove user from target users and add to read users (for seen popups)----------//
		Popup popup;
		User removeUser = new User();
		for(Long l : ids) {
			popup = popupDao.getPopup(l);
			for(User u : popup.getTargetUsers()){
				if(u.getId().equals(user.getId())) {
					removeUser = u;
					break;
				}
			}
			popup.getTargetUsers().remove(removeUser);
			popup.getReadUsers().add(removeUser);
			
			popupDao.savePopup(popup);
		}
		//----------------------------------------------------------------//
	}

	@RequestMapping("/department/{dept}/popup/current")
	public String current(@PathVariable String dept, ModelMap models) {
		User user = SecurityUtils.getUser();
		Department department = departmentDao.getDepartment(dept);
		models.put("user", user);
		models.put("popups", popupDao.getPopups(department));
		return "popup/current";
	}

	@RequestMapping("/department/{dept}/popup/delete")
	public String delete(@PathVariable String dept, @RequestParam Long id) {
		Popup popup = popupDao.getPopup(id);
		popup.setExpireDate(Calendar.getInstance());
		if(popup.getNews() != null)
			popup.getNews().setExpireDate(Calendar.getInstance());
		popup = popupDao.savePopup(popup);

		logger.info(SecurityUtils.getUser().getUsername() + " deleted popup " + popup.getId());

		return "redirect:/department/" + dept + "/popup/current";
	}

}
