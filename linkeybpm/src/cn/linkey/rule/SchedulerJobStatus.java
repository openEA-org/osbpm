package cn.linkey.rule;

public enum SchedulerJobStatus {
	
	RUN("1"), PAUSE("2"), STOP("3");
	
	private String status;
	
	SchedulerJobStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}
