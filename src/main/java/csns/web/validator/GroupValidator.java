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

package csns.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import csns.model.academics.Department;
import csns.model.academics.Group;
import csns.model.academics.dao.GroupDao;

@Component
public class GroupValidator implements Validator {
	
	@Autowired
	GroupDao groupDao;

	@Override
	public boolean supports(Class<?> clazz){
		return Group.class.isAssignableFrom( clazz );
	}

	@Override
	public void validate(Object target, Errors errors) {
		Group group = (Group) target;
        Long id = group.getId();
		
		String name = group.getName();
        if( !StringUtils.hasText( name ) )
            errors.rejectValue( "name", "error.field.required" );
        else
        {
            Group g = groupDao.getGroup( name );
            if( g != null && !g.getId().equals( id ) )
                errors.rejectValue( "name", "error.group.name.taken" );
        }
		
		ValidationUtils.rejectIfEmptyOrWhitespace( errors,
	            "description", "error.field.required" );
	}
}
