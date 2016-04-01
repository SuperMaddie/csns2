package csns.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.model.academics.dao.TermDao;
import csns.model.preRegistration.request.dao.PreRegistrationRequestDao;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.TermPropertyEditor;

@Controller
@SessionAttributes({ "section" })
public class PreRegistrationController {

	@Autowired
	private TentativeScheduleDao scheduleDao;
	
	@Autowired
	private TermDao termDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	PreRegistrationRequestDao requestDao;

	@Autowired
	private WebApplicationContext context;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Term.class, (TermPropertyEditor) context.getBean("termPropertyEditor"));
		binder.registerCustomEditor(Course.class, (CoursePropertyEditor) context.getBean("coursePropertyEditor"));
	}

	@RequestMapping("/department/{dept}/preRegistration")
	public String list(@PathVariable String dept, ModelMap models, @RequestParam(required=false) Term term) {
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);

		TentativeSchedule schedule = scheduleDao.getSchedule(department, term) != null
				? scheduleDao.getSchedule(department, term) : new TentativeSchedule();

		Term currentTerm = new Term();
		Term nextTerm = currentTerm.next();
		Term nextTerm2 = nextTerm.next();
		Term nextTerm3 = nextTerm2.next();
		if (term == null) {
			term = nextTerm;
		}
		if (!terms.contains(term)) {
			terms.add(0, term);
		}
		if (!terms.contains(nextTerm3)) {
			terms.add(0, nextTerm3);
		}
		if (!terms.contains(nextTerm2)) {
			terms.add(0, nextTerm2);
		}
		if(!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}

		models.put("department", department);
		models.put("term", term);
		models.put("terms", terms);
		models.put("schedule", schedule);
		models.put("terms", terms);
		return "preRegistration/list";
	}
	
	
	@RequestMapping(value = "/department/{dept}/preRegistration/manage", method = RequestMethod.GET)
	@PreAuthorize("principal.isFaculty(#dept)")
	public String manage(@PathVariable String dept, ModelMap models, @RequestParam(required=false) Term term) {
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);

		TentativeSchedule schedule = scheduleDao.getSchedule(department, term);

		Term currentTerm = new Term();
		Term nextTerm = currentTerm.next();
		Term nextTerm2 = nextTerm.next();
		Term nextTerm3 = nextTerm2.next();
		if (term == null) {
			term = nextTerm;
		}
		if (!terms.contains(term)) {
			terms.add(0, term);
		}
		if (!terms.contains(nextTerm3)) {
			terms.add(0, nextTerm3);
		}
		if (!terms.contains(nextTerm2)) {
			terms.add(0, nextTerm2);
		}
		if(!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}

		models.put("department", department);
		models.put("term", term);
		models.put("terms", terms);
		models.put("schedule", schedule);
		models.put("terms", terms);
		return "preRegistration/manage";
	}
	
	@RequestMapping(value = "/department/{dept}/preRegistration/manage", method = RequestMethod.POST)
	@PreAuthorize("principal.isFaculty(#dept)")
	public String manage(@PathVariable String dept, ModelMap models, @RequestParam(required=false) Term term,
			@ModelAttribute TentativeSchedule schedule, SessionStatus status) {
		
		return "";
	}

}