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
package csns.model.popup;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import csns.model.academics.Standing;
import csns.model.academics.Department;
import csns.model.core.User;
import csns.model.news.News;

@Entity
@Table(name = "popups")
public class Popup implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger( Popup.class );

    @Id
    @GeneratedValue
    private Long id;
    
    @JsonIgnore
    @Column
    private String subject;
    
    @JsonIgnore
    @Column
    private String content;
    
    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "publish_date")
    private Calendar publishDate;
    
    @Column(name = "expire_date")
    private Calendar expireDate;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Column(name = "set_as_news")
    private boolean setAsNews;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "news_id")
    private News news;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "popup_target_standings", 
    joinColumns = @JoinColumn(name = "popup_id"), 
    inverseJoinColumns = @JoinColumn(name = "standing_id"))
    private Set<Standing> targetStandings;
    
    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "popup_target_roles",
        joinColumns = @JoinColumn(name = "popup_id"))
    @Column(name = "role")
    private Set<String> targetRoles;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    /*List of users that have seen the popup*/
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "popup_readby",
        joinColumns = { @JoinColumn(name = "user_id", nullable = false) },
        inverseJoinColumns = { @JoinColumn(name = "popup_id", nullable = false) })
    private Set<User> readUsers;
    
    /*List of all the target users*/
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "popup_target_users",
        joinColumns = { @JoinColumn(name = "user_id", nullable = false) },
        inverseJoinColumns = { @JoinColumn(name = "popup_id", nullable = false) })
    private Set<User> targetUsers;
    
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "popup_indi_target_users",
        joinColumns = { @JoinColumn(name = "user_id", nullable = false) },
        inverseJoinColumns = { @JoinColumn(name = "popup_id", nullable = false) })
    private Set<User> individualTargetUsers;
    

    public Popup()
    {
    	readUsers = new HashSet<User>();
    	targetUsers = new HashSet<User>();
    	individualTargetUsers = new HashSet<User>();
    	
    	targetRoles = new HashSet<String>();
    	targetStandings = new HashSet<Standing>();
    	
    	createDate = new Date();
    	publishDate = Calendar.getInstance();
    	
        expireDate = Calendar.getInstance();
        expireDate.add( Calendar.DATE, 7 );
        expireDate.set( Calendar.HOUR_OF_DAY, 23 );
        expireDate.set( Calendar.MINUTE, 59 );
        expireDate.set( Calendar.SECOND, 59 );
        expireDate.set( Calendar.MILLISECOND, 59 );
    }
    

    public boolean isPublished()
    {
        return publishDate != null
            && Calendar.getInstance().after( publishDate );
    }

    public boolean isExpired()
    {
        return expireDate != null && Calendar.getInstance().after( expireDate );
    }

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}
	

	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Calendar getExpireDate() {
		return expireDate;
	}


	public void setExpireDate(Calendar expireDate) {
		this.expireDate = expireDate;
	}


	public Calendar getPublishDate() {
		return publishDate;
	}


	public void setPublishDate(Calendar publishDate) {
		this.publishDate = publishDate;
	}


	public Set<User> getTargetUsers() {
		return targetUsers;
	}


	public void setTargetUsers(Set<User> targetUsers) {
		this.targetUsers = targetUsers;
	}
	

	public Set<User> getIndividualTargetUsers() {
		return individualTargetUsers;
	}


	public void setIndividualTargetUsers(Set<User> individualTargetUsers) {
		this.individualTargetUsers = individualTargetUsers;
	}


	public Set<User> getReadUsers() {
		return readUsers;
	}

	public void setReadUsers(Set<User> readUsers) {
		this.readUsers = readUsers;
	}

	public Set<Standing> getTargetStandings() {
		return targetStandings;
	}


	public void setTargetStandings(Set<Standing> targetStandings) {
		this.targetStandings = targetStandings;
	}
	
	public void setTargetStandings(List<Standing> targetStandings) {
		this.targetStandings.addAll(targetStandings);
	}


	public Set<String> getTargetRoles() {
		return targetRoles;
	}


	public void setTargetRoles(Set<String> targetRoles) {
		this.targetRoles = targetRoles;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Department getDepartment() {
		return department;
	}


	public void setDepartment(Department department) {
		this.department = department;
	}


	public User getAuthor() {
		return author;
	}


	public void setAuthor(User author) {
		this.author = author;
	}


	public boolean isSetAsNews() {
		return setAsNews;
	}

	public void setSetAsNews(boolean setAsNews) {
		this.setAsNews = setAsNews;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}
}
