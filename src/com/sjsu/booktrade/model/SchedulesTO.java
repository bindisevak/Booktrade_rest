package com.sjsu.booktrade.model;

import java.util.Date;
import java.util.List;

public class SchedulesTO {

	private int scheduleId;
	private int bookId;
	private int buyerId;
	private Date dateFrom;
	private Date dateTo;
	private boolean availableOnWeekends;
	private List<TimeSlotTO> timeSlots;
	
	public int getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public int getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	public boolean isAvailableOnWeekends() {
		return availableOnWeekends;
	}
	public void setAvailableOnWeekends(boolean availableOnWeekends) {
		this.availableOnWeekends = availableOnWeekends;
	}
	public List<TimeSlotTO> getTimeSlots() {
		return timeSlots;
	}
	public void setTimeSlots(List<TimeSlotTO> timeSlots) {
		this.timeSlots = timeSlots;
	}
	
}
