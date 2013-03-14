package simulation;
import java.awt.Image;


public class Agent {

	private Long id;
	private Integer xPos;
	private Integer yPos;
	private Image image;
	private Double speed;
	
	public Agent(long id){
		this.id = id;
		this.xPos = 0;
		this.yPos = 0;
		this.speed = 0.0;
		this.image = null;
	}
	
	public Long getId(){
		return id;
	}
	
	public Integer getXPos(){
		return xPos;
	}
	
	public Integer getYPos(){
		return yPos;
	}

	public Double getSpeed(){
		return speed;
	}
	
	public Image getImage(){
		return image;
	}
	
	public void setID(long id){
		this.id = id;
	}
	
	public void setXPos(Integer xPos){
		this.xPos = xPos;
	}
	
	public void setYPos(Integer yPos){
		this.yPos = yPos;
	}
	
	public void setSpeed(Double speed){
//		System.out.println("Speed : " + speed);
		this.speed = speed;
	}
	
	public void setImage(Image image){
		this.image = image;
	}
	
	public String toString(){
		return "Agent id[" + id + "] - xPos[" + xPos + "] - yPos [" + yPos + "]";
	}
}
