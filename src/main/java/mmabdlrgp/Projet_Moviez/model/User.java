package mmabdlrgp.Projet_Moviez.model;

import java.io.Serializable;

public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	private Integer userId;
	
	
	public User(Integer userId) {
		super();
		this.userId = userId;
	}
 
	public Integer getUserId() {
		return userId;
	}
 
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
 

	@Override
	public String toString() {
		return "User [userId=" + userId +"]";
	}
	

 
}