package com.jahanzaib.fixit.profile;

/**
 * Created by Jahanzaib on 1/8/17.
 */

public class User {

	private int ID;
	private String USER_NAME;
	private String FIRST_NAME;
	private String LAST_NAME;
	private String EMAIL;
	private String LOCATION;
	private String PASSWORD;
	private int IMAGE;
	private String IS_ACTIVE;
	private String CREATED_AT;

	public User() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String USER_NAME) {
		this.USER_NAME = USER_NAME;
	}

	public String getFIRST_NAME() {
		return FIRST_NAME;
	}

	public void setFIRST_NAME(String FIRST_NAME) {
		this.FIRST_NAME = FIRST_NAME;
	}

	public String getLAST_NAME() {
		return LAST_NAME;
	}

	public void setLAST_NAME(String LAST_NAME) {
		this.LAST_NAME = LAST_NAME;
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public void setEMAIL(String EMAIL) {
		this.EMAIL = EMAIL;
	}

	public String getLOCATION() {
		return LOCATION;
	}

	public void setLOCATION(String LOCATION) {
		this.LOCATION = LOCATION;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String PASSWORD) {
		this.PASSWORD = PASSWORD;
	}

	public int getIMAGE() {
		return IMAGE;
	}

	public void setIMAGE(int IMAGE) {
		this.IMAGE = IMAGE;
	}

	public String getIS_ACTIVE() {
		return IS_ACTIVE;
	}

	public void setIS_ACTIVE(String IS_ACTIVE) {
		this.IS_ACTIVE = IS_ACTIVE;
	}

	public String getCREATED_AT() {
		return CREATED_AT;
	}

	public void setCREATED_AT(String CREATED_AT) {
		this.CREATED_AT = CREATED_AT;
	}
}
