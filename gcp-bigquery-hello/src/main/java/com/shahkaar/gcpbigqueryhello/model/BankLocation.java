package com.shahkaar.gcpbigqueryhello.model;

import com.google.auto.value.AutoValue.Builder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class BankLocation {	
	private int fdicCertificateNumber;
	private String institutionName;
	private String branchName;
	private int branchNumber;
	private boolean mainOffice;
	private String branchAddress;
	private String branchCity;
	private String zipCode;
	private String branchCounty;
	private String countyFipsCode;
	private String state;
	private String stateName;
}
