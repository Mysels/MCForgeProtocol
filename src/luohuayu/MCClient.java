package luohuayu;

import java.util.HashMap;

import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.event.session.ConnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectingEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.PacketSentEvent;
import org.spacehq.packetlib.event.session.SessionListener;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import luohuayu.MCForgeProtocol.MCForge;

public class MCClient {
	private Client client;
	private MCForge forge;
	
	public void connect(String ip,int port,String username) {
		HashMap<String,String> modList=new HashMap<String,String>();
		modList.put("customnpcs","1.7.10b");
		modList.put("IC2","2.2.821-experimental");
		
		client=new Client(ip,port,new MinecraftProtocol(username), new TcpSessionFactory());
		client.getSession().addListener(new SessionListener() {
			public void packetReceived(PacketReceivedEvent e) {
				forge.handle(e.getSession(),e.getPacket());
				if(e.getPacket() instanceof ServerPluginMessagePacket) {
					//ServerPluginMessagePacket packet=(ServerPluginMessagePacket)e.getPacket();
					//log("S->C PluginMessage:"+packet.getChannel()+" "+new String(packet.getData()));
				}else if(e.getPacket() instanceof ServerChatPacket) {
					ServerChatPacket packet=(ServerChatPacket)e.getPacket();
					log(packet.getMessage().getFullText());
				}
				if (e.getPacket() instanceof ServerJoinGamePacket) {
					log("���ӳɹ�");
				}
			}
			public void packetSent(PacketSentEvent e){}
			public void connected(ConnectedEvent e){}
			public void disconnecting(DisconnectingEvent e){}
			public void disconnected(DisconnectedEvent e){
				//e.getCause().printStackTrace();
				log("�Ͽ����ӣ� "+e.getReason());
			}
		});
		
		forge=new MCForge(client.getSession(),modList);
		client.getSession().connect();
		
		log("�������ӷ�����..");
	}
	
	public Client getClient() {
		return this.client;
	}
	
	private void log(String str) {
		System.out.println("[Client] "+str);
	}
}
