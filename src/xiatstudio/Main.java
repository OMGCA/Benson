package xiatstudio;

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
import java.io.RandomAccessFile;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Main extends JFrame {
	/* Default loading data */
	static String data = "./Benson_Data/empty.txt";
	static JPanel panel;
	static int displayMode = 0;

	public static void main(String[] args) {
		/* Load GUI component */
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
		JFrame frame = new JFrame();
		JMenuBar menuBar = new JMenuBar();
		JMenu menu, menu2, menu3, exportMenu, exportAllMenu;
		JMenuItem menuItem, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6, menuItem7;
		JMenuItem mode1, mode2, mode3, mode4, exportLibSVMData, setCGPParams;
		JMenuItem pen_offON, pen_offOFF;

		/* Background color */
		Color bg = new Color(54, 63, 70);

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
		mode1 = new JMenuItem("All");
		mode2 = new JMenuItem("Horizontal");
		mode3 = new JMenuItem("Vertical");
		mode4 = new JMenuItem("Oblique");
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
		menu2.add(mode1);
		menu2.add(mode2);
		menu2.add(mode3);
		menu2.add(mode4);
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
		ImageIcon xt_logo = new ImageIcon("xt_logo.png");
		frame.setIconImage(xt_logo.getImage());

		mode1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMode = 0;
				panel.repaint();

			}
		});

		mode2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMode = 1;
				panel.repaint();

			}
		});

		mode3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMode = 2;
				panel.repaint();

			}
		});

		mode4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMode = 3;
				panel.repaint();

			}
		});

		pen_offON.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMode = 4;
				panel.repaint();

			}
		});

		pen_offOFF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMode = 0;
				panel.repaint();

			}
		});

		/* Open file action */
		menuItem.addActionListener(new ActionListener() {
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
		});

		setCGPParams.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/* New pop up windows */
				JFrame frame = new JFrame();
				frame.setSize(450,370);
				frame.setTitle("Set CGP Parameters");
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLayout(new GridBagLayout());
				frame.setIconImage(xt_logo.getImage());

				/* Set Layout Manager */
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;

				String cgpTags[] = { "Threshold 1", "Threshold 2", "Threshold 3", "Nodes", "Arity", "Max Generations",
						"Update Frequency", "Random number seed", "Mutation Rate", "Input(s)", "Output(s)" };

				String defaultValue[] = { "10", "20", "30", "20", "3", "100000", "500", "1234", "0.08", "17", "1" };
				JLabel params[] = new JLabel[defaultValue.length];
				TextField cgpParams[] = new TextField[defaultValue.length];

				/* Adding components above to the menu */
				for (int i = 0; i < defaultValue.length; i++) {
					c.gridx = 0;
					c.gridy = i;
					params[i] = new JLabel(cgpTags[i]);
					params[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
					frame.add(params[i], c);

					c.gridx = 1;
					c.gridy = i;
					cgpParams[i] = new TextField(10);
					cgpParams[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
					cgpParams[i].setText(defaultValue[i]);

					frame.add(cgpParams[i], c);
				}

				JButton export = new JButton("Save parameter");
				JButton launchCGP = new JButton("Launch CGP (in YARCC)");
				JButton localCGP = new JButton("Launch CGP (in local)");
				export.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				c.gridx = 0;
				c.gridy = defaultValue.length;
				frame.add(export, c);

				launchCGP.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				c.gridx = 1;
				c.gridy = defaultValue.length;
				frame.add(launchCGP, c);

				localCGP.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				c.gridx = 2;
				c.gridy = defaultValue.length;
				frame.add(localCGP, c);

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
						try {
							Runtime.getRuntime().exec("putty.exe [redacted]@[redacted] -pw [redacted]");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});

				localCGP.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Runtime.getRuntime().exec("cmd /c start cmd.exe /K \" cd Algorithm_Training && Algorithm_Training.exe\"");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});

			}
		});

		exportLibSVMData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".\\Sheets");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File(*.csv)", "csv");
				fileChooser.setFileFilter(filter);

				switch (fileChooser.showOpenDialog(panel)) {
				case JFileChooser.APPROVE_OPTION:
					String dataPending = fileChooser.getSelectedFile().getPath();
					exportLibSVMData(dataPending);
					System.gc();
					break;
				}

			}
		});

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
				Benson b = new Benson(data);
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
				popUp.setSize(750, 320);
				popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				popUp.setLayout(new GridBagLayout());
				popUp.setTitle("Exporting CGP compatible data set");
				popUp.setIconImage(xt_logo.getImage());

				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;

				/* GUI components */
				JLabel ratioPrompt = new JLabel("Ratio for training data (in %)");
				ratioPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));

				TextField trRatio = new TextField(10);
				trRatio.setText("60");

				JButton exportData = new JButton("Export");
				exportData.setFont(new Font("Segoe UI", Font.PLAIN, 12));

				JCheckBox copyData = new JCheckBox("Copy");
				copyData.setSelected(true);
				JCheckBox recallData = new JCheckBox("Recall");
				recallData.setSelected(true);

				JRadioButton singleOutput = new JRadioButton("Single Output");
				JRadioButton fourOutputs = new JRadioButton("Four Outputs");

				ButtonGroup bGroup = new ButtonGroup();

				/* Only one option allow each time */
				bGroup.add(singleOutput);
				bGroup.add(fourOutputs);
				singleOutput.setSelected(true);

				String featureTag[] = { "Total Time", "Total Length", "Size", "Aspect Ratio", "Velocity SD", "Angle SD",
						"Pen-Up Portion", "Horizontal Portion", "Vertical Portion", "Oblique Portion", "Horizontal SD",
						"Vertical SD", "Oblique SD", "Hesitation Counts(down)", "Hesitation Counts(up)",
						"Hesitation Portion(down)", "Hesitation Portion(up)" };

				JCheckBox featureSelection[] = new JCheckBox[featureTag.length];

				c.gridx = 0;
				c.gridy = 0;
				popUp.add(ratioPrompt, c);

				c.gridx = 1;
				c.gridy = 0;
				popUp.add(trRatio, c);

				c.gridx = 2;
				c.gridy = 0;
				popUp.add(exportData, c);

				c.gridx = 0;
				c.gridy = 1;
				popUp.add(copyData, c);

				c.gridx = 1;
				c.gridy = 1;
				popUp.add(recallData, c);

				c.gridx = 2;
				c.gridy = 1;
				popUp.add(singleOutput, c);

				c.gridx = 3;
				c.gridy = 1;
				popUp.add(fourOutputs, c);

				TextField tierRange[] = new TextField[8];
				JLabel tier[] = new JLabel[4];
				JLabel tierTitle = new JLabel("Tier definition");
				tierTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
				c.gridx = 0;
				c.gridy = 2;
				popUp.add(tierTitle, c);

				for (int i = 0; i < 4; i++) {
					tierRange[i * 2] = new TextField(2);
					tierRange[i * 2].setText(String.valueOf(i * 5));
					tierRange[i * 2 + 1] = new TextField(2);
					tierRange[i * 2 + 1].setText(String.valueOf(i * 5 + 4));

					if (i * 5 + 4 > 17)
						tierRange[i * 2 + 1].setText(String.valueOf(17));

					tier[i] = new JLabel("Class " + String.valueOf(i + 1));
					c.gridx = i;
					c.gridy = 3;
					popUp.add(tierRange[i * 2], c);

					c.gridy = 4;
					popUp.add(tier[i], c);

					c.gridy = 5;
					popUp.add(tierRange[i * 2 + 1], c);
				}
				JLabel featureTitle = new JLabel("Feature selection");
				featureTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
				c.gridx = 0;
				c.gridy++;
				popUp.add(featureTitle, c);

				int x = -1;
				int y = 7;
				for (int i = 0; i < featureTag.length; i++) {
					x++;
					featureSelection[i] = new JCheckBox(featureTag[i]);
					featureSelection[i].setSelected(true);
					featureSelection[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
					c.gridx = x;
					c.gridy = y;
					if (x == 4) {
						y++;
						x = -1;
					}
					popUp.add(featureSelection[i], c);
				}

				boolean featureSelected[] = new boolean[featureTag.length];

				JLabel msg = new JLabel("Cover existing data set?");
				msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				c.gridx = 0;
				c.gridy = y + 1;
				popUp.add(msg, c);

				JButton confirm = new JButton("Yes");
				confirm.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				c.weightx = 0.5;
				c.gridx = 1;
				c.gridy = y + 1;
				popUp.add(confirm, c);

				JButton noConfirm = new JButton("No");
				noConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				c.weightx = 0.4;
				c.gridx = 2;
				c.gridy = y + 1;
				popUp.add(noConfirm, c);
				noConfirm.setSize(50, 30);
				confirm.setSize(50, 30);

				msg.setVisible(false);
				confirm.setVisible(false);
				noConfirm.setVisible(false);

				TextField statusBar = new TextField(400);
				statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				statusBar.setEditable(false);
				c.weightx = 0.2;
				c.gridwidth = 3;
				c.ipadx = 400;
				c.gridx = 0;
				c.gridy = y + 2;
				popUp.add(statusBar, c);

				exportData.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int outputMode = 0;// 0: all, 1:copy only, 2:recall only
						String dataSetFolder = "";

						if (copyData.isSelected() && recallData.isSelected())
							outputMode = 0;
						else if (copyData.isSelected() && !recallData.isSelected())
							outputMode = 1;
						else if (!copyData.isSelected() && recallData.isSelected())
							outputMode = 2;

						int cgpoutputMode = 0;// 0:One output, 1:Four outputs

						if (fourOutputs.isSelected())
							cgpoutputMode = 1;
						else
							cgpoutputMode = 0;

						for (int i = 0; i < featureTag.length; i++) {
							featureSelected[i] = featureSelection[i].isSelected();
						}

						int tierDef[] = new int[8];
						for (int i = 0; i < 8; i++) {
							tierDef[i] = Integer.parseInt(tierRange[i].getText());
						}

						double trainingRatio = Double.parseDouble(trRatio.getText()) / 100;
						statusBar.setText("Setting tiers.");
						exportCustomTier(tierDef);
						statusBar.setText("Exporting data set.");
						dataSetFolder = exportCGPDataSet(trainingRatio, outputMode, featureSelected, cgpoutputMode);
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

				Benson b = new Benson(dataList[i].replace("\\", "/"));
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
			System.out.println("ERROR: Specific data file can not be found.");
		} catch (IOException e) {
			System.out.println("ERROR: Specific data file can not be accessed.");
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

				Benson b = new Benson(dataList[i].replace("\\", "/"));
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

	public static String exportCGPDataSet(double trainingRatio, int mode, boolean[] selections, int outputMode) {
		File newDataFolder = new File(
				".\\Sheets\\DataSet\\" + new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()));
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

		try {
			br = new BufferedReader(new FileReader(".\\Sheets\\rating.csv"));
			while ((line = br.readLine()) != null) {
				/* Counting total objects in all, copy only and recall only */
				classTotal[Integer.parseInt(line.split(",")[2]) - 1]++;

				if (line.split(",")[1].equals("Copy"))
					copyTotal[Integer.parseInt(line.split(",")[2]) - 1]++;
				if (line.split(",")[1].equals("Recall"))
					recallTotal[Integer.parseInt(line.split(",")[2]) - 1]++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Specific data file can not be found.");
		} catch (IOException e) {
			System.out.println("ERROR: Specific data file can not be accessed.");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			/* Calculate number of objects required for each data set segment */
			trainingClasses[i][0] = (int) Math.floor(classTotal[i] * trainingRatio);
			trainingClasses[i][1] = (int) Math.floor(copyTotal[i] * trainingRatio);
			trainingClasses[i][2] = (int) Math.floor(recallTotal[i] * trainingRatio);

			validationClasses[i][0] = (int) Math.floor(classTotal[i] * (1 - trainingRatio) / 2);
			validationClasses[i][1] = (int) Math.floor(copyTotal[i] * (1 - trainingRatio) / 2);
			validationClasses[i][2] = (int) Math.floor(recallTotal[i] * (1 - trainingRatio) / 2);

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

			fwTraining.append(cgpIOPair + (trainingClasses[0][mode] + trainingClasses[1][mode]
					+ trainingClasses[2][mode] + trainingClasses[3][mode]) + ",");
			fwTraining.append("\r\n");

			fwValidation.append(cgpIOPair + (validationClasses[0][mode] + validationClasses[1][mode]
					+ validationClasses[2][mode] + validationClasses[3][mode]) + ",");
			fwValidation.append("\r\n");

			fwTesting.append(cgpIOPair + (testingClasses[0][mode] + testingClasses[1][mode] + testingClasses[2][mode]
					+ testingClasses[3][mode]) + ",");
			fwTesting.append("\r\n");

			for (int i = 0; i < overallDataList.length; i++) {
				if (i < controlDataList.length)
					overallDataList[i] = controlDataList[i];
				else
					overallDataList[i] = patientDataList[i - controlDataList.length];
			}

			for (int i = 0; i < overallDataList.length; i++) {
				Benson b = new Benson(overallDataList[i].replace("\\", "/"));
				b.calcThreeLength();

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
						String.valueOf((double) b.getPenUpHesiPortion() * 10), String.valueOf(b.getRating()) };

				/* Check whether this data is entitled to be exported */
				if (dataWriteHandshake(mode, b, ".\\Sheets\\rating.csv")) {
					String alterRating[] = { "0", "0", "0", "0" };
					alterRating[b.getRating() - 1] = "1";

					/* Remove unselected feature */
					/* 2018-11-27: Bug, when deselect features for the second time */
					/* IndexOutOfBoundsException at line 989 */
					/* 2018-11-28: Bug fix */
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
						/* Update 2018-11-26 */
						/* Allow user to switch output mode back */
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

	public static void updateCGPDataSetFirstLine(String fileName, String newLine) {
		try {
			RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
			raf.seek(0);
			raf.writeBytes(newLine);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportCustomTier(int[] customTier) {
		File ratingSheet = new File(".\\Sheets\\rating.csv");
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

	public static boolean dataWriteHandshake(int mode, Benson b, String ratingSheet) {
		/* Part I: Check the rating sheet */
		BufferedReader br = null;
		String line = "";
		ArrayList<String> writeQueue = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(ratingSheet));

			while ((line = br.readLine()) != null) {
				writeQueue.add(line.split(",")[0] + line.split(",")[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean writeApprove = false;

		if (writeQueue.contains(b.getID() + b.getFigureMode()))
			writeApprove = true;

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
			Benson testFigure = new Benson(data);
			testFigure.drawBenson(g2, displayMode);
		}
	}
}
