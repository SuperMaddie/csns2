/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012-2014, Mahdiye Jamali (mjamali@calstatela.edu).
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
package csns.model.academics.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.OfferedSection;
import csns.model.academics.Term;
import csns.model.academics.dao.OfferedSectionDao;
import csns.model.core.User;

@Repository
public class OfferedSectionDaoImpl implements OfferedSectionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OfferedSection getSection( Long id )
    {
        return entityManager.find( OfferedSection.class, id );
    }

    @Override
    public OfferedSection getSection( Term term, Course course, int number )
    {
//        String query = "from OfferedSection "
//            + "course = :course and number = :number "
//        	+ "and deleted = false";
//
//        List<OfferedSection> sections = entityManager.createQuery( query,
//    		OfferedSection.class )
//            .setParameter( "course", course )
//            .setParameter( "number", number )
//            .getResultList();
//        return sections.size() == 0 ? null : sections.get( 0 );
    	return null;
    }

    public List<OfferedSection> getUndergraduateSections( Department department,
        Term term )
    {
//        String query = "select s from OfferedSection s, "
//            + "Department d join d.undergraduateCourses c "
//            + "where d = :department and s.term = :term and s.course = c "
//            + "and s.deleted = false "
//            + "order by c.code asc, s.number asc";
//
//        return entityManager.createQuery( query, OfferedSection.class )
//            .setParameter( "department", department )
//            .setParameter( "term", term )
//            .getResultList();
    	return null;
    }

    public List<OfferedSection> getGraduateSections( Department department,
		Term term )
    {
//        String query = "select s from OfferedSection s, "
//            + "Department d join d.graduateCourses c "
//            + "where d = :department and s.term = :term and s.course = c "
//            + "and s.deleted = false "
//            + "order by c.code asc, s.number asc";
//
//        return entityManager.createQuery( query, OfferedSection.class )
//            .setParameter( "department", department )
//            .setParameter( "term", term )
//            .getResultList();
    	return null;
    }

    @Override
    public List<OfferedSection> getSections( Department department, Term term )
    {
//        List<OfferedSection> sections = new ArrayList<OfferedSection>();
//        sections.addAll( getUndergraduateSections( department, term ) );
//        sections.addAll( getGraduateSections( department, term ) );
//        return sections;
    	return null;
    }

    @Override
    public List<OfferedSection> getSectionsByInstructor( User instructor,
        Term term )
    {
//        String query = "select section from OfferedSection section "
//            + "join section.instructors instructor "
//            + "where instructor = :instructor and section.term = :term "
//            + "and section.deleted = false "
//            + "order by section.course.code asc, section.number asc";
//
//        return entityManager.createQuery( query, OfferedSection.class )
//            .setParameter( "instructor", instructor )
//            .setParameter( "term", term )
//            .getResultList();
    	return null;
    }

    @Override
    public List<OfferedSection> getSectionsByInstructor( User instructor,
        Term term, Course course )
    {
//        String query = "select section from OfferedSection section "
//            + "join section.instructors instructor "
//            + "where instructor = :instructor and section.term = :term "
//            + "and section.course = :course "
//            + "and s.deleted = false "
//            + "order by section.course.code asc, section.number asc";
//
//        return entityManager.createQuery( query, OfferedSection.class )
//            .setParameter( "instructor", instructor )
//            .setParameter( "term", term )
//            .setParameter( "course", course )
//            .getResultList();
    	return null;
    }

    @Override
    public List<OfferedSection> getSectionsByStudent( User student, Term term )
    {
//        String query = "select section from Section section "
//            + "join section.appliedUsers user "
//            + "where user = :student and section.term = :term "
//            + "and section.deleted = false "
//            + "order by section.course.code asc, section.number asc";
//
//        return entityManager.createQuery( query, OfferedSection.class )
//            .setParameter( "student", student )
//            .setParameter( "term", term )
//            .getResultList();
    	return null;
    }

    @Override
    public List<OfferedSection> searchSections( String term, int maxResults )
    {
//        TypedQuery<OfferedSection> query = entityManager.createNamedQuery(
//            "offeredSection.search", OfferedSection.class );
//        if( maxResults > 0 ) query.setMaxResults( maxResults );
//        return query.setParameter( "term", term ).getResultList();
    	return null;
    }

    @Override
    @Transactional
    @PreAuthorize("authenticated and (principal.admin or #section.isInstructor(principal))")
    public OfferedSection addSection( Term term, Course course, User instructor )
    {
//        String query = "select max(number) from OfferedSection "
//            + "where term = :term and course = :course";
//
//        Integer result = entityManager.createQuery( query, Integer.class )
//            .setParameter( "term", term )
//            .setParameter( "course", course )
//            .getSingleResult();
//        Integer currentNum = result == null ? 0 : result;
//
//        OfferedSection section = new OfferedSection();
//        section.setCourse( course );
//        section.getInstructors().add( instructor );
//        section.setNumber( currentNum + 1 );
//
//        return saveSection( section );
    	return null;
    }

    @Override
    @Transactional
    //@PreAuthorize("authenticated and (principal.admin or #section.isInstructor(principal))")
    public OfferedSection deleteSection( OfferedSection section )
    {
        section.getInstructors().clear();
        section.setDeleted( true );
        return entityManager.merge( section );
    }

    @Override
    @Transactional
    //@PreAuthorize("authenticated and (principal.admin or #section.isInstructor(principal))")
    public OfferedSection saveSection( OfferedSection section )
    {
        return entityManager.merge( section );
    }

}
