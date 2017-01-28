package com.example.myfest;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Fest {
	String id, name, imageUrl;

	public Fest() {
	}

	@ParcelConstructor
	public Fest(String id, String name, String imageUrl) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
