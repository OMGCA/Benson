package xiatstudio;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class ConsoleExport {
	public static void main(String args[]) {
		String[] controlDataList = getDataList(".\\Benson_Data\\Controls\\");
		try{
			exportData(controlDataList, "controlConsole.csv");
		} catch (IOException e){
			e.printStackTrace();
		}

		String[] patientDataList = getDataList(".\\Benson_Data\\Patients\\");
		try{
			exportData(patientDataList, "patientConsole.csv");
		} catch (IOException e){
			e.printStackTrace();
		}

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
