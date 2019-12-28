package org.quickperf.maven.bench;

public class AllocationTimePair {

	private final Long allocationInBytes;
	private final Long lengthInSeconds;

	public AllocationTimePair(Long allocationInBytes, Long lengthInSeconds) {
		this.allocationInBytes = allocationInBytes;
		this.lengthInSeconds = lengthInSeconds;
	}

	public Long getAllocationInBytes() {
		return allocationInBytes;
	}

	public Long getLengthInSeconds() {
		return lengthInSeconds;
	}

}

