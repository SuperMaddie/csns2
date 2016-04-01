package csns.web.controller;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Course;
import csns.model.academics.Standing;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.model.core.User;
import csns.security.SecurityUtils;
import csns.web.editor.CalendarPropertyEditor;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.StandingPropertyEditor;
import csns.web.editor.TermPropertyEditor;
import csns.web.editor.UserPropertyEditor;

@Controller
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
		binder.registerCustomEditor(Standing.class, (StandingPropertyEditor) context.getBean("standingPropertyEditor"));
		binder.registerCustomEditor(Term.class, (TermPropertyEditor) context.getBean("termPropertyEditor"));
		binder.registerCustomEditor(User.class, (UserPropertyEditor) context.getBean("userPropertyEditor"));
		binder.registerCustomEditor(Course.class, (CoursePropertyEditor) context.getBean("coursePropertyEditor"));

		binder.registerCustomEditor(Calendar.class, new CalendarPropertyEditor("MM/dd/yyyy"));
	}
	
	@RequestMapping(value = "/department/{dept}/tentativeSchedule/create", method = RequestMethod.GET)
	public String create(@PathVariable String dept, @RequestParam Term term, ModelMap models) {
		
		TentativeSchedule schedule = new TentativeSchedule();
		schedule.setDepartment(departmentDao.getDepartment(dept));
		schedule.setTerm(term);
		schedule = scheduleDao.saveSchedule(schedule);
		
		logger.info(SecurityUtils.getUser() + " created schedule " + schedule.getId());
		
		return "redirect:/department/" + dept + "/preRegistration/manage?term=" + term;
	}
	
	@RequestMapping(value = "/department/{dept}/tentativeSchedule/edit", method = RequestMethod.GET)
	public String edit(@PathVariable String dept, @RequestParam Term term, ModelMap models, 
			@RequestParam(required=false) Calendar publishDate, @RequestParam(required=false) Calendar expireDate) {
		
		TentativeSchedule schedule = scheduleDao.getSchedule(departmentDao.getDepartment(dept), term);
		if(schedule != null && publishDate != null) {
			schedule.setPublishDate(publishDate);
		}
		if(schedule != null && expireDate != null) {
			schedule.setExpireDate(expireDate);
		}
		schedule = scheduleDao.saveSchedule(schedule);
		logger.info(SecurityUtils.getUser().getUsername() + " editted schedule " + schedule.getId());
		
		return "redirect:/department/" + dept + "/preRegistration/manage?term=" + term;
	}
}
