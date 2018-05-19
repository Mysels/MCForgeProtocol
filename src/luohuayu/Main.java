package luohuayu;

public class Main {
	public static void main(String[] args){
		MCClient mc=new MCClient();
		mc.connect("127.0.0.1",25565,"Luohuayu");
		
		while(true) {
			sleep(1000);
		}
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
