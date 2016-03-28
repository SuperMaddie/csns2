package csns.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.TermDao;
import csns.model.preRegistration.request.dao.PreRegistrationRequestDao;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.TermPropertyEditor;

@Controller
@SessionAttributes({ "section" })
public class PreRegistrationController {

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

	@RequestMapping("/department/{dept}/preRegistration/list")
	public String list(@PathVariable String dept, ModelMap models) {
		Department department = departmentDao.getDepartment(dept);
		List<Term> terms = termDao.getOfferedSectionTerms(department);

		models.put("department", department);
		models.put("quarters", terms);
		return "preRegistration/list";
	}

}