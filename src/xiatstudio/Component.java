package xiatstudio;

public class Component {
	float[] xAxis;
	float[] yAxis;
	float[] startPoint;
	float[] endPoint;
	double avgAngle;
	double length;
	int index;
	int ticks;

	public Component(int index) {
		this.index = index;
		this.ticks = 0;
		this.xAxis = new float[20000];
		this.yAxis = new float[20000];
		this.avgAngle = 0;
		this.length = 0;
		this.startPoint = new float[2];
		this.endPoint = new float[2];
	}

	public void updateTicks() {
		this.ticks++;
	}

	public void addNewAxis(float x, float y) {
		this.xAxis[this.ticks] = x;
		this.yAxis[this.ticks] = y;
		updateTicks();
	}
	
	public float getXAxis(int index) {
		return this.xAxis[index];
	}

	public float[] getWholeX(){
		return this.xAxis;
	}

	public float[] getWholeY(){
		return this.yAxis;
	}
	
	public float getYAxis(int index) {
		return this.yAxis[index];
	}

	public float[] getStartPoint(){
		return this.startPoint;
	}

	public float[] getEndPoint(){
		return this.endPoint;
	}

	public void resizeAxis() {
		float[] newX = new float[this.ticks];
		float[] newY = new float[this.ticks];
		for (int i = 0; i < this.ticks; i++) {
			newX[i] = this.xAxis[i];
			newY[i] = this.yAxis[i];
		}

		this.xAxis = newX;
		this.yAxis = newY;

		this.startPoint[0] = newX[0];
		this.startPoint[1] = newY[0];

		this.endPoint[0] = newX[this.ticks-1];
		this.endPoint[1] = newY[this.ticks-1];

	}

	public void combineCompo(float[] arrX, float[] arrY){
		float[] newX = new float[this.ticks + arrX.length];
		float[] newY = new float[this.ticks + arrY.length];

		for(int i = 0; i < newX.length - 1; i++){
			if(i < this.ticks){
				newX[i] = this.xAxis[i];
				newY[i] = this.yAxis[i];
			}
			else if (i >= this.ticks){
				newX[i] = arrX[i-this.ticks];
				newY[i] = arrY[i-this.ticks];
			}
		}

		this.xAxis = newX;
		this.yAxis = newY;

		this.endPoint[0] = newX[newX.length - 1];
		this.endPoint[1] = newY[newY.length - 1];
	}

	public double getDistanceBetweenPoints(float[] x, float[] y) {
		return Math.sqrt((x[0] - x[1]) * (x[0] - x[1]) + (y[0] - y[1]) * (y[0] - y[1]));
	}

	public double getAngleBetweenPoints(float[] x, float[] y) {
		return Math.toDegrees(Math.atan2(Math.abs(y[1] - y[0]), Math.abs(x[1] - x[0])));
	}

	public void calcLength() {
		double tmpTotal = 0;
		for (int i = 0; i < this.ticks - 1; i++) {
			float[] tmpX = { this.xAxis[i], this.xAxis[i + 1] };
			float[] tmpY = { this.yAxis[i], this.yAxis[i + 1] };
			tmpTotal += getDistanceBetweenPoints(tmpX, tmpY);
		}

		this.length = tmpTotal;
	}

	public void calcAngle() {
		double angle = 0;
		int moveCounter = 0;
		int tickJump = 30;
		for (int i = 0; i < this.ticks; i++) {
			float[] tmpX = { this.xAxis[i], this.xAxis[i + tickJump] };
			float[] tmpY = { this.yAxis[i], this.yAxis[i + tickJump] };
			if (tmpX[1] != tmpX[0] && tmpY[1] != tmpY[0]) {
				angle += getAngleBetweenPoints(tmpX, tmpY);
				moveCounter++;
			} else if (tmpX[1] == tmpX[0] && tmpY[0] == tmpY[1]) {
				angle += 0;
			} else if (tmpX[0] == tmpX[1]) {
				angle += 90;
				moveCounter++;
			}
		}

		this.avgAngle = angle / moveCounter;
	}

	public double calcVeloSD() {
		double avgLength = this.length / this.ticks;
		double tmpLength;
		double tmpTotal = 0;
		for (int i = 0; i < this.ticks - 1; i++) {
			float[] tmpX = { this.xAxis[i], this.xAxis[i + 1] };
			float[] tmpY = { this.yAxis[i], this.yAxis[i + 1] };
			tmpLength = getDistanceBetweenPoints(tmpX, tmpY);
			tmpTotal += Math.pow(tmpLength - avgLength, 2);
		}

		return Math.sqrt(tmpTotal / (this.ticks + 1));
	}

	public double calcAnglSD() {
		double tmpAngle = 0;
		double tmpTotal = 0;
		int moveCount = 1;
		int tickJump = 30;
		for (int i = 0; i < this.ticks - tickJump; i++) {
			float[] tmpX = { this.xAxis[i], this.xAxis[i + tickJump] };
			float[] tmpY = { this.yAxis[i], this.yAxis[i + tickJump] };
			if (tmpX[1] != tmpX[0] && tmpY[1] != tmpY[0]) {
				tmpAngle = getAngleBetweenPoints(tmpX, tmpY);
				moveCount++;
			} else if (tmpX[1] == tmpX[0] && tmpY[0] == tmpY[1]) {
				tmpAngle = 0;
			} else if (tmpX[0] == tmpX[1]) {
				tmpAngle = 90;
				moveCount++;
			}

			tmpTotal += Math.pow(tmpAngle - this.avgAngle, 2);
		}

		return Math.sqrt(tmpTotal / moveCount);
	}

	public int getIndex() {
		return this.index + 1;
	}

	public int getTicks() {
		return this.ticks;
	}

	public int getAxisSize() {
		return this.xAxis.length;
	}

	public int[] getAvgPos() {
		if (this.ticks != 0) {
			float tmpXTotal = this.xAxis[0] + this.xAxis[getTicks() - 1];
			float tmpYTotal = this.yAxis[0] + this.yAxis[getTicks() - 1];
			float xAvg = tmpXTotal / 2;
			float yAvg = tmpYTotal / 2;
			int[] avgPost = { (int) xAvg, (int) yAvg };
			return avgPost;
		} else {
			int[] empty = { 0, 0 };
			return empty;
		}
	}
	
	public double getLength() {
		return this.length;
	}

}
