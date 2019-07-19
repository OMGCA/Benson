package xiatstudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleExport {
	public static void main(String args[]) {
		// String[] controlDataList = getDataList(".\\Benson_Data\\Controls\\");
		// try{
		// exportData(controlDataList, "controlConsole.csv");
		// } catch (IOException e){
		// e.printStackTrace();
		// }
		//
		// String[] patientDataList = getDataList(".\\Benson_Data\\Patients\\");
		// try{
		// exportData(patientDataList, "patientConsole.csv");
		// } catch (IOException e){
		// e.printStackTrace();
		// }
		// newKFoldTest(".\\Sheets\\DataSet\\2019-07-15_14_11_25",10);
		newKFoldTest2(".\\Sheets\\DataSet\\2019-07-15_14_11_25", 10);
	}

	public static void arrListInsertionSort(ArrayList<String> arr, int numInputs, int numOutputs) {
		int i = 1;
		while (i < arr.size()) {
			int j = i;
			while (j > 0 && getDataLineClass(arr.get(j - 1), numInputs, numOutputs) > getDataLineClass(arr.get(j),
					numInputs, numOutputs)) {

				String tmpStr = arr.get(j - 1);
				arr.set(j - 1, arr.get(j));
				arr.set(j, tmpStr);
				j--;
			}
			i++;
		}
	}

	public static int getClassNum(ArrayList<String> arr, int numInputs, int numOutputs) {
		List<Integer> classLabel = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++) {
			if (!classLabel.contains(getDataLineClass(arr.get(i), numInputs, numOutputs)))
				classLabel.add(getDataLineClass(arr.get(i), numInputs, numOutputs));
		}

		return classLabel.size();
	}

	public static int getDataLineClass(String dataLine, int numInputs, int numOutputs) {
		String[] tmpStr = dataLine.split(",");
		char[] tmpChar = new char[numOutputs];
		for (int i = 0; i < numOutputs; i++) {
			tmpChar[i] = tmpStr[numInputs + i].charAt(0);
		}

		return Integer.parseInt(String.valueOf(tmpChar));
	}

	public static void newKFoldTest2(String srcFolder, int iteration) {
		File srcFileFolder = new File(srcFolder);
		File srcTraining = new File(srcFileFolder.getPath() + "\\01_training.csv");
		File srcValidation = new File(srcFileFolder.getPath() + "\\02_validation.csv");
		File srcTesting = new File(srcFileFolder.getPath() + "\\03_testing.csv");

		File kFoldFile = new File(srcFolder + "_fold");
		kFoldFile.mkdir();
		File[] nFold = new File[3];

		FileWriter[] fwArray = new FileWriter[3];

		for (int i = 0; i < iteration; i++) {
			File nFolder = new File(kFoldFile.getPath() + "\\fold_" + i);
			nFolder.mkdir();
			nFold[0] = new File(nFolder.getPath() + "\\01_training.csv");
			nFold[1] = new File(nFolder.getPath() + "\\02_validation.csv");
			nFold[2] = new File(nFolder.getPath() + "\\03_testing.csv");
			try{
				for(int tmp = 0; tmp < 3; tmp++){
					nFold[tmp].createNewFile();
					fwArray[tmp] = new FileWriter(nFold[tmp]);
				}
			} catch (Exception e) {
					e.printStackTrace();
			}

			BufferedReader brArray[] = new BufferedReader[3];
			ArrayList<String>[] dataSets = new ArrayList[3];
			ArrayList<String>[] swapCache = new ArrayList[3];
			int numInputs = 0;
			int numOutputs = 0;
			try {
				brArray[0] = new BufferedReader(new FileReader(srcTraining));
				brArray[1] = new BufferedReader(new FileReader(srcValidation));
				brArray[2] = new BufferedReader(new FileReader(srcTesting));

				for (int tmp = 0; tmp < 3; tmp++) {
					swapCache[tmp] = new ArrayList<String>();
					dataSets[tmp] = new ArrayList<String>();
					String line = "";
					line = brArray[tmp].readLine();
					fwArray[tmp].append(line+"\r\n");
					numInputs = Integer.parseInt(line.split(",")[0]);
					numOutputs = Integer.parseInt(line.split(",")[1]);
					while ((line = brArray[tmp].readLine()) != null) {
						dataSets[tmp].add(line);
					}
					arrListInsertionSort(dataSets[tmp], numInputs, numOutputs);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			int[][] classLimit = new int[3][getClassNum(dataSets[0], numInputs, numOutputs)];
			int[][] swapLimit = new int[3][getClassNum(dataSets[0], numInputs, numOutputs)];
			for (int tmp = 0; tmp < 3; tmp++) {
				for (int tmp2 = 0; tmp2 < classLimit[tmp].length; tmp2++) {
					classLimit[tmp][tmp2] = 0;
				}
				int tmpCounter = 0;
				for (int tmp2 = 0; tmp2 < dataSets[tmp].size(); tmp2++) {
					if (tmp2 > 0) {
						if (getDataLineClass(dataSets[tmp].get(tmp2), numInputs,
								numOutputs) != getDataLineClass(dataSets[tmp].get(tmp2 - 1), numInputs, numOutputs)) {
							tmpCounter++;

						}
					}
					classLimit[tmp][tmpCounter]++;
				}

				for (int tmp2 = 0; tmp2 < tmpCounter + 1; tmp2++) {
					swapLimit[tmp][tmp2] = (int) Math.floor(((double) classLimit[tmp][tmp2] * 2 / iteration) + 0.5);
					if (swapLimit[tmp][tmp2] == 0)
						swapLimit[tmp][tmp2] = 1;
				}
			}
			int[][] swapIndex = new int[3][classLimit[0].length];
			for (int tmp = 0; tmp < 3; tmp++) {

				int tmpIndex = 0;
				for (int tmp2 = 0; tmp2 < classLimit[0].length; tmp2++) {
					if (tmp2 == 0)
						swapIndex[tmp][tmp2] = tmpIndex + classLimit[tmp][tmp2] + (0 - swapLimit[0][tmp2]);
					else
						swapIndex[tmp][tmp2] = tmpIndex + classLimit[tmp][tmp2]
								+ (swapLimit[0][tmp2 - 1] - swapLimit[0][tmp2]);
					tmpIndex = swapIndex[tmp][tmp2];

					for (int tmp3 = 0; tmp3 < swapLimit[0][tmp2]; tmp3++) {
						swapCache[tmp].add(dataSets[tmp].get(swapIndex[tmp][tmp2] + tmp3));
					}
					
					
				}
				if(i == 0) {
					for(int tmp3 = 0; tmp3 < swapCache[tmp].size(); tmp3++) {
						
						System.out.println(swapCache[tmp].get(tmp3));
					}
					System.out.println();
				}

			}
			ArrayList<String>[] writePendingData = new ArrayList[3];
			int[][] classIndex = new int[3][classLimit[0].length];
			for (int tmp = 0; tmp < 3; tmp++) {
				classIndex[tmp][0] = 0;
				for (int tmp2 = 1; tmp2 < classLimit[0].length; tmp2++) {
					classIndex[tmp][tmp2] = classIndex[tmp][tmp2 - 1] + classLimit[tmp][tmp2 - 1];

				}

			}
			for (int tmp = 0; tmp < 3; tmp++) {
				writePendingData[tmp] = new ArrayList<String>();
				int swapCacheIndex = 0;
				for (int tmp2 = 0; tmp2 < classLimit[0].length; tmp2++) {

					for (int tmp3 = 0; tmp3 < classLimit[tmp][tmp2]; tmp3++) {
						if (tmp3 < swapLimit[0][tmp2]) {
							writePendingData[tmp].add(swapCache[swapFunc(tmp)].get(swapCacheIndex));
							swapCacheIndex++;
						} else {
							writePendingData[tmp]
									.add(dataSets[tmp].get(classIndex[tmp][tmp2] + tmp3 - swapLimit[tmp][tmp2]));

						}
					}
				}

				for (int tmp2 = 0; tmp2 < writePendingData[tmp].size(); tmp2++) {
					try {
						fwArray[tmp].append(writePendingData[tmp].get(tmp2) + "\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			srcTraining = new File(nFolder.getPath() + "\\01_training.csv");
			srcValidation = new File(nFolder.getPath() + "\\02_validation.csv");
			srcTesting = new File(nFolder.getPath() + "\\03_testing.csv");

			for(int tmp = 0; tmp < 3; tmp++){
				try {
					fwArray[tmp].flush();
					fwArray[tmp].close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		}
	}

	public static void newKFoldTest(String srcFolder, int iteration) {
		/* Initialize dataset files */
		File srcFileFolder = new File(srcFolder);
		File srcTraining = new File(srcFileFolder.getPath() + "\\01_training.csv");
		File srcValidation = new File(srcFileFolder.getPath() + "\\02_validation.csv");
		File srcTesting = new File(srcFileFolder.getPath() + "\\03_testing.csv");
		File srcOverall = new File(srcFileFolder.getPath() + "\\00_overall.csv");

		File kFoldFile = new File(srcFolder + "_fold");

		/* Create dedicated fold directory */
		kFoldFile.mkdir();

		String line = "";

		BufferedReader brArray[] = new BufferedReader[4];
		int[] setCount = new int[3];
		try {
			brArray[0] = new BufferedReader(new FileReader(srcTraining));
			brArray[1] = new BufferedReader(new FileReader(srcValidation));
			brArray[2] = new BufferedReader(new FileReader(srcTesting));
			brArray[3] = new BufferedReader(new FileReader(srcOverall));

			/* Read the first line of any data set for parameters */
			line = brArray[0].readLine();
			String[] dataParams = line.split(",");
			setCount[0] = Integer.parseInt(dataParams[2]);

			line = brArray[1].readLine();
			dataParams = line.split(",");
			setCount[1] = Integer.parseInt(dataParams[2]);

			line = brArray[2].readLine();
			dataParams = line.split(",");
			setCount[2] = Integer.parseInt(dataParams[2]);

			brArray[3].readLine();

			/* Assign parameter value to ints */
			int numInputs = Integer.parseInt(dataParams[0]);
			int numOutputs = Integer.parseInt(dataParams[1]);

			/* List to store labels and their counts */
			ArrayList<String>[] dataLabel = new ArrayList[4];
			ArrayList<Integer>[] labelCount = new ArrayList[4];

			for (int i = 0; i < 3; i++) {
				dataLabel[i] = new ArrayList<String>();
				labelCount[i] = new ArrayList<Integer>();

				/* Enumerate whole dataset */
				while ((line = brArray[i].readLine()) != null) {
					String[] dataLine = line.split(",");

					/* Extract output information */
					char[] tmpArr = new char[numOutputs];
					for (int j = 0; j < numOutputs; j++) {
						tmpArr[j] = dataLine[numInputs + j].charAt(0);
					}
					String tmpStr = String.valueOf(tmpArr);

					/* Add label to list */
					if (!dataLabel[i].contains(tmpStr)) {
						dataLabel[i].add(tmpStr);
						labelCount[i].add(1);
					} else {
						/* Get the index of current dataset label */
						int labelIndex = 0;
						for (int j = 0; j < dataLabel[i].size(); j++) {
							if (dataLabel[i].get(j).equals(tmpStr))
								labelIndex = j;
						}

						/* Update label count */
						labelCount[i].set(labelIndex, labelCount[i].get(labelIndex) + 1);
					}
				}
			}

			/* 3-D array to store amount of data need to be shifted per class per dataset */
			int dataShift[][] = new int[3][dataLabel[0].size()];
			/*
			 * 3-D array to store amount of data can be kept before swapping per class per
			 * dataset
			 */
			int dataShiftKeepLimit[][] = new int[3][dataLabel[0].size()];
			for (int tmp = 0; tmp < 3; tmp++) {
				for (int i = 0; i < dataShift.length; i++) {
					dataShift[tmp][i] = (int) Math.floor(((double) labelCount[tmp].get(i) * 2 / iteration) + 0.5);

					dataShiftKeepLimit[tmp][i] = labelCount[tmp].get(i) - dataShift[tmp][i];

					if (dataShift[tmp][i] == 0)
						dataShift[tmp][i]++;

					if (dataShiftKeepLimit[tmp][i] == 0)
						dataShiftKeepLimit[tmp][i]++;

				}

				for (int i = 0; i < 4; i++) {
					System.out.println(dataLabel[tmp].get(i) + " " + labelCount[tmp].get(i) + " " + dataShift[tmp][i]
							+ " " + dataShiftKeepLimit[tmp][i]);

				}
				System.out.println();
			}

			brArray[0] = null;
			brArray[1] = null;
			brArray[2] = null;

			/* Store swap and keep dataset per dataset */
			ArrayList<String>[] swapData = new ArrayList[3];

			ArrayList<String>[] keepData = new ArrayList[3];

			File[] nFold = new File[3];

			FileWriter[] fwArray = new FileWriter[3];

			for (int i = 0; i < iteration; i++) {

				swapData[0] = new ArrayList<String>();
				swapData[1] = new ArrayList<String>();
				swapData[2] = new ArrayList<String>();

				keepData[0] = new ArrayList<String>();
				keepData[1] = new ArrayList<String>();
				keepData[2] = new ArrayList<String>();

				File nFolder = new File(kFoldFile.getPath() + "\\fold_" + i);
				nFolder.mkdir();

				nFold[0] = new File(nFolder.getPath() + "\\01_training.csv");
				nFold[1] = new File(nFolder.getPath() + "\\02_validation.csv");
				nFold[2] = new File(nFolder.getPath() + "\\03_testing.csv");

				try {
					for (int j = 0; j < 3; j++) {

						nFold[j].createNewFile();
						fwArray[j] = new FileWriter(nFold[j]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					brArray[0] = new BufferedReader(new FileReader(srcTraining));
					brArray[1] = new BufferedReader(new FileReader(srcValidation));
					brArray[2] = new BufferedReader(new FileReader(srcTesting));

					/* j: Data set index (TR, VA, TE) */
					/* k: Data line count in each index (first line parameter) */
					for (int j = 0; j < 3; j++) {
						line = brArray[j].readLine(); // Get dataset parameter line
						fwArray[j].append(line + "\r\n"); // Parse parameter into fold dataset
						int labelIndex = 0;
						int[] labelCountArr = new int[dataLabel[j].size()];
						for (int tmp = 0; tmp < labelCountArr.length; tmp++) {
							labelCountArr[tmp] = 0;
						}

						for (int k = 0; k < setCount[j]; k++) {
							/* Get the index of the label in the ArrayList */
							line = brArray[j].readLine();

							String[] tmpStr = line.split(",");
							char[] tmpArr = new char[numOutputs];

							for (int tmp = 0; tmp < numOutputs; tmp++) {

								tmpArr[tmp] = tmpStr[numInputs + tmp].charAt(0);
							}

							String labelInfo = String.valueOf(tmpArr);

							for (int tmp = 0; tmp < dataLabel[0].size(); tmp++) {
								if (dataLabel[j].get(tmp).equals(labelInfo)) {
									labelIndex = tmp;

								}

							}

							/* Determine whether keep the data or swap the data */
							if (labelCountArr[labelIndex] < dataShiftKeepLimit[j][labelIndex]) {
								keepData[j].add(line);
								labelCountArr[labelIndex]++;
							} else {
								swapData[j].add(line);
							}

						}

						for (int tmp = 0; tmp < labelCountArr.length; tmp++) {
							System.out.print(labelCountArr[tmp] + " ");
						}
						System.out.println();

					}
					System.out.println();

					/* Write data into new fold */
					for (int j = 0; j < 3; j++) {
						/* Ensure list not out of bounds */
						int swapCounter = 0;
						int keepCounter = 0;

						/* In each data set */
						for (int k = 0; k < setCount[j]; k++) {
							/* Retrieve next available data */
							String tmpSwap = swapData[swapFunc(j)].get(swapCounter);
							String tmpKeep = keepData[j].get(keepCounter);

							if (k < swapData[swapFunc(j)].size()) {
								fwArray[j].append(tmpSwap + "\r\n");
								swapCounter++;
							} else {
								fwArray[j].append(tmpKeep + "\r\n");
								keepCounter++;
							}

							if (swapCounter >= swapData[swapFunc(j)].size())
								swapCounter--;

							if (keepCounter >= keepData[j].size())
								keepCounter--;

						}
						System.out.println(swapData[swapFunc(j)].size() + " " + keepData[j].size());

					}
					System.out.println();

				} catch (Exception e) {
					e.printStackTrace();
				}

				srcTraining = new File(nFolder.getPath() + "\\01_training.csv");
				srcValidation = new File(nFolder.getPath() + "\\02_validation.csv");
				srcTesting = new File(nFolder.getPath() + "\\03_testing.csv");

				for (int j = 0; j < 3; j++) {
					fwArray[j].flush();
					fwArray[j].close();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int swapFunc(int a) {
		if (a == 0)
			return 2;
		else if (a == 1)
			return 0;
		else
			return 1;
	}

	public static void exportData(String[] dataList, String fileName) throws IOException {
		File f = new File(fileName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileWriter writer = new FileWriter(f, true);

		for (int i = 0; i < dataList.length; i++) {
			Benson tmpBenson = new Benson(dataList[i], 0);
			tmpBenson.calcThreeLength();
			String[] tmpBensonDataSplit = tmpBenson.getData().split("\\\\");

			String[] dataPending = { tmpBensonDataSplit[tmpBensonDataSplit.length - 1].split("_")[1],
					String.valueOf(tmpBenson.timeSpent), String.valueOf(tmpBenson.getTotalLength()),
					String.valueOf(tmpBenson.getSize()[0] * tmpBenson.getSize()[1]),
					String.valueOf((double) (tmpBenson.getSize()[0] / tmpBenson.getSize()[1] / 10)),
					String.valueOf(tmpBenson.getVelocitySD()), String.valueOf(tmpBenson.getAngleSD()),
					String.valueOf(tmpBenson.penoffCount() / (tmpBenson.getTimeStamp() + 1)),
					String.valueOf((double) (tmpBenson.getHoriPortion())),
					String.valueOf((double) tmpBenson.getVertPortion()),
					String.valueOf((double) tmpBenson.getObliPortion()),
					String.valueOf((double) tmpBenson.getThreeSD()[0]),
					String.valueOf((double) tmpBenson.getThreeSD()[1]),
					String.valueOf((double) tmpBenson.getThreeSD()[2]),
					String.valueOf((double) tmpBenson.getHesitation()),
					String.valueOf((double) tmpBenson.getPenUpHesitation()),
					String.valueOf((double) tmpBenson.getHesitationPortion()),
					String.valueOf((double) tmpBenson.getPenUpHesiPortion()) };
			writeData(writer, dataPending);
			writer.append("\n");
		}

		writer.flush();
		writer.close();
	}

	public static void writeData(FileWriter writer, String[] data) throws IOException {
		for (int i = 0; i < data.length; i++) {

			writer.append(data[i]);
			writer.append(',');
		}
	}

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
}
