package com.lzq.trafficcaptor;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JButton;

import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TrafficCaptorWindow implements TrafficCaptorRefresher{

	private JFrame frmTrafficcaptor;
	private PortRecorder recorder;
	
	private TextArea  LogInfoTextArea;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TrafficCaptorWindow window = new TrafficCaptorWindow();
					window.frmTrafficcaptor.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TrafficCaptorWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTrafficcaptor = new JFrame();
		frmTrafficcaptor.setResizable(false);
		frmTrafficcaptor.setTitle("TrafficCaptor");
		frmTrafficcaptor.setBounds(100, 100, 450, 455);
		frmTrafficcaptor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTrafficcaptor.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 432, 382);
		frmTrafficcaptor.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblProcessIdYou = new JLabel("Process ID you want to capture:");
		lblProcessIdYou.setBounds(0, 0, 248, 24);
		panel.add(lblProcessIdYou);
		
		final JTextPane txtpnPid = new JTextPane();
		txtpnPid.setText("PID");
		txtpnPid.setBounds(262, 0, 156, 24);
		panel.add(txtpnPid);
		
		JButton StartButton = new JButton("Capture");
		StartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(recorder!=null)
				{
					JOptionPane.showMessageDialog(null, "A Recorder is running, please stop it first.", "ErroR", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					String pid=txtpnPid.getText();
					if(!isNumeric(pid))
					{
						JOptionPane.showMessageDialog(null, "PID not available!", "ErroR", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						LogInfoTextArea.setText("");
						recorder=new PortRecorder(TrafficCaptorWindow.this,pid);
						recorder.start();
					}

				}

			}
		});
		StartButton.setBounds(0, 37, 432, 27);
		panel.add(StartButton);
		
		JButton StopButton = new JButton("Stop");
		StopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(recorder==null)
				{
					JOptionPane.showMessageDialog(null, "No Recorder running, nothing to stop.", "ErroR", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					recorder.end();
					recorder=null;
				}
			}
		});
		StopButton.setBounds(0, 62, 432, 27);
		panel.add(StopButton);
		
		JButton UploadButton = new JButton("Upload trace file");
		UploadButton.setBounds(0, 86, 432, 27);
		panel.add(UploadButton);
		
		JLabel lblNetworkLog = new JLabel("Network log: ");
		lblNetworkLog.setBounds(0, 142, 156, 18);
		panel.add(lblNetworkLog);
		
		LogInfoTextArea = new TextArea();
		LogInfoTextArea.setBounds(0, 166, 432, 216);
		panel.add(LogInfoTextArea);
		
		JMenuBar menuBar = new JMenuBar();
		frmTrafficcaptor.setJMenuBar(menuBar);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmAboutUs = new JMenuItem("About us");
		mnAbout.add(mntmAboutUs);
		centralize();
	}
	
	
	private void centralize()
	{
		int windowWidth = frmTrafficcaptor.getWidth();                    //获得窗口宽
        int windowHeight = frmTrafficcaptor.getHeight();                  //获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit();             //定义工具包
        Dimension screenSize = kit.getScreenSize();            //获取屏幕的尺寸
        int screenWidth = screenSize.width;                    //获取屏幕的宽
        int screenHeight = screenSize.height;                  //获取屏幕的高
        frmTrafficcaptor.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示
	}

	@Override
	public void refreshResult(String s) {
		// TODO Auto-generated method stub
		LogInfoTextArea.append(s+"\n");
		
	}
	
	public boolean isNumeric(String str){
		  for (int i = str.length();--i>=0;){  
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		}
	
}
