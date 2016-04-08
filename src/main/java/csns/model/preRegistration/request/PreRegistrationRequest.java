package csns.model.preRegistration.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Table;

import csns.model.academics.OfferedSection;
import csns.model.academics.Term;
import csns.model.core.User;

@Entity
@Table(name="pre_register_requests")
public class PreRegistrationRequest implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "code",
        column = @Column(name = "term", nullable = false)) })
	private Term term;
    
    @ManyToMany
    @JoinTable(name="pre_register_request_sections", joinColumns = @JoinColumn(name="request_id"),
    		inverseJoinColumns = @JoinColumn(name="section_id"))
    private List<OfferedSection> sections;
    
    @Column
    private String comment;
    
    @Column
    private Date date;
    
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User requester;
    
    public PreRegistrationRequest() {
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

	public List<OfferedSection> getSections() {
		return sections;
	}

	public void setSections(List<OfferedSection> sections) {
		this.sections = sections;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

}
