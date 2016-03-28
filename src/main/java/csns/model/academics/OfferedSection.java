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
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import csns.model.core.Day;
import csns.model.core.User;
import csns.model.preRegistration.request.PreRegistrationRequest;

@Entity
@Table(name="offered_sections", uniqueConstraints = @UniqueConstraint(columnNames = {
	    "quarter", "course_id", "number" }))
public class OfferedSection implements Serializable, Comparable<OfferedSection> {

    private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "code",
        column = @Column(name = "quarter", nullable = false)) })
	private Term quarter;
    
    @Column
    private int capacity;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @OneToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @ManyToMany(mappedBy = "sections")
    private List<PreRegistrationRequest> requests;
    
    @Column(nullable = false)
    private int number;
    
    @Column
    private Day day;
    
    @Column(name = "start_time")
    private Time startTime;
    
    @Column(name = "end_time")
    private Time endTime;
    
    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "publish_date")
    private Calendar publishDate;
    
    @Column(name = "expire_date")
    private Calendar expireDate;
    
    @Column
    private boolean deleted;
    
    @ManyToMany
    @JoinTable(name = "offered_section_users",
    		joinColumns = @JoinColumn(name="section_id"),
    		inverseJoinColumns = @JoinColumn(name="user_id"))
    private List<User> appliedUsers;
    
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
    	capacity = 20;
        instructors = new ArrayList<User>();
        appliedUsers = new ArrayList<User>();
        deleted = false;
	}
    
	@Override
	public int compareTo(OfferedSection section) {
		if( section == null )
            throw new IllegalArgumentException( "Cannot compare to NULL." );

        if( id.equals( section.id ) ) return 0;

        int cmp = getTerm().getCode() - section.getTerm().getCode();
        if( cmp != 0 ) return cmp;

        cmp = getCourse().getCode().compareTo( section.getCourse().getCode() );
        if( cmp != 0 ) return cmp;

        return getNumber() - section.getNumber();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Term getTerm() {
		return quarter;
	}

	public void setTerm(Term quarter) {
		this.quarter = quarter;
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

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<User> getAppliedUsers() {
		return appliedUsers;
	}

	public void setAppliedUsers(List<User> appliedUsers) {
		this.appliedUsers = appliedUsers;
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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Calendar getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Calendar publishDate) {
		this.publishDate = publishDate;
	}

	public Calendar getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Calendar expireDate) {
		this.expireDate = expireDate;
	}

	public List<PreRegistrationRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<PreRegistrationRequest> requests) {
		this.requests = requests;
	}
	
}
