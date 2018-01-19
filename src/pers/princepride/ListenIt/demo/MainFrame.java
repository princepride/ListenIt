package pers.princepride.ListenIt.demo;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Font;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.translate.demo.TransApi;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainFrame {

	private JFrame frame;
	private JTextField vocabularyField;
	private JTextArea translateTextArea;
	private JTextField gapField;
	private JTextField numberField;
	private AutoDictationThread dictationThread = new AutoDictationThread();
	private JList list = new JList();
	private static final String APP_ID = "20170928000085675";
	private static final String SECURITY_KEY = "nSqxf1ZRNCNvkltE4Zgq";
	private JTextField repeatTextField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 280, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		vocabularyField = new JTextField();
		vocabularyField.setBounds(10, 10, 110, 21);
		frame.getContentPane().add(vocabularyField);
		vocabularyField.setColumns(10);

		translateTextArea = new JTextArea();
		translateTextArea.setLineWrap(true);
		translateTextArea.setBounds(10, 41, 244, 122);
		frame.getContentPane().add(translateTextArea);
		vocabularyField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					TransApi api = new TransApi(APP_ID, SECURITY_KEY);
					String vocabulary = vocabularyField.getText();
					String string = api.getTransResult(vocabulary, "auto", "zh");
					Pattern pattern = Pattern.compile("\\\\[0-9a-z]+");
					Matcher matcher = pattern.matcher(string);
					char ch;
					String string2 = "";
					while (matcher.find()) {
						char c = (char) Integer.parseInt(matcher.group(0).replaceAll("\\\\u", ""), 16);
						string2 += c;
					}
					translateTextArea.setText(string2);
				}
			}
		});

		JButton searchButton = new JButton("\u67E5\u8BE2");
		searchButton.setFont(new Font("宋体", Font.PLAIN, 11));
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				TransApi api = new TransApi(APP_ID, SECURITY_KEY);
				String vocabulary = vocabularyField.getText();
				String string = api.getTransResult(vocabulary, "auto", "zh");
				Pattern pattern = Pattern.compile("\\\\[0-9a-z]+");
				Matcher matcher = pattern.matcher(string);
				char ch;
				String string2 = "";
				while (matcher.find()) {
					char c = (char) Integer.parseInt(matcher.group(0).replaceAll("\\\\u", ""), 16);
					string2 += c;
				}
				translateTextArea.setText(string2);
			}
		});
		searchButton.setBounds(130, 9, 56, 23);
		frame.getContentPane().add(searchButton);

		JButton insertButton = new JButton("\u5F55\u5165");
		insertButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					File fileWords = new File("words");
					if (!fileWords.exists()) {
						fileWords.mkdirs();
					}
					String date = getDate();
					File dateWords = new File("words\\" + date + ".txt");
					if (!dateWords.exists()) {
						dateWords.createNewFile();
					}
					File fileVideos = new File("videos");
					if (!fileVideos.exists()) {
						fileVideos.mkdirs();
					}
					File dateVideos = new File("videos\\" + date);
					if (!dateVideos.exists()) {
						dateVideos.mkdir();
					}
					boolean exist = false;
					String vocabularyAndTranslate = vocabularyField.getText() + "#$$#" + translateTextArea.getText();
					FileReader fileReader = new FileReader(dateWords);
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String temp;
					while ((temp = bufferedReader.readLine()) != null) {
						String[] existVocabulary = temp.split("#$$#");
						if (existVocabulary.length > 0) {
							if (existVocabulary[0].equals(vocabularyField.getText())) {
								exist = true;
								break;
							}
						}
					}
					bufferedReader.close();
					fileReader.close();
					if (exist == false) {
						FileWriter fileWriter = new FileWriter(dateWords, true);
						fileWriter.write(vocabularyAndTranslate + "\n");
						fileWriter.close();
						String vocabulary = vocabularyField.getText();
						String mp3Url = "https://ssl.gstatic.com/dictionary/static/sounds/oxford/" + vocabulary
								+ "--_gb_1.mp3";
						InputStream in = new URL(mp3Url).openConnection().getInputStream();
						FileOutputStream f = new FileOutputStream("videos\\" + date + "\\" + vocabulary + ".mp3");
						byte[] bb = new byte[1024];
						int len;
						while ((len = in.read(bb)) > 0) {
							f.write(bb, 0, len);
						}
						f.close();
						in.close();
					} else {
						translateTextArea.setText("你今天已经背了这个单词了，再回去好好看看吧！");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				list.setModel(new DefaultComboBoxModel(getValues()));
			}
		});
		insertButton.setBounds(82, 186, 97, 23);
		frame.getContentPane().add(insertButton);

		JRadioButton orderRadioButton = new JRadioButton("\u4E71\u5E8F");
		orderRadioButton.setBounds(34, 284, 64, 23);
		frame.getContentPane().add(orderRadioButton);

		numberField = new JTextField();
		numberField.setText("20");
		numberField.setBounds(102, 310, 76, 21);
		frame.getContentPane().add(numberField);
		numberField.setColumns(10);

		JButton dictationButton = new JButton("\u542C\u5199");
		dictationButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dictationThread.setGapTimes(Integer.valueOf(gapField.getText()));
				dictationThread.setRepeatTimes(Integer.valueOf(repeatTextField.getText()));
				dictationThread.setOrder(!orderRadioButton.isSelected());
				dictationThread.setWordsNum(Integer.valueOf(numberField.getText()));
				if (list.getSelectedValue() != null) {
					dictationThread.setTargetFile(list.getSelectedValue().toString().replaceAll(".txt", ""));
				}
				Thread autoThread = new Thread(dictationThread);
				autoThread.run();
			}
		});
		dictationButton.setBounds(151, 371, 65, 23);
		frame.getContentPane().add(dictationButton);

		gapField = new JTextField();
		gapField.setText("0");
		gapField.setBounds(99, 372, 21, 21);
		frame.getContentPane().add(gapField);
		gapField.setColumns(10);

		JLabel lblS = new JLabel("s");
		lblS.setFont(new Font("宋体", Font.PLAIN, 20));
		lblS.setBounds(128, 373, 13, 15);
		frame.getContentPane().add(lblS);

		JLabel label = new JLabel("\u5355\u8BCD\u95F4\u9694");
		label.setBounds(34, 375, 58, 15);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel("\u9009\u62E9\u65E5\u671F");
		label_1.setBounds(34, 233, 58, 15);
		frame.getContentPane().add(label_1);

		JLabel label_2 = new JLabel("\u542C\u5199\u4E2A\u6570");
		label_2.setBounds(34, 313, 58, 15);
		frame.getContentPane().add(label_2);

		JLabel label_3 = new JLabel("\u5355\u8BCD\u91CD\u590D");
		label_3.setBounds(34, 338, 58, 15);
		frame.getContentPane().add(label_3);

		repeatTextField = new JTextField();
		repeatTextField.setText("1");
		repeatTextField.setBounds(103, 335, 76, 21);
		frame.getContentPane().add(repeatTextField);
		repeatTextField.setColumns(10);

		JLabel label_4 = new JLabel("\u6B21");
		label_4.setBounds(189, 338, 28, 15);
		frame.getContentPane().add(label_4);

		JButton voiceButton = new JButton("\u53D1\u97F3");
		voiceButton.setFont(new Font("宋体", Font.PLAIN, 11));
		voiceButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					String vocabulary = vocabularyField.getText();
					String mp3Url = "https://ssl.gstatic.com/dictionary/static/sounds/oxford/" + vocabulary
							+ "--_gb_1.mp3";
					InputStream in = new URL(mp3Url).openConnection().getInputStream();
					Player player = new Player(in);
					player.play();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		voiceButton.setBounds(198, 9, 56, 23);
		frame.getContentPane().add(voiceButton);

		JLabel label_5 = new JLabel("\u4E2A");
		label_5.setBounds(188, 313, 58, 15);
		frame.getContentPane().add(label_5);

		JPanel panel = new JPanel();
		panel.setBounds(102, 224, 97, 71);
		ListModel jListModel = new DefaultComboBoxModel(getValues());
		list.setModel(jListModel);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new java.awt.Dimension(97, 71));
		scrollPane.setViewportView(list);
		panel.add(scrollPane);
		frame.getContentPane().add(panel);

		JButton btnNewButton_1 = new JButton("<html>开<br>始<br>记</html>");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (list.getSelectedValue() != null) {
					RememberFrame rememberFrame = new RememberFrame(list.getSelectedValue().toString());
					rememberFrame.show();
				}
			}
		});
		btnNewButton_1.setFont(new Font("宋体", Font.PLAIN, 12));
		btnNewButton_1.setBounds(219, 229, 35, 66);
		frame.getContentPane().add(btnNewButton_1);
	}

	class AutoDictationThread implements Runnable {

		private int gapTimes = 0;
		private int repeatTimes = 1;
		private boolean order = true;
		private int wordsNum = 20;
		String targetFile = getDate();

		public void setTargetFile(String targetFile) {
			this.targetFile = targetFile;
		}

		public void setGapTimes(int gapTimes) {
			this.gapTimes = gapTimes;
		}

		public void setRepeatTimes(int repeatTimes) {
			this.repeatTimes = repeatTimes;
		}

		public void setOrder(boolean order) {
			this.order = order;
		}

		public void setWordsNum(int wordsNum) {
			this.wordsNum = wordsNum;
		}

		@Override
		public void run() {
			// File parentFolder=new File("videos\\2018.1.17");
			try {
				FileReader fileReader = new FileReader("words\\" + targetFile + ".txt");
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String temp;
				String[] tempStr;
				String[][] str = new String[100][2];
				int k = 0;
				while ((temp = bufferedReader.readLine()) != null) {
					tempStr = temp.split("\\#\\$\\$\\#");
					if (tempStr.length > 1) {
						str[k][0] = tempStr[0];
						str[k][1] = tempStr[1];
						k++;
					}
				}
				bufferedReader.close();
				fileReader.close();
				// File[] childrenFile=parentFolder.listFiles();
				int number;
				if (k < wordsNum) {
					number = k;
				} else {
					number = wordsNum;
				}
				if (order == false) {
					String tempV, tempT;
					Random random = new Random();
					int tempX;
					for (int i = 0; i < number; i++) {
						tempV = str[i][0];
						tempT = str[i][1];
						tempX = Math.abs(random.nextInt()) % number;
						str[i][0] = str[tempX][0];
						str[i][1] = str[tempX][1];
						str[tempX][0] = tempV;
						str[tempX][1] = tempT;
					}
				}
				for (int i = 0; i < number; i++) {
					System.out.println(str[i][0] + " " + str[i][1]);
					for (int j = 0; j < repeatTimes; j++) {
						BufferedInputStream buffer = new BufferedInputStream(
								new FileInputStream("videos\\" + targetFile + "\\" + str[i][0] + ".mp3"));
						Player player = new Player(buffer);
						player.play();
						Thread.sleep(500);
					}
					Thread.sleep(gapTimes * 1000);
				}
				System.out.println();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getDate() {
		Calendar now = Calendar.getInstance();
		String string = "";
		return now.get(Calendar.YEAR) + "." + String.valueOf(now.get(Calendar.MONTH) + 1) + "."
				+ now.get(Calendar.DAY_OF_MONTH);
	}

	private String[] getValues() {
		File wordsFile = new File("words");
		File[] dateFile = wordsFile.listFiles();
		int len = dateFile.length;
		String[] value = new String[len];
		for (int i = 0; i < len; i++) {
			value[i] = dateFile[i].getName();
			// System.out.println(dateFile[i].getName());
		}
		return value;
	}
}
