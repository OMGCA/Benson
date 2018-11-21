package xiatstudio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import xiatstudio.Component;

public class Benson {
	float[] xAxis;
	float[] yAxis;
	float[] xTilt;
	float[] yTilt;
	float[] penPressure;
	double horiLength;
	double vertLength;
	double obliLength;
	float timeSpent;
	int timeStamp;
	int rating;
	String data;
	ArrayList<Component> components;

	ArrayList<Color> colorSet = new ArrayList<Color>();
	

	public Benson(String data) {
		this.data = data;
		this.timeStamp = fetchTimeStamp(data);
		this.xAxis = new float[timeStamp];
		this.yAxis = new float[timeStamp];
		this.xTilt = new float[timeStamp];
		this.yTilt = new float[timeStamp];
		this.horiLength = 0;
		this.vertLength = 0;
		this.obliLength = 0;
		this.timeSpent = 0;
		registerRating(".\\Sheets\\rating.csv");
		this.penPressure = new float[timeStamp];
		this.components = new ArrayList<Component>();

		colorSet.add(new Color(87,207,244));
		colorSet.add(new Color(200,236,89));
		colorSet.add(new Color(218,157,223));

		initData();
		positionCentre();
		// registerComponents();
		angleBasedCompoReg();

	}

	public int fetchTimeStamp(String data) {
		int counter = 0;
		@SuppressWarnings("unused")
		String line = "";

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(data));
			while ((line = br.readLine()) != null) {
				counter++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return counter;
	}

	public void registerRating(String ratingSheet){
		BufferedReader br = null;
		String line = "";
		try{
			br = new BufferedReader(new FileReader(ratingSheet));
			while((line = br.readLine()) != null){
				String[] ratingPair = line.split(",");
				if(ratingPair[0].equals(getID())){
					this.rating = Integer.parseInt(ratingPair[1]);
					break;
				}
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
	}

	public int getRating(){
		return this.rating;
	}

	public void initData() {
		String splitBy = "\\s+";
		String line = "";

		int i = 0;

		/* Changing figure visiable size */
		int multipiler = 1200;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.data));
			while ((line = br.readLine()) != null) {
				String[] tmpArray = line.split(splitBy);
				this.xAxis[i] = Float.parseFloat(tmpArray[1]);
				this.xAxis[i] *= multipiler;
				this.yAxis[i] = Float.parseFloat(tmpArray[2]);
				this.yAxis[i] *= multipiler;
				this.xTilt[i] = Float.parseFloat(tmpArray[3]);
				this.xTilt[i] *= multipiler;
				this.yTilt[i] = Float.parseFloat(tmpArray[4]);
				this.yTilt[i] *= multipiler;
				this.penPressure[i] = Float.parseFloat(tmpArray[5]);
				i++;
				this.timeSpent = Float.parseFloat(tmpArray[0]);
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
	}

	public String getData() {
		return this.data;
	}

	public void registerComponents() {
		int index = 0;
		this.components.add(new Component(index));

		/* Current standard for segmentation */
		/* Based on pen pressure change */

		for (int i = 0; i < this.timeStamp - 1; i++) {
			if (this.penPressure[i] != 0) {
				this.components.get(index).addNewAxis(this.xAxis[i], this.yAxis[i]);
			}
			if (this.penPressure[i] != 0 && this.penPressure[i + 1] == 0) {
				this.components.get(index).resizeAxis();
				index++;
				this.components.add(new Component(index));
			}
		}

	}

	public void positionCentre() {
		/* Horizontal anchor point */
		float stdX = (max(this.xAxis) + min(this.xAxis)) / 2 - 640;
		for (int i = 0; i < this.xAxis.length; i++) {
			this.xAxis[i] -= stdX;
		}
		/* Vertical anchor point */
		float stdY = (max(this.yAxis) + min(this.yAxis)) / 2 - 360;
		for (int i = 0; i < this.yAxis.length; i++) {
			this.yAxis[i] -= stdY;
		}

	}

	public int getComponentCount() {
		return this.components.size();
	}

	/* Getter functions */
	public int getTimeStamp() {
		return this.timeStamp;
	}

	public float[] getX() {
		return this.xAxis;
	}

	public float[] getY() {
		return this.yAxis;
	}

	public float[] getXTilt() {
		return this.xTilt;
	}

	public float[] getYTilt() {
		return this.yTilt;
	}

	public float[] getPenPressure() {
		return this.penPressure;
	}

	/* Get drawing mode from data file name */
	public String getFigureMode() {
		try {
			if (this.data.split("/")[this.data.split("/").length - 1].split("_")[2].contains("copy"))
				return "Copy";
			else
				return "Recall";
		} catch (ArrayIndexOutOfBoundsException e) {
			return "Unknown";
		}

	}

	public String getGroup() {
		try {
			return this.data.split("/")[this.data.split("/").length - 2];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "Unknown";
		}
	}

	public String getID() {
		try {
			return this.data.split("/")[this.data.split("/").length - 1].split("_")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "Unknown";
		}
	}

	public double getHoriLength() {
		return this.horiLength;
	}

	public double getVertLength() {
		return this.vertLength;
	}

	public double getObliLength() {
		return this.obliLength;
	}

	public double getHoriPortion() {
		return this.horiLength / getTotalLength();
	}

	public double getVertPortion() {
		return this.vertLength / getTotalLength();
	}

	public double getObliPortion() {
		return this.obliLength / getTotalLength();
	}

	public void markComponent(Component c, Graphics2D g2) {
		int[] centre = { 640, 360 };
		int[] compAvgPos = c.getAvgPos();
		g2.drawString("Seg " + c.getIndex(), rotateCoordinate(centre, compAvgPos)[0],
				rotateCoordinate(centre, compAvgPos)[1]);

	}

	public int[] rotateCoordinate(int[] x, int[] y) {
		int newX = Math.abs(x[0] + x[0] - y[0]);
		int newY = Math.abs(x[1] + x[1] - y[1]);

		int[] newPos = { newX, newY };

		return newPos;
	}

	public void drawBenson(Graphics2D g2, int displayMode) {
		/* Set stroke */
		g2.setStroke(new BasicStroke(4));
		/* Text color */
		Color c = new Color(234, 234, 234);
		g2.setColor(c);
		g2.setFont(new Font("Inconsolata", Font.PLAIN, 20));
		String group, id, mode;

		group = getGroup();
		id = getID();
		mode = getFigureMode();

		infoBoard(g2, group, id, mode);

		g2.setFont(new Font("Inconsolata", Font.PLAIN, 15));

		/* Rotate figure to recognizable orientation */
		g2.rotate(Math.toRadians(180), 640, 360);

		groupComp(g2, displayMode);
		g2.setColor(new Color(255,0,0));

		plotHesitation(g2);

		//divideComp(g2, displayMode);

		vertexPoint(g2);
	}

	public void infoBoard(Graphics2D g2, String group, String id, String mode) {
		int yCoorBase = 40;
		int vertGap = 25;
		g2.drawString("Group: " + group, 30, yCoorBase + vertGap * 0);
		g2.drawString("ID:    " + id, 30, yCoorBase + vertGap * 1);
		g2.drawString("Mode:  " + mode, 30, yCoorBase + vertGap * 2);
		g2.drawString("Data Location: " + this.data, 30, yCoorBase + vertGap * 3);
		g2.drawString("Total time spent: " + this.timeSpent / 1000 + " s", 30, yCoorBase + vertGap * 4);
		g2.drawString("Velocity Stability: " + getVelocitySD(), 30, yCoorBase + vertGap * 5);
		g2.drawString("Angle Stability: " + getAngleSD(), 30, yCoorBase + vertGap * 6);
		g2.drawString("Length: " + getTotalLength(), 30, yCoorBase + vertGap * 7);
		g2.drawString("Size: " + (int) getSize()[0] + " x " + (int) getSize()[1], 30, yCoorBase + vertGap * 8);
		g2.drawString("Pen Off: " + penoffCount() * 100 / (this.timeStamp + 1) + " %", 30, yCoorBase + vertGap * 9);
		infoMsg(g2,"Hesitation: ",getHesitation(),yCoorBase+vertGap*10);
	}

	public void infoMsg(Graphics2D g2, String infoTitle, Object info, int pos){
		g2.drawString(infoTitle + info, 30, pos);
	}

	public void vertexPoint(Graphics2D g2) {
		/* Figure vertex point marking */
		g2.setColor(new Color(255, 81, 81));
		g2.fillOval((int) min(this.xAxis), (int) min(this.yAxis), 20, 20);
		g2.setColor(new Color(96, 175, 240));
		g2.fillOval((int) min(this.xAxis), (int) max(this.yAxis), 20, 20);
		g2.setColor(new Color(188, 235, 101));
		g2.fillOval((int) max(this.xAxis), (int) min(this.yAxis), 20, 20);
		g2.setColor(new Color(248, 193, 154));
		g2.fillOval((int) max(this.xAxis), (int) max(this.yAxis), 20, 20);
	}

	public void calcThreeLength() {
		for (int i = 0; i < this.timeStamp - 1; i++) {
			float[] tmpX = { this.xAxis[i], this.xAxis[i + 1] };
			float[] tmpY = { this.yAxis[i], this.yAxis[i + 1] };

			double tmpAngle = getPointAngle(tmpX, tmpY);
			double angleRange[] = { 13, 65 };

			if (this.penPressure[i] != 0) {
				if (tmpAngle <= angleRange[0])
					this.horiLength += getDistanceBetweenPoints(tmpX, tmpY);
				else if (tmpAngle >= angleRange[1])
					this.vertLength += getDistanceBetweenPoints(tmpX, tmpY);
				else if (tmpAngle > angleRange[0] && tmpAngle < angleRange[1])
					this.obliLength += getDistanceBetweenPoints(tmpX, tmpY);
			}
		}
	}

	public float[] resizeFloatArr(float[] arr, int newSize) {
		float[] tmpArr = new float[newSize];
		if (newSize < arr.length) {
			for (int i = 0; i < newSize; i++) {
				tmpArr[i] = arr[i];
			}
		}

		return arr;
	}

	public double[] getThreeSD() {
		float[] tmpHoriX = new float[20000];
		float[] tmpHoriY = new float[20000];
		float[] tmpVertX = new float[20000];
		float[] tmpVertY = new float[20000];
		float[] tmpObliX = new float[20000];
		float[] tmpObliY = new float[20000];
		int horiCounter = 0;
		int vertCounter = 0;
		int obliCounter = 0;

		for (int i = 0; i < this.timeStamp - 1; i++) {
			float[] tmpX = { this.xAxis[i], this.xAxis[i + 1] };
			float[] tmpY = { this.yAxis[i], this.yAxis[i + 1] };

			double tmpAngle = getPointAngle(tmpX, tmpY);
			double angleRange[] = { 10, 65 };

			if (this.penPressure[i] != 0) {
				if (tmpAngle <= angleRange[0]) {
					tmpHoriX[horiCounter] = this.xAxis[i];
					tmpHoriY[horiCounter] = this.yAxis[i];
					horiCounter++;

				}

				else if (tmpAngle >= angleRange[1]) {
					tmpVertX[vertCounter] = this.xAxis[i];
					tmpVertY[vertCounter] = this.yAxis[i];
					vertCounter++;
				}

				else if (tmpAngle > angleRange[0] && tmpAngle < angleRange[1]) {
					tmpObliX[obliCounter] = this.xAxis[i];
					tmpObliY[obliCounter] = this.yAxis[i];
					obliCounter++;
				}

			}
		}

		float[] newHoriX = resizeFloatArr(tmpHoriX, horiCounter);
		float[] newHoriY = resizeFloatArr(tmpHoriY, horiCounter);
		float[] newVertX = resizeFloatArr(tmpVertX, vertCounter);
		float[] newVertY = resizeFloatArr(tmpVertY, vertCounter);
		float[] newObliX = resizeFloatArr(tmpObliX, obliCounter);
		float[] newObliY = resizeFloatArr(tmpObliY, obliCounter);

		double[] horiAng = new double[horiCounter - 1];
		double[] vertAng = new double[vertCounter - 1];
		double[] obliAng = new double[obliCounter - 1];

		for (int i = 0; i < horiCounter - 1; i++) {
			float[] tmpX = { newHoriX[i], newHoriX[i + 1] };
			float[] tmpY = { newHoriY[i], newHoriY[i + 1] };

			horiAng[i] = getPointAngle(tmpX, tmpY);

		}

		for (int i = 0; i < vertCounter - 1; i++) {
			float[] tmpX = { newVertX[i], newVertX[i + 1] };
			float[] tmpY = { newVertY[i], newVertY[i + 1] };

			vertAng[i] = getPointAngle(tmpX, tmpY);
		}

		for (int i = 0; i < obliCounter - 1; i++) {
			float[] tmpX = { newObliX[i], newObliX[i + 1] };
			float[] tmpY = { newObliY[i], newObliY[i + 1] };

			obliAng[i] = getPointAngle(tmpX, tmpY);
		}

		double[] groupSD = new double[3];
		groupSD[0] = getStandardDeviation(horiAng);
		groupSD[1] = getStandardDeviation(vertAng);
		groupSD[2] = getStandardDeviation(obliAng);

		return groupSD;

	}

	public void drawMode(Graphics2D g2, int mode, float[] tmpX, float[] tmpY) {
		double tmpAngle = getPointAngle(tmpX, tmpY);
		double angleRange[] = { 10, 65 };
		if (mode == 0) {
			if (tmpAngle <= angleRange[0]) {
				g2.setColor(new Color(87, 207, 244));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			} else if (tmpAngle >= angleRange[1]) {
				g2.setColor(new Color(200, 236, 89));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			} else if (tmpAngle > angleRange[0] && tmpAngle < angleRange[1]) {
				g2.setColor(new Color(218, 157, 223));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			}
		} else if (mode == 1) {
			if (tmpAngle <= angleRange[0]) {
				g2.setColor(new Color(87, 207, 244));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			}
		} else if (mode == 2) {
			if (tmpAngle >= angleRange[1]) {
				g2.setColor(new Color(200, 236, 89));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			}
		} else if (mode == 3) {
			if (tmpAngle > angleRange[0] && tmpAngle < angleRange[1]) {
				g2.setColor(new Color(218, 157, 223));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			}
		} else if (mode == 4) {
			if (tmpAngle <= angleRange[0]) {
				g2.setColor(new Color(87, 207, 244));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			} else if (tmpAngle >= angleRange[1]) {
				g2.setColor(new Color(200, 236, 89));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			} else if (tmpAngle > angleRange[0] && tmpAngle < angleRange[1]) {
				g2.setColor(new Color(218, 157, 223));
				g2.draw(new Line2D.Float(tmpX[0], tmpY[0], tmpX[1], tmpY[1]));
			}
			markPenoff(g2);
		} else if (mode == 5)
			mode = 0;
	}

	public void angleBasedCompoReg() {
		int compoGroup = 0;
		int compoIndex = 0;
		double[] angleRange = { 10, 70 };

		for (int i = 0; i < this.timeStamp - 1; i++) {

			if (this.penPressure[i] != 0) {
				float[] tmpX = { this.xAxis[i], this.xAxis[i + 1] };
				float[] tmpY = { this.yAxis[i], this.yAxis[i + 1] };
				double tmpAngle = getPointAngle(tmpX, tmpY);
				if (tmpAngle <= angleRange[0]) {
					if (compoGroup != 1) {
						compoGroup = 1;
						this.components.add(new Component(1));
						compoIndex++;

					}
					if (tmpX[0] != 0 && tmpY[0] != 0)
						this.components.get(compoIndex - 1).addNewAxis(tmpX[0], tmpY[0]);

				} else if (tmpAngle >= angleRange[1]) {
					if (compoGroup != 2) {
						compoGroup = 2;
						this.components.add(new Component(2));
						compoIndex++;

					}
					this.components.get(compoIndex - 1).addNewAxis(tmpX[0], tmpY[0]);

				} else if (tmpAngle > angleRange[0] && tmpAngle < angleRange[1]) {
					if (compoGroup != 3) {
						compoGroup = 3;
						this.components.add(new Component(3));
						compoIndex++;

					}
					this.components.get(compoIndex - 1).addNewAxis(tmpX[0], tmpY[0]);
				}

			}
			if (this.penPressure[i] == 0 && this.penPressure[i + 1] != 0) {
				this.components.add(new Component(compoGroup));
				compoIndex++;
			}

		}
	}

	public void initAllCompo() {
		for (int i = 0; i < this.components.size(); i++) {
			if (this.components.get(i).getTicks() != 0) {
				this.components.get(i).resizeAxis();
				this.components.get(i).calcLength();
			}
		}
	}

	public ArrayList<Component> groupComp(Graphics2D g2, int displayMode) {
		initAllCompo();

		ArrayList<Component> groupComponents = new ArrayList<Component>();
		
		if (this.components.size() > 0) {
			for (int l = 0; l < 3; l++) {
				int j = 0;
				do {
					j++;
				} while (this.components.get(j).getIndex() != l + 2);
				groupComponents.add(this.components.get(j));

				for (int i = j + 1; i < this.components.size() - 1; i++) {
					if (this.components.get(i).getIndex() == l + 2 && this.components.get(i).getLength() > 0) {
						groupComponents.get(l).combineCompo(this.components.get(i).getWholeX(),
								this.components.get(i).getWholeY());
					}
				}
				groupComponents.get(l).resizeAxis();
			}
			
			if (displayMode == 0) {
				for (int i = 0; i < 3; i++) {
					g2.setColor(colorSet.get(i));
					groupComponents.get(i).drawComponent(g2);
				}
			} else{
				if(displayMode < 4){
					g2.setColor(colorSet.get(displayMode-1));
					groupComponents.get(displayMode-1).drawComponent(g2);
				}
				else{
					for (int i = 0; i < 3; i++) {
						g2.setColor(colorSet.get(i));
						groupComponents.get(i).drawComponent(g2);
					}
					markPenoff(g2);
				}
				
			}
				
		}

		return groupComponents;
	}

	public void divideComp(Graphics2D g2, int displayMode) {
		if (this.components.size() > 0) {
			ArrayList<Component> groupCompos = groupComp(g2, displayMode);
			Component currentCompo = groupCompos.get(0);
			ArrayList<Component> indieCompos = new ArrayList<Component>();
			indieCompos.add(new Component(0));
			for (int i = 0; i < currentCompo.getAxisSize() - 1; i++) {
				float[] tmpX = { currentCompo.getXAxis(i), currentCompo.getXAxis(i + 1) };
				float[] tmpY = { currentCompo.getYAxis(i), currentCompo.getYAxis(i + 1) };
				double tmpDistance = getDistanceBetweenPoints(tmpX, tmpY);
				if (tmpX[1] != 0 && tmpY[1] != 0) {
					if (tmpDistance < 20) {
						indieCompos.get(indieCompos.size()-1).addNewAxis(tmpX[0], tmpY[0]);
					} else {
						indieCompos.add(new Component(0));
						indieCompos.get(indieCompos.size()-1).addNewAxis(tmpX[0], tmpY[0]);
					}
				}
			}
		}
			
	}

	public Color randomColor() {
		Random rand = new Random();
		Color c = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
		return c;
	}

	public float min(float[] arr) {
		float minNum = 500000;

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < minNum && this.penPressure[i] != 0)
				minNum = arr[i];
		}

		return minNum;
	}

	public float max(float[] arr) {
		float maxNum = 0;

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > maxNum && this.penPressure[i] != 0)
				maxNum = arr[i];
		}

		return maxNum;
	}

	public double getDistanceBetweenPoints(float[] x, float[] y) {
		return Math.sqrt((x[0] - x[1]) * (x[0] - x[1]) + (y[0] - y[1]) * (y[0] - y[1]));
	}

	public int getHesitation(){
		int hesitation = 0;
		int tickJump = 2;
		
		for(int i = 0; i < this.timeStamp - tickJump; i++){
			if(this.penPressure[i] != 0 && this.penPressure[i+tickJump] != 0){
				float[] tmpX = {this.xAxis[i],this.xAxis[i+tickJump]};
				float[] tmpY = {this.yAxis[i],this.yAxis[i+tickJump]};
				if((getDistanceBetweenPoints(tmpX, tmpY)) < 0.1){
					hesitation++;
				}
			}
		}
		return hesitation;
	}

	public double getHesitationPortion(){
		if(this.timeStamp != 0)
			return (double)getHesitation() / this.timeStamp;
		else
			return -1;
	}

	public void plotHesitation(Graphics2D g2){
		int tickJump = 5;

		for(int i = 0; i < this.timeStamp - tickJump; i++){
			if(this.penPressure[i] != 0 && this.penPressure[i+tickJump] != 0){
				float[] tmpX = {this.xAxis[i],this.xAxis[i+tickJump]};
				float[] tmpY = {this.yAxis[i],this.yAxis[i+tickJump]};
				if((getDistanceBetweenPoints(tmpX, tmpY)) < 0.1){
					g2.setColor(new Color(255,0,0));
					g2.fillOval((int)tmpX[0],(int)tmpY[0],8,8);
				}
			}
		}
	}

	public double getPointAngle(float[] x, float[] y) {
		double angle = 0;
		if (x[1] != x[0] && y[1] != y[0]) {
			angle = Math.toDegrees(Math.atan2(Math.abs(y[1] - y[0]), Math.abs(x[1] - x[0])));
		} else if (y[1] == y[0]) {
			angle = 0;
		} else if (x[1] == x[0]) {
			angle = 90;
		}

		return angle;
	}

	public double getTotalLength() {
		double totalLength = 0;
		for (int i = 0; i < this.timeStamp - 1; i++) {
			if (this.penPressure[i] != 0) {
				float[] tmpX = { this.xAxis[i], this.xAxis[i + 1] };
				float[] tmpY = { this.yAxis[i], this.yAxis[i + 1] };
				totalLength += getDistanceBetweenPoints(tmpX, tmpY);
			}
		}

		return totalLength;
	}

	public double[] getSize() {
		double[] size = new double[2];
		double horiDiff = (max(this.xAxis) - min(this.xAxis));
		double vertDiff = (max(this.yAxis) - min(this.yAxis));
		size[0] = horiDiff;
		size[1] = vertDiff;
		return size;
	}

	public double getStandardDeviation(double[] arr) {
		double tmpAvg = getAvg(arr);

		double tmpTotal = 0;
		for (int i = 0; i < arr.length; i++) {
			tmpTotal += Math.pow((arr[i] - tmpAvg), 2);
		}
		tmpTotal /= arr.length;

		return Math.sqrt(tmpTotal / arr.length);
	}

	public double getAvg(double[] arr) {
		double tmpAvg = 0;
		for (int i = 0; i < arr.length; i++) {
			tmpAvg += arr[i];
		}

		return tmpAvg / arr.length;
	}

	public double getAngleSD() {
		double sampleRate = 25;
		double[] angle = new double[(int) (this.timeStamp / sampleRate)];
		double tmpAngle = 0;
		int k = 0;

		for (int i = 0; i < angle.length; i++) {
			tmpAngle = 0;
			for (int j = 0; j < sampleRate; j++) {
				float[] tmpX = { this.xAxis[k], this.xAxis[k + 1] };
				float[] tmpY = { this.yAxis[k], this.yAxis[k + 1] };
				tmpAngle += getPointAngle(tmpX, tmpY);
				if (k >= this.timeStamp - 2)
					break;
				k++;
			}
			tmpAngle /= sampleRate;

			angle[i] = tmpAngle;
			if (k >= this.timeStamp - 2)
				break;
		}

		return getStandardDeviation(angle);
	}

	public double getVelocitySD() {
		double sampleRate = 25;
		double[] distance = new double[(int) (this.timeStamp / sampleRate)];
		double tmpDistance = 0;
		int k = 0;

		for (int i = 0; i < distance.length; i++) {
			tmpDistance = 0;
			for (int j = 0; j < sampleRate; j++) {
				float[] tmpX = { this.xAxis[k], this.xAxis[k + 1] };
				float[] tmpY = { this.yAxis[k], this.yAxis[k + 1] };
				tmpDistance += getDistanceBetweenPoints(tmpX, tmpY);
				if (k >= this.timeStamp - 2)
					break;
				k++;
			}

			distance[i] = tmpDistance;
			if (k >= this.timeStamp - 2)
				break;
		}

		return getStandardDeviation(distance);
	}

	public double penoffCount() {
		double penOff = 0;
		for (int i = 0; i < this.timeStamp - 1; i++) {
			if (this.penPressure[i] == 0)
				penOff++;
		}

		return penOff;
	}

	public void markPenoff(Graphics2D g2) {
		Color c = new Color(226, 226, 226);
		g2.setColor(c);
		for (int i = 0; i < this.timeStamp - 1; i++) {
			if (this.penPressure[i] == 0) {
				g2.fillOval((int) this.xAxis[i], (int) this.yAxis[i], 2, 2);
			}
		}
	}

	public int linearSearch(float[] arr, float entity) {
		int i = 0;
		int found = 0;
		while (i < arr.length && found == 0) {
			if (entity == arr[i])
				found = 1;
			i++;
		}

		return i;
	}

	public void paintComponent(Graphics2D g2, int displayMode) {
		for (int i = 0; i < this.components.size(); i++) {
			Component tmpComp = this.components.get(i);
			g2.setColor(colorSet.get(tmpComp.getIndex()-2));

			initAllCompo();

			if (tmpComp.getLength() > 1) {
				if (displayMode == 0) {
					for (int j = 0; j < tmpComp.getAxisSize() - 1; j++) {
						tmpComp.drawComponent(g2);
					}
				} else if (displayMode == 1) {
					if (tmpComp.getIndex() == 2) {
							tmpComp.drawComponent(g2);	
					}
				} else if (displayMode == 2) {
					if (tmpComp.getIndex() == 3) {
						tmpComp.drawComponent(g2);
					}
				} else if (displayMode == 3) {
					if (tmpComp.getIndex() == 4) {
							tmpComp.drawComponent(g2);
					}
				} else if (displayMode == 4) {
					for (int j = 0; j < tmpComp.getAxisSize() - 1; j++) {
						tmpComp.drawComponent(g2);
					}
					markPenoff(g2);
				}
			}
		}
	}
}
