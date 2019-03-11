package xiatstudio;

import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Main extends JFrame {
	/* Default loading data */
	static String data = "./Benson_Data/empty.txt";
	static JPanel panel;
	static int displayMode = 0;
	static Font xtDefault = new Font("Segoe UI", Font.PLAIN, 14);
	static String yarccAddress = "@research2.york.ac.uk";
	static ImageIcon xt_logo = new ImageIcon("xt_logo.png");
	static JFrame frame = new JFrame();

	public static void main(String[] args) {
		/* Load GUI component */
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		GUISetup();
		System.gc();
	}

	public static void GUISetup() {
		/* Windows look and feel */
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Initialize JFrame and Menu bar */
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu, menu2, menu3, exportMenu, exportAllMenu;
		JMenuItem menuItem, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6, menuItem7;
		JMenuItem exportLibSVMData, setCGPParams;
		JMenuItem displayModeMenu[] = new JMenuItem[4];
		JMenuItem pen_offON, pen_offOFF;

		/* Background color */
		// Color bg = new Color(54, 63, 70);
		Color bg = new Color(250, 250, 250);

		menu = new JMenu("File");
		menu2 = new JMenu("Component");
		menu3 = new JMenu("Off-paper tracking");
		pen_offON = new JMenuItem("ON");
		pen_offOFF = new JMenuItem("OFF");
		menu3.add(pen_offON);
		menu3.add(pen_offOFF);

		menuBar.add(menu);
		menuBar.add(menu2);
		menuBar.add(menu3);
		menuItem = new JMenuItem("Open");

		exportMenu = new JMenu("Export as...");
		exportAllMenu = new JMenu("Export all as...");
		exportLibSVMData = new JMenuItem("Convert to LibSVM Data");
		setCGPParams = new JMenuItem("Set CGP Parameters");
		menuItem2 = new JMenuItem("PNG Image");
		menuItem3 = new JMenuItem("CSV File");
		menuItem4 = new JMenuItem("PNG Image");
		menuItem5 = new JMenuItem("CSV File");
		menuItem6 = new JMenuItem("CSV File (data only)");
		menuItem7 = new JMenuItem("Training Data Set");

		menu.add(menuItem);
		menu.add(exportMenu);
		menu.add(exportAllMenu);
		menu.add(exportLibSVMData);
		menu.add(setCGPParams);

		exportMenu.add(menuItem2);
		exportMenu.add(menuItem3);
		exportAllMenu.add(menuItem4);
		exportAllMenu.add(menuItem5);
		exportAllMenu.add(menuItem6);
		exportAllMenu.add(menuItem7);

		panel = new GPanel();
		frame.setJMenuBar(menuBar);
		frame.add(panel);
		frame.setTitle("Currently viewing: " + data);
		panel.setBackground(bg);
		frame.setSize(1280, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		frame.setIconImage(xt_logo.getImage());

		String displayMenuSet[] = { "All", "Horizontal", "Vertical", "Oblique" };
		for (int i = 0; i < 4; i++) {
			displayModeMenu[i] = new JMenuItem(displayMenuSet[i]);
			menu2.add(displayModeMenu[i]);
			final int tmpIndex = i;
			displayModeMenu[i].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					displayMode = tmpIndex;
					panel.repaint();
				}
			});
		}

		pen_offON.addActionListener(penOff_On);

		pen_offOFF.addActionListener(penOff_Off);

		/* Open file action */
		menuItem.addActionListener(openFileAction);

		setCGPParams.addActionListener(setCGP);

		exportLibSVMData.addActionListener(exportLibSVMAction);

		menuItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage imagebuf = null;
				imagebuf = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);

				panel.paint(imagebuf.getGraphics());
				try {
					ImageIO.write(imagebuf, "png", new File(data + ".png"));
				} catch (Exception e1) {
					System.out.println("error");
				}
			}
		});

		menuItem3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Benson b = new Benson(data, 0);
				exportSingleData(b, data.substring(0, data.lastIndexOf('.')) + ".csv");
			}

		});

		menuItem4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] controlDataList = getDataList(".\\Benson_Data\\Controls\\");
				outputPNGInBatch(controlDataList, panel);
				String[] patientDataList = getDataList(".\\Benson_Data\\Patients\\");
				outputPNGInBatch(patientDataList, panel);
			}
		});

		menuItem5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] controlDataList = getDataList(".\\Benson_Data\\Controls\\");
				exportAllData(controlDataList, ".\\Sheets\\control_"
						+ new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) + ".csv");
				String[] patientDataList = getDataList(".\\Benson_Data\\Patients\\");
				exportAllData(patientDataList, ".\\Sheets\\patient_"
						+ new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) + ".csv");
			}
		});

		menuItem6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] controlDataList = getDataList(".\\Benson_Data\\Controls\\");
				exportDataOnly(controlDataList, ".\\Sheets\\control_data_only_"
						+ new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) + ".csv");
				String[] patientDataList = getDataList(".\\Benson_Data\\Patients\\");
				exportDataOnly(patientDataList, ".\\Sheets\\patient_data_only_"
						+ new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) + ".csv");
			}
		});

		menuItem7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/* New pop up windows */
				JFrame popUp = new JFrame();
				popUp.setVisible(true);
				popUp.setSize(910, 400);
				popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				popUp.setLayout(new GridBagLayout());
				popUp.setTitle("Exporting CGP compatible data set");
				popUp.setIconImage(xt_logo.getImage());
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;

				/* GUI components */
				JLabel ratioPrompt = new JLabel("Ratio for training data (in %)");
				ratioPrompt.setFont(xtDefault);
				windowAddComponent(popUp, c, 0, 0, ratioPrompt);

				TextField trRatio = new TextField(10);
				trRatio.setText("60");
				windowAddComponent(popUp, c, 1, 0, trRatio);

				JButton exportData = new JButton("Export");
				exportData.setFont(xtDefault);
				windowAddComponent(popUp, c, 2, 0, exportData);

				JRadioButton copyData = new JRadioButton("Copy");
				copyData.setFont(xtDefault);
				windowAddComponent(popUp, c, 0, 1, copyData);

				JRadioButton recallData = new JRadioButton("Recall");
				recallData.setFont(xtDefault);
				recallData.setSelected(true);
				windowAddComponent(popUp, c, 1, 1, recallData);

				ButtonGroup drawinTask = new ButtonGroup();
				drawinTask.add(copyData);
				drawinTask.add(recallData);
				copyData.setSelected(true);

				JRadioButton visualClassify = new JRadioButton("Visual");
				visualClassify.setFont(xtDefault);
				windowAddComponent(popUp, c, 4, 1, visualClassify);

				JRadioButton conditionClassify = new JRadioButton("Condition");
				conditionClassify.setFont(xtDefault);
				windowAddComponent(popUp, c, 5, 1, conditionClassify);

				JRadioButton updrsClassify = new JRadioButton("UPDRS");
				updrsClassify.setFont(xtDefault);
				windowAddComponent(popUp, c, 6, 1, updrsClassify);

				JRadioButton singleOutput = new JRadioButton("Single Output");
				singleOutput.setFont(xtDefault);
				windowAddComponent(popUp, c, 2, 1, singleOutput);
				JRadioButton fourOutputs = new JRadioButton("Four Outputs");
				fourOutputs.setFont(xtDefault);
				windowAddComponent(popUp, c, 3, 1, fourOutputs);

				ButtonGroup bGroup = new ButtonGroup();

				/* Only one option allow each time */
				bGroup.add(singleOutput);
				bGroup.add(fourOutputs);
				singleOutput.setSelected(true);

				ButtonGroup classifyGroup = new ButtonGroup();

				classifyGroup.add(visualClassify);
				classifyGroup.add(conditionClassify);
				classifyGroup.add(updrsClassify);
				visualClassify.setSelected(true);

				String featureTag[] = { "Total Time", "Total Length", "Size", "Aspect Ratio", "Velocity SD", "Angle SD",
						"Pen-Up Portion", "Horizontal Portion", "Vertical Portion", "Oblique Portion", "Horizontal SD",
						"Vertical SD", "Oblique SD", "Hesitation Counts(down)", "Hesitation Counts(up)",
						"Hesitation Portion(down)", "Hesitation Portion(up)" };

				JCheckBox featureSelection[] = new JCheckBox[featureTag.length];

				String pdCondition[] = { "PD-NC", "PD-MCI", "PD-D", "Control" };
				JCheckBox pdSelect[] = new JCheckBox[pdCondition.length];

				TextField tierRange[] = new TextField[8];
				JLabel tier[] = new JLabel[4];
				JLabel tierTitle = new JLabel("Tier definition");
				tierTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));

				for (int i = 0; i < pdCondition.length; i++) {
					pdSelect[i] = new JCheckBox(pdCondition[i]);
					pdSelect[i].setFont(xtDefault);
					pdSelect[i].setSelected(true);
					windowAddComponent(popUp, c, i, 2, pdSelect[i]);
				}

				windowAddComponent(popUp, c, 0, 3, tierTitle);

				for (int i = 0; i < 4; i++) {
					tierRange[i * 2] = new TextField(2);
					tierRange[i * 2].setText(String.valueOf(i * 5));
					tierRange[i * 2].setFont(xtDefault);
					tierRange[i * 2 + 1] = new TextField(2);
					tierRange[i * 2 + 1].setText(String.valueOf(i * 5 + 4));
					tierRange[i * 2 + 1].setFont(xtDefault);

					if (i * 5 + 4 > 17)
						tierRange[i * 2 + 1].setText(String.valueOf(17));

					tier[i] = new JLabel("Class " + String.valueOf(i + 1));
					tier[i].setFont(xtDefault);

					windowAddComponent(popUp, c, i, 4, tierRange[i * 2]);

					windowAddComponent(popUp, c, i, 5, tier[i]);

					windowAddComponent(popUp, c, i, 6, tierRange[i * 2 + 1]);

				}
				JLabel featureTitle = new JLabel("Feature selection");
				featureTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
				windowAddComponent(popUp, c, 0, 7, featureTitle);

				int x = -1;
				int y = 8;
				for (int i = 0; i < featureTag.length; i++) {
					x++;
					featureSelection[i] = new JCheckBox(featureTag[i]);
					featureSelection[i].setSelected(true);
					featureSelection[i].setFont(xtDefault);
					windowAddComponent(popUp, c, x, y, featureSelection[i]);
					if (x == 4) {
						y++;
						x = -1;
					}
				}

				boolean featureSelected[] = new boolean[featureTag.length];

				JLabel msg = new JLabel("Cover existing data set?");
				msg.setFont(xtDefault);

				windowAddComponent(popUp, c, 0, y + 1, msg);

				JButton confirm = new JButton("Yes");
				confirm.setFont(xtDefault);
				c.weightx = 0.5;
				windowAddComponent(popUp, c, 1, y + 1, confirm);
				confirm.setSize(50, 30);

				JButton noConfirm = new JButton("No");
				noConfirm.setFont(xtDefault);
				c.weightx = 0.4;
				windowAddComponent(popUp, c, 2, y + 1, noConfirm);
				noConfirm.setSize(50, 30);

				msg.setVisible(false);
				confirm.setVisible(false);
				noConfirm.setVisible(false);

				TextField statusBar = new TextField(400);
				statusBar.setFont(xtDefault);
				statusBar.setEditable(false);
				c.weightx = 0.2;
				c.gridwidth = 3;
				windowAddComponent(popUp, c, 0, y + 2, statusBar);

				exportData.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int outputMode = 0;// 0: all, 1:copy only, 2:recall only
						String dataSetFolder = "";

						int classificationScheme = 0;

						if (copyData.isSelected())
							outputMode = 1;
						else if (recallData.isSelected())
							outputMode = 2;

						int cgpOutputMode = 0;// 0:One output, 1:Four outputs

						if (fourOutputs.isSelected())
							cgpOutputMode = 1;
						else
							cgpOutputMode = 0;

						for (int i = 0; i < featureTag.length; i++) {
							featureSelected[i] = featureSelection[i].isSelected();
						}

						int tierDef[] = new int[8];
						for (int i = 0; i < 8; i++) {
							tierDef[i] = Integer.parseInt(tierRange[i].getText());
						}

						if (visualClassify.isSelected())
							classificationScheme = 0;
						else if (conditionClassify.isSelected())
							classificationScheme = 1;
						else if (updrsClassify.isSelected())
							classificationScheme = 2;

						double trainingRatio = Double.parseDouble(trRatio.getText()) / 100;
						statusBar.setText("Setting tiers.");
						exportCustomTier(tierDef);
						statusBar.setText("Exporting data set.");

						int pdCond[] = new int[pdSelect.length];

						for (int i = 0; i < pdSelect.length; i++) {
							pdCond[i] = 0;
							if (pdSelect[i].isSelected())
								pdCond[i] = i + 1;
						}

						try {
							dataSetFolder = exportCGPDataSet(trainingRatio, outputMode, featureSelected, cgpOutputMode,
									classificationScheme, pdCond);
						} catch (Exception e1) {
							statusBar.setText("Data export failed.");
						}

						statusBar.setText("Data set exported to " + dataSetFolder + " folder.");

						/* Provide data overwrite option when exporting is done */
						msg.setVisible(true);
						confirm.setVisible(true);
						noConfirm.setVisible(true);

						confirm.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								/* Get previous data set */
								File prevTraining = new File(".//Algorithm_Training//01_training.csv");
								File prevValidation = new File(".//Algorithm_Training//02_validation.csv");
								File prevTesting = new File(".//Algorithm_Training//03_testing.csv");
								File tmpPath = new File(".\\Sheets\\DataSet");

								/*
								 * Get folder of new data set, typically the newest folder in the DataSet folder
								 */
								String tmpDirs[] = tmpPath.list(new FilenameFilter() {
									@Override
									public boolean accept(File current, String name) {
										return new File(current, name).isDirectory();
									}
								});

								String tmpFolder = ".\\Sheets\\DataSet\\" + tmpDirs[tmpDirs.length - 1];
								File training = new File(tmpFolder + "\\01_training.csv");
								File validation = new File(tmpFolder + "\\02_validation.csv");
								File testing = new File(tmpFolder + "\\03_testing.csv");

								try {
									/* Overwrite new data set to the cgp root folder */
									copyFile(training, prevTraining);
									copyFile(validation, prevValidation);
									copyFile(testing, prevTesting);
									statusBar.setText("Data set copied to training root folder.");
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						});

						noConfirm.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								statusBar.setText("Operation cancelled, previous data set not covered.");
							}
						});
					}
				});

			}
		});

	}

	public static void copyFile(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	public static void objectCSVFileCreation(String fileName) {
		File f = new File(fileName);

		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputPNGInBatch(String[] dataList, JPanel p) {
		BufferedImage imagebuf = null;

		for (int i = 0; i < dataList.length; i++) {
			data = dataList[i];
			data = data.replace("\\", "/");

			p.repaint();

			imagebuf = new BufferedImage(p.getWidth(), p.getHeight(), BufferedImage.TYPE_INT_RGB);
			p.paint(imagebuf.getGraphics());
			try {
				ImageIO.write(imagebuf, "png",
						new File(dataList[i].substring(0, dataList[i].lastIndexOf('.')) + "-drawing.png"));
				System.out.println("Generating image " + dataList[i].substring(0, dataList[i].lastIndexOf('.'))
						+ "-drawing.png ...");
			} catch (Exception e1) {
				System.out.println("error");
			}
		}
	}

	public static void windowAddComponent(JFrame frame, GridBagConstraints c, int gridX, int gridY,
			java.awt.Component o) {
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = gridX;
		c.gridy = gridY;
		frame.add(o, c);
	}

	/* This function cannot be used to find folders in a directory */
	public static String[] getDataList(String path) {
		File folder = new File(path);

		FilenameFilter fileNameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {

					// get last index for '.' char
					int lastIndex = name.lastIndexOf('.');

					// get extension
					String str = name.substring(lastIndex);

					// match path name extension
					if (str.equals(".txt")) {
						return true;
					}
				}

				return false;
			}
		};

		File[] listOfFiles = folder.listFiles(fileNameFilter);

		String[] fileList = new String[listOfFiles.length];

		for (int i = 0; i < listOfFiles.length; i++) {
			fileList[i] = path + listOfFiles[i].getName();
		}

		return fileList;
	}

	public static void exportDataOnly(String[] dataList, String fileName) {
		objectCSVFileCreation(fileName);
		FileWriter writer;

		try {
			writer = new FileWriter(fileName, true);

			for (int i = 0; i < dataList.length; i++) {

				Benson b = new Benson(dataList[i].replace("\\", "/"), 0);
				b.calcThreeLength();
				String[] dataPending = { b.getID(), b.getFigureMode(), String.valueOf(b.timeSpent / 100000),
						String.valueOf((double) (b.getTotalLength() / 10000)),
						String.valueOf(b.getSize()[0] * b.getSize()[1] / 1000000),
						String.valueOf((double) (b.getSize()[0] / b.getSize()[1] / 10)),
						String.valueOf(b.getVelocitySD() / 10), String.valueOf(b.getAngleSD() / 10),
						String.valueOf(b.penoffCount() / (b.getTimeStamp() + 1)),
						String.valueOf((double) (b.getHoriPortion())), String.valueOf((double) b.getVertPortion()),
						String.valueOf((double) b.getObliPortion()), String.valueOf((double) b.getThreeSD()[0]),
						String.valueOf((double) b.getThreeSD()[1]), String.valueOf((double) b.getThreeSD()[2]),
						String.valueOf((double) b.getHesitation() / 1000),
						String.valueOf((double) b.getHesitationPortion()), String.valueOf(b.getRating()) };

				System.out.println("Exporting data from " + b.getID() + "_" + b.getFigureMode());

				writeData(writer, dataPending);

				writer.append("\r\n");

			}

			System.out.println("File " + fileName + " created");
			System.out.println(" ");

			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportLibSVMData(String fileName) {
		String libsvmFilename = fileName + ".libsvm";
		objectCSVFileCreation(libsvmFilename);
		FileWriter writer;
		String line = "";
		BufferedReader br = null;

		try {
			writer = new FileWriter(libsvmFilename, true);
			br = new BufferedReader(new FileReader(fileName));

			while ((line = br.readLine()) != null) {
				String[] tmpArray = line.split(",");
				if (Integer.parseInt(tmpArray[tmpArray.length - 1]) > 2) {
					writer.append("+1");
					writer.append(" ");
				} else {
					writer.append("-1");
					writer.append(" ");
				}
				for (int i = 1; i < tmpArray.length - 1; i++) {
					writer.append(i + ":" + tmpArray[i]);
					writer.append(" ");
				}

				writer.append("\r\n");
			}

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("LIBSVM_DATA_ERROR: Specific data file can not be found.");
		} catch (IOException e) {
			System.out.println("LIBSVM_DATA_ERROR: Specific data file can not be accessed.");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void exportAllData(String[] dataList, String fileName) {
		objectCSVFileCreation(fileName);
		FileWriter writer;

		try {
			writer = new FileWriter(fileName, true);
			String[] title = { "Subject ID", "Mode", "Total time", "Total length", "Size", "Aspect Ratio",
					"Velocity Stability", "Angular Stability", "Pen Off %", "Hori Portion", "Vert Portion",
					"Obli Portion", "Hori SD", "Vert SD", "Obli SD", "Hesitation Count", "Hesitation Portion" };
			writeData(writer, title);
			writer.append("\r\n");

			for (int i = 0; i < dataList.length; i++) {

				Benson b = new Benson(dataList[i].replace("\\", "/"), 0);
				b.calcThreeLength();
				String[] dataPending = { b.getID(), b.getFigureMode(), String.valueOf(b.timeSpent / 10000),
						String.valueOf(b.getTotalLength() / 10000),
						String.valueOf(b.getSize()[0] * b.getSize()[1] / 1000000),
						String.valueOf((double) (b.getSize()[0] / b.getSize()[1] / 10)),
						String.valueOf(b.getVelocitySD() / 10), String.valueOf(b.getAngleSD() / 10),
						String.valueOf(b.penoffCount() / (b.getTimeStamp() + 1)),
						String.valueOf((double) (b.getHoriPortion())), String.valueOf((double) b.getVertPortion()),
						String.valueOf((double) b.getObliPortion()), String.valueOf((double) b.getThreeSD()[0]),
						String.valueOf((double) b.getThreeSD()[1]), String.valueOf((double) b.getThreeSD()[2]),
						String.valueOf((double) b.getHesitation() / 1000),
						String.valueOf((double) b.getHesitationPortion()), String.valueOf(b.getRating()) };

				System.out.println("Exporting data from " + b.getID() + "_" + b.getFigureMode());

				writeData(writer, dataPending);

				writer.append("\r\n");

			}

			System.out.println("File " + fileName + " created");
			System.out.println(" ");

			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String exportCGPDataSet(double trainingRatio, int mode, boolean[] selections, int outputMode,
			int classificationScheme, int[] pdCond) {
		String dataFolder = ".\\Sheets\\DataSet\\" + new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date());
		File newDataFolder = new File(dataFolder);
		newDataFolder.mkdir();
		String overall = newDataFolder.getPath() + "\\00_overall" + ".csv";
		String training = newDataFolder.getPath() + "\\01_training" + ".csv";
		String validation = newDataFolder.getPath() + "\\02_validation" + ".csv";
		String testing = newDataFolder.getPath() + "\\03_testing" + ".csv";

		objectCSVFileCreation(overall);
		objectCSVFileCreation(training);
		objectCSVFileCreation(validation);
		objectCSVFileCreation(testing);

		double classTotal[] = { 0, 0, 0, 0 };
		double copyTotal[] = { 0, 0, 0, 0 };
		double recallTotal[] = { 0, 0, 0, 0 };

		BufferedReader br = null;
		String line = "";

		int trainingClasses[][] = new int[4][3];

		int validationClasses[][] = new int[4][3];

		int testingClasses[][] = new int[4][3];

		String ratingSheet;

		if (classificationScheme == 0)
			ratingSheet = ".\\Sheets\\visual_rating.csv";
		else
			ratingSheet = ".\\Sheets\\condition_rating.csv";

		try {

			br = new BufferedReader(new FileReader(ratingSheet));

			while ((line = br.readLine()) != null) {
				/* Counting total objects in all, copy only and recall only */
				classTotal[Integer.parseInt(line.split(",")[2]) - 1]++;

				if (line.split(",")[1].equals("Copy"))
					copyTotal[Integer.parseInt(line.split(",")[2]) - 1]++;
				if (line.split(",")[1].equals("Recall"))
					recallTotal[Integer.parseInt(line.split(",")[2]) - 1]++;
			}
		} catch (Exception e) {
			System.out.println("RATING_SHEET_ERROR: File not found or can not be accessed.");
		}

		for (int i = 0; i < 4; i++) {

			/* Calculate number of objects required for each data set segment */
			trainingClasses[i][0] = (int) Math.floor(classTotal[i] * trainingRatio);
			trainingClasses[i][1] = (int) Math.floor(copyTotal[i] * trainingRatio);
			trainingClasses[i][2] = (int) Math.floor(recallTotal[i] * trainingRatio);

			validationClasses[i][0] = (int) Math.floor((classTotal[i] - trainingClasses[i][0]) / 2);
			validationClasses[i][1] = (int) Math.floor((copyTotal[i] - trainingClasses[i][1]) / 2);
			validationClasses[i][2] = (int) Math.floor((recallTotal[i] - trainingClasses[i][2]) / 2);

			testingClasses[i][0] = (int) (classTotal[i] - trainingClasses[i][0] - validationClasses[i][0]);
			testingClasses[i][1] = (int) (copyTotal[i] - trainingClasses[i][1] - validationClasses[i][1]);
			testingClasses[i][2] = (int) (recallTotal[i] - trainingClasses[i][2] - validationClasses[i][2]);

		}

		int trainingCounter[] = { 0, 0, 0, 0 };
		int validationCounter[] = { 0, 0, 0, 0 };
		int testingCounter[] = { 0, 0, 0, 0 };

		FileWriter fwOverall, fwTraining, fwValidation, fwTesting;

		try {
			fwOverall = new FileWriter(overall, true);
			fwTraining = new FileWriter(training, true);
			fwValidation = new FileWriter(validation, true);
			fwTesting = new FileWriter(testing, true);

			String[] controlDataList = getDataList(".\\Benson_Data\\Controls\\");
			String[] patientDataList = getDataList(".\\Benson_Data\\Patients\\");
			String[] overallDataList = new String[controlDataList.length + patientDataList.length];
			int selectedCount = 0;
			for (int i = 0; i < selections.length; i++) {
				/* Counting how many features are selected */
				if (selections[i])
					selectedCount++;
			}

			/* Data set header */
			String cgpIOPair = String.valueOf(selectedCount) + ",1,";

			if (outputMode == 1)
				cgpIOPair = String.valueOf(selectedCount) + ",4,";

			int trainingTotal = 0;
			int validateTotal = 0;
			int testTotal = 0;

			for (int i = 0; i < pdCond.length; i++) {
				if (pdCond[i] == i + 1) {
					trainingTotal += trainingClasses[i][mode];
					validateTotal += validationClasses[i][mode];
					testTotal += testingClasses[i][mode];
				}
			}

			fwTraining.append(cgpIOPair + trainingTotal + ",");
			fwTraining.append("\r\n");

			fwValidation.append(cgpIOPair + validateTotal + ",");
			fwValidation.append("\r\n");

			fwTesting.append(cgpIOPair + testTotal + ",");
			fwTesting.append("\r\n");

			for (int i = 0; i < overallDataList.length; i++) {
				if (i < controlDataList.length)
					overallDataList[i] = controlDataList[i];
				else
					overallDataList[i] = patientDataList[i - controlDataList.length];
			}

			for (int i = 0; i < overallDataList.length; i++) {
				Benson b = new Benson(overallDataList[i].replace("\\", "/"), classificationScheme);
				b.calcThreeLength();

				String ratingString = String.valueOf(b.getRating());
				if (classificationScheme == 2)
					ratingString = String.valueOf(b.getUPDRS());

				String[] dataPending = { String.valueOf(b.timeSpent / 100000),
						String.valueOf(b.getTotalLength() / 10000),
						String.valueOf(b.getSize()[0] * b.getSize()[1] / 1000000),
						String.valueOf((double) (b.getSize()[0] / b.getSize()[1] / 10)),
						String.valueOf(b.getVelocitySD() / 10), String.valueOf(b.getAngleSD() / 10),
						String.valueOf(b.penoffCount() / (b.getTimeStamp() + 1)),
						String.valueOf((double) (b.getHoriPortion())), String.valueOf((double) b.getVertPortion()),
						String.valueOf((double) b.getObliPortion()), String.valueOf((double) b.getThreeSD()[0]),
						String.valueOf((double) b.getThreeSD()[1]), String.valueOf((double) b.getThreeSD()[2]),
						String.valueOf((double) b.getHesitation() / 1000),
						String.valueOf((double) b.getPenUpHesitation() / 1000),
						String.valueOf((double) b.getHesitationPortion() * 10),
						String.valueOf((double) b.getPenUpHesiPortion() * 10), ratingString };

				/* Check whether this data is entitled to be exported */
				if (dataWriteHandshake(mode, b, ratingSheet, pdCond)) {
					String alterRating[] = { "0", "0", "0", "0" };
					alterRating[b.getRating() - 1] = "1";

					for (int j = 0; j < selections.length; j++) {
						if (!selections[j]) {
							dataPending[j] = null;
						}
					}

					List<String> list = new ArrayList<String>(Arrays.asList(dataPending));

					if (outputMode == 1) {
						list.remove(dataPending.length - 1);
						for (int j = 0; j < 4; j++) {
							list.add(alterRating[j]);
							dataPending = list.toArray(new String[0]);
						}

						if (outputMode == 0) {
							for (int j = 0; j < 4; j++) {
								list.remove(dataPending.length - 1);
								dataPending = list.toArray(new String[0]);
							}
							list.add(String.valueOf(b.getRating()));
							dataPending = list.toArray(new String[0]);
						}
					}

					String dataToWrite[] = removeNull(dataPending);
					writeData(fwOverall, dataToWrite);
					fwOverall.append("\r\n");

					if (trainingCounter[b.getRating() - 1] != trainingClasses[b.getRating() - 1][mode]) {
						writeData(fwTraining, dataToWrite);
						fwTraining.append("\r\n");
						trainingCounter[b.getRating() - 1]++;
						System.out.println(
								"Data " + b.getID() + "_" + b.getFigureMode() + " exported to training data set.");
					} else if (validationCounter[b.getRating() - 1] != validationClasses[b.getRating() - 1][mode]) {
						writeData(fwValidation, dataToWrite);
						fwValidation.append("\r\n");
						validationCounter[b.getRating() - 1]++;
						System.out.println(
								"Data " + b.getID() + "_" + b.getFigureMode() + " exported to validation data set.");
					} else if (testingCounter[b.getRating() - 1] != testingClasses[b.getRating() - 1][mode]) {
						writeData(fwTesting, dataToWrite);
						fwTesting.append("\r\n");
						testingCounter[b.getRating() - 1]++;
						System.out.println(
								"Data " + b.getID() + "_" + b.getFigureMode() + " exported to testing data set.");
					}
				}
			}

			fwTraining.flush();
			fwTraining.close();

			fwValidation.flush();
			fwValidation.close();

			fwTesting.flush();
			fwTesting.close();

			System.out.println("Data set export complete.");

			fwOverall.flush();
			fwOverall.close();

			System.gc();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return newDataFolder.getPath() + "\\";

	}

	public static String[] removeNull(String[] a) {
		ArrayList<String> removedNull = new ArrayList<String>();
		for (String str : a)
			if (str != null)
				removedNull.add(str);
		return removedNull.toArray(new String[0]);
	}

	public static void exportCustomTier(int[] customTier) {
		File ratingSheet = new File(".\\Sheets\\visual_rating.csv");
		FileWriter fwRating;
		BufferedReader br;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(".\\Sheets\\original_rating.csv"));
			fwRating = new FileWriter(ratingSheet, false);
			while ((line = br.readLine()) != null) {
				int tmpRate = Integer.parseInt(line.split(",")[2]);
				for (int i = 1; i <= 7; i += 2) {
					if (tmpRate >= customTier[i - 1] && tmpRate <= customTier[i]) {
						fwRating.append(line.split(",")[0]);
						fwRating.append(",");
						fwRating.append(line.split(",")[1]);
						fwRating.append(",");
						fwRating.append(String.valueOf((i + 1) / 2));
						fwRating.append("\r\n");
					}
				}
			}

			fwRating.flush();
			fwRating.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean dataWriteHandshake(int mode, Benson b, String ratingSheet, int[] rating) {
		/* Part I: Check the rating sheet */
		BufferedReader br = null;
		String line = "";
		ArrayList<String> writeQueue = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(ratingSheet));

			while ((line = br.readLine()) != null) {
				writeQueue.add(line.split(",")[0] + line.split(",")[1] + "_" + line.split(",")[2]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean writeApprove = false;

		for (int i = 0; i < rating.length; i++) {
			if (writeQueue.contains(b.getID() + b.getFigureMode() + "_" + rating[i])) {
				writeApprove = true;
				break;
			}

		}

		/* Part II: Check mode */
		if (writeApprove) {
			switch (mode) {
			case 0:
				return true;
			case 1:
				if (b.getFigureMode().equals("Copy"))
					return true;
				else
					return false;
			case 2:
				if (b.getFigureMode().equals("Recall"))
					return true;
				else
					return false;
			default:
				return true;
			}
		} else {
			return false;
		}
	}

	public static void writeData(FileWriter writer, String[] data) throws IOException {
		for (int i = 0; i < data.length; i++) {

			writer.append(data[i]);
			writer.append(',');
		}
	}

	public static void exportSingleData(Benson b, String fileName) {
		objectCSVFileCreation(fileName);

		FileWriter writer;
		try {
			writer = new FileWriter(fileName, true);
			String[] title = { "Subject ID", "Mode", "Total time", "Total length", "Size", "Aspect Ratio",
					"Velocity Stability", "Angular Stability", "Pen Off %" };
			writeData(writer, title);
			writer.append("\r\n");

			String[] dataPending = { b.getID(), b.getFigureMode(), String.valueOf(b.timeSpent),
					String.valueOf(b.getTotalLength()), String.valueOf(b.getSize()[0] * b.getSize()[1]),
					String.valueOf((double) (b.getSize()[0] / b.getSize()[1])), String.valueOf(b.getVelocitySD()),
					String.valueOf(b.getAngleSD()), String.valueOf(b.penoffCount() / (b.getTimeStamp() + 1)) };

			writeData(writer, dataPending);

			writer.append("\r\n");

			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class GPanel extends JPanel {
		public void Panel() {
			super.setPreferredSize(new Dimension(1280, 720));
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setRenderingHints(hints);
			Benson testFigure = new Benson(data, 0);
			testFigure.drawBenson(g2, displayMode);
		}
	}

	static ActionListener penOff_On = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			displayMode = 4;
			panel.repaint();
		}
	};

	static ActionListener penOff_Off = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			displayMode = 0;
			panel.repaint();

		}
	};
	
	static ActionListener openFileAction = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			/* Default path */
			JFileChooser fileChooser = new JFileChooser(".\\Benson_Data");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt, *.text)", "txt",
					"text");
			fileChooser.setFileFilter(filter);

			switch (fileChooser.showOpenDialog(panel)) {
			case JFileChooser.APPROVE_OPTION:
				data = fileChooser.getSelectedFile().getPath();
				/* Replace backslash in the path */
				data = data.replace("\\", "/");
				/* Update content */
				displayMode = 0;
				panel.repaint();
				frame.setTitle("Currently viewing: " + data);
				System.gc();
				// System.out.println(data);
				break;
			}
		}
	};

	static ActionListener setCGP = new ActionListener(){
		@Override
			public void actionPerformed(ActionEvent e) {
				/* New pop up windows */
				JFrame frame = new JFrame();
				frame.setSize(600, 360);
				frame.setTitle("Set CGP Parameters");
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLayout(new GridBagLayout());
				frame.setIconImage(xt_logo.getImage());

				/* Set Layout Manager */
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;

				String cgpTags[] = { "Threshold Initial", "Threshold Increment", "Class Numbers", "Nodes", "Arity",
						"Max Generations", "Update Frequency", "Random number seed", "Mutation Rate", "Input(s)",
						"Output(s)" };

				String defaultValue[] = { "10", "10", "4", "20", "3", "100000", "500", "1234", "0.08", "17", "1" };
				JLabel params[] = new JLabel[defaultValue.length];
				TextField cgpParams[] = new TextField[defaultValue.length];

				/* Adding components above to the menu */
				for (int i = 0; i < defaultValue.length; i++) {
					params[i] = new JLabel(cgpTags[i]);
					params[i].setFont(xtDefault);
					windowAddComponent(frame, c, 0, i, params[i]);

					cgpParams[i] = new TextField(10);
					cgpParams[i].setFont(xtDefault);
					cgpParams[i].setText(defaultValue[i]);
					windowAddComponent(frame, c, 1, i, cgpParams[i]);
				}

				JButton export = new JButton("Save parameter");
				JButton launchCGP = new JButton("Launch CGP (in YARCC)");
				JButton localCGP = new JButton("Launch CGP (in local)");

				export.setFont(xtDefault);
				windowAddComponent(frame, c, 0, defaultValue.length, export);

				launchCGP.setFont(xtDefault);
				windowAddComponent(frame, c, 1, defaultValue.length, launchCGP);

				localCGP.setFont(xtDefault);
				windowAddComponent(frame, c, 2, defaultValue.length, localCGP);

				export.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						FileWriter writer;
						try {
							File cgp_Param = new File(".\\Algorithm_Training\\cgp_params.txt");
							writer = new FileWriter(cgp_Param, false);// false parameter will overwrite previous file

							for (int i = 0; i < cgpTags.length; i++) {
								writer.append(cgpParams[i].getText());
								writer.append("\n");
							}

							writer.flush();
							writer.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}

					}
				});

				launchCGP.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFrame yarccLogin = new JFrame();
						yarccLogin.setSize(370, 150);
						yarccLogin.setVisible(true);
						yarccLogin.setLayout(new GridBagLayout());
						yarccLogin.setTitle("Logging in to YARCC");
						GridBagConstraints c = new GridBagConstraints();
						c.fill = GridBagConstraints.HORIZONTAL;

						JLabel infoBoard = new JLabel("After login, type ./cgp_run.sh");
						infoBoard.setFont(xtDefault);
						windowAddComponent(yarccLogin, c, 0, 0, infoBoard);

						JLabel userName = new JLabel("User Name");
						userName.setFont(xtDefault);
						windowAddComponent(yarccLogin, c, 0, 1, userName);

						JLabel pw = new JLabel("Password");
						pw.setFont(xtDefault);
						windowAddComponent(yarccLogin, c, 0, 2, pw);

						TextField userInput = new TextField(10);
						JPasswordField pwInput = new JPasswordField(10);
						windowAddComponent(yarccLogin, c, 1, 1, userInput);
						windowAddComponent(yarccLogin, c, 1, 2, pwInput);

						JButton loginConfirm = new JButton("Log In");
						windowAddComponent(yarccLogin, c, 0, 3, loginConfirm);

						loginConfirm.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									yarccLogin.dispose();
									Runtime.getRuntime().exec("putty.exe " + userInput.getText() + yarccAddress
											+ " -pw " + pwInput.getText());
								} catch (Exception e1) {
									infoBoard.setText("PuTTY.exe missing.");
								}
							}
						});
					}
				});

				localCGP.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Runtime.getRuntime().exec(
									"cmd /c start cmd.exe /K \" cd Algorithm_Training && Algorithm_Training.exe\"");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});

			}
	};

	static ActionListener exportLibSVMAction = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(".\\Sheets");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File(*.csv)", "csv");
			fileChooser.setFileFilter(filter);

			switch (fileChooser.showOpenDialog(panel)) {
			case JFileChooser.APPROVE_OPTION:
				String dataPending = fileChooser.getSelectedFile().getPath();
				// exportLibSVMData(dataPending);
				System.gc();
				break;
			}

		}
	};

}