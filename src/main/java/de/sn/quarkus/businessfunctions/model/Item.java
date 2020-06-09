package de.sn.quarkus.businessfunctions.model;
import java.util.List;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;

@Entity
public class Item extends PanacheEntity{
	
	@Column(name= "name", length = 20, nullable = false)//Database
	public String name;
	
	@Column(name= "imageurl", length = 255, nullable = true)//Database
	public String imageURL;
	
	@Column(name= "level", nullable = false)//Database
	public Integer level;

	//Projects containing items
	@ManyToOne
	@JsonbTransient
	public Project project; 
	//Lower items in hierarchy
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<Item> subItems;
	
	//Higher item in hierarchy
	@ManyToOne
    @JsonbTransient
    public Item item; 
	
	//Customized queries...
	public static List<Item> findByLevelAndProjectId(Integer level, Long projectid){
		 return list("level = :level and project.id = :projectid",
	         Parameters.with("level", level).and("projectid", projectid));
	}
}
