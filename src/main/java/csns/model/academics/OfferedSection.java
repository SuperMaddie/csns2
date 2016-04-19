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
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import csns.model.core.User;
import csns.model.preRegistration.request.PreRegistrationRequest;

@Entity
@Table(name = "offered_sections", uniqueConstraints = @UniqueConstraint(columnNames = { "course_id", "number" }))
public class OfferedSection implements Serializable, Comparable<OfferedSection> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "term", nullable = false)) })
	private Term term;

	@Column
	private String subject;

	@Column(name = "course_code")
	private int courseCode;

	@Column(nullable = false)
	private int number;

	@Column(name = "class_number")
	private int classNumber;

	@Column(name="title")
	private String sectionTitle;

	@Column
	private String day;

	@Column(name = "start_time")
	private String startTime;

	@Column(name = "end_time")
	private String endTime;

	@Column
	private String location;

	@Column
	private String type;

	@ManyToMany
	@JoinTable(name = "offered_section_instructors", joinColumns = @JoinColumn(name = "section_id"), inverseJoinColumns = @JoinColumn(name = "instructor_id"))
	@OrderColumn(name = "instructor_order")
	private List<User> instructors;

	@Column
	private int units;

	@Column
	private String notes;

	@Column
	private int capacity;

	@Column
	private boolean deleted;
	
	@OneToMany
	@JoinTable(name="offered_section_links", joinColumns = @JoinColumn(name = "section_id1"), 
		inverseJoinColumns = @JoinColumn(name = "section_id2"))
	private List<OfferedSection> linkedSections;
	
	@OneToMany
	@JoinTable(name="offered_section_equivalents", joinColumns = @JoinColumn(name = "section_id1"), 
		inverseJoinColumns = @JoinColumn(name = "section_id2"))
	private List<OfferedSection> equivalentSections;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	@ManyToMany(mappedBy = "sections")
	private List<PreRegistrationRequest> requests;

	public OfferedSection() {
		number = 1;
		capacity = 30;
		instructors = new ArrayList<>();
		deleted = false;
		linkedSections = new ArrayList<>();
		equivalentSections = new ArrayList<>();
	}

	@Override
	public int compareTo(OfferedSection section) {
		if (section == null)
			throw new IllegalArgumentException("Cannot compare to NULL.");

		if (id.equals(section.id))
			return 0;

		int cmp = getCourseCode() - section.getCourseCode();

		return cmp != 0 ? cmp : getNumber() - section.getNumber();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(int courseCode) {
		this.courseCode = courseCode;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getClassNumber() {
		return classNumber;
	}

	public void setClassNumber(int classNumber) {
		this.classNumber = classNumber;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String title) {
		this.sectionTitle = title;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<User> getInstructors() {
		return instructors;
	}

	public void setInstructors(List<User> instructors) {
		this.instructors = instructors;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<PreRegistrationRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<PreRegistrationRequest> requests) {
		this.requests = requests;
	}
	
    public List<OfferedSection> getLinkedSections() {
		return linkedSections;
	}

	public void setLinkedSections(List<OfferedSection> linkedSections) {
		this.linkedSections = linkedSections;
	}
	
	public List<Long> getLinkedSectionIds(){
		List<Long> ids = new ArrayList<>();
		for(OfferedSection s: this.linkedSections){
			ids.add(s.getId());
		}
		return ids;
	}

	public List<OfferedSection> getEquivalentSections() {
		return equivalentSections;
	}

	public void setEquivalentSections(List<OfferedSection> equivalentSections) {
		this.equivalentSections = equivalentSections;
	}

	public List<Long> getEquivalentSectionIds(){
		List<Long> ids = new ArrayList<>();
		for(OfferedSection s: this.equivalentSections){
			ids.add(s.getId());
		}
		return ids;
	}
	
	public boolean isGraduate(){
    	if(courseCode >= 5000) {
    		return true;
    	}
    	return false;
    }

}
