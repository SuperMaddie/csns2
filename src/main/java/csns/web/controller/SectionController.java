/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012, Chengyu Sun (csun@calstatela.edu).
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.Quarter;
import csns.model.academics.Section;
import csns.model.academics.dao.CourseDao;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.QuarterDao;
import csns.model.academics.dao.SectionDao;
import csns.model.core.User;
import csns.model.core.dao.UserDao;
import csns.security.SecurityUtils;
import csns.web.editor.QuarterPropertyEditor;

@Controller
public class SectionController {

    @Autowired
    UserDao userDao;

    @Autowired
    CourseDao courseDao;

    @Autowired
    QuarterDao quarterDao;

    @Autowired
    SectionDao sectionDao;

    @Autowired
    DepartmentDao departmentDao;

    @Autowired
    WebApplicationContext context;

    private static final Logger logger = LoggerFactory.getLogger( SectionController.class );

    @InitBinder
    public void initBinder( WebDataBinder binder )
    {
        binder.registerCustomEditor( Quarter.class,
            (QuarterPropertyEditor) context.getBean( "quarterPropertyEditor" ) );
    }

    private String list( String type, Quarter quarter, ModelMap models,
        HttpSession session )
    {
        if( quarter != null )
            session.setAttribute( "quarter", quarter );
        else if( session.getAttribute( "quarter" ) != null )
            quarter = (Quarter) session.getAttribute( "quarter" );
        else
            quarter = new Quarter();

        User user = SecurityUtils.getUser();
        List<Quarter> quarters = null;
        List<Section> sections = null;
        String view = "error";
        switch( type )
        {
            case "taught":
                quarters = quarterDao.getQuartersByInstructor( user );
                sections = sectionDao.getSectionsByInstructor( user, quarter );
                view = "section/taught";
                break;
            case "taken":
                quarters = quarterDao.getQuartersByStudent( user );
                sections = sectionDao.getSectionsByStudent( user, quarter );
                view = "section/taken";
                break;
            default:
                logger.warn( "Invalid section type: " + type );
        }

        Quarter currentQuarter = new Quarter();
        if( !quarters.contains( currentQuarter ) )
            quarters.add( 0, currentQuarter );

        models.put( "quarter", quarter );
        models.put( "quarters", quarters );
        models.put( "sections", sections );

        return view;
    }

    @RequestMapping("/section/taught")
    public String taught( @RequestParam(required = false) Quarter quarter,
        ModelMap models, HttpSession session )
    {
        return list( "taught", quarter, models, session );
    }

    @RequestMapping("/section/taken")
    public String taken( @RequestParam(required = false) Quarter quarter,
        ModelMap models, HttpSession session )
    {
        return list( "taken", quarter, models, session );
    }

    @RequestMapping("/section/add")
    public String add( @RequestParam Long courseId,
        @RequestParam Integer quarterCode )
    {
        Quarter quarter = new Quarter( quarterCode );
        Course course = courseDao.getCourse( courseId );
        User instructor = SecurityUtils.getUser();
        sectionDao.addSection( quarter, course, instructor );

        return "redirect:/section/taught";
    }

    @RequestMapping("/section/delete")
    public String delete( @RequestParam Long id, ModelMap models )
    {
        Section section = sectionDao.getSection( id );
        if( section.getEnrollments().size() > 0
            && section.getInstructors().size() == 1 )
        {
            models.put( "message", "error.section.nonempty" );
            models.put( "backUrl", "/section/taught#section-" + section.getId() );
            return "error";
        }

        User user = SecurityUtils.getUser();
        for( User instructor : section.getInstructors() )
            if( instructor.getId().equals( user.getId() ) )
            {
                section.getInstructors().remove( instructor );
                sectionDao.saveSection( section );
                break;
            }

        return "redirect:/section/taught";
    }

    @RequestMapping(value = "/section/edit", method = RequestMethod.GET)
    public String edit( @RequestParam Long id, ModelMap models )
    {
        Section section = sectionDao.getSection( id );
        Department department = departmentDao.getDepartment( section.getCourse()
            .getDepartmentCode() );

        List<User> instructors = new ArrayList<User>();
        instructors.addAll( department.getFaculty() );
        instructors.addAll( department.getInstructors() );
        instructors.removeAll( section.getInstructors() );

        JSONArray jsonArray = new JSONArray();
        for( User instructor : instructors )
        {
            Map<String, String> json = new HashMap<String, String>();
            json.put( "id", instructor.getId().toString() );
            json.put( "value", instructor.getName() );
            json.put( "label", instructor.getCin() + " " + instructor.getName() );
            jsonArray.put( json );
        }

        models.put( "section", section );
        models.put( "instructors", jsonArray );

        return "section/edit";
    }

    @RequestMapping(value = "/section/edit", params = "instructor=add")
    public String addInstructor( @RequestParam Long id,
        @RequestParam Long instructorId )
    {
        Section section = sectionDao.getSection( id );
        User instructor = userDao.getUser( instructorId );
        if( !section.getInstructors().contains( instructor ) )
        {
            section.getInstructors().add( instructor );
            sectionDao.saveSection( section );
        }

        return "redirect:/section/edit?id=" + id;
    }

    @RequestMapping(value = "/section/edit", params = "instructor=remove")
    public String removeInstructor( @RequestParam Long id,
        @RequestParam Long instructorId )
    {
        Section section = sectionDao.getSection( id );
        User instructor = userDao.getUser( instructorId );
        if( section.getInstructors().contains( instructor ) )
        {
            section.getInstructors().remove( instructor );
            sectionDao.saveSection( section );
        }

        return "redirect:/section/edit?id=" + id;
    }

}
