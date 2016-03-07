package com.tribaltech.android.entities;

public class Credits {

	public String name;

	public String priceUsPennies;
	public String credits;
	public String baseCredits;
	public String productId;
	public String id;

	public Credits() {
	}

	public Credits(String name, String priceUsPennies, String credits,
                   String baseCredits, String productId, String id) {
		this.name = name;
		this.priceUsPennies = priceUsPennies;
		this.credits = credits;
		this.baseCredits = baseCredits;
		this.productId = productId;
		this.id = id;
	}

}