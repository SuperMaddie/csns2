package csns.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.OfferedSection;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.OfferedSectionDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.model.academics.dao.TermDao;
import csns.model.core.User;
import csns.model.core.dao.UserDao;
import csns.model.preRegistration.request.PreRegistrationRequest;
import csns.model.preRegistration.request.dao.PreRegistrationRequestDao;
import csns.security.SecurityUtils;
import csns.web.editor.CalendarPropertyEditor;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.TermPropertyEditor;

@Controller
@SessionAttributes("schedule")
public class PreRegistrationController {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private OfferedSectionDao sectionDao;

	@Autowired
	private PreRegistrationRequestDao requestDao;

	@Autowired
	private TentativeScheduleDao scheduleDao;

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
		binder.registerCustomEditor(Calendar.class, new CalendarPropertyEditor("MM/dd/yyyy"));
	}

	@RequestMapping(value = "/department/{dept}/preRegistration", method = RequestMethod.GET)
	public String list(@PathVariable String dept, ModelMap models, @RequestParam(required = false) Term term) {
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);

		Term nextTerm = new Term().next();
		if (term == null) {
			term = nextTerm;
		}

		TentativeSchedule schedule = scheduleDao.getSchedule(department, term);
		if (schedule != null)
			models.put("schedule", schedule);
		models.put("department", department);
		models.put("term", term);

		if (SecurityUtils.getUser().isStudent(dept)) {
			PreRegistrationRequest request = requestDao.getRequest(SecurityUtils.getUser(), term);
			List<Long> ids = new ArrayList<>();
			String comment = "";
			if (request != null) {
				for (OfferedSection s : request.getSections()) {
					ids.add(s.getId());
				}
				comment = request.getComment();
			}
			models.put("ids", ids);
			models.put("comment", comment);
			terms = termDao.getOpenScheduledTerms(department);
			//---- student sees list of terms with open schedules ---//
			models.put("terms", terms);

			if (schedule != null)
				models.put("limit", schedule.getGraduateLimit());
			return "preRegistration/studentView";
		}
		//----- add future terms for admin/faculty ---//
		if (!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}
		models.put("terms", terms);

		return "preRegistration/view";
	}

	@RequestMapping(value = "/department/{dept}/preRegistration", method = RequestMethod.POST)
	public String list(@PathVariable String dept, ModelMap models,
			@ModelAttribute("schedule") TentativeSchedule schedule, SessionStatus status) {

		schedule = scheduleDao.saveSchedule(schedule);
		status.setComplete();

		return "redirect:/department/" + dept + "/preRegistration?term=" + schedule.getTerm().getCode();
	}

	@RequestMapping(value = "/department/{dept}/preRegistration/createSchedule", method = RequestMethod.POST)
	public String createSchedule(@PathVariable String dept, ModelMap models, @RequestParam(required = false) Term term,
			HttpServletRequest httpRequest) {

		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);

		Term nextTerm = new Term().next();
		if (term == null) {
			term = nextTerm;
		}
		if (!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(0, nextTerm);
		}

		if (httpRequest.getParameter("cancel") != null) {
			return "redirect:/section/taught";
		}

		if (httpRequest.getParameter("create") != null) {
			TentativeSchedule schedule = new TentativeSchedule();
			schedule.setTerm(term);
			schedule.setDepartment(department);
			schedule = scheduleDao.saveSchedule(schedule);

			models.put("department", department);
			models.put("term", term);
			models.put("terms", terms);
			models.put("schedule", schedule);
			return "redirect:/department/" + dept + "/preRegistration?term=" + term.getCode();
		}
		return "redirect:/section/taught";
	}

	@RequestMapping(value = "/department/{dept}/preRegistration/request", method = RequestMethod.POST)
	public String request(@PathVariable String dept, ModelMap models, @RequestParam(required = false) Term term,
			@RequestParam(value = "sectionId", required = false) Long ids[],
			@RequestParam(value = "comment", required = false) String comment) {

		// --------set student request parameters-------//
		PreRegistrationRequest request = requestDao.getRequest(SecurityUtils.getUser(), term);
		if (request == null) {
			request = new PreRegistrationRequest();
			request.setTerm(term);
			request.setRequester(SecurityUtils.getUser());
		}

		request.setComment(comment);
		request.setDate(new Date());
		List<OfferedSection> sections = request.getSections();
		sections.clear();
		// ---- add new sections -------//
		for (Long id : ids) {
			sections.add(sectionDao.getSection(id));
		}

		request = requestDao.saveRequest(request);

		models.put("backUrl", "/section/taken");
		models.put("message", "status.request.sent");
		return "status";
	}
	
	@RequestMapping(value = "/department/{dept}/preRegistration/edit", method = RequestMethod.GET)
	public String edit(@PathVariable String dept, @RequestParam(value="studentId") Long studentId, ModelMap models, @RequestParam(required = false) Term term) {
		
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);
		User user = userDao.getUser(studentId);

		Term nextTerm = new Term().next();
		if (term == null) {
			term = nextTerm;
		}

		TentativeSchedule schedule = scheduleDao.getSchedule(department, term);
		if (schedule != null)
			models.put("schedule", schedule);
		models.put("department", department);
		models.put("term", term);
		models.put("terms", terms);
		models.put("user", user);

		PreRegistrationRequest request = requestDao.getRequest(user, term);
		List<Long> ids = new ArrayList<>();
		if (request != null) {
			for (OfferedSection s : request.getSections()) {
				ids.add(s.getId());
			}
		}
		models.put("ids", ids);
		
		return "preRegistration/edit";
	}
	
	@RequestMapping(value = "/department/{dept}/preRegistration/edit", method = RequestMethod.POST)
	public String edit(@PathVariable String dept, ModelMap models, @RequestParam Term term,
			@RequestParam(value="studentId") Long studentId,
			@RequestParam(value = "sectionId", required = false) Long ids[]) {
		
		// --------set student request parameters-------//
		PreRegistrationRequest request = requestDao.getRequest(userDao.getUser(studentId), term);
		if (request == null) {
			request = new PreRegistrationRequest();
			request.setTerm(term);
			request.setRequester(SecurityUtils.getUser());
		}
		List<OfferedSection> sections = request.getSections();
		sections.clear();
		// ---- add new sections -------//
		for (Long id : ids) {
			sections.add(sectionDao.getSection(id));
		}

		request = requestDao.saveRequest(request);

		models.put("backUrl", "/department/" + dept + "/preRegistration?term=" + term.getCode());
		models.put("message", "status.request.edited");
		return "status";
	}

}