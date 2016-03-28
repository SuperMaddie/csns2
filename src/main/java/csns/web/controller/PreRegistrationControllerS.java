/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2014, Mahdiye Jamali (mjamali@calstatela.edu).
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

import java.util.Date;
import java.util.List;

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
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.OfferedSectionDao;
import csns.model.preRegistration.request.PreRegistrationRequest;
import csns.model.preRegistration.request.dao.PreRegistrationRequestDao;
import csns.security.SecurityUtils;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.OfferedSectionPropertyEditor;
import csns.web.editor.TermPropertyEditor;

@Controller
@SessionAttributes({"request", })
public class PreRegistrationControllerS {
	
	@Autowired
	private OfferedSectionDao offeredSectionDao;
	@Autowired
	private DepartmentDao departmentDao;
	@Autowired
	private PreRegistrationRequestDao preRegistrationRequestDao;
	
	private static final Logger logger = LoggerFactory.getLogger(PreRegistrationControllerS.class);
	
    @Autowired
    private WebApplicationContext context;
    
    @InitBinder
    public void initBinder( WebDataBinder binder )
    {
		binder.registerCustomEditor(OfferedSection.class,
				(OfferedSectionPropertyEditor) context.getBean( "offeredSectionPropertyEditor" ));
        binder.registerCustomEditor( Term.class,
            (TermPropertyEditor) context.getBean( "quarterPropertyEditor" ) );
        binder.registerCustomEditor( Course.class,
            (CoursePropertyEditor) context.getBean( "coursePropertyEditor" ) );
    }
	
	@RequestMapping(value="/department/{dept}/preRegistration/request", method=RequestMethod.GET)
	public String request(@PathVariable String dept, @RequestParam Term term, ModelMap models){
		
		Department department = departmentDao.getDepartment(dept);
		List<OfferedSection> sections = offeredSectionDao.getSections(department, term);
		PreRegistrationRequest request = new PreRegistrationRequest();
		
		models.put("request", request);
		models.put("sections", sections);
		models.put("department", department);
		return "preRegistration/request";
	}
	
	@RequestMapping(value="/department/{dept}/preRegistration/request", method=RequestMethod.POST)
	public String request(@PathVariable String dept, @ModelAttribute PreRegistrationRequest request, 
			BindingResult result, SessionStatus sessionStatus){
		
		request.setRequester(SecurityUtils.getUser());
		request.setDate(new Date());

		request = preRegistrationRequestDao.saveRequest(request);
		sessionStatus.setComplete();
		return "redirect:/department/"+ dept + "/preRegistration/list";
	}

}
