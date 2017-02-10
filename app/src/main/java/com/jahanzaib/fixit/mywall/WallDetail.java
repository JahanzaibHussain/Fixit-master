package com.jahanzaib.fixit.mywall;

/**
 * Created by Jahanzaib on 1/2/17.
 */

public class WallDetail {

	private String username;
	private String image;
	private String des;
	private String location;
	private int userId;
	private String relatedTo;

	public WallDetail() {
	}

	public WallDetail(String username, String image, String des, String location, String relatedTo) {
		this.username = username;
		this.image = image;
		this.des = des;
		this.location = location;
		this.relatedTo = relatedTo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getRelatedTo() {
		return relatedTo;
	}

	public void setRelatedTo(String relatedTo) {
		this.relatedTo = relatedTo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
