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
//		String[] controlDataList = getDataList(".\\Benson_Data\\Controls\\");
//		try{
//			exportData(controlDataList, "controlConsole.csv");
//		} catch (IOException e){
//			e.printStackTrace();
//		}
//
//		String[] patientDataList = getDataList(".\\Benson_Data\\Patients\\");
//		try{
//			exportData(patientDataList, "patientConsole.csv");
//		} catch (IOException e){
//			e.printStackTrace();
//		}
		newKFoldTest(".\\Sheets\\DataSet\\2019-07-08_15_45_00",10);

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
			
			for(int i = 0; i < 3; i++) {
				dataLabel[i] = new ArrayList<String>();
				labelCount[i] = new ArrayList<Integer>();

				/* Enumerate whole dataset */
				while((line = brArray[i].readLine()) != null) {
					String[] dataLine = line.split(",");
					
					/* Extract output information */
					char[] tmpArr = new char[numOutputs];
					for(int j = 0; j < numOutputs; j++) {
						tmpArr[j] = dataLine[numInputs+j].charAt(0);
					}
					String tmpStr = String.valueOf(tmpArr);
					
					/* Add label to list */
					if(!dataLabel[i].contains(tmpStr)) {
						dataLabel[i].add(tmpStr);
						labelCount[i].add(1);
					}
					else {
						/* Get the index of current dataset label */
						int labelIndex = 0;
						for(int j = 0; j < dataLabel[i].size(); j++) {
							if(dataLabel[i].get(j).equals(tmpStr))
								labelIndex = j;
						}
						
						/* Update label count */
						labelCount[i].set(labelIndex,labelCount[i].get(labelIndex)+1);
					}
				}
			}
			
			
			
			/* 3-D array to store amount of data need to be shifted per class per dataset */
			int dataShift[][] = new int[3][dataLabel[0].size()];
			/* 3-D array to store amount of data can be kept before swapping per class per dataset */
			int dataShiftKeepLimit[][] = new int[3][dataLabel[0].size()];
			for(int tmp = 0; tmp < 3; tmp++) {
				for(int i = 0; i < dataShift.length; i++) {
					dataShift[tmp][i] = (int)Math.floor(((double)labelCount[tmp].get(i) * 2 / iteration)+0.5);
					
					dataShiftKeepLimit[tmp][i] = labelCount[tmp].get(i) - dataShift[tmp][i];
					
					if(dataShift[tmp][i] == 0)
						dataShift[tmp][i]++;
					
					if(dataShiftKeepLimit[tmp][i] == 0)
						dataShiftKeepLimit[tmp][i]++;
					
				}
				
				for(int i = 0; i < 4; i++) {
					System.out.println(dataLabel[tmp].get(i) + " " + labelCount[tmp].get(i) + " " + dataShift[tmp][i] + " " + dataShiftKeepLimit[tmp][i]);
					
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

			for(int i = 0; i < iteration; i++) {
				
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
					for(int j = 0; j < 3; j++) {
						
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
					
					/*j: Data set index (TR, VA, TE)*/
					/*k: Data line count in each index (first line parameter)*/
					for(int j = 0; j < 3; j++) {
						line = brArray[j].readLine();   //Get dataset parameter line
						fwArray[j].append(line+"\r\n"); //Parse parameter into fold dataset
						int labelIndex = 0;
						for(int k = 0; k < setCount[j]; k++) {
							/* Get the index of the label in the ArrayList */
							line = brArray[j].readLine();
							
							String[] tmpStr = line.split(",");
							char[] tmpArr = new char[numOutputs];
							
							for(int tmp = 0; tmp < numOutputs; tmp++) {
								
								tmpArr[tmp] = tmpStr[numInputs+tmp].charAt(0);
							}
							
							String labelInfo = String.valueOf(tmpArr);
							
							
							for(int tmp = 0; tmp < dataLabel[0].size(); tmp++) {
								if(dataLabel[j].get(tmp).equals(labelInfo))
									labelIndex = tmp;
							}
							
							
							/* Determine whether keep the data or swap the data */
							if(k < dataShiftKeepLimit[j][labelIndex]) {
								keepData[j].add(line);
							}
							else {
								swapData[j].add(line);
							}
							
						}
									
					}

					/* Write data into new fold */
					for(int j = 0; j < 3; j++) {
						/* Ensure list not out of bounds */
						int swapCounter = 0;
						int keepCounter = 0;
						
						/* In each data set*/
						for(int k = 0; k < setCount[j]; k++) {
							/* Retrieve next available data */
							String tmpSwap = swapData[swapFunc(j)].get(swapCounter);
							String tmpKeep = keepData[j].get(keepCounter);
							
							if(k < swapData[swapFunc(j)].size()) {
								fwArray[j].append(tmpSwap + "\r\n");
								swapCounter++;
							}
							else {
								fwArray[j].append(tmpKeep + "\r\n");
								keepCounter++;
							}

							if(swapCounter >= swapData[swapFunc(j)].size())
								swapCounter--;
							
							if(keepCounter >= keepData[j].size())
								keepCounter--;
							
						}
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				srcTraining = new File(nFolder.getPath() + "\\01_training.csv");
				srcValidation = new File(nFolder.getPath() + "\\02_validation.csv");
				srcTesting = new File(nFolder.getPath() + "\\03_testing.csv");
				
				for(int j = 0; j < 3; j++) {
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

			String[] dataPending = { tmpBensonDataSplit[tmpBensonDataSplit.length-1].split("_")[1],
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
