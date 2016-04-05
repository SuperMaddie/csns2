package csns.web.controller;

import java.util.Calendar;
import java.util.Date;
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
import csns.model.academics.OfferedSection;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.OfferedSectionDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.model.academics.dao.TermDao;
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
	private OfferedSectionDao sectionDao;
	
	@Autowired
	private PreRegistrationRequestDao preRegistrationRequestDao;
	
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
		binder.registerCustomEditor(Calendar.class, new CalendarPropertyEditor("MM/dd/yyyy"));
	}

	@RequestMapping(value = "/department/{dept}/preRegistration", method = RequestMethod.GET)
	public String list(@PathVariable String dept, ModelMap models, @RequestParam(required=false) Term term) {
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);

		Term nextTerm = new Term().next();
		if (term == null) {
			term = nextTerm;
		}
		if (!terms.contains(term)) {
			terms.add(terms.size(), term);
		}
		if(!terms.contains(nextTerm)) {
			terms.add(terms.size(), nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(terms.size(), nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(terms.size(), nextTerm);
		}
		
		TentativeSchedule schedule = scheduleDao.getSchedule(department, term) != null
				? scheduleDao.getSchedule(department, term) : new TentativeSchedule();

		models.put("department", department);
		models.put("term", term);
		models.put("terms", terms);
		models.put("schedule", schedule);
		return "preRegistration/list";
	}
	
	@RequestMapping(value = "/department/{dept}/preRegistration", method = RequestMethod.POST)
	public String list(@PathVariable String dept, ModelMap models, @RequestParam(required=false) Term term,
			@RequestParam(value = "sectionId", required = false) Long ids[], 
			@RequestParam(value="comment", required = false) String comment, SessionStatus sessionStatus) {
		
		//set request parameters
		PreRegistrationRequest request = new PreRegistrationRequest();
		request.setRequester(SecurityUtils.getUser());
		if(comment != null && !comment.isEmpty()){
			request.setComment(comment);
		}
		request.setDate(new Date());
		List<OfferedSection> sections = request.getSections();
		for(Long id : ids){
			sections.add(sectionDao.getSection(id));
		}
		
		request = preRegistrationRequestDao.saveRequest(request);
		sessionStatus.setComplete();
		
		//models.put( "backUrl", "/department/" + dept + "/taken" );
        //models.put( "message", "status.request.sent" );
        //return "status";
		return "redirect:/department/" + dept + "/taken";
	}
	
	
	@RequestMapping(value = "/department/{dept}/preRegistration/manage", method = RequestMethod.GET)
	@PreAuthorize("principal.isFaculty(#dept)")
	public String manage(@PathVariable String dept, ModelMap models, @RequestParam(required=false) Term term) {
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getScheduledTerms(department);

		Term nextTerm = new Term().next();
		if (term == null) {
			term = nextTerm;
		}
		if (!terms.contains(term)) {
			terms.add(terms.size(), term);
		}
		if(!terms.contains(nextTerm)) {
			terms.add(terms.size(), nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(terms.size(), nextTerm);
		}
		nextTerm = nextTerm.next();
		if (!terms.contains(nextTerm)) {
			terms.add(terms.size(), nextTerm);
		}
		
		TentativeSchedule schedule;
		TentativeSchedule dbSchedule = scheduleDao.getSchedule(department, term);
		if(dbSchedule != null){
			schedule = dbSchedule;
		}else {
			schedule = new TentativeSchedule();
			schedule.setTerm(term);
			schedule.setDepartment(department);
			schedule = scheduleDao.saveSchedule(schedule);
		}

		models.put("department", department);
		models.put("term", term);
		models.put("terms", terms);
		models.put("schedule", schedule);
		return "preRegistration/manage";
	}
	
	@RequestMapping(value = "/department/{dept}/preRegistration/manage", method = RequestMethod.POST)
	@PreAuthorize("principal.isFaculty(#dept)")
	public String manage(@PathVariable String dept, ModelMap models, @RequestParam(required=false) Term term,
			@ModelAttribute TentativeSchedule schedule, SessionStatus status) {
		
		Department department = departmentDao.getDepartment(dept);
		TentativeSchedule dbSchedule = scheduleDao.getSchedule(department, term);
		dbSchedule.setExpireDate(schedule.getExpireDate());
		dbSchedule.setPublishDate(schedule.getPublishDate());

		dbSchedule = scheduleDao.saveSchedule(dbSchedule);
		status.setComplete();
		
		return "redirect:/department/" + dept + "/preRegistration/manage?term=" + term.getCode();
	}

}