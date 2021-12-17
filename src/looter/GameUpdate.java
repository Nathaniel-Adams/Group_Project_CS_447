package looter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class GameUpdate {

	LooterGame Game;
	private Timer timer = new Timer();
	private GameUpdateTimerTask gameTimer;
	private GameSendTimerTask sendTimer;

	private boolean isLoaded = false;


	public GameUpdate(LooterGame game) {
		this.Game = game;
		startGameUpdateTimerTask();
		startGameSendTimerTask();
	}
	
	public void updateLoop() throws IOException, ClassNotFoundException {

		if (Game != null) {
			Object o = GameClient.received.poll();
			if (o != null) {
				DataPacket pack = (DataPacket) o;
				switch (pack.dType) {
				case STRING:
					Game.mpHandler.status = (String) pack.data;
					if("LOAD".equals((String) pack.data)) {
						Game.enterState(GameState.PLAYINGSTATE.ordinal());
					}
					break;
				case WORLD:
					System.out.println("I got it");
					Game.worldspace = (WorldSpace) pack.data;
					break;
				case EVENT:
					break;
				case PLAYER:
					Player temp = (Player) pack.data;
					if (Game.worldspace != null) {
						for (Player p: Game.worldspace.players) {
							if(p.id == temp.id) p = temp;
						}
					}
					
					break;
				case INT:
					if(Game.is_hosting) {
						Game.worldspace=new WorldSpace();
						Game.worldspace.init();
						Game.worldspace.startGame((int)pack.data);
						Game.enterState(GameState.PLAYINGSTATE.ordinal());
					}
					else {
						Game.mpID = (int)pack.data;
					}
					
				default:
					break;
				}
				
			} else {
//				System.out.println("oof");
			}
		}
		

	}

	public void sendLoop() throws IOException, ClassNotFoundException, InterruptedException {

		if (Game != null) {
			GameClient.send(new DataPacket(TransmissionType.heartbeat, DataType.EMPTY, ""));
			if (Game.worldspace != null) {
				GameClient.send(new DataPacket(TransmissionType.broadcast, DataType.PLAYER, Game.worldspace.players.stream().filter(e -> e.id == Game.mpID).collect(Collectors.toList()).get(0)));
			}
			
		}
		if (Game.is_hosting) {
			GameClient.send(new DataPacket(TransmissionType.broadcast, DataType.STRING, "You are Listening to 41.0 the buzz"));
			if (Game.worldspace != null) {
				GameClient.send(new DataPacket(TransmissionType.broadcast, DataType.WORLD, Game.worldspace));
				Thread.sleep(500);
			}
			
		}
		
		
		
		
	}

	public void startGameUpdateTimerTask() {
		scheduleTimer(timer, gameTimer, 0, 1000, 0);
	}

	private void startGameSendTimerTask() {

		scheduleTimer(timer, gameTimer, 0, 1000, 1);

	}

	public void scheduleTimer(Timer timer, TimerTask timerTask, int delay, int period, int type) {
		if (null != timerTask) {
			timerTask.cancel();
		}
		if (null != timer) {
			timer.purge();
		}
		if (type == 0) {
			timerTask = new GameUpdateTimerTask();
		}
		else {
			timerTask = new GameSendTimerTask();
		}
		timer.schedule(timerTask, delay, period);
	}

	class GameUpdateTimerTask extends TimerTask {

		public GameUpdateTimerTask() {
			super();
		}

		@Override
		public void run() {
			try {
				updateLoop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GameSendTimerTask extends TimerTask {

		public GameSendTimerTask() {
			super();
		}

		@Override
		public void run() {
			try {
				sendLoop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
