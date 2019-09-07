package com.redhat.bpms.narl;

public class multithreadTest {
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 10; i++) {
			System.out.println("Starting thread...");
			Thread t = new LocalNarlProcClient();
			t.start();
			System.out.println("t.start()...");
		}
		System.out.println("main() is ending...");
	}
}
