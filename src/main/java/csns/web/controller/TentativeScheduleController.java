package csns.web.controller;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Department;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.security.SecurityUtils;
import csns.web.editor.CalendarPropertyEditor;
import csns.web.editor.TermPropertyEditor;

@Controller
@SessionAttributes("schedule")
public class TentativeScheduleController {

	@Autowired
	private TentativeScheduleDao scheduleDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private WebApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(TentativeSchedule.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Term.class, (TermPropertyEditor) context.getBean("termPropertyEditor"));
		binder.registerCustomEditor(Calendar.class, new CalendarPropertyEditor("MM/dd/yyyy"));
	}

	@RequestMapping(value = "/department/{dept}/tentativeSchedule/edit", method = RequestMethod.GET)
	public String edit(@PathVariable String dept, @RequestParam Term term, ModelMap models) {
		
		Department department = departmentDao.getDepartment(dept);
		TentativeSchedule schedule = scheduleDao.getSchedule(department, term);
		models.put("schedule", schedule);
		models.put("term", term);
		models.put("department", department);
		return "tentativeSchedule/edit";
	}

	@RequestMapping(value = "/department/{dept}/tentativeSchedule/edit", method = RequestMethod.POST)
	public String edit(@PathVariable String dept, ModelMap models,
			@ModelAttribute("schedule") TentativeSchedule schedule) {

		schedule = scheduleDao.saveSchedule(schedule);
		logger.info(SecurityUtils.getUser().getUsername() + " editted schedule " + schedule.getId());
		return "redirect:/department/" + dept + "/preRegistration?term=" + schedule.getTerm().getCode();
	}
}
