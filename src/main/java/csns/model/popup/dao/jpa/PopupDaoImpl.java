/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2016, Mahdiye Jamali (mjamali@calstatela.edu).
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
package csns.model.popup.dao.jpa;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import csns.model.academics.Department;
import csns.model.academics.dao.DepartmentDao;
import csns.model.core.User;
import csns.model.popup.Popup;
import csns.model.popup.dao.PopupDao;

@Repository
public class PopupDaoImpl implements PopupDao{
	
	@Autowired
	DepartmentDao departmentDao;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Popup getPopup(Long id) {
		return entityManager.find(Popup.class, id);
	}
	
	@Override
	public List<Popup> getPopups( User user ) {		
		
		String query = "Select popup from Popup popup "
				+ "join popup.targetUsers user "
				+ "where user.id = :userId "
                + "and popup.expireDate > :now "
                + "and popup.publishDate < :now order by popup.id desc";

            return entityManager.createQuery( query, Popup.class )
                .setParameter( "userId", user.getId() )
                .setParameter( "now", Calendar.getInstance() )
                .getResultList();
	}
	
	@Override
	public List<Popup> getPopups( Department department) {
        String query = "from Popup where department = :department "
                + "and expireDate > :now order by id desc";

            return entityManager.createQuery( query, Popup.class )
                .setParameter( "department", department )
                .setParameter( "now", Calendar.getInstance() )
                .getResultList();
	}

	@Override
	@Transactional
	public Popup savePopup(Popup popup) {
		return entityManager.merge( popup );
	}
}
