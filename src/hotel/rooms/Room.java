package hotel.rooms;

import java.time.LocalDate;

import hotel.customers.Customer;

public class Room {
	private String roomType;
	private String view;
	
	private String maxPer;
	private String size;
	
	private String price;
	
	private String options[];
	
	private Customer customers[];
	private LocalDate startDates[];
	private LocalDate endDates[];
	
	public Room(String roomType, String view,String maxPer , String size, String price, String options[]) {
		setRoomType(roomType);
		setView(view);
		setSize(size);
		setMaxPer(maxPer);
		setPrice(price);
		setOptions(options);
		setCustomers(new Customer[3]);
		setStartDates(new LocalDate[3]);
		setEndDates(new LocalDate[3]);
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}


	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String options[]) {
		this.options = options;
	}

	public Customer[] getCustomers() {
		return customers;
	}

	public void setCustomers(Customer customers[]) {
		this.customers = customers;
	}

	public String getMaxPer() {
		return maxPer;
	}

	public void setMaxPer(String maxPer) {
		this.maxPer = maxPer;
	}

	public LocalDate[] getStartDates() {
		return startDates;
	}

	public void setStartDates(LocalDate startDates[]) {
		this.startDates = startDates;
	}

	public LocalDate[] getEndDates() {
		return endDates;
	}

	public void setEndDates(LocalDate endDates[]) {
		this.endDates = endDates;
	}
	
	
	
}
