package com.redhat.prj.narl.api.purchase;


public class NarlErpService {
	public static String importErp(String formId, Integer stageId) {
		System.out.println("import ERP, Form ID: "+ formId + " Stage ID: "+stageId);
		
		return "success";
	}
}
