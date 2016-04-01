/*
 * This file is part of the CSNetwork Services (CSNS) project.
 *
 * Copyright 2013, Mahdiye Jamali (mjamali@calstatela.edu).
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.OfferedSection;
import csns.model.academics.Standing;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.OfferedSectionDao;
import csns.model.academics.dao.StandingDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.model.core.Day;
import csns.model.core.User;
import csns.security.SecurityUtils;
import csns.web.editor.CalendarPropertyEditor;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.StandingPropertyEditor;
import csns.web.editor.TermPropertyEditor;
import csns.web.editor.UserPropertyEditor;
import csns.web.validator.OfferedSectionValidator;

@Controller
@SessionAttributes({ "section" })
public class OfferedSectionControllerS {

	@Autowired
	private TentativeScheduleDao scheduleDao;
	
	@Autowired
	private StandingDao standingDao;

	@Autowired
	private OfferedSectionDao offeredSectionDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private OfferedSectionValidator offeredSectionValidator;

	private static final Logger logger = LoggerFactory.getLogger(OfferedSectionControllerS.class);

	private List<Day> days;
	private List<Standing> standings;

	@PostConstruct
	public void init() {
		days = Arrays.asList(Day.values());
		standings = standingDao.getStandings();
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Standing.class, (StandingPropertyEditor) context.getBean("standingPropertyEditor"));
		binder.registerCustomEditor(Term.class, (TermPropertyEditor) context.getBean("termPropertyEditor"));
		binder.registerCustomEditor(User.class, (UserPropertyEditor) context.getBean("userPropertyEditor"));
		binder.registerCustomEditor(Course.class, (CoursePropertyEditor) context.getBean("coursePropertyEditor"));

		binder.registerCustomEditor(Calendar.class, new CalendarPropertyEditor("MM/dd/yyyy"));
	}

	@RequestMapping(value = "/department/{dept}/offeredSection/offer", method = RequestMethod.GET)
	public String offer(@PathVariable String dept, @RequestParam Term term, ModelMap models) {

		Department department = departmentDao.getDepartment(dept);
		OfferedSection section = new OfferedSection();

		models.put("standings", standings);
		models.put("days", days);
		models.put("term", term);
		models.put("department", department);
		models.put("section", section);

		return "offeredSection/offer";
	}

	@RequestMapping(value = "/department/{dept}/offeredSection/offer", method = RequestMethod.POST)
	public String offer(@ModelAttribute("section") OfferedSection section, @PathVariable String dept,
			@RequestParam Term term, SessionStatus sessionStatus, BindingResult result) {

		Department department = departmentDao.getDepartment(dept);
		offeredSectionValidator.validate(section, result);
		if (result.hasErrors()) {
		}
		TentativeSchedule schedule = scheduleDao.getSchedule(department, term);
		schedule.getSections().add(section);

		schedule = scheduleDao.saveSchedule(schedule);
		
		logger.info(SecurityUtils.getUser().getUsername() + " added section " + section.getCourse().getCode() + "-" + section.getNumber()
						+ " to schedule of term " + term.getShortString());

		sessionStatus.setComplete();
		return "redirect:/department/" + dept + "/preRegistration/manage?term=" + term.getCode();
	}

	@RequestMapping(value = "/department/{dept}/offeredSection/edit", method = RequestMethod.GET)
	public String edit(@PathVariable String dept, @RequestParam Term term, @RequestParam Long id, ModelMap models) {

		Department department = departmentDao.getDepartment(dept);
		OfferedSection section = offeredSectionDao.getSection(id);

		models.put("standings", standings);
		models.put("days", days);
		models.put("term", term);
		models.put("department", department);
		models.put("section", section);
		return "offeredSection/edit";
	}

	@RequestMapping(value = "/department/{dept}/offeredSection/edit", method = RequestMethod.POST)
	public String edit(@ModelAttribute("section") OfferedSection section, @RequestParam Term term, @PathVariable String dept,
			SessionStatus sessionStatus, BindingResult result) {

		section = offeredSectionDao.saveSection(section);
		logger.info(SecurityUtils.getUser().getUsername() + " editted section " + section.getCourse().getCode() + 
				"-" + section.getNumber() + " of term " + term.getShortString());

		sessionStatus.setComplete();
		return "redirect:/department/" + dept + "/preRegistration/manage?term=" + term.getCode();
	}

	@RequestMapping(value = "/department/{dept}/offeredSection/delete")
	public String delete(@PathVariable String dept, @RequestParam Long id, @RequestParam Term term) {

		TentativeSchedule schedule = scheduleDao.getSchedule(departmentDao.getDepartment(dept), term);
		
		OfferedSection section = offeredSectionDao.deleteSection(offeredSectionDao.getSection(id));
		
		//delete section from schedule
		List<OfferedSection> sections = schedule.getSections();
		for(OfferedSection sec : sections) {
			if(sec.getId() == section.getId()) {
				sections.remove(sec);
				break;
			}
		}
		schedule = scheduleDao.saveSchedule(schedule);

		logger.info(SecurityUtils.getUser().getName() + " deleted offered section " + section.getId()
				+ " " + section.getCourse().getCode() + "-" + section.getNumber());

		return "redirect:/department/" + dept + "/preRegistration/manage?term=" + term.getCode();
	}

}
