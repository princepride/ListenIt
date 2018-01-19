package pers.princepride.ListenIt.demo;

import java.awt.Button;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javazoom.jl.player.Player;

public class RememberFrame extends JFrame {
	
	String date=MainFrame.getDate();
	int vocabularyLab=0;
	int k=0;
	
	public RememberFrame(String date) throws HeadlessException {
		super();
		this.date = date;
		FileReader fileReader;
		try {
			fileReader = new FileReader("words\\"+date);
			BufferedReader bufferedReader=new BufferedReader(fileReader);
			String temp;
			String[] tempStr;
			String[][] str=new String[100][2];
			while((temp=bufferedReader.readLine())!=null) {
				tempStr=temp.split("\\#\\$\\$\\#");
				if(tempStr.length>1) {
					str[k][0]=tempStr[0];
					str[k][1]=tempStr[1];
					k++;
				}
			}
			bufferedReader.close();
			fileReader.close();
			setVisible(true);
			setBounds(400, 200, 400, 300);
			setResizable(false);
			setAlwaysOnTop(true);
//			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().setLayout(null);
			JButton voiceButton=new JButton("发音");
			voiceButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
				try {
					String vocabulary=str[vocabularyLab][0];
					BufferedInputStream buffer=new BufferedInputStream(new FileInputStream("videos\\"+date.replace(".txt", "")+"\\"+str[vocabularyLab][0]+".mp3"));
					Player player=new Player(buffer);
					player.play();
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
				}
			});
			voiceButton.setBounds(300, 10, 76, 33);
			getContentPane().add(voiceButton);
			JLabel vocabularyLabel=new JLabel(str[vocabularyLab][0]);
			vocabularyLabel.setFont(new Font("宋体", Font.PLAIN, 30));
			vocabularyLabel.setBounds(10, 10, 250, 30);
			getContentPane().add(vocabularyLabel);
			JLabel translateLabel=new JLabel(str[vocabularyLab][1]);
			translateLabel.setFont(new Font("宋体", Font.PLAIN, 15));
			translateLabel.setBounds(10, 10, 380, 150);
			getContentPane().add(translateLabel);
			JButton beforeButton=new JButton("上一个");
			beforeButton.setBounds(10, 200, 76, 33);
			beforeButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent arg0) {
					if(vocabularyLab>0) {
						vocabularyLab--;
					}
					else {
						vocabularyLab=k-1;
					}
					vocabularyLabel.setText(str[vocabularyLab][0]);
					translateLabel.setText(str[vocabularyLab][1]);
				}
			});
			getContentPane().add(beforeButton);
			JButton nextButton=new JButton("下一个");
			nextButton.setBounds(300, 200, 76, 33);
			nextButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent arg0) {
					if(vocabularyLab<k-1) {
						vocabularyLab++;
					}
					else {
						vocabularyLab=0;
					}
					vocabularyLabel.setText(str[vocabularyLab][0]);
					translateLabel.setText(str[vocabularyLab][1]);
				}
			});
			getContentPane().add(nextButton);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
