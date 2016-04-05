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

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import csns.model.academics.Course;
import csns.model.academics.OfferedSection;
import csns.model.academics.Term;
import csns.model.academics.dao.OfferedSectionDao;

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
        String query = "from OfferedSection "
            + "where course = :course and number = :number "
        	+ "and deleted = false";

        List<OfferedSection> sections = entityManager.createQuery( query,
    		OfferedSection.class )
            .setParameter( "course", course )
            .setParameter( "number", number )
            .getResultList();
        return sections.size() == 0 ? null : sections.get( 0 );
    }

    @Override
    @Transactional
    public OfferedSection deleteSection( OfferedSection section )
    {
        section.getInstructors().clear();
        section.setDeleted( true );
        return entityManager.merge( section );
    }

    @Override
    @Transactional
    public OfferedSection saveSection( OfferedSection section )
    {
        return entityManager.merge( section );
    }

}
