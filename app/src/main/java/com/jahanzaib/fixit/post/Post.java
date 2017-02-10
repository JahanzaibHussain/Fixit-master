package com.jahanzaib.fixit.post;

/**
 * Created by Jahanzaib on 1/8/17.
 */

public class Post {

	private int userId;
	private String imageUrl;
	private String fullName;
	private String location;
	private String relatedTo;
	private String description;

	public Post() {
	}

	public Post(String fullName, String imageUrl, String location, String description) {
		this.fullName = fullName;
		this.imageUrl = imageUrl;
		this.location = location;
		this.description = description;
	}

	public Post(int userId, String imageUrl, String location, String relatedTo, String description) {
		this.userId = userId;
		this.imageUrl = imageUrl;
		this.location = location;
		this.relatedTo = relatedTo;
		this.description = description;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRelatedTo() {
		return relatedTo;
	}

	public void setRelatedTo(String relatedTo) {
		this.relatedTo = relatedTo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}