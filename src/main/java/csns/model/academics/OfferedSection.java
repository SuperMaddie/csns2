/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012-2015, Mahdiye Jamali (mjamali@calstatela.edu).
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
package csns.model.academics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import csns.model.core.Day;
import csns.model.core.User;
import csns.model.preRegistration.request.PreRegistrationRequest;

@Entity
@Table(name="offered_sections", uniqueConstraints = @UniqueConstraint(columnNames = { "course_id", "number" }))
public class OfferedSection implements Serializable, Comparable<OfferedSection> {

    private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
    
    @Column
    private int capacity;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToMany(mappedBy = "sections")
    private List<PreRegistrationRequest> requests;
    
    @Column(nullable = false)
    private int number;
    
    @Column
    private Day day;
    
    @Column(name = "start_time")
    private Date startTime;
    
    @Column(name = "end_time")
    private Date endTime;
    
    @Column
    private String location;
    
    @Column
    private boolean deleted;
    
    @ManyToMany
    @JoinTable(name = "offered_section_instructors",
        joinColumns = @JoinColumn(name = "section_id"),
        inverseJoinColumns = @JoinColumn(name = "instructor_id"))
    @OrderColumn(name = "instructor_order")
    private List<User> instructors;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "offered_section_target_standings", 
    joinColumns = @JoinColumn(name = "section_id"), 
    inverseJoinColumns = @JoinColumn(name = "standing_id"))
    private Set<Standing> targetStandings;
	
    public OfferedSection() {
    	number = 1;
    	capacity = 30;
        instructors = new ArrayList<User>();
        deleted = false;
	}
    
	@Override
	public int compareTo(OfferedSection section) {
		if( section == null )
            throw new IllegalArgumentException( "Cannot compare to NULL." );

        if( id.equals( section.id ) ) return 0;

        int cmp = getCourse().getCode().compareTo( section.getCourse().getCode() );
        if( cmp != 0 ) return cmp;

        return getNumber() - section.getNumber();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<User> getUsers() {
		List<User> users = new ArrayList<>();
		for(PreRegistrationRequest req : requests) {
			users.add(req.getRequester());
		}
		return users;
	}

	public List<User> getInstructors() {
		return instructors;
	}

	public void setInstructors(List<User> instructors) {
		this.instructors = instructors;
	}

	public Set<Standing> getTargetStandings() {
		return targetStandings;
	}

	public void setTargetStandings(Set<Standing> targetStandings) {
		this.targetStandings = targetStandings;
	}

	public List<PreRegistrationRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<PreRegistrationRequest> requests) {
		this.requests = requests;
	}
	
    public boolean isInstructor( User user )
    {
        if( user != null )
        {
            for( User instructor : instructors )
                if( instructor.getId().equals( user.getId() ) ) return true;
        }

        return false;
    }
	
}
