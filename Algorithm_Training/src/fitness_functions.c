#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "cgp-sls.h"
#include <time.h>
#include "gmp.h"
#include "fitness_functions.h"
#include <limits.h>

mpf_t *softmaxMPF(double arr[], int arrLength);
mpf_t* mpfArrTest(void);
/* Find the maximum output for multi-output data */
/* Used for FTC */
mpf_t* mpfArrTest(void)
{
    mpf_t *foo;
    foo = malloc(4*sizeof(mpf_t));

    int i ;
    for(i = 0; i < 4; i++)
    {
        mpf_init_set_ui(foo[i],INT_MAX*i);
    }

    return foo;

}

int maxIndex(double *arr)
{
	int i = 0;
	int max = 0;
	double tmp = arr[0];

	for (i = 0; i < 4; i++)
	{
		if (tmp < arr[i])
		{
			tmp = arr[i];
			max = i;
		}
	}

	return max;
}

double fourOutputFitnessFunction(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	/* Routine check */
	if (getNumChromosomeInputs(chromo) != getNumDataSetInputs(data))
	{
		printf("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	if (getNumChromosomeOutputs(chromo) != getNumDataSetOutputs(data))
	{
		printf("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	int i;

	/* Counter to keep a record of error matches */
	double threshError = 0;

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		/* Get the chromosome output */
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double *chromoOutput = malloc(4 * sizeof(double));
		int j = 0;
		for (j = 0; j < 4; j++)
		{
			chromoOutput[j] = getChromosomeOutput(chromo, j);
		}

		/* Get acutal output */
		double *expectedOutput = getDataSetSampleOutputs(data, i);

		/* Check if the maximum output is in the index of the actual maximum output's */
		if (maxIndex(chromoOutput) != maxIndex(expectedOutput))
			threshError++;
	}

	return threshError / (getNumDataSetSamples(data));
}

double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	/* Routine check */
	if (getNumChromosomeInputs(chromo) != getNumDataSetInputs(data))
	{
		printf("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	if (getNumChromosomeOutputs(chromo) != getNumDataSetOutputs(data))
	{
		printf("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	int i;
	double threshError = 0;
	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		/* Get actual output and expected class */
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutput = getChromosomeOutput(chromo, 0);
		int expectedClass = getDataSetSampleOutputs(data, i)[0];

		/* If the actual output doesn't lie in the threshold range that defined by the class */
		/* Error counter increments by 1 */

		if (chromoOutput < threshold + (expectedClass - 1) * threshIncre || chromoOutput >= threshold + expectedClass * threshIncre)
		{
			threshError++;
		}
	}

	return threshError / (getNumDataSetSamples(data));
}

double totalSum(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	/* Routine check */
	if (getNumChromosomeInputs(chromo) != getNumDataSetInputs(data))
	{
		printf("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	if (getNumChromosomeOutputs(chromo) != getNumDataSetOutputs(data))
	{
		printf("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	int i = 0;
	double error = 0;

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutput = getChromosomeOutput(chromo, 0);
		double expectedOutput = getDataSetSampleOutputs(data, i)[0];

		/* Set error threshold */
		if (fabs(expectedOutput - chromoOutput) >= 8)
			error++;
	}

	return error / getNumDataSetSamples(data);
}

double* getBestEntity(char *fileName, struct dataSet* testData, double* tmpBest)
{
	FILE *fp;
	char line[256];
	int i = 0;
	double testDataNum = getNumDataSetSamples(testData);
	double overfitThreshold = 300/testDataNum;
	double undefitThreshold = 100/testDataNum;
    for(i = 0; i < 5; i++){
        tmpBest[i] = 0;
    }

	fp = fopen(fileName, "r");
	if (fp == NULL)
	{
		printf("\nFile not found.\n");
		return 0;
	}

	while (fgets(line, sizeof(line), fp))
	{
		i = 0;
		char *pch = strtok(line, " ");
		char **fitnessSeg = malloc(5 * sizeof(char *));

		while (pch != NULL)
		{
			fitnessSeg[i] = malloc(10 * sizeof(char));
			strcpy(fitnessSeg[i], pch);
			pch = strtok(NULL, " ");
			i++;
		}

		double numGen = atof(fitnessSeg[0]);
		double training = atof(fitnessSeg[1]);
		double validation = atof(fitnessSeg[2]);
		double testing = atof(fitnessSeg[3]);
		double confidence = atof(fitnessSeg[4]);

        if(training > tmpBest[1])
        {
            if(validation - tmpBest[2] > -undefitThreshold && testing - tmpBest[3] > -undefitThreshold)
            {

                if(fabs(doubleMin(validation, testing) - training) < overfitThreshold)
                {
                    if(fabs(doubleMax(validation,testing) - training) < overfitThreshold)
                    {
                        if(doubleMax(validation,testing) <= training)
                        {
                            tmpBest[0] = numGen;
                            tmpBest[1] = training;
                            tmpBest[2] = validation;
                            tmpBest[3] = testing;
                            tmpBest[4] = confidence;
                        }
                    }
                }
            }
        }
        else if (training == tmpBest[1])
        {
            if(doubleMax(validation, testing) == doubleMax(tmpBest[2], tmpBest[3]))
            {
                if(doubleMin(validation, testing) > doubleMin(tmpBest[2],tmpBest[3]))
                {
                    if(fabs(doubleMin(validation, testing) - training) < overfitThreshold)
                    {
                        if(fabs(doubleMax(validation,testing) - training) < overfitThreshold)
                        {
                            if(doubleMax(validation,testing) <= training)
                            {
                                tmpBest[0] = numGen;
                                tmpBest[1] = training;
                                tmpBest[2] = validation;
                                tmpBest[3] = testing;
                                tmpBest[4] = confidence;
                            }
                        }
                    }
                }
            }
            else if(doubleMax(validation, testing) > doubleMax(tmpBest[2], tmpBest[3]))
            {
                if(doubleMin(validation, testing) - doubleMin(tmpBest[2],tmpBest[3]) > -undefitThreshold)
                {
                    if(fabs(doubleMin(validation, testing) - training) < overfitThreshold)
                    {
                        if(fabs(doubleMax(validation,testing) - training) < overfitThreshold)
                        {
                            if(doubleMax(validation,testing) <= training)
                            {
                                tmpBest[0] = numGen;
                                tmpBest[1] = training;
                                tmpBest[2] = validation;
                                tmpBest[3] = testing;
                                tmpBest[4] = confidence;
                            }
                        }
                    }
                }
            }
        }

		for (i = 0; i < 5; i++)
		{
			free(fitnessSeg[i]);
		}
		free(fitnessSeg);
	}
	printf("\nBest gen at %.0f with fitness of %.2f, %.2f %.2f, mean confidence %.2f\n", tmpBest[0], tmpBest[1], tmpBest[2], tmpBest[3], tmpBest[4]);
	return tmpBest;
}

double doubleMax(double a, double b)
{
    if(a > b)
        return a;
    else
        return b;
}

double doubleMin(double a, double b)
{
    if(a < b)
        return a;
    else
        return b;
}

void stcAction(struct chromosome *chromo, struct dataSet *testData)
{
	int i;

	char **originalDataSet = importFile("dataSet.csv");
	int entries = 163;

	char **entrantID = malloc(entries * sizeof(char*));
	for(i = 0; i < entries; i++)
	{
		entrantID[i] = (char*) malloc(10*sizeof(char));
	}


	double *entrantFootprint = malloc(entries*sizeof(double));

	for(i = 0; i < entries; i++)
	{
		char* tmpID = (char*) strSplit(originalDataSet[i],",",0);
		strcpy(entrantID[i], tmpID);
		free(tmpID);

		char* tmpData = strSplit(originalDataSet[i],",",1);
		entrantFootprint[i] = atof(tmpData);
		free(tmpData);
	}

	insertionSort(entrantFootprint, entrantID, entries);

	int mismatchError = 0;
	for (i = 0; i < getNumDataSetSamples(testData); i++)
	{

		executeChromosome(chromo, getDataSetSampleInputs(testData, i));
		double chromoOutput = 0;

		chromoOutput = getChromosomeOutput(chromo, 0);
		int expectedOutput = getDataSetSampleOutputs(testData, i)[0];
		double dataInput = getDataSetSampleInput(testData,i,0);

		int idIndex = binarySearch(entrantFootprint, entries, dataInput*100000);
		int thresholdMargin[2] = {threshold + (expectedOutput - 1) * threshIncre,threshold + expectedOutput * threshIncre};
		int confidenceMargin[2] = {threshold + stcOutputDecode(chromoOutput) * threshIncre,threshold + (stcOutputDecode(chromoOutput)+1) * threshIncre};
		printf("\nEntity %d | Entrant ID %s\n", i + 1, entrantID[idIndex]);
		printf("CGP Output: ");
		printf("%.2f ", chromoOutput);

		printf("\nExpected Class: \t");
		pdDecode(expectedOutput-1);
		printf("\nConfidence Level: %.3f %%",stcConfidence(confidenceMargin,chromoOutput)*100);

		printf("\nCGP Output Class: \t");
		pdDecode(stcOutputDecode(chromoOutput));
		printf("\n");

		if (chromoOutput <= thresholdMargin[0] || chromoOutput > thresholdMargin[1])
		{
			printf("Mismatch ");
			mismatchError++;
		}
		else
		{
			printf("Match ");
		}

		printf(" %.2f", getDataSetSampleOutputs(testData, i)[0]);
		printf("\n");
	}

	printf("\nAccuracy = %.4f (%d/%d)", 100 - ((float)mismatchError * 100 / getNumDataSetSamples(testData)), (getNumDataSetSamples(testData) - mismatchError), getNumDataSetSamples(testData));

	printf("\n");
}

void ftcAction(struct chromosome *chromo, struct dataSet *testData)
{
	int i;
	int mismatchError = 0;

	char **originalDataSet = importFile("dataSet.csv");
	int entries = 163;

	char **entrantID = malloc(entries * sizeof(char*));
	for(i = 0; i < entries; i++)
	{
		entrantID[i] = (char*) malloc(50*sizeof(char));
	}


	double *entrantFootprint = malloc(entries*sizeof(double));

	for(i = 0; i < entries; i++)
	{
		char* tmpID = (char*) strSplit(originalDataSet[i],",",0);
		strcpy(entrantID[i], tmpID);
		free(tmpID);

		char* tmpData = strSplit(originalDataSet[i],",",1);
		entrantFootprint[i] = atof(tmpData);
		free(tmpData);
	}

	insertionSort(entrantFootprint, entrantID, entries);

	for (i = 0; i < getNumDataSetSamples(testData); i++)
	{
		executeChromosome(chromo, getDataSetSampleInputs(testData, i));

		double *chromoOutput = malloc(4 * sizeof(double));
		double *expectedOutput = getDataSetSampleOutputs(testData, i);
		double dataInput = getDataSetSampleInput(testData,i,0);

		int idIndex = binarySearch(entrantFootprint, entries, dataInput*100000);
		printf("Entity %d | Entrant ID %s\n", i + 1, entrantID[idIndex]);
		printf("CGP Output: ");
		int j = 0;
		for (j = 0; j < 4; j++)
		{
			chromoOutput[j] = getChromosomeOutput(chromo, j);
			printf("%.2f ", chromoOutput[j]);
		}
		printf("\nSoftmax Probability: ");
		double *softmaxOutput = softmax(chromoOutput, 4);
		//mpf_t *mpfSoftmaxOutput = softmaxMPF(chromoOutput, 4);

		for (j = 0; j < 4; j++)
		{
			printf("%.2f%% ", softmaxOutput[j] * 100);
            //gmp_printf("%.Ff%% ",mpfSoftmaxOutput[j]);

		}

		printf("\nExpected Class: \t");
		pdDecode(maxIndex(expectedOutput));
		printf("\tSoftmax: %.2f%%",softmaxOutput[maxIndex(expectedOutput)] * 100);
		//gmp_printf("\tSoftmax: %.Ff%%",mpfSoftmaxOutput[maxIndex(expectedOutput)]);

		printf("\nCGP Output Class: \t");
		pdDecode(maxIndex(chromoOutput));
		printf("\tSoftmax: %.2f%%",softmaxOutput[maxIndex(chromoOutput)] * 100);
		//gmp_printf("\tSoftmax: %.Ff%%",mpfSoftmaxOutput[maxIndex(chromoOutput)]);

		if (maxIndex(chromoOutput) != maxIndex(expectedOutput))
			mismatchError++;

		free(softmaxOutput);
		free(chromoOutput);

		printf("\n\n");

	}
	free(entrantFootprint);
	int j = 0;
	for(j = 0; j < entries; j++)
	{
		free(entrantID[i]);
		free(originalDataSet[i]);
	}

	free(entrantID);
	free(originalDataSet);
	printf("Accuracy = %.4f (%d/%d)", 100 - ((float)mismatchError * 100 / getNumDataSetSamples(testData)), (getNumDataSetSamples(testData) - mismatchError), getNumDataSetSamples(testData));
}

void tsAction(struct chromosome *chromo, struct dataSet *testData)
{
	int i;
	for (i = 0; i < getNumDataSetSamples(testData); i++)
	{

		executeChromosome(chromo, getDataSetSampleInputs(testData, i));
		double chromoOutput = 0;

		chromoOutput = getChromosomeOutput(chromo, 0);
		int expectedOutput = getDataSetSampleOutputs(testData, i)[0];
		printf("%.2f ", chromoOutput);

		printf("Diff: %.2f ", fabs(chromoOutput - expectedOutput));

		printf(" %.2f", getDataSetSampleOutputs(testData, i)[0]);
		printf("\n");
	}

	printf("\n");
}

void setFitnessFromText(char *arr, struct parameters *params)
{
	if (strcmp(arr, "STC") == 0)
	{
		setCustomFitnessFunction(params, simpleThresholdClassifier, "STC");
	}
	else if (strcmp(arr, "FTC") == 0)
	{
		setCustomFitnessFunction(params, fourOutputFitnessFunction, "FTC");
	}
	else if (strcmp(arr, "TS") == 0)
	{
		setCustomFitnessFunction(params, totalSum, "TS");
	}
}

void setDisplayAction(char *arr, struct chromosome *chromo, struct dataSet *testData)
{
	if (strcmp(arr, "STC") == 0)
	{
		stcAction(chromo, testData);
	}
	else if (strcmp(arr, "FTC") == 0)
	{
		ftcAction(chromo, testData);
	}
	else if (strcmp(arr, "TS") == 0)
	{
		tsAction(chromo, testData);
	}
}
//
//void runKFold(struct parameters *params, int numGens, int kFoldVar, char *fitnessFunction, char *randomNum)
//{
//	char *kFoldDataSrc = "./kfolddata";
//
//	struct chromosome *kFoldChromo[kFoldVar];
//	struct dataSet *kFoldTraining[kFoldVar];
//	struct dataSet *kFoldValidation[kFoldVar];
//	struct dataSet *kFoldTest[kFoldVar];
//
//	int i = 0;
//	for (i = 0; i < kFoldVar; i++)
//	{
//		kFoldChromo[i] = NULL;
//		kFoldTraining[i] = NULL;
//		kFoldValidation[i] = NULL;
//		kFoldTest[i] = NULL;
//
//		char foldIndex[kFoldVar];
//		char nFold[80];
//
//		strcpy(nFold, kFoldDataSrc);
//		strcat(nFold, "/fold_");
//		itoa(i, foldIndex, kFoldVar);
//		strcat(nFold, foldIndex);
//
//		char foldTrain[80];
//		strcpy(foldTrain, nFold);
//
//		char foldValidate[80];
//		strcpy(foldValidate, nFold);
//
//		char foldTest[80];
//		strcpy(foldTest, nFold);
//
//		strcat(foldTrain, "/01_training.csv");
//		strcat(foldValidate, "/02_validation.csv");
//		strcat(foldTest, "/03_testing.csv");
//
//		kFoldTraining[i] = initialiseDataSetFromFile(foldTrain);
//		kFoldValidation[i] = initialiseDataSetFromFile(foldValidate);
//		kFoldTest[i] = initialiseDataSetFromFile(foldTest);
//
//		kFoldChromo[i] = runValiTestCGP(params, kFoldTraining[i], kFoldValidation[i], kFoldTest[i], numGens);
//
//		printChromosome(kFoldChromo[i], 0);
//
//		saveChromosome(kFoldChromo[i], "latest_chromo.chromo");
//		saveChromosomeDot(kFoldChromo[i], 0, "chromo.dot");
//
//		//setDisplayAction(strtok(cgp_params[11],"\n"), chromo, testData);
//        double tmpBest[5];
//		getBestEntity(randomNum, kFoldTest,tmpBest);
//
//		//printf("%s\n%s\n%s\n",foldTrain, foldValidate, foldTest);
//		freeDataSet(kFoldTraining[i]);
//		freeDataSet(kFoldValidation[i]);
//	}
//
//	for (i = 0; i < kFoldVar; i++)
//	{
//		printf("\nIteration %d: \n", i);
//		setDisplayAction(fitnessFunction, kFoldChromo[i], kFoldTest[i]);
//		freeChromosome(kFoldChromo[i]);
//		freeDataSet(kFoldTest[i]);
//	}
//}

double *softmax(double arr[], int arrLength)
{
	double logSum = 0;

	double *logArr = malloc(arrLength * sizeof(double));
	double *logAns = malloc(arrLength * sizeof(double));
	int i = 0;

	for (i = 0; i < arrLength; i++)
	{
		logArr[i] = exp(arr[i]);

		logSum += exp(arr[i]);
	}
	for (i = 0; i < arrLength; i++)
	{

		logAns[i] = logArr[i] / logSum;

	}

	free(logArr);

	return logAns;
}

mpf_t *softmaxMPF(double arr[], int arrLength)
{
    mpf_t logSum;
    mpf_init_set_ui(logSum,0);
    mpf_t *logArr = malloc(arrLength * sizeof(mpf_t));
    mpf_t *logAns = malloc(arrLength * sizeof(mpf_t));

    int i;

    for(i = 0; i < arrLength; i++)
    {
        mpf_init_set_ui(logArr[i],exp(arr[i]));

        mpf_add(logSum, logSum, logArr[i]);

        mpf_init_set_ui(logAns[i],0);
    }

    for(i = 0; i < arrLength; i++)
    {
        mpf_div(logAns[i],logArr[i],logSum);
		mpf_mul_ui(logAns[i],logAns[i],100);
    }

    mpf_clear(logSum);

    for(i = 0; i < arrLength; i++)
	{
		mpf_clear(logArr[i]);
	}
	free(logArr[i]);
    return logAns;
}

double stcConfidence(int thresholdMargin[], double output)
{
    double middlePoint = (thresholdMargin[0]+thresholdMargin[1])/2;

    double distance = fabs(output - middlePoint);

    return 1 - distance/fabs(middlePoint - thresholdMargin[0]);
}

void pdDecode(int index)
{
	if (index == 0)
		printf("PD-NC");
	else if (index == 1)
		printf("PD-MCI");
	else if (index == 2)
		printf("PD-D");
	else if (index == 3)
		printf("HC");
    else
        printf("Non-classified");
}

int stcOutputDecode(double chromoOutput)
{
    double tmpResult = floor(chromoOutput / threshIncre);
    int i = 0;
    if(chromoOutput < threshold)
        return -1;

    while(tmpResult != 0)
    {
        i++;
        chromoOutput-=threshIncre;
        tmpResult = floor(chromoOutput/threshIncre);
    }

    return i-1;
}

char **importFile(char *fileName)
{
	FILE *fp;
	char line[1024];
	int i = 1;

	char **strArr = malloc(sizeof(char *));

	fp = fopen(fileName, "r");

	if (fp == NULL)
	{
		printf("File not found");
		return 0;
	}


	while (fgets(line, sizeof(line), fp))
	{
	    strArr = realloc(strArr, i * sizeof(char*));
		strArr[i-1] = malloc(1024*sizeof(char));
		strcpy(strArr[i-1], line);
		i++;
	}

	fclose(fp);
	return strArr;
}

char *strSplit(char *strArr, char *delimiter, int index)
{

    int i = 0;
    char *tmpBuffer = (char*) malloc(strlen(strArr)*2*sizeof(char));

    strcpy(tmpBuffer, strArr);
    char *ptr = (char*) malloc(64*sizeof(char));
    //ptr = strtok(tmpBuffer, delimiter);
    strcpy(ptr,strtok(tmpBuffer, delimiter));


    for (i = 0; i < index; i++)
    {
        strcpy(ptr,strtok(NULL, delimiter));
        //ptr = strtok(NULL, delimiter);
    }

    char *rtnStr = (char*) malloc(strlen(ptr)*sizeof(char));
    strcpy(rtnStr,ptr);

    free(tmpBuffer);
    free(ptr);


	return rtnStr;
}

void insertionSort(double *arr, char **idArr, int length)
{
	int i, j;
	double key;
	for(i = 1; i < length; i++)
	{
		key = arr[i];
		char *key2 = idArr[i];
		j = i - 1;
		while(j >= 0 && arr[j] > key)
		{
			arr[j+1] = arr[j];
			idArr[j+1] = idArr[j];
			j--;
		}
		arr[j+1] = key;
		idArr[j+1] = key2;
	}
}

int binarySearch(double *arr, int arrSize, double target)
{
	int left = 0;
	int right = arrSize - 1;
	int middle;
	while(left <= right)
	{
		middle = (int)floor((left+right)/2);
		if(arr[middle] < target)
			left = middle + 1;
		else if(arr[middle] > target)
			right = middle - 1;
		else
			return middle;
	}

	return -1;
}

void exportBestChromo(double* bestTemp, int kIndex)
{
    /* Concatrating file name */
    char**  cgp_params  = importFile("cgp_params.txt");
	double mutRate = atof(cgp_params[8]);
	strtok(cgp_params[3],"\n");
    strtok(cgp_params[4],"\n");
    strtok(cgp_params[8],"\n");

    mutRate*=100;

	char mutRateChar[3];
	char chromoFileName[30] = "./CGP_BestChromo/";
    sprintf(mutRateChar, "%.f",mutRate);
	strcat(chromoFileName,cgp_params[3]);
    strcat(chromoFileName,"_");
    strcat(chromoFileName,cgp_params[4]);
    strcat(chromoFileName,"_");
    strcat(chromoFileName,mutRateChar);
	strcat(chromoFileName,"_bestChromo.csv");


	/* Read file, if not exist, create one */
	FILE *tmpFile = fopen(chromoFileName, "a");
	if(tmpFile == NULL){
		printf("Error with opening file.\n");
	}
	/* Write parameters before exporting chromo performance details */
	fprintf(tmpFile, "%d,", atoi(cgp_params[7]));
	/* Write kfold detail */
	fprintf(tmpFile, "Fold %d,",kIndex);


	/* Write chromo performance detail */
	fprintf(tmpFile, "%.0f,%.2f,%.2f,%.2f,%.2f\n",bestTemp[0],bestTemp[1],bestTemp[2],bestTemp[3],bestTemp[4]);

	free(cgp_params);
	fclose(tmpFile);

}
