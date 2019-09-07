package com.redhat.bpms.narl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.org.narl.common.model.Approver;
import tw.org.narl.common.model.Form;
import tw.org.narl.common.model.Stage;

/**
 * Hello world!
 * 
 */
public class LocalNarlProcClient extends Thread {

	protected static Logger log = LoggerFactory.getLogger(LocalNarlProcClient.class);

	private static final String DEPLOYMENT_ID = "deploymentId";
	private static final String DEPLOYMENT_URL = "deployment.url";
	private static final String USER_ID = "userId";
	private static final String PASSWORD = "password";
	private static final String PROCESSID = "processId";

	private String deploymentId;
	private URL deploymentUrl;
	private String initiaterId;
	private String password;
	private String processId;

	public LocalNarlProcClient() throws Exception {
		// this.deploymentId = "com.redhat.prj.narl:purchase:1.0";
		this.deploymentId = "com.redhat.lab:procurement:1.0";
		this.deploymentUrl = new URL("http://localhost:8080/business-central");
		this.password = "1qaz@WSX";
	}

	private KieSession getKieSession() {
		RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder().addDeploymentId(deploymentId)
				.addUserName(initiaterId).addPassword(password).addUrl(deploymentUrl).build();

		return engine.getKieSession();
	}

	private TaskService getTaskService() {
		RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder().addDeploymentId(deploymentId)
				.addUserName(initiaterId).addPassword(password).addUrl(deploymentUrl).build();

		return engine.getTaskService();
	}

	/*
	 * Main Process of PR: Start process - temp/approving
	 */
	@Test
	public void majorproc0() {
		this.initiaterId = "bpmsAdmin";
		// this.processId = "purchase.majorproc";
		this.processId = "procurement.procurementproc";
		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

		// RemoteRestRuntimeEngineFactory factory =
		// RemoteRestRuntimeEngineFactory
		// .newBuilder().addDeploymentId(deploymentId)
		// .addUrl(deploymentUrl).addUserName(initiaterId)
		// .addPassword(password).build();
		// RemoteRuntimeEngine engine = factory.newRuntimeEngine();
		// KieSession ksession = engine.getKieSession();

		KieSession ksession = this.getKieSession();

		System.out.println("Prepare Form mock data");
		Form form = prepareForm();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("majorForm", form);
		ksession.startProcess(processId, params);
	}

	/*
	 * Main Process of PR: Save Temp Form - approving
	 */
	@Test
	public void fillformproc() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.fillformproc";
		/* change here */
		long taskId = 1;
		String userId = "bpmsAdmin";
		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//		TaskService taskService = engine.getTaskService();
		
		
		TaskService taskService = this.getTaskService();
		
		Map<String, Object> params = taskService.getTaskContent(taskId);
		for (Object k : params.keySet()) {
			System.out.println("keys:" + k.toString());
		}
		Form form = (Form) params.get("taskForm");
		form.setStatus("approving");
		System.out.println("taskForm:" + form + "\n form.status: " + form.getStatus() + "\n");
		taskService.start(taskId, userId);
		params.put("taskForm", form);
		// -------------
		taskService.complete(taskId, userId, params);
	}

	/* Scenario 2 - resend */
	@Test
	public void majorproc2() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.majorproc";
		/* change here */
		long taskId = 5;
		String userId = "bpmsAdmin";
		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();
		
		TaskService taskService = this.getTaskService();
		
		Map<String, Object> params = taskService.getTaskContent(taskId);

		Form form = (Form) params.get("form");
		System.out.println("Org Form Status: " + form.getStatus());
		form.setStatus("resend");
		System.out.println("form:" + form + "\n form.status: " + form.getStatus() + "\n");
		taskService.start(taskId, userId);
		params.put("form", form);
		// -------------
		taskService.complete(taskId, userId, params);
	}

	/* Scenario 3 - errordate - recheck */
	@Test
	public void majorproc3() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.majorproc";
		/* change here */
		long taskId = 11;
		String userId = "bpmsAdmin";
		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();
		
		TaskService taskService = this.getTaskService();
		
		Map<String, Object> params = taskService.getTaskContent(taskId);

		Form form = (Form) params.get("form");
		System.out.println("Org Form Status: " + form.getStatus());
		form.setStatus("approving");
		System.out.println("form:" + form + "\n form.status: " + form.getStatus() + "\n");
		taskService.start(taskId, userId);
		params.put("form", form);
		// -------------
		taskService.complete(taskId, userId, params);
	}

	@Test
	public void stagemajorproc0() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.stagemajorproc";
		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		KieSession ksession = engine.getKieSession();

		KieSession ksession = this.getKieSession();
		
		System.out.println("Prepare Form mock data");
		Form form = new Form();
		form.setFormId("1");
		form.setPrice(new Long(90000));
		form.setCurrentStage("0");
		form.setStageList(new LinkedList<Stage>());
		form.setStatus("approving");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("form", form);
		ksession.startProcess(processId, params);
	}

	/* Scenario 1 - happy path */
	@Test
	public void stagemajorproc1() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.approvalproc";
		/* change here */
		long taskId = 12;
		String userId = "bpmsAdmin";

		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();
		
		TaskService taskService = this.getTaskService();
		
		// List<Long> tasks =
		// taskService.getTasksByProcessInstanceId(instanceId);
		// Task task = taskService.getTaskById(taskId);
		Map<String, Object> params = taskService.getTaskContent(taskId);
		Form form = (Form) params.get("taskForm");
		System.out.println("taskForm:" + form + "\n form.status: " + form.getStatus() + "\n");
		Stage stage = (Stage) params.get("taskStage");
		System.out.println("taskStage:" + stage + "\n stage.status: " + stage.getStatus() + "\n");
		stage.setStatus("approved");
		// stage.setStatus("rejected");
		// taskService.claim(taskId, userId);
		taskService.start(taskId, userId);
		params.put("taskStage", stage);
		// -------------
		taskService.complete(taskId, userId, params);
	}

	/* Scenario 2-1 - join stage with/without approved */
	@Test
	public void stagemajorproc2() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.approvalproc";
		/* change here */
		long taskId = 14;
		String userId = "bpmsAdmin";

		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();

		TaskService taskService = this.getTaskService();
		
		// List<Long> tasks =
		// taskService.getTasksByProcessInstanceId(instanceId);
		// Task task = taskService.getTaskById(taskId);
		Map<String, Object> params = taskService.getTaskContent(taskId);
		Form form = (Form) params.get("taskForm");
		System.out.println("taskForm:" + form + "\n form.status" + form.getStatus() + "\n");
		Stage stage = (Stage) params.get("taskStage");
		System.out.println("taskStage:" + stage + "\n stage.status" + stage.getStatus() + "\n");

		List<Stage> stages = new LinkedList<Stage>();
		Stage s = new Stage();
		s.setApprover(new Approver("groupC"));
		s.setStatus("Approving");
		stages.add(s);
		s = new Stage();
		s.setApprover(new Approver("groupB"));
		s.setStatus("Approving");
		stages.add(s);
		System.out.println("joinStage.size: " + stages.size());

		stage.setJoinStage(stages);
		/* change here */
		// stage.setStatus("approved");

		// taskService.claim(taskId, userId);
		taskService.start(taskId, userId);
		params.put("taskStage", stage);
		// -------------
		taskService.complete(taskId, userId, params);
	}

	/* Scenario 2-2 - join stage with/without approved */
	@Test
	public void stagemajorproc22() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.counterapprovalproc";
		/* change here */
		long taskId = 15;
		String userId = "bpmsAdmin";

		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();
		
		TaskService taskService = this.getTaskService();
		
		// List<Long> tasks =
		// taskService.getTasksByProcessInstanceId(instanceId);
		// Task task = taskService.getTaskById(taskId);
		Map<String, Object> params = taskService.getTaskContent(taskId);
		Form form = (Form) params.get("taskForm");
		System.out.println("taskForm:" + form + "\n form.status" + form.getStatus() + "\n");
		Stage stage = (Stage) params.get("taskStage");
		System.out.println("taskStage:" + stage + "\n stage.status" + stage.getStatus() + "\n");
		stage.setStatus("approved");
		// taskService.claim(taskId, userId);
		taskService.start(taskId, userId);
		params.put("taskStage", stage);
		// -------------
		taskService.complete(taskId, userId, params);
	}

	/* Scenario 3-2 - Reapprover */
	@Test
	public void stagemajorproc32() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.counterapprovalproc";
		/* change here */
		long taskId = 18;
		String userId = "bpmsAdmin";

		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();
		
		TaskService taskService = this.getTaskService();

		// List<Long> tasks =
		// taskService.getTasksByProcessInstanceId(instanceId);
		// Task task = taskService.getTaskById(taskId);
		Map<String, Object> params = taskService.getTaskContent(taskId);
		Form form = (Form) params.get("taskForm");
		System.out.println("taskForm:" + form + "\n form.status" + form.getStatus() + "\n");
		Stage stage = (Stage) params.get("taskStage");
		System.out.println("taskStage:" + stage + "\n stage.status" + stage.getStatus() + "\n");
		stage.setStatus("approved");
		// taskService.claim(taskId, userId);
		taskService.start(taskId, userId);
		params.put("taskStage", stage);
		// -------------
		taskService.complete(taskId, userId, params);
	}

	@Test
	public void manipulateForm() {
		this.initiaterId = "bpmsAdmin";
//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();
		
		TaskService taskService = this.getTaskService();
		
		// for (int i=0;i<15;i++){
		List<TaskSummary> taskSums = taskService.getTasksAssignedAsPotentialOwner(this.initiaterId, "en-UK");
		System.out.println("taskSummaries.size: " + taskSums.size());
		for (TaskSummary ts : taskSums) {
			System.out.println("######");
			System.out.println("id: " + ts.getId());
			System.out.println("name: " + ts.getName());
			System.out.println("Owner: " + ts.getActualOwner());
			System.out.println("processId: " + ts.getProcessId());
			System.out.println("subject: " + ts.getSubject());
			System.out.println("description: " + ts.getDescription());
			System.out.println("task staus: " + ts.getStatus());
		}

		if (taskSums.size() < 2) {
			TaskSummary ts = taskSums.get(0);
			Map<String, Object> params = taskService.getTaskContent(ts.getId());

			/* For Form Complete */
			Form form = (Form) params.get("taskForm");
			System.out.println("taskForm:" + form + "\n form.status" + form.getStatus() + "\n");
			form.setStatus("approving");
			// form.setStatus("resend");
			// form.setStatus("cancel");
			taskService.start(ts.getId(), this.initiaterId);
			params.put("taskForm", form);
			/* For Form Complete - END */

			taskService.complete(ts.getId(), this.initiaterId, params);
		}
		// }
	}

	@Test
	public void manipulateStage() {
		this.initiaterId = "bpmsAdmin";
//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();

//		AuditLogService als = engine.getAuditLogService();
//		TaskService taskService = engine.getTaskService();

		
		TaskService taskService = this.getTaskService();
		
		// for (int i=0;i<14;i++){
		List<TaskSummary> taskSums = taskService.getTasksAssignedAsPotentialOwner(this.initiaterId, "en-UK");
		System.out.println("taskSummaries.size: " + taskSums.size());
		for (TaskSummary ts : taskSums) {
			System.out.println("######");
			System.out.println("id: " + ts.getId());
			System.out.println("name: " + ts.getName());
			System.out.println("Owner: " + ts.getActualOwner());
			System.out.println("processId: " + ts.getProcessId());
			System.out.println("subject: " + ts.getSubject());
			System.out.println("description: " + ts.getDescription());
			System.out.println("task staus: " + ts.getStatus());
		}

		TaskSummary ts = taskSums.get(0);
		long taskId = ts.getId();
		// taskId = 42;
		Map<String, Object> params = taskService.getTaskContent(taskId);

		/* For Stage Complete */
		Stage stage = (Stage) params.get("taskStage");
		System.out.println("taskStage: " + stage + "\n stage.status: " + stage.getStatus() + "\n");

		// >>> for approve <<<
		// stage.setStatus("approved");
		// >>><<<
		// >>> for reject <<<
		// stage.setStatus("rejected");
		// >>><<<
		// >>> for reapprove <<<
		stage.setStatus("reapprove");
		// >>><<<

		// >>> for re-approve without change <<<

		List<Stage> reapproveStage = new LinkedList<Stage>();
		Stage s1 = new Stage();
		s1.setApprover(new Approver("groupA"));
		s1.setStatus("reapprove");
		reapproveStage.add(s1);
		s1 = new Stage();
		s1.setApprover(new Approver("groupB"));
		s1.setStatus("reapprove");
		reapproveStage.add(s1);
		stage.setReapproveStage(reapproveStage);

		// stage.setReapproveStage(null);

		// >>><<<

		// *** for join approve with approved ***
		// stage.setStatus("approved");
		// Stage joinStage1 = new Stage();
		// joinStage1.setApprover(new Approver("groupA"));
		// List joinStages = new LinkedList();
		// joinStages.add(joinStage1);
		// stage.setJoinStage(joinStages);
		// ******
		// === for counter approve without change ===
		// stage.setStatus("approving");
		// Stage joinStage1 = new Stage();
		// joinStage1.setApprover(new Approver("groupA"));
		// List joinStages = new LinkedList();
		// joinStages.add(joinStage1);
		// stage.setJoinStage(joinStages);
		// ======

		// >>> add CommentStages <<<
		List<Stage> commentStages = new LinkedList<Stage>();
		Stage cs1 = new Stage();
		cs1.setApprover(new Approver("groupB"));
		commentStages.add(cs1);
		stage.setCommentStage(commentStages);
		// >>><<<

		taskService.start(taskId, this.initiaterId);
		params.put("taskStage", stage);
		/* For Stage Complete - END */

		// -------------
		taskService.complete(taskId, this.initiaterId, params);
		// }
	}

	@Test
	public void signalProcess() {
		long procId = 25;
		this.initiaterId = "bpmsAdmin";
		this.processId = "purchase.majorproc";
		System.out.println(deploymentId + "\n" + deploymentUrl + "\n" + initiaterId + "\n");

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		KieSession ksession = engine.getKieSession();

		KieSession ksession = this.getKieSession();
		
		System.out.println("Send Signal to Process " + procId + " for caneling application");
		ksession.signalEvent("cancelSig", null, procId);
	}

	@Test
	public void queryTask() {
		this.initiaterId = "bpmsAdmin";
//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(initiaterId).addPassword(password)
//				.build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		TaskService taskService = engine.getTaskService();
		
		TaskService taskService = this.getTaskService();
		
		List<Status> statuses = new ArrayList<Status>();
		statuses.add(Status.Completed);
		statuses.add(Status.Ready);

		List<TaskSummary> taskSums = taskService.getTasksOwnedByStatus("bpmsAdmin", statuses, "en-UK");
		System.out.println("taskSummaries.size: " + taskSums.size());
		for (TaskSummary ts : taskSums) {
			System.out.println("######");
			System.out.println("id: " + ts.getId());
			System.out.println("name: " + ts.getName());
			System.out.println("Owner: " + ts.getActualOwner());
			System.out.println("processId: " + ts.getProcessId());
			System.out.println("subject: " + ts.getSubject());
			System.out.println("description: " + ts.getDescription());
			System.out.println("task staus: " + ts.getStatus());
		}
	}

	private InitialContext getRemoteJbossInitialContext(URL url, String user, String password) {
		Properties initialProps = new Properties();
		initialProps.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY,
				"org.jboss.naming.remote.client.InitialContextFactory");
		String jbossServerHostName = url.getHost();
		initialProps.setProperty(InitialContext.PROVIDER_URL, "remote://" + jbossServerHostName + ":4447");
		initialProps.setProperty(InitialContext.SECURITY_PRINCIPAL, user);
		initialProps.setProperty(InitialContext.SECURITY_CREDENTIALS, password);
		// initialProps.setProperty(InitialContext.SECURITY_PROTOCOL, "ssl");

		for (Object keyObj : initialProps.keySet()) {
			String key = (String) keyObj;
			System.setProperty(key, (String) initialProps.get(key));
		}
		try {
			return new InitialContext(initialProps);
		} catch (NamingException e) {
			System.out.println("get InitialContext fail");
			return null;
		}
	}

	private Form prepareForm() {
		Form form = new Form();
		form.setCreatorId(this.initiaterId);
		form.setFormId("1");
		form.setPrice(new Long(90000));
		form.setCurrentStage("0");
		form.setStageList(new LinkedList<Stage>());
		form.setStatus("temp");

		form.setCountersignid1("admin");
		form.setCountersignid2("admin");
		form.setCountersignId3("admin");
		form.setCountersignId4("admin");
		form.setCountersignId5("admin");

		form.setApplicanRole("admin");
		form.setPlanRole("admin");
		form.setBossRoleLevel("15");
		form.setPlanId("admin");

		form.setVd_02010000("admin");
		form.setDirector_02010000("admin");

		form.setFbcRoleId("admin");
		form.setStaff_pa("admin");
		form.setPoRoleId("admin");
		form.setPo_mgr("admin");
		form.setDirector_01140000("admin");

		form.setLs("admin");
		form.setPeRoleId("admin");
		form.setDh("admin");
		form.setDirector_01020000("admin");

		form.setFinRoleId("admin");
		form.setFin_mgr("admin");
		form.setDirector_01110000("admin");

		form.setPresident("admin");
		return form;
	}

	@Test
	public void forTest() {
		this.initiaterId = "bpmsAdmin";
		this.processId = "project1.singleeventproc";

//		RemoteRestRuntimeEngineFactory factory = RemoteRestRuntimeEngineFactory.newBuilder()
//				.addDeploymentId("org.kie.example:project1:1.0").addUrl(this.deploymentUrl).addUserName(initiaterId)
//				.addPassword(password).build();
//		RemoteRuntimeEngine engine = factory.newRuntimeEngine();
//
//		KieSession ksession = engine.getKieSession();
		
		KieSession ksession = this.getKieSession();
		
		System.out.println("Prepare Form mock data");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("var1", "test parameter");
		ksession.startProcess(processId, params);
		ksession.signalEvent("cancelSig", null, 12);
		// ksession.signalEvent("cancelSig", null);

		// TaskService ts = engine.getTaskService();
	}

	public void run() {
		majorproc0();
		System.out.println("majorproc0");
	}

}
