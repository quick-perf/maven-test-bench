package org.quickperf.maven.bench;

public class AllocationTimePair {

	private final Long allocationInBytes;
	private final  Long lenghtInSeconds;

	public AllocationTimePair(Long allocationInBytes, Long lenghtInSeconds) {
		this.allocationInBytes = allocationInBytes;
		this.lenghtInSeconds = lenghtInSeconds;
	}

	public Long getAllocationInBytes() {
		return allocationInBytes;
	}

	public Long getLenghtInSeconds() {
		return lenghtInSeconds;
	}


}

