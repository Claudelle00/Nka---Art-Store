package fi.haagahelia.artstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fi.haagahelia.artstore.domain.Series;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Art {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private String artistName;
    private int creationYear;
    private double price;

    @JsonIgnoreProperties ("arts")
    @ManyToOne
    @JoinColumn(name = "seriesid")
    private Series series;

    public Art(){};

    public Art(String title, String artistName, int creationYear,double price){

        this.title = title;
        this.artistName = artistName;
        this.creationYear = creationYear;
        this.price = price;
    }

    //Art ID
     public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    //Art Title
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    //Artist Name
    public String getArtistName(){
        return artistName;
    }
    public void setArtistName(String artistName){
        this.artistName = artistName;
    }

    //Creation Year
     public int getCreationYear(){
        return creationYear;
    }
    public void setCreationYear(int creationYear){
        this.creationYear = creationYear;
    }

    //Price
    public double getPrice(){
        return price;
    }
    public void setPrice(double price){
        this.price = price;
    }

    //Series
    public Series getSeries(){
        return series;
    }
    public void setSeries(Series series){
        this.series = series;
    }


}
