package fi.haagahelia.artstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Series {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;
    private String name;

    @JsonIgnoreProperties("series")
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "series")
	private List<Art> arts;

    public Series(){}

    public Series(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    //Series ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //Series Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Art fetching and setting to list 
    public List<Art> getArts(){
        return arts;
    }

    public void getArts(List<Art> arts){
        this.arts=arts;
    }
}
