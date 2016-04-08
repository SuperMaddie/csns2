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
import java.util.Calendar;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="tentative_schedules")
public class TentativeSchedule implements Serializable, Comparable<OfferedSection>{
	
    private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "code",
        column = @Column(name = "term", nullable = false)) })
	private Term term;
    
    @OneToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @ManyToMany(cascade=CascadeType.MERGE)
    @JoinTable(name="tentative_schedule_sections", joinColumns=@JoinColumn(name="schedule_id"),
    inverseJoinColumns=@JoinColumn(name="section_id"))
    private List<OfferedSection> sections;
    
    @Column(name = "publish_date")
    private Calendar publishDate;
    
    @Column(name = "expire_date")
    private Calendar expireDate;
    
    @Column
    private boolean deleted;
    
    @Column(name = "graduate_limit")
    private int graduateLimit;

    @Column(name = "undergraduate_limit")
    private int undergraduateLimit;
    
    public TentativeSchedule(){
    	undergraduateLimit = 18;
    	graduateLimit = 16;
    	sections = new ArrayList<>();
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

	public List<OfferedSection> getSections() {
		return sections; 
	}

	public void setSections(List<OfferedSection> sections) {
		this.sections = sections;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getGraduateLimit() {
		return graduateLimit;
	}

	public void setGraduateLimit(int graduateLimit) {
		this.graduateLimit = graduateLimit;
	}

	public int getUndergraduateLimit() {
		return undergraduateLimit;
	}

	public void setUndergraduateLimit(int undergraduateLimit) {
		this.undergraduateLimit = undergraduateLimit;
	}

	@Override
	public int compareTo(OfferedSection arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean isPublished(){
		return publishDate != null && Calendar.getInstance().after(publishDate);
	}
	
	public boolean isClosed(){
		return expireDate != null && Calendar.getInstance().after(expireDate);
	}
	
}
