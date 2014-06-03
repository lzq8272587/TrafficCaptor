package com.lzq.trafficcaptor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileSystemView;

public class PortRecorder extends Thread{
	private TrafficCaptorRefresher tcr = null;
	private boolean running;

	private String pid;
	public PortRecorder(TrafficCaptorRefresher tcr,String pid)
	{
		this.tcr = tcr;
		this.pid=pid;
	}

	
	public void run()
	{
		recording();
	}
	
	public void end()
	{
		stoprecording();
	}
	public void recording() {
		// TODO Auto-generated method stub

		FileSystemView fsv = FileSystemView.getFileSystemView();
		System.err.println(fsv.getHomeDirectory());

		running=true;
		try {

			String PID =pid;

			// create connection log file
			File connection_log = new File(fsv.getHomeDirectory() + "\\connection_log.csv");
			if (connection_log.exists()) {
				connection_log.delete();
			}
			connection_log.createNewFile();

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					connection_log));

			Process process = null;
			BufferedReader br = null;

			HashSet<Connection> PriorCntSet = new HashSet<Connection>();
			HashSet<Connection> CurCntSet = new HashSet<Connection>();

			HashMap<Integer, Connection> ConnectionInfo = new HashMap<Integer, Connection>();
			HashMap<String, Connection> ConnectionInfoBuffer = new HashMap<String, Connection>();

			HashSet<String> ConnectionInfoBufferKey=new HashSet<String>();
			
			int index = 0;

			// check netstat every 1 second
			while (running) {
				System.out.println("##" + new Date().toGMTString());
				Thread.sleep(1000);

				tcr.refreshResult("##" + new Date().toGMTString());
				// TCP connection list for prior second and current second
				PriorCntSet = (HashSet<Connection>) CurCntSet.clone();
				CurCntSet = new HashSet<Connection>();

				process = Runtime.getRuntime().exec("netstat -ano");
				br = new BufferedReader(new InputStreamReader(
						process.getInputStream()));

				String line = null;

				while ((line = br.readLine()) != null) {
					// filter: get all TCP connection
					if (line.contains("TCP") && line.contains(PID)) {
						String[] temp = line.split("  *|:");
						// connection wrapper
						Connection cnt = new Connection(temp[1], temp[2],
								temp[3], temp[4], temp[5]);
						System.out.println("PriorCntSet= " + PriorCntSet);
						System.out.println("cnt= " + cnt);
						System.out.println(PriorCntSet.contains(cnt));
						CurCntSet.add(cnt);
						System.out.println("    " + line);
						tcr.refreshResult("  "+line);
					}
				}

				System.out.println("PriorCntSet: " + PriorCntSet);
				System.out.println("CurCntSet: " + CurCntSet);

				if (CurCntSet.equals(PriorCntSet))
					continue;

				// new connection
				HashSet<Connection> startingCntSet = differencing(CurCntSet,
						PriorCntSet);
				// ended connection
				HashSet<Connection> endedCntSet = differencing(PriorCntSet,
						CurCntSet);

				// add start time for each new connection
				for (Connection c : startingCntSet) {
					Connection c_clone = c.clone();
					c_clone.setStartTime(System.currentTimeMillis());
					ConnectionInfoBuffer
							.put(c_clone.getSrcIP() + c_clone.getSrcPort()
									+ c_clone.getDesIP() + c_clone.getDesPort(),
									c_clone);
					ConnectionInfoBufferKey.add(c_clone.getSrcIP() + c_clone.getSrcPort()
									+ c_clone.getDesIP() + c_clone.getDesPort());
					// System.err.println("##### in start set: "+c.toString());
				}

				// add end time for each ended connection
				for (Connection c : endedCntSet) {
					Connection cc = ConnectionInfoBuffer.get(c.getSrcIP()
							+ c.getSrcPort() + c.getDesIP() + c.getDesPort());
					cc.setEndTime(System.currentTimeMillis());
					ConnectionInfoBuffer.remove(c.getSrcIP() + c.getSrcPort()
							+ c.getDesIP() + c.getDesPort());
					ConnectionInfoBufferKey.remove(c.getSrcIP() + c.getSrcPort()
							+ c.getDesIP() + c.getDesPort());
					ConnectionInfo.put(index++, cc);
					// System.err.println("##### in start set: "+cc.toString());
					recordConnection(bw, cc);
				}

			}

			
			for(String s:ConnectionInfoBufferKey)
			{
				Connection cc=ConnectionInfoBuffer.get(s);
				cc.setEndTime(System.currentTimeMillis());
				recordConnection(bw, cc);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void stoprecording()
	{
		running = false;
	}

	private HashSet<Connection> differencing(HashSet<Connection> A,
			HashSet<Connection> B) {
		HashSet<Connection> C = new HashSet<Connection>();
		C.clear();
		C.addAll(A);
		C.removeAll(B);
		return C;
	}

	private void recordConnection(BufferedWriter bw, Connection c) {
		try {
			bw.append(c.getSrcIP() + ",");
			bw.append(c.getSrcPort() + ",");
			bw.append(c.getDesIP() + ",");
			bw.append(c.getDesPort() + ",");
			bw.append(c.getStartTime() + ",");
			bw.append(c.getEndTime() + "");
			bw.append("\n");
			bw.flush();
			System.out.println("****append a new connection!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
