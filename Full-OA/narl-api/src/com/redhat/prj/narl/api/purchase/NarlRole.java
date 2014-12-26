package com.redhat.prj.narl.api.purchase;

public class NarlRole {

	// 取得組室
	public static String getDeptRole(String applicanRole) {
		return "admin";
	}
	
	// 取得預控
	public static String getBCRole(String applicanRole) {
		return "admin";
	}
	
	// 取得直屬主管
	public static String getSupRole(String applicanRole, String bossRoleLevel) {
		return "admin";
	}
	
	// 取得分項計畫主持人
	public static String getPlanRole(String applicanRole) {
		return "admin";
	}
	
	// 取得總項計畫主持人
	public static String getTPlanRole(String applicanRole) {
		return "admin";
	}
	
	// 取得整合型計畫主持人
	public static String getPlanMgrRole(String planId) {
		return "admin";
	}
	
	public static String getSpaceApproveRole(String group, String stage) {
		
		switch(stage) {
			case "stage2-dept":
				return "dept";
			case "stage2-bc":
				return "bc";
			case "stage3":
				return "plan";
			case "stage4":
				return "planPM";
			case "stage5":
				return "planTM";
			case "stage6":
				return "planIM";
				
			default:
				return "";

		}
	}
	
	public static String getPolicyApproveRole(String group, String stage) {
		
		switch(stage) {
			case "stage2-dept":
				return "dept";
			case "stage2-bc":
				return "bc";
			case "stage3":
				return "plan";
			case "stage4":
				return "planPM";
			case "stage5":
				return "planTM";
			case "stage6":
				return "planIM";
				
			default:
				return "";

		}
	}
	
	public static void testCallModel() {
		
	}
}
