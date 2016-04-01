/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012, Mahdiye Jamali (mjamali@calstatela.edu).
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.OfferedSection;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.OfferedSectionDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.model.academics.dao.TermDao;
import csns.model.core.User;
import csns.model.preRegistration.request.PreRegistrationRequest;
import csns.model.preRegistration.request.dao.PreRegistrationRequestDao;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.TermPropertyEditor;

@Controller
@SessionAttributes("section")
public class OfferedSectionController {

	@Autowired
	private TentativeScheduleDao scheduleDao;

	@Autowired
	private OfferedSectionDao offeredSectionDao;

	@Autowired
	private PreRegistrationRequestDao requestDao;

	@Autowired
	private TermDao termDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private WebApplicationContext context;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Term.class, (TermPropertyEditor) context.getBean("termPropertyEditor"));
		binder.registerCustomEditor(Course.class, (CoursePropertyEditor) context.getBean("coursePropertyEditor"));
	}

	@RequestMapping("/department/{dept}/offeredSection")
	public String view(@PathVariable String dept, @RequestParam Long id, ModelMap models) {

		Department department = departmentDao.getDepartment(dept);
		OfferedSection section = offeredSectionDao.getSection(id);
		List<PreRegistrationRequest> requests = requestDao.getRequests(section);
		List<User> commenters = new ArrayList<>();
		List<String> comments = new ArrayList<>();
		for (PreRegistrationRequest req : requests) {
			if (req.getComment() != null) {
				commenters.add(req.getRequester());
				comments.add(req.getComment());
			}
		}

		models.put("comments", comments);
		models.put("commenters", commenters);
		models.put("requests", requests);
		models.put("section", section);
		models.put("department", department);
		return "offeredSection/view";
	}

	// Offered Sections
	@RequestMapping("/department/{dept}/offeredSections")
	public String offeredSections(@PathVariable String dept, @RequestParam(required = false) Term term,
			ModelMap models) {
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);
		List<OfferedSection> sections = scheduleDao.getSchedule(department, term) != null
				? scheduleDao.getSchedule(department, term).getSections() : new ArrayList<OfferedSection>();

		Term currentTerm = new Term();
		Term nextTerm = currentTerm.next();
		Term nextNextTerm = nextTerm.next();
		if (term == null) {
			term = nextTerm;
		}
		if (!terms.contains(term)) {
			terms.add(0, term);
		}
		if (term.getCode() == nextTerm.getCode() && !terms.contains(nextNextTerm)) {
			terms.add(0, nextNextTerm);
		} else if (term.getCode() == nextNextTerm.getCode() && !terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}

		models.put("department", department);
		models.put("term", term);
		models.put("terms", terms);
		models.put("sections", sections);
		return "department/offeredSections";
	}

}
